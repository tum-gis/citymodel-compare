package jgraf.citygml;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import com.github.davidmoten.rtree.geometry.Rectangle;
import jgraf.neo4j.Neo4jGraphRef;
import jgraf.neo4j.diff.Change;
import jgraf.neo4j.diff.SizeChange;
import jgraf.neo4j.diff.TranslationChange;
import jgraf.neo4j.factory.AuxEdgeTypes;
import jgraf.neo4j.factory.AuxNodeLabels;
import jgraf.neo4j.factory.EdgeTypes;
import jgraf.neo4j.factory.PropNames;
import org.citygml4j.model.citygml.building.Building;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.IndexDefinition;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Traverser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.script.*;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.ZonedDateTime;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.StreamSupport;

public class Patterns {
    public enum _RuleNodeLabels implements Label {
        RULE
    }

    public enum _ChangePropNames {
        change_type,
        x,
        y,
        z,
        tags
    }

    private enum _RuleNodePropNames {
        change_type,
        join,
        calc_scope,
        tags
    }

    private enum _RuleRelTypes implements RelationshipType {
        AGGREGATED_TO,
        SAVED_FOR,
        AUX,
        SCOPED_TO
    }

    private enum _RuleRelPropNames {
        next_content_type,
        name,
        search_length,
        not_contains,
        scope,
        conditions,
        propagate,
        weight
    }

    private enum _MemoryNodeLabels implements Label {
        MEMORY
    }

    private enum _MemoryPropNames {
        next_change_type,
        count_value_, // Prefix + change type
        cap_value_, // Prefix + change type
        property_, // Prefix + property name
        name_, // Prefix + property name
        done // Used for cleaning up memory nodes in post-processing
    }

    private enum _ScopeNodeLabels implements Label {
        SCOPE
    }

    private enum _ScopePropNames {
        change_type,
        constraints,
        scope,
        number_type_count,
        number_type_in_spatial,
        number_type_cap,
        spatial,
        coverage_spatial_over_all,
        coverage_type_over_all,
        coverage_type_in_spatial
    }

    private enum _ScopePropValues {
        global,
        clustered
    }

    private static final String WILDCARD = "*";
    private final static Logger logger = LoggerFactory.getLogger(Patterns.class);

    public static void createRuleNetwork(GraphDatabaseService graphDb, String file, long timeout) {
        // Read Cypher script file into a string
        String query = null;
        try {
            query = Files.readString(Path.of(file));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        try (Transaction tx = graphDb.beginTx(); Result result = tx.execute(query)) {
            long count = tx.findNodes(_RuleNodeLabels.RULE).stream().count();
            tx.commit();
            logger.info("Created in total {} rule nodes from Cypher script file in {}", count, file);
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }

        // Create index on all rule nodes
        IndexDefinition ruleIndex, scopeIndex, memoryIndex;
        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = tx.schema();
            ruleIndex = schema.indexFor(_RuleNodeLabels.RULE)
                    .on(_RuleNodePropNames.change_type.toString())
                    .withName("ruleIndex")
                    .create();
            scopeIndex = schema.indexFor(_ScopeNodeLabels.SCOPE)
                    .on(_ScopePropNames.change_type.toString())
                    //.on(_ScopePropNames.constraints.toString()) // TODO Currently Neo4j only supports single-key indexing
                    .withName("scopeIndex")
                    .create();
            memoryIndex = schema.indexFor(_MemoryNodeLabels.MEMORY)
                    //.on(_MemoryPropNames.next_change_type.toString()) // TODO Currently Neo4j only supports single-key indexing
                    .on(_MemoryPropNames.done.toString())
                    .withName("memoryIndex")
                    .create();
            tx.commit();
            logger.info("Created index on rule nodes");
        }

        // Wait for index to complete
        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = tx.schema();
            schema.awaitIndexOnline(ruleIndex, timeout, TimeUnit.SECONDS);
            schema.awaitIndexOnline(scopeIndex, timeout, TimeUnit.SECONDS);
            schema.awaitIndexOnline(memoryIndex, timeout, TimeUnit.SECONDS);
            tx.commit();
            logger.info("Rule index online");
        }
    }

    public static void interpret(
            GraphDatabaseService graphDb,
            String jsFile,
            RTree<Neo4jGraphRef, Geometry> rtree,
            double precision,
            int timeout
    ) {
        logger.info("|--> Applying pattern rules");

        // Init list of all top-level features // TODO Memory check?
        List<String> toplevelIds = new ArrayList<>();
        try (Transaction tx = graphDb.beginTx()) {
            // Find all top-level features and their changes
            tx.findNodes(Label.label(Building.class.getName())).forEachRemaining(toplevel -> {
                // TODO Extend for CityGML v3, and all other top-level features

                // Only search for nodes from the first dataset (partition index 0)
                if (!toplevel.hasLabel(Label.label(AuxNodeLabels.__PARTITION_INDEX__ + "0"))) return;

                toplevelIds.add(toplevel.getElementId());
            });
            logger.info("Found {} top-level features", toplevelIds.size());
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }

        // Script Engine for evaluating conditions in JavaScript syntax
        logger.info("Initializing Script Engine for evaluating conditions");
        System.setProperty("nashorn.args", "--language=es6");
        ScriptEngineManager mgr = new ScriptEngineManager();
        ScriptEngine engine = mgr.getEngineByName("nashorn");
        // https://stackoverflow.com/a/30159424 says Script Engines can be used in multithreading, except bindings
        String jsFnString;
        try {
            jsFnString = Files.readString(Path.of(jsFile));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Phase 1: Multi-threaded processing of sub-elements of top-level features
        logger.info("Phase 1: Interpreting changes of sub-elements of top-level features");
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        toplevelIds.forEach(toplevelId -> executorService.submit(()
                -> processPhase1(graphDb, toplevelId, toplevelIds.size(), rtree, jsFnString, engine, precision)));

        // Wait for all threads to finish
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(timeout, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        // Phase 2: Single-threaded aggregation of top-level features (including calculating scope)
        logger.info("Phase 2: Interpreting changes over top-level features");
        processPhase2(graphDb, toplevelIds, rtree, jsFnString, engine, precision);

        // Post-processing
        long nrCleaned = cleanUsedMemory(graphDb);
        logger.info("Cleaned {} used memory nodes", nrCleaned);

        logger.info("-->| Interpreted all changes based on given rules");
    }

    private static void processPhase1(
            GraphDatabaseService graphDb,
            String toplevelId,
            int toplevelSize,
            RTree<Neo4jGraphRef, Geometry> rtree,
            String jsFnString,
            ScriptEngine engine,
            double precision
    ) {
        try (Transaction tx = graphDb.beginTx()) {
            // Get top-level node based on internal ID
            Node toplevel = tx.getNodeByElementId(toplevelId);

            // Find all changes that are attached to the descendants of the top-level node
            Traverser traverser = tx.traversalDescription()
                    .depthFirst()
                    .expand(PathExpanders.forDirection(Direction.OUTGOING))
                    .evaluator(Evaluators.fromDepth(1)) // excluding top-level feature nodes (for phase 2)
                    .evaluator(path -> {
                        // The relationship TANDEM is used to connect changes to their content nodes in the older dataset
                        if (path.endNode().hasRelationship(Direction.INCOMING, AuxEdgeTypes.TANDEM))
                            return Evaluation.INCLUDE_AND_CONTINUE;
                        return Evaluation.EXCLUDE_AND_CONTINUE;
                    })
                    .traverse(toplevel);

            // Init a FIFO queue for processing and interpreting changes
            Queue<Node> queue = new LinkedList<>();
            traverser.forEach(path -> path.endNode().getRelationships(Direction.INCOMING, AuxEdgeTypes.TANDEM).forEach(rel -> {
                queue.add(rel.getStartNode());
            }));

            // Can be reused in both phase 1 and 2
            logger.debug("Found {} literal changes for top-level feature {}", queue.size(), toplevelId);
            processCore(graphDb, tx, queue, toplevelSize, rtree, jsFnString, engine, precision);

            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    private static void processPhase2(
            GraphDatabaseService graphDb,
            List<String> toplevelIds,
            RTree<Neo4jGraphRef, Geometry> rtree,
            String jsFnString,
            ScriptEngine engine,
            double precision
    ) {
        try (Transaction tx = graphDb.beginTx()) {
            // Init a FIFO queue for processing and interpreting changes
            Queue<Node> queue = new LinkedList<>();
            toplevelIds.forEach(id -> {
                Node toplevel = tx.getNodeByElementId(id);
                toplevel.getRelationships(Direction.INCOMING, AuxEdgeTypes.TANDEM).forEach(rel -> {
                    Node change = rel.getStartNode();
                    queue.add(change);
                });
            });

            // Can be reused in both phase 1 and 2
            logger.debug("Found {} changes across all top-level features", queue.size());
            processCore(graphDb, tx, queue, toplevelIds.size(), rtree, jsFnString, engine, precision);

            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    private static void processCore(
            GraphDatabaseService graphDb,
            Transaction tx,
            Queue<Node> queue,
            int toplevelSize,
            RTree<Neo4jGraphRef, Geometry> rtree,
            String jsFnString,
            ScriptEngine engine,
            double precision) {
        // Init
        Rectangle entireBbox = rtree.mbr().get();

        while (!queue.isEmpty()) {
            // Take changes from the queue until none is left
            Node change = queue.poll();
            String changeType = change.getProperty(_ChangePropNames.change_type.toString()).toString();

            // Content node
            Node content = change.getSingleRelationship(AuxEdgeTypes.TANDEM, Direction.OUTGOING).getEndNode();
            if (!content.hasLabel(Label.label(AuxNodeLabels.__PARTITION_INDEX__ + "0")))
                throw new RuntimeException("Content node is not from old dataset");

            // Find all rules that match the change
            List<Node> rules = tx.findNodes(_RuleNodeLabels.RULE, _RuleNodePropNames.change_type.toString(), changeType)
                    .stream().toList();
            if (rules.size() > 1) {
                throw new RuntimeException("At most one rule node is allowed in the rule network for change type "
                        + change.getProperty(_ChangePropNames.change_type.toString()));
            }
            if (rules.isEmpty()) {
                // No rule defined for this change
                continue;
            }
            // Exactly one rule node found
            Node rule = rules.get(0);

            // Calculate scope (only top-level features can have a scope)
            // This must be done before evaluating conditions, because the scope node is needed for rule edges
            if (rule.hasProperty(_RuleNodePropNames.calc_scope.toString())) {
                String calcScope = rule.getProperty(_RuleNodePropNames.calc_scope.toString()).toString();
                Set<String> scopeTypes = new HashSet<>();
                for (String s : calcScope.split(";")) {
                    scopeTypes.add(s.trim());
                }

                // Check if the scope node has already been created
                Node scope = getScope(rule, change, changeType, precision);
                if (scope != null) continue; // Already scoped

                // The scope node has not yet been created -> Scope over ALL changes of this change type
                scope = tx.createNode(_ScopeNodeLabels.SCOPE);
                Node finalScope = scope;

                double[] bboxLower = {Double.MAX_VALUE, Double.MAX_VALUE};
                double[] bboxUpper = {Double.MIN_VALUE, Double.MIN_VALUE}; // TODO Support more spatial types other than bbox?
                AtomicInteger count = new AtomicInteger();
                tx.findNodes(Label.label(Change.class.getName()), _ChangePropNames.change_type.toString(), changeType).stream()
                        .filter(c -> fuzzyEquals(c, change, "", "", scopeTypes, precision)) // TODO Support more than equals (>=, <=, etc.)
                        .filter(c -> getScope(c, scopeTypes, precision) == null) // Skip if this other change has been scoped
                        .forEach(c -> {
                            // Connect to scope node
                            c.createRelationshipTo(finalScope, _RuleRelTypes.SCOPED_TO);

                            // Retrieve building node
                            Node toplevel = c.getSingleRelationship(AuxEdgeTypes.TANDEM, Direction.OUTGOING).getEndNode();
                            Node envelope = toplevel
                                    .getSingleRelationship(EdgeTypes.boundedBy, Direction.OUTGOING).getEndNode()
                                    .getSingleRelationship(EdgeTypes.envelope, Direction.OUTGOING).getEndNode();
                            Node lowerCorner = envelope
                                    .getSingleRelationship(EdgeTypes.lowerCorner, Direction.OUTGOING).getEndNode()
                                    .getSingleRelationship(EdgeTypes.value, Direction.OUTGOING).getEndNode()
                                    .getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode();
                            Node upperCorner = envelope
                                    .getSingleRelationship(EdgeTypes.upperCorner, Direction.OUTGOING).getEndNode()
                                    .getSingleRelationship(EdgeTypes.value, Direction.OUTGOING).getEndNode()
                                    .getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode();
                            // TODO Compare classes Envelope and DirectPosition in CityGMl v2 and v3 here
                            double iminx = Double.parseDouble(lowerCorner.getProperty(PropNames.ARRAY_MEMBER + "[0]").toString());
                            double iminy = Double.parseDouble(lowerCorner.getProperty(PropNames.ARRAY_MEMBER + "[1]").toString());
                            double imaxx = Double.parseDouble(upperCorner.getProperty(PropNames.ARRAY_MEMBER + "[0]").toString());
                            double imaxy = Double.parseDouble(upperCorner.getProperty(PropNames.ARRAY_MEMBER + "[1]").toString());

                            // Compare and update spatial extents
                            bboxLower[0] = Math.min(bboxLower[0], iminx);
                            bboxLower[1] = Math.min(bboxLower[1], iminy);
                            bboxUpper[0] = Math.max(bboxUpper[0], imaxx);
                            bboxUpper[1] = Math.max(bboxUpper[1], imaxy);

                            // Increase count
                            count.getAndIncrement();
                        });

                Rectangle bbox = Geometries.rectangle(bboxLower[0], bboxLower[1], bboxUpper[0], bboxUpper[1]);
                int countInSpatial = rtree.search(bbox).count().toBlocking().single();

                // Store information in the scope node after ALL changes of this change type have been processed
                scope.setProperty(_ScopePropNames.change_type.toString(), changeType);
                scope.setProperty(_ScopePropNames.constraints.toString(), calcScope);
                scope.setProperty(_ScopePropNames.scope.toString(), count.get() == toplevelSize
                        ? _ScopePropValues.global.toString()
                        : _ScopePropValues.clustered.toString()); // TODO Currently only either clustered or global
                scope.setProperty(_ScopePropNames.spatial.toString(),
                        bboxLower[0] + "," + bboxLower[1] + ";" + bboxUpper[0] + "," + bboxUpper[1]);
                scope.setProperty(_ScopePropNames.coverage_spatial_over_all.toString(),
                        bbox.area() / entireBbox.area());
                scope.setProperty(_ScopePropNames.coverage_type_over_all.toString(),
                        count.get() * 1. / toplevelSize);
                scope.setProperty(_ScopePropNames.coverage_type_in_spatial.toString(),
                        count.get() * 1. / countInSpatial);
                scope.setProperty(_ScopePropNames.number_type_count.toString(), count.get());
                scope.setProperty(_ScopePropNames.number_type_in_spatial.toString(), countInSpatial);
                scope.setProperty(_ScopePropNames.number_type_cap.toString(), toplevelSize);
                for (String type : scopeTypes) {
                    scope.setProperty(type, change.getProperty(type).toString());
                }
                // If the scope node has already been created -> all changes have been scoped (incl. this one) -> skip
            }

            rule.getRelationships(Direction.OUTGOING, _RuleRelTypes.AGGREGATED_TO).stream().forEach(rel -> {
                // Evaluate conditions, engine bindings are reset
                boolean conditionForAll = false; // e.g., "x;y;z" means for all same values of x, y, z across changes
                if (rel.hasProperty(_RuleRelPropNames.conditions.toString())) {
                    String conditions = rel.getProperty(_RuleRelPropNames.conditions.toString()).toString();
                    conditionForAll = conditions.contains(";");
                    if (!conditionForAll && !checkLocalConditions(change, null, conditions, jsFnString, engine)) {
                        return;
                    }
                }

                // Next rule node
                Node nextRule = rel.getEndNode();
                String nextChangeType = nextRule.getProperty(_RuleNodePropNames.change_type.toString()).toString();

                // Check if the next change node has already been created
                if (change.hasRelationship(Direction.OUTGOING, _RuleRelTypes.AGGREGATED_TO)
                        && change.getRelationships(Direction.OUTGOING, _RuleRelTypes.AGGREGATED_TO).stream()
                        .anyMatch(r -> {
                            Node tmp = r.getEndNode();
                            if (!tmp.hasLabel(Label.label(Change.class.getName()))) return false;
                            if (!tmp.hasProperty(_ChangePropNames.change_type.toString())) return false;
                            String t = tmp.getProperty(_ChangePropNames.change_type.toString()).toString();
                            return t.equals(nextChangeType);
                        })) {
                    // The next change node has already been created -> skip
                    return;
                }

                // Next content node
                List<Label> notContainedLabels;
                if (rel.hasProperty(_RuleRelPropNames.not_contains.toString())) {
                    notContainedLabels = Arrays.stream(rel.getProperty(_RuleRelPropNames.not_contains.toString())
                                    .toString()
                                    .replaceAll("\\s+", "")
                                    .split(";"))
                            .map(Label::label).toList();
                } else {
                    notContainedLabels = null;
                }
                int searchLength = rel.hasProperty(_RuleRelPropNames.search_length.toString()) ?
                        Integer.parseInt(rel.getProperty(_RuleRelPropNames.search_length.toString()).toString()) :
                        Integer.MAX_VALUE;
                Node nextContent = getNextContent(tx, content,
                        Label.label(rel.getProperty(_RuleRelPropNames.next_content_type.toString()).toString()),
                        searchLength, notContainedLabels);
                if (nextContent == null) return;

                // Evaluate scope (only for top-level features)
                if (rel.hasProperty(_RuleRelPropNames.scope.toString())) {
                    Node scope = getScope(rule, change, changeType, precision);

                    // Check if the found scope node satisfies the scope condition (global/clustered)
                    String scRel = rel.getProperty(_RuleRelPropNames.scope.toString()).toString();
                    String scNode = scope.getProperty(_ScopePropNames.scope.toString()).toString();
                    if (!scRel.trim().equals(scNode.trim())) return;

                    // Create next change WITHOUT MEMORY NODE (which is only needed for non-top-level features)
                    Node nextChange = tx.createNode(Label.label(Change.class.getName()));

                    // Change type
                    nextChange.setProperty(_ChangePropNames.change_type.toString(), nextChangeType);

                    // Save properties from scope to next change
                    for (String k : scope.getPropertyKeys()) {
                        if (k.equals(_ChangePropNames.change_type.toString())) continue; // Do not override change type
                        nextChange.setProperty(k, scope.getProperty(k));
                    }

                    // Tags
                    if (nextRule.hasProperty(_RuleNodePropNames.tags.toString())) {
                        String tags = nextRule.getProperty(_RuleNodePropNames.tags.toString()).toString();
                        nextChange.setProperty(_ChangePropNames.tags.toString(), tags);
                    }

                    // Connect next change to next content node
                    nextChange.createRelationshipTo(nextContent, AuxEdgeTypes.TANDEM);

                    // Connect scope node to this next change
                    scope.createRelationshipTo(nextChange, _RuleRelTypes.AGGREGATED_TO);

                    // Add this next change to the queue
                    queue.add(nextChange);

                    return;
                }

                // A memory node contains ALL information needed for the current next rule node
                // Multiple memory nodes may exist for the same content node
                List<Relationship> memoryRels = nextContent.getRelationships(Direction.INCOMING, _RuleRelTypes.SAVED_FOR).stream()
                        .filter(r -> r.getStartNode().getProperty(_MemoryPropNames.next_change_type.toString()).toString()
                                .equals(nextChangeType))
                        .toList();
                if (memoryRels.size() > 1) {
                    throw new RuntimeException("Multiple memory nodes for same next rule node "
                            + nextRule.getProperty(_RuleNodePropNames.change_type.toString()));
                }
                Node memory;
                if (memoryRels.isEmpty()) {
                    // Init memory node
                    memory = tx.createNode(_MemoryNodeLabels.MEMORY);

                    // Next change type (exactly one)
                    memory.setProperty(_MemoryPropNames.next_change_type.toString(), nextChangeType);

                    // Store properties if conditions contain ";" (for all same values of x, y, z across changes)
                    if (conditionForAll) {
                        Set<String> properties = new HashSet<>();
                        Arrays.stream(rel.getProperty(_RuleRelPropNames.conditions.toString()).toString().split(";"))
                                .forEach(s -> properties.add(s.trim()));
                        for (String property : properties) {
                            memory.setProperty(_MemoryPropNames.property_ + property,
                                    normalize(change.getProperty(property).toString(), precision));
                        }
                    }

                    // Connect memory to next content node
                    memory.createRelationshipTo(nextContent, _RuleRelTypes.SAVED_FOR);
                } else {
                    // Fetch memory node for this next rule node
                    memory = memoryRels.get(0).getStartNode();
                }
                // Cap values
                String capKey = _MemoryPropNames.cap_value_
                        + rule.getProperty(_RuleNodePropNames.change_type.toString()).toString();
                if (!memory.hasProperty(capKey)) {
                    if (rel.hasProperty(_RuleRelPropNames.weight.toString())) {
                        if (rel.getProperty(_RuleRelPropNames.weight.toString()).toString().equals(WILDCARD)) {
                            // "*" -> Automatically calculate cap value by counting descendants
                            memory.setProperty(capKey, countDescendants(tx, nextContent,
                                    content.getLabels(), notContainedLabels));
                        } else {
                            memory.setProperty(capKey, Integer.parseInt(
                                    rel.getProperty(_RuleRelPropNames.weight.toString()).toString()));
                        }
                    }
                }

                // Check conditions for all same values of x, y, z across changes (e.g., "x;y;z")
                if (conditionForAll) {
                    Set<String> properties = new HashSet<>();
                    Arrays.stream(rel.getProperty(_RuleRelPropNames.conditions.toString()).toString().split(";"))
                            .forEach(s -> properties.add(s.trim()));
                    if (!fuzzyEquals(memory, change, _MemoryPropNames.property_.toString(), "", properties, precision)) {
                        return;
                    }
                }

                // Update count values of not yet visited changes
                if (!change.hasRelationship(Direction.OUTGOING, _RuleRelTypes.AUX)
                        || change.getRelationships(Direction.OUTGOING, _RuleRelTypes.AUX).stream()
                        .noneMatch(r -> r.getEndNode().equals(memory))) {
                    // Count values
                    String countKey = _MemoryPropNames.count_value_
                            + rule.getProperty(_RuleNodePropNames.change_type.toString()).toString();
                    if (memory.hasProperty(countKey)) {
                        int value = Integer.parseInt(memory.getProperty(countKey).toString());
                        memory.setProperty(countKey, value + 1);
                    } else {
                        memory.setProperty(countKey, 1);
                    }

                    // Connect change to the memory (for later redirection to next change)
                    change.createRelationshipTo(memory, _RuleRelTypes.AUX);
                }

                // Propagate properties from changes to memory
                if (rel.hasProperty(_RuleRelPropNames.propagate.toString())) {
                    String propagate = rel.getProperty(_RuleRelPropNames.propagate.toString()).toString();
                    propagate(change, memory, propagate, _MemoryPropNames.property_.toString(), precision);
                }

                // Check if all cap values are full
                Map<String, Boolean> capFilled = new HashMap<>();
                nextRule.getRelationships(Direction.INCOMING, _RuleRelTypes.AGGREGATED_TO).forEach(r -> {
                    String capK = r.getStartNode().getProperty(_RuleNodePropNames.change_type.toString()).toString();
                    capFilled.put(capK, false);
                });
                for (String k : memory.getPropertyKeys()) {
                    if (k.startsWith(_MemoryPropNames.count_value_.toString())) {
                        int count = Integer.parseInt(memory.getProperty(k).toString());
                        String key = k.replace(_MemoryPropNames.count_value_.toString(), "");
                        int cap = Integer.parseInt(memory.getProperty(_MemoryPropNames.cap_value_ + key).toString());
                        if (cap > count) return;
                        if (cap < count)
                            logger.warn("Cap value exceeded " + cap + " < " + count + " for change type " + key);
                        //throw new RuntimeException("Cap value exceeded " + cap + " < " + count + " for change type " + key);
                        capFilled.put(key, true);
                    }
                }
                if (capFilled.values().stream().anyMatch(b -> !b)) return;

                // THE FOLLOWING CODE BLOCKS only apply if ALL cap values are full

                // Joining converging edges
                // - all cap values are filled
                // - all converging edges are named
                if (nextRule.hasProperty(_RuleNodePropNames.join.toString())) { // TODO
                    String join = nextRule.getProperty(_RuleNodePropNames.join.toString()).toString();

                    if (!join.contains(".")) {
                        throw new RuntimeException("Join conditions must call named edges with dot notation");
                    }

                    // Create JSON object containing properties for each converging edge
                    StringBuilder json = new StringBuilder();
                    memory.getRelationships(Direction.INCOMING, _RuleRelTypes.AUX).forEach(r -> {
                        Node c = r.getStartNode();
                        String t = c.getProperty(_ChangePropNames.change_type.toString()).toString();

                        // Store named edges in memory
                        String name = nextRule.getRelationships(Direction.INCOMING, _RuleRelTypes.AGGREGATED_TO).stream()
                                .filter(r2 -> r2.getStartNode().getProperty(_RuleNodePropNames.change_type.toString())
                                        .toString().equals(t))
                                .toList().get(0)
                                .getProperty(_RuleRelPropNames.name.toString()).toString();
                        if (!memory.hasProperty(_MemoryPropNames.name_ + t)) {
                            // If there are multiple change instances for this change type -> use the first one
                            memory.setProperty(_MemoryPropNames.name_ + t, name);
                        }

                        // Copy all properties from a previous change to json.<name>
                        json.append(name).append(" = { ");
                        for (String k : c.getPropertyKeys()) {
                            json.append(k).append(": \"").append(c.getProperty(k)).append("\", ");
                        }

                        // Set satisfied to true since all conditions for this change are satisfied
                        json.append("satisfied: true, ");

                        // Scope in join
                        if (join.contains(name + ".scope.")) {
                            // The scope node is either connected before or after the change node
                            Node scope = null;
                            if (c.hasRelationship(Direction.OUTGOING, _RuleRelTypes.SCOPED_TO)) {
                                scope = c.getSingleRelationship(_RuleRelTypes.SCOPED_TO, Direction.OUTGOING).getEndNode();
                            } else if (c.hasRelationship(Direction.INCOMING, _RuleRelTypes.AGGREGATED_TO)) {
                                scope = c.getSingleRelationship(_RuleRelTypes.AGGREGATED_TO, Direction.INCOMING).getStartNode();
                            } else {
                                throw new RuntimeException("Scope node not found for change type " + t);
                            }
                            // Create a sub-object for scope
                            json.append("scope: { ");
                            for (String k : scope.getPropertyKeys()) {
                                json.append(k).append(": \"").append(scope.getProperty(k)).append("\", ");
                            }
                            // Remove last comma
                            json.delete(json.length() - 2, json.length());
                            json.append(" }, ");
                        }

                        // Remove last comma
                        json.delete(json.length() - 2, json.length());
                        json.append(" };");
                    });

                    if (!checkLocalConditions(memory, json.toString(), join, jsFnString, engine)) {
                        return;
                    }
                }

                // All conditions for the next change node are satisfied
                Node nextChange = tx.createNode(Label.label(Change.class.getName()));

                // Change type
                nextChange.setProperty(_ChangePropNames.change_type.toString(), nextChangeType);

                // Save properties from memory to next change
                for (String k : memory.getPropertyKeys()) {
                    if (k.startsWith(_MemoryPropNames.property_.toString())) {
                        String key = k.replace(_MemoryPropNames.property_.toString(), "");
                        nextChange.setProperty(key, memory.getProperty(k));
                    }
                }

                // Tags
                if (nextRule.hasProperty(_RuleNodePropNames.tags.toString())) {
                    String tags = nextRule.getProperty(_RuleNodePropNames.tags.toString()).toString();
                    nextChange.setProperty(_ChangePropNames.tags.toString(), tags);
                }

                // Connect change to next content node
                nextChange.createRelationshipTo(nextContent, AuxEdgeTypes.TANDEM);

                // Connect all previous changes to this next change
                memory.getRelationships(Direction.INCOMING, _RuleRelTypes.AUX).forEach(r -> {
                    r.getStartNode().createRelationshipTo(nextChange, _RuleRelTypes.AGGREGATED_TO);
                    r.delete();
                });
                // Do not delete memory now, because another may be created again for the same content node
                // --> Mark memory later for clean up
                memory.setProperty(_MemoryPropNames.done.toString(), true);

                // Add this next change to the queue
                queue.add(nextChange);
            });
        }
    }

    private static Node getScope(Node rule, Node change, String changeType, double precision) {
        if (!rule.hasProperty(_RuleNodePropNames.calc_scope.toString())) {
            throw new RuntimeException("The scope condition in a rule edge must come " +
                    "with a directive to calculate scope in the previous rule node");
        }

        Set<String> scopeTypes = new HashSet<>();
        Arrays.stream(rule.getProperty(_RuleNodePropNames.calc_scope.toString()).toString().split(";"))
                .forEach(s -> scopeTypes.add(s.trim()));
        return getScope(change, scopeTypes, precision);
    }

    private static Node getScope(Node change, Set<String> scopeTypes, double precision) {
        String changeType = change.getProperty(_ChangePropNames.change_type.toString()).toString();
        List<Node> scopes = new ArrayList<>();
        if (change.hasRelationship(Direction.OUTGOING, _RuleRelTypes.SCOPED_TO)) {
            scopes = change.getRelationships(Direction.OUTGOING, _RuleRelTypes.SCOPED_TO).stream()
                    .filter(r -> {
                        Node tmp = r.getEndNode();
                        if (!tmp.getProperty(_ScopePropNames.change_type.toString()).toString().equals(changeType))
                            return false;
                        return fuzzyEquals(change, tmp, "", "", scopeTypes, precision);
                    })
                    .map(Relationship::getEndNode)
                    .toList();
        }
        if (scopes.size() > 1)
            throw new RuntimeException("Multiple scope nodes for same change type " + changeType + " and properties " + scopeTypes);
        if (scopes.size() == 1) return scopes.get(0);
        return null;
    }

    private static void propagate(Node from, Node to, String propagate, String prefix, double precision) { // propagate = "x;y;z" or "*"
        String[] keys;
        if (propagate.equals(WILDCARD)) {
            keys = from.getAllProperties().keySet().toArray(new String[0]);
        } else {
            keys = propagate.split(";");
        }
        for (String key : keys) {
            if (!to.hasProperty(prefix + key)) {
                to.setProperty(prefix + key, normalize(from.getProperty(key).toString(), precision));
            }
        }
    }

    private static boolean checkLocalConditions(Node context, String jsonProperties, String conditions,
                                                String jsFnString, ScriptEngine engine) {
        // Create a new bindings object for each condition
        Bindings bindings = engine.createBindings();
        bindings.putAll(context.getAllProperties());
        try {
            if (jsonProperties != null) {
                engine.eval(jsonProperties, bindings);
            }
            // TODO Ideal would be to call engine.eval(jsFnString) only once before starting multithreading
            // But ScriptEngine seems to only work with bindings
            if (jsFnString != null) {
                engine.eval(jsFnString, bindings);
            }
            if (conditions != null) {
                Object obj = engine.eval(conditions, bindings);
                if (obj instanceof Boolean) {
                    return (boolean) obj;
                } else {
                    throw new RuntimeException("Condition " + conditions + " must return boolean ");
                }
            }
            return false;
        } catch (ScriptException e) {
            throw new RuntimeException(e);
        }
    }

    private static Node getNextContent(Transaction tx, Node content, Label nextLabel, int searchLength, List<Label> notContainedLabels) {
        Traverser traverser = tx.traversalDescription()
                .depthFirst()
                .expand(PathExpanders.forDirection(Direction.INCOMING))
                .evaluator(Evaluators.includingDepths(0, searchLength)) // next content node may be content node itself
                .evaluator(path -> {
                    boolean isNotContained = notContainedLabels == null || notContainedLabels.stream().noneMatch(l -> path.endNode().hasLabel(l));
                    if (!isNotContained) return Evaluation.EXCLUDE_AND_PRUNE;

                    boolean isLeft = path.endNode().hasLabel(Label.label(AuxNodeLabels.__PARTITION_INDEX__ + "0"));
                    if (!isLeft) return Evaluation.EXCLUDE_AND_PRUNE;

                    boolean isType = path.endNode().hasLabel(nextLabel);
                    if (isType)
                        return Evaluation.INCLUDE_AND_PRUNE;
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                })
                .traverse(content);
        AtomicInteger count = new AtomicInteger(0);
        AtomicReference<Node> nextContent = new AtomicReference<>(null);
        traverser.forEach(path -> {
            if (count.get() > 1)
                throw new RuntimeException("Multiple next content nodes for content node " + content.getLabels());
            count.getAndIncrement();
            nextContent.set(path.endNode());
        });
        return nextContent.get();
    }

    private static int countDescendants(
            Transaction tx,
            Node node,
            Iterable<Label> descendantLabels,
            List<Label> notContainedLabels
    ) {
        Traverser traverser = tx.traversalDescription()
                .depthFirst()
                .expand(PathExpanders.forDirection(Direction.OUTGOING))
                .evaluator(Evaluators.fromDepth(0)) // descendants may be node itself
                .evaluator(path -> {
                    boolean isNotContained = notContainedLabels == null
                            || notContainedLabels.stream().noneMatch(l -> path.endNode().hasLabel(l));
                    if (!isNotContained) return Evaluation.EXCLUDE_AND_PRUNE;

                    boolean isLeft = path.endNode().hasLabel(Label.label(AuxNodeLabels.__PARTITION_INDEX__ + "0"));
                    if (!isLeft) return Evaluation.EXCLUDE_AND_PRUNE;

                    boolean isType = StreamSupport.stream(descendantLabels.spliterator(), false)
                            .allMatch(l -> path.endNode().hasLabel(l));
                    if (isType)
                        return Evaluation.INCLUDE_AND_CONTINUE;
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                })
                .traverse(node);
        AtomicInteger count = new AtomicInteger(0);
        traverser.forEach(path -> count.getAndIncrement());
        if (count.get() == 0)
            throw new RuntimeException("No descendants for node " + node.getLabels());
        return count.get();
    }

    private static String normalize(String value, double precision) {
        if (value.matches("\\d+")) return value;
        if (value.matches("[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)")) {
            int nrAfterComma = (precision + "").split("\\.")[1].length();
            double d = Double.parseDouble(value);
            return Math.round(d * Math.pow(10, nrAfterComma)) / Math.pow(10, nrAfterComma) + "";
        }
        return value;
    }

    private static boolean fuzzyEquals(Node n1, Node n2,
                                       String prefix1, String prefix2, Set<String> properties, double precision) {
        for (String property : properties) {
            // TODO Not only equals, but also other comparison operators?
            if (!fuzzyEquals(n1.getProperty(prefix1 + property).toString(),
                    n2.getProperty(prefix2 + property).toString(), precision))
                return false;
        }
        return true;
    }

    private static boolean fuzzyEquals(String s1, String s2, double precision) {
        // Trim strings
        String a = s1.trim();
        String b = s2.trim();

        // Numeric values
        String numericRegex = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)";
        if (a.matches(numericRegex) && b.matches(numericRegex)) {
            return Math.abs(Double.parseDouble(a) - Double.parseDouble(b)) <= precision;
        }

        // Zoned date-time values such as "2016-11-22T00:00+01:00[Europe/Berlin]"
        String zonedDateTimeRegex = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}[+-][0-9]{2}:[0-9]{2}\\[.*]";
        if (a.matches(zonedDateTimeRegex) && b.matches(zonedDateTimeRegex)) {
            return ZonedDateTime.parse(a).equals(ZonedDateTime.parse(b));
        }

        return a.equals(b);
    }

    public static void markGeoChange(Transaction tx, Class<?> changeClass, Node leftNode, Node rightNode, double[] vector, double precision) {
        // Do not create a pattern node if an exact node like it already exists
        if (geoChangeExists(leftNode, rightNode, vector, precision)) return;

        Node node = tx.createNode(Label.label(Change.class.getName()), Label.label(changeClass.getName()));
        node.createRelationshipTo(leftNode, AuxEdgeTypes.TANDEM);
        node.createRelationshipTo(rightNode, AuxEdgeTypes.RIGHT_NODE);
        node.setProperty(_ChangePropNames.change_type.toString(), changeClass.getSimpleName());
        node.setProperty(_ChangePropNames.x.toString(), vector[0]);
        node.setProperty(_ChangePropNames.y.toString(), vector[1]);
        node.setProperty(_ChangePropNames.z.toString(), vector.length == 3 ? vector[2] : "");
    }

    public static void markTranslation(Transaction tx, Node leftNode, Node rightNode, double[] vector, double precision) {
        markGeoChange(tx, TranslationChange.class, leftNode, rightNode, vector, precision);
    }

    public static void markSizeChange(Transaction tx, Node leftNode, Node rightNode, double[] vector, double precision) {
        markGeoChange(tx, SizeChange.class, leftNode, rightNode, vector, precision);
    }

    private static boolean geoChangeExists(Node leftNode, Node rightNode, double[] vector, double precision) {
        if (!leftNode.hasRelationship(Direction.INCOMING, AuxEdgeTypes.TANDEM)) return false;
        if (!rightNode.hasRelationship(Direction.INCOMING, AuxEdgeTypes.RIGHT_NODE)) return false;
        // There should exist only one pattern node between leftNode and rightNode with exact vector value
        Node pattern = leftNode.getSingleRelationship(AuxEdgeTypes.TANDEM, Direction.INCOMING).getStartNode();
        if (!pattern.hasLabel(Label.label(TranslationChange.class.getName()))) return false;
        if (!pattern.hasProperty(_ChangePropNames.x.toString())) return false;
        if (Math.abs((double) pattern.getProperty(_ChangePropNames.x.toString()) - vector[0]) > precision)
            return false;
        if (!pattern.hasProperty(_ChangePropNames.y.toString())) return false;
        if (Math.abs((double) pattern.getProperty(_ChangePropNames.y.toString()) - vector[1]) > precision)
            return false;
        if (!pattern.hasProperty(_ChangePropNames.z.toString())) return false;
        if (Math.abs((double) pattern.getProperty(_ChangePropNames.z.toString()) - vector[2]) > precision)
            return false;
        return true;
    }

    // Return a translation vector if there is a translation, else null
    public static double[] checkTranslation(Node node) {
        Optional<Relationship> opt = node.getRelationships(Direction.INCOMING, AuxEdgeTypes.TANDEM).stream().findFirst();
        if (opt.isEmpty()) return null;

        Relationship rel = opt.get();
        Node start = rel.getStartNode();
        if (start.hasLabel(Label.label(TranslationChange.class.getName()))) {
            // Found a translation detected previously
            double x = (double) start.getProperty(_ChangePropNames.x.toString());
            double y = (double) start.getProperty(_ChangePropNames.y.toString());
            double z = (double) start.getProperty(_ChangePropNames.z.toString());
            return new double[]{x, y, z};
        }

        return null;
    }

    private static long cleanUsedMemory(GraphDatabaseService graphDb) {
        AtomicLong count = new AtomicLong();
        try (Transaction tx = graphDb.beginTx()) {
            tx.findNodes(Label.label(_MemoryNodeLabels.MEMORY.toString()), _MemoryPropNames.done.toString(), true).stream()
                    .forEach(n -> {
                        // Remove all relationships to this memory node
                        n.getRelationships().forEach(Relationship::delete);
                        // Then delete
                        // n.delete(); // TODO Check if elementIDs are stored offline anywhere before calling this
                        count.getAndIncrement();
                    });
            tx.commit();
        }
        return count.get();
    }
}
