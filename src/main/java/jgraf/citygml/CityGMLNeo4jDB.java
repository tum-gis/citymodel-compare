package jgraf.citygml;


import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometry;
import jgraf.core.GraphRef;
import jgraf.neo4j.Neo4jDB;
import jgraf.neo4j.Neo4jGraphRef;
import jgraf.neo4j.diff.*;
import jgraf.neo4j.factory.*;
import jgraf.utils.ClazzUtils;
import jgraf.utils.GraphUtils;
import org.apache.commons.geometry.euclidean.threed.ConvexPolygon3D;
import org.apache.commons.geometry.euclidean.threed.Plane;
import org.apache.commons.geometry.euclidean.threed.line.Line3D;
import org.apache.commons.numbers.core.Precision;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

public abstract class CityGMLNeo4jDB extends Neo4jDB {
    protected Neo4jGraphRef ROOT_RTREES;
    protected RTree<Neo4jGraphRef, Geometry>[] rtrees;
    protected static Set<Class<?>> uuidClasses;
    protected static Set<Class<?>> idClasses;
    protected static Set<Class<?>> hrefClasses;
    protected final static String hrefPrefix = "#";
    private final static Logger logger = LoggerFactory.getLogger(CityGMLNeo4jDB.class);

    @SuppressWarnings("unchecked")
    public CityGMLNeo4jDB(CityGMLNeo4jDBConfig config) {
        super(config);
        rtrees = new RTree[config.MAPPER_DATASET_PATHS.size()];
        Change.END_NODE_LABEL = Label.label(getCityModelClass().getName());
    }

    protected abstract void partitionPreProcessing(Object topLevelFeature);

    protected abstract Neo4jGraphRef mapFileCityGML(String filePath, int partitionIndex, boolean connectToRoot);

    protected abstract Class<?> getCityModelClass();

    protected abstract void partitionMapPostProcessing(Object chunk, Neo4jGraphRef graphRef, int partitionIndex, boolean connectToRoot);

    protected void setIndexesIfNew() {
        logger.info("|--> Updating indexes");
        mappedClassesTmp.stream()
                .filter(cl -> ClazzUtils.isSubclass(cl, uuidClasses))
                .forEach(cl -> setIndex(cl, AuxPropNames.__UUID__.toString()));
        mappedClassesTmp.stream()
                .filter(cl -> ClazzUtils.isSubclass(cl, idClasses))
                .forEach(cl -> setIndex(cl, PropNames.id.toString()));
        mappedClassesTmp.stream()
                .filter(cl -> ClazzUtils.isSubclass(cl, hrefClasses))
                .forEach(cl -> setIndex(cl, PropNames.href.toString()));
        waitForIndexes();
        // Store and clear mapped classes for next call
        mappedClassesSaved.addAll(mappedClassesTmp);
        mappedClassesTmp.clear();
        logger.info("-->| Updated indexes");
    }

    protected void connectCityModelToRoot(Neo4jGraphRef cityModelRef, Map<String, Object> relationshipProperties) {
        try (Transaction tx = graphDb.beginTx()) {
            Node cityModel = cityModelRef.getRepresentationNode(tx);
            Node mapperRoot = ROOT_MAPPER.getRepresentationNode(tx);
            // root -[rel]-> createdNode
            Relationship rel = mapperRoot.createRelationshipTo(cityModel, AuxEdgeTypes.COLLECTION_MEMBER);
            for (Map.Entry<String, Object> entry : relationshipProperties.entrySet()) {
                rel.setProperty(entry.getKey(), entry.getValue());
            }
            tx.commit();
            logger.debug("Connected a city model to the database");
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public void mapFromConfig() {
        for (int i = 0; i < config.MAPPER_DATASET_PATHS.size(); i++) {
            // An RTree layer for each input dataset
            rtrees[i] = RTree.star().create();

            String stringPath = config.MAPPER_DATASET_PATHS.get(i);
            Path path = Path.of(stringPath);
            if (Files.isDirectory(path)) {
                // Consider all files from this directory as one single dataset
                logger.info("Input CityGML directory {} found", stringPath);
                mapDirCityGML(path, i);
            } else {
                // Is a file
                mapFileCityGML(stringPath, i, true);
            }
        }
        if (!config.NEO4J_RTREE_IMG_PATH.isBlank()) {
            // Export all RTree layers' footprint as images
            exportRTreeFootprints(config.NEO4J_RTREE_IMG_PATH);
        }
        logger.info("Finished mapping all files from config");
    }

    protected void mapDirCityGML(Path path, int partitionIndex) {
        // Set provides constant time for adds and removes of huge lists
        Set<Neo4jGraphRef> cityModelRefs = new HashSet<>();
        try (Stream<Path> st = Files.walk(path)) {
            st.filter(Files::isRegularFile).forEach(file -> {
                cityModelRefs.add(mapFileCityGML(file.toString(), partitionIndex, false));
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Merge all CityModel objects to one
        try (Transaction tx = graphDb.beginTx()) {
            Node mergedCityModelNode = null;
            for (Neo4jGraphRef cityModelRef : cityModelRefs) {
                Node cityModelNode = cityModelRef.getRepresentationNode(tx);
                if (mergedCityModelNode == null) {
                    mergedCityModelNode = GraphUtils.clone(tx, cityModelNode, true);
                    connectCityModelToRoot(cityModelRef, Map.of(
                            AuxPropNames.COLLECTION_INDEX.toString(), partitionIndex,
                            AuxPropNames.COLLECTION_MEMBER_TYPE.toString(), getCityModelClass()
                    ));
                    continue;
                }
                GraphUtils.cloneRelationships(cityModelNode, mergedCityModelNode, true);
                cityModelNode.delete();
            }
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public void resolveXLinks(BiConsumer<Node, Node> resolveLinkRules,
                              Consumer<Node> correctLinkRules,
                              int partitionIndex) {
        logger.info("|--> Resolving XLinks");
        mappedClassesSaved.stream()
                .filter(clazz -> ClazzUtils.isSubclass(clazz, hrefClasses))
                .forEach(hrefClass -> {
                    AtomicInteger transactionCount = new AtomicInteger();
                    try (Transaction tx = graphDb.beginTx()) {
                        tx.findNodes(Label.label(hrefClass.getName())).stream()
                                .filter(hrefNode -> hrefNode.hasLabel(
                                        Label.label(AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex)))
                                .filter(hrefNode -> hrefNode.hasProperty(PropNames.href.toString()))
                                .forEach(hrefNode -> {
                                    correctLinkRules.accept(hrefNode);
                                    AtomicInteger idCount = new AtomicInteger();
                                    String id = hrefNode.getProperty(PropNames.href.toString()).toString()
                                            .replace("#", "");
                                    mappedClassesSaved.stream()
                                            .filter(clazz -> ClazzUtils.isSubclass(clazz, idClasses))
                                            .forEach(idClass -> tx.findNodes(
                                                    Label.label(idClass.getName()),
                                                    PropNames.id.toString(),
                                                    id
                                            ).stream().filter(idNode -> idNode.hasLabel(
                                                    Label.label(AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex))
                                            ).forEach(idNode -> {
                                                // Connect linked node to source id node
                                                resolveLinkRules.accept(idNode, hrefNode);
                                                idCount.getAndIncrement();
                                                transactionCount.getAndIncrement();
                                            }));
                                    if (idCount.intValue() == 0) {
                                        logger.warn("No element with referenced ID = {} found", id);
                                    } else if (idCount.intValue() >= 2) {
                                        logger.warn("{} elements of the same ID = {} detected", idCount, id);
                                    }
                                });
                        tx.commit();
                    } catch (Exception e) {
                        logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
                    }
                    if (transactionCount.get() > 0)
                        logger.info("Found and resolved {} XLink(s)", transactionCount);
                });
        logger.info("-->| Resolved XLinks");
    }

    // Returns true if the given node is reachable from the CityModel node assigned with a partition index
    // Use this AFTER AT LEAST one city model has been fully mapped and its links resolved
    protected Function<Node, Boolean> idFilterRules() {
        return (Node idNode) -> GraphUtils.isReachable(getCityModelClass().getName(), idNode);
    }

    // Define rules how to link an ID and referenced node (such as href)
    protected BiConsumer<Node, Node> resolveLinkRules() {
        return (Node idNode, Node linkedNode) -> {
            // (:CityModel) -[:cityObjectMember]-> (:COLLECTION)
            //              -[:COLLECTION_MEMBER]-> (:CityObjectMember.href)
            //              -[:object]-> (:Feature.id)
            // (:Building)  -[:lod2Solid]-> (:SolidProperty)
            //              -[:object]-> (:Solid)
            //              -[:exterior]-> (:SurfaceProperty)
            //              -[:object]-> (:CompositeSurface)
            //              -[:surfaceMember]-> (:COLLECTION)
            //              -[:COLLECTION_MEMBER]-> (:SurfaceProperty)
            //              -[:object]-> (:Polygon)
            linkedNode.createRelationshipTo(idNode, EdgeTypes.object);
            linkedNode.removeProperty(PropNames.href.toString());
        };
    }

    protected Consumer<Node> correctLinkRules() {
        return (Node node) -> {
            // Correct hrefs without prefix "#"
            Object propertyHref = node.getProperty(PropNames.href.toString());
            if (propertyHref == null) return;
            String valueHref = node.getProperty(PropNames.href.toString()).toString();
            if (valueHref.isBlank()) {
                logger.warn("Ignored empty href");
                return;
            }
            if (valueHref.charAt(0) != '#') {
                logger.warn("Added prefix \"#\" to incomplete href = {}", valueHref);
                node.setProperty(PropNames.href.toString(), "#" + valueHref);
            }

            // More corrections when needed...
        };
    }

    public boolean diff(int leftPartitionIndex, int rightPartitionIndex) {
        if ((leftPartitionIndex < 0 || leftPartitionIndex >= config.MAPPER_DATASET_PATHS.size())
                || (rightPartitionIndex < 0 || rightPartitionIndex >= config.MAPPER_DATASET_PATHS.size()))
            throw new RuntimeException("Invalid partition indices " + leftPartitionIndex + " and " + rightPartitionIndex);
        dbStats.startTimer();

        // Init indexing for changes
        setIndex(Change.class, Patterns._ChangePropNames.change_type.toString());
        waitForIndexes();

        // Preparations
        AtomicBoolean diffFound = new AtomicBoolean(false);
        String leftListNodeId = null;
        String rightListNodeId = null;
        List<String> leftIdList = Collections.synchronizedList(new ArrayList<>());
        List<String> rightIdList = Collections.synchronizedList(new ArrayList<>());
        try (Transaction tx = graphDb.beginTx()) {
            Node rootMapperNode = ROOT_MAPPER.getRepresentationNode(tx);
            Node leftCityModelNode = GraphUtils.getCollectionMemberNode(rootMapperNode, leftPartitionIndex);
            Node rightCityModelNode = GraphUtils.getCollectionMemberNode(rootMapperNode, rightPartitionIndex);
            if (leftCityModelNode == null || rightCityModelNode == null)
                throw new RuntimeException("Null city model node");

            Node leftListNode = getTopLevelListNode(leftCityModelNode);
            Node rightListNode = getTopLevelListNode(rightCityModelNode);

            leftListNodeId = leftListNode.getElementId();
            rightListNodeId = rightListNode.getElementId();

            leftListNode.getRelationships(Direction.OUTGOING).forEach(r -> leftIdList.add(r.getEndNode().getElementId()));
            rightListNode.getRelationships(Direction.OUTGOING).forEach(r -> rightIdList.add(r.getEndNode().getElementId()));

            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }

        // Multi-threading
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String finalLeftListNodeId = leftListNodeId;
        String finalRightListNodeId = rightListNodeId;
        leftIdList.forEach(leftRelNodeId -> executorService.submit(() -> {
            try (Transaction tx = graphDb.beginTx()) {
                Node leftListNode = tx.getNodeByElementId(finalLeftListNodeId);
                Node rightListNode = tx.getNodeByElementId(finalRightListNodeId);
                Node leftRelNode = tx.getNodeByElementId(leftRelNodeId);
                Relationship leftRel = leftRelNode.getRelationships(Direction.INCOMING).stream()
                        .filter(r -> r.getStartNode().equals(leftListNode))
                        .collect(Collectors.toSet()).iterator().next();
                Map.Entry<Node, DiffResult> resultEntry = findBest(tx, leftRel, rightListNode);
                Node rightRelNode = resultEntry.getKey();
                DiffResult diffResult = resultEntry.getValue();
                if (diffResult.getLevel() == SimilarityLevel.SIMILAR_GEOMETRY) {
                    // Found geometric matched top-level
                    rightIdList.remove(rightRelNode.getElementId());
                    boolean tmpDiffFound = diff(tx, leftRelNode, rightRelNode, true,
                            null, ((DiffResultGeo) diffResult).getSkip());
                    if (tmpDiffFound) diffFound.set(true);
                } else {
                    // Found no match
                    diffFound.set(true);
                    new DeleteNodeChange(tx, leftListNode, rightListNode, leftRel);
                }
                tx.commit();
            } catch (Exception e) {
                logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }));

        // Wait for all threads to finish
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(config.MATCHER_CONCURRENT_TIMEOUT, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }

        // Remaining multi-relationships in right
        if (!rightIdList.isEmpty()) {
            diffFound.set(true);
            try (Transaction tx = graphDb.beginTx()) {
                Node leftListNode = tx.getNodeByElementId(leftListNodeId);
                Node rightListNode = tx.getNodeByElementId(rightListNodeId);
                rightIdList.forEach(rightRelNodeId -> {
                    Node rightRelNode = tx.getNodeByElementId(rightRelNodeId);
                    Relationship rightRel = rightRelNode.getRelationships(Direction.INCOMING).stream()
                            .filter(r -> r.getStartNode().equals(rightListNode))
                            .collect(Collectors.toSet()).iterator().next();
                    new InsertNodeChange(tx, leftListNode, rightListNode, rightRel);
                });
                tx.commit();
            } catch (Exception e) {
                logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
        }

        dbStats.stopTimer("Match city models indexed at " + leftPartitionIndex + " and " + rightPartitionIndex);
        return diffFound.get();
    }

    protected abstract Node getTopLevelListNode(Node cityModelNode);

    private void diff(Transaction tx, GraphRef leftGraphRef, GraphRef rightGraphRef, boolean set) {
        if (leftGraphRef == null || rightGraphRef == null) return;
        if (!(leftGraphRef instanceof Neo4jGraphRef && rightGraphRef instanceof Neo4jGraphRef))
            throw new RuntimeException("Expected graph references of type " + Neo4jGraphRef.class.getName());
        diff(
                tx,
                ((Neo4jGraphRef) leftGraphRef).getRepresentationNode(tx),
                ((Neo4jGraphRef) rightGraphRef).getRepresentationNode(tx),
                set,
                null,
                null
        );
    }

    private boolean diff(Transaction tx,
                         Node leftNode,
                         Node rightNode,
                         boolean set,
                         List<String> skipProps,
                         List<Label> skipLabels) {
        if (leftNode == null || rightNode == null) throw new RuntimeException("Could not diff null nodes");

        // Match labels
        Set<Label> leftLabels = StreamSupport.stream(leftNode.getLabels().spliterator(), false)
                .filter(label -> !label.name().startsWith(AuxNodeLabels.__PARTITION_INDEX__.name()))
                .collect(Collectors.toSet());
        Set<Label> rightLabels = StreamSupport.stream(rightNode.getLabels().spliterator(), false)
                .filter(label -> !label.name().startsWith(AuxNodeLabels.__PARTITION_INDEX__.name()))
                .collect(Collectors.toSet());
        if (!leftLabels.equals(rightLabels))
            throw new RuntimeException("Could not diff nodes of different labels " + leftLabels + " and " + rightLabels);
        // logger.debug("Matching {}", leftLabels);

        AtomicBoolean diffFound = new AtomicBoolean(false);

        // Match properties
        Set<String> leftKeys = StreamSupport.stream(leftNode.getPropertyKeys().spliterator(), false)
                .filter(k -> !AuxPropNames.isIn(k))
                .filter(k -> skipProps == null || skipProps.isEmpty() || !skipProps.contains(k))
                .collect(Collectors.toSet());
        Set<String> rightKeys = StreamSupport.stream(rightNode.getPropertyKeys().spliterator(), false)
                .filter(k -> !AuxPropNames.isIn(k))
                .filter(k -> skipProps == null || skipProps.isEmpty() || !skipProps.contains(k))
                .collect(Collectors.toSet());
        // Only in left
        leftKeys.stream().filter(k -> !rightKeys.contains(k)).forEach(k -> {
            diffFound.set(true);
            if (set)
                new DeletePropChange(tx, leftNode, rightNode, k, leftNode.getProperty(k).toString());
        });
        // Only in right
        rightKeys.stream().filter(k -> !leftKeys.contains(k)).forEach(k -> {
            diffFound.set(true);
            if (set)
                new InsertPropChange(tx, leftNode, rightNode, k, rightNode.getProperty(k).toString());
        });
        // Intersection
        leftKeys.stream().filter(rightKeys::contains).forEach(k -> {
            String leftValue = leftNode.getProperty(k).toString();
            String rightvalue = rightNode.getProperty(k).toString();
            if (sameProps(leftValue, rightvalue)) return;
            diffFound.set(true);
            if (set)
                new UpdatePropChange(tx, leftNode, rightNode, k, leftValue, rightvalue);
        });

        // Match relationships
        // There should only be a small number of unique relationship types
        Set<RelationshipType> leftRelTypes = leftNode.getRelationships(Direction.OUTGOING).stream()
                .map(Relationship::getType).collect(Collectors.toSet());
        Set<RelationshipType> rightRelTypes = rightNode.getRelationships(Direction.OUTGOING).stream()
                .map(Relationship::getType).collect(Collectors.toSet());
        // Only in left
        leftRelTypes.stream().filter(t -> !rightRelTypes.contains(t)).forEach(t -> {
            diffFound.set(true);
            if (set) {
                leftNode.getRelationships(Direction.OUTGOING, t).stream()
                        .filter(r -> skipLabels == null || skipLabels.isEmpty()
                                || skipLabels.stream().noneMatch(l -> r.getEndNode().hasLabel(l)))
                        .forEach(r -> new DeleteNodeChange(tx, leftNode, rightNode, r));
            }
        });
        // Only in right
        rightRelTypes.stream().filter(r -> !leftRelTypes.contains(r)).forEach(t -> {
            diffFound.set(true);
            if (set) {
                rightNode.getRelationships(Direction.OUTGOING, t).stream()
                        .filter(r -> skipLabels == null || skipLabels.isEmpty()
                                || skipLabels.stream().noneMatch(l -> r.getEndNode().hasLabel(l)))
                        .forEach(r -> new InsertNodeChange(tx, leftNode, rightNode, r));
            }
        });
        // Intersection
        leftRelTypes.stream().filter(rightRelTypes::contains).forEach(t -> {
            List<String> rightMatchedNodes = Collections.synchronizedList(new ArrayList<>());
            AtomicBoolean isTopLevel = new AtomicBoolean(false);
            leftNode.getRelationships(Direction.OUTGOING, t).forEach(leftRel -> {
                Node leftRelNode = leftRel.getEndNode();
                isTopLevel.set(isTopLevel(leftRelNode));

                // Skip if label matches
                for (Label l : skipLabels) {
                    if (leftRelNode.hasLabel(l)) {
                        logger.debug("Skipped label {}", l.name());
                        return;
                    }
                }

                // May contain single or multiple relationships of the same type
                // TODO Define more rules for efficient matching of n:n relationships (gmlids, RTree, etc.)
                Map.Entry<Node, DiffResult> resultEntry = findBest(tx, leftRel, rightNode);
                Node rightRelNode = resultEntry.getKey();
                DiffResult diffResult = resultEntry.getValue();
                switch (diffResult.getLevel()) {
                    case EQUIVALENCE -> {
                        // Found an equivalence -> no need to match further
                        rightMatchedNodes.add(rightRelNode.getElementId());
                    }
                    case SIMILAR_GEOMETRY -> {
                        // Found geometric equivalence -> skip geometric and match non-geometric
                        rightMatchedNodes.add(rightRelNode.getElementId());
                        boolean tmpDiffFound = diff(
                                tx, leftRelNode, rightRelNode, set,
                                GraphUtils.listAll(skipProps, null),
                                GraphUtils.listAll(skipLabels, ((DiffResultGeo) diffResult).getSkip())
                        );
                        if (tmpDiffFound) diffFound.set(true);
                    }
                    case SIMILAR_GEOMETRY_TRANSLATION -> {
                        // Found a geometric match but translated by a vector != 0
                        // First create an interpretation node for later analyses
                        Patterns.markTranslation(tx, leftRelNode, rightRelNode, ((DiffResultGeoTranslation) diffResult).getVector(), config.MATCHER_TOLERANCE_LENGTHS);
                        // Then treat it as usual matched geometry
                        rightMatchedNodes.add(rightRelNode.getElementId());
                        boolean tmpDiffFound = diff(
                                tx, leftRelNode, rightRelNode, set,
                                GraphUtils.listAll(skipProps, null),
                                GraphUtils.listAll(skipLabels, ((DiffResultGeo) diffResult).getSkip())
                        );
                        if (tmpDiffFound) diffFound.set(true);
                    }
                    case SIMILAR_GEOMETRY_SIZE_CHANGE -> {
                        // Found a geometric match with a different size
                        // First create an interpretation node for later analyses
                        Patterns.markSizeChange(tx, leftRelNode, rightRelNode, ((DiffResultGeoSize) diffResult).getDelta(), config.MATCHER_TOLERANCE_LENGTHS);
                        // Then treat it as usual matched geometry
                        rightMatchedNodes.add(rightRelNode.getElementId());
                        boolean tmpDiffFound = diff(
                                tx, leftRelNode, rightRelNode, set,
                                GraphUtils.listAll(skipProps, null),
                                GraphUtils.listAll(skipLabels, ((DiffResultGeo) diffResult).getSkip())
                        );
                        if (tmpDiffFound) diffFound.set(true);
                    }
                    case SAME_PROPS, SAME_ID -> {
                        // Found match with similar properties -> skip these properties
                        rightMatchedNodes.add(rightRelNode.getElementId());
                        boolean tmpDiffFound = diff(
                                tx, leftRelNode, rightRelNode, set,
                                GraphUtils.listAll(skipProps, ((DiffResultProp) diffResult).getSkip()),
                                GraphUtils.listAll(skipLabels, null)
                        );
                        if (tmpDiffFound) diffFound.set(true);
                    }
                    case SIMILAR_STRUCTURE, SAME_LABELS -> {
                        // Similar rel types and number of rels
                        rightMatchedNodes.add(rightRelNode.getElementId());
                        boolean tmpDiffFound = diff(
                                tx, leftRelNode, rightRelNode, set,
                                GraphUtils.listAll(skipProps, null),
                                GraphUtils.listAll(skipLabels, null)
                        );
                        if (tmpDiffFound) diffFound.set(true);
                    }
                    case NONE -> {
                        // Found no match
                        diffFound.set(true);
                        new DeleteNodeChange(tx, leftNode, rightNode, leftRel);
                    }
                }
            });

            if (rightMatchedNodes.isEmpty()) return;
            // Remaining multi-relationships in right
            StreamSupport.stream(rightNode.getRelationships(Direction.OUTGOING, t).spliterator(), false)
                    .filter(rightRel -> !rightMatchedNodes.contains(rightRel.getEndNode().getElementId()))
                    .filter(rightRel -> skipLabels == null || skipLabels.isEmpty()
                            || skipLabels.stream().noneMatch(l -> rightRel.getEndNode().hasLabel(l)))
                    .forEach(rightRel -> {
                        diffFound.set(true);
                        if (set) {
                            new InsertNodeChange(tx, leftNode, rightNode, rightRel);
                        }
                    });
        });

        // TODO Batch commit

        return diffFound.get();
    }

    public void interpretDiff() {
        Patterns.createRuleNetwork(
                graphDb,
                ((CityGMLNeo4jDBConfig) config).INTERPRETATION_RULES_PATH,
                config.MATCHER_CONCURRENT_TIMEOUT
        );
        Patterns.interpret(
                graphDb,
                ((CityGMLNeo4jDBConfig) config).INTERPRETATION_FUNCTIONS_PATH,
                rtrees[0], // old city model
                config.MATCHER_TOLERANCE_LENGTHS,
                config.MATCHER_CONCURRENT_TIMEOUT
        );
    }

    protected abstract boolean isTopLevel(Node node);

    protected boolean sameProps(String leftProp, String rightProp) {
        // Ignore leading and trailing spaces
        leftProp = leftProp.trim();
        rightProp = rightProp.trim();
        try {
            // Check if doubles
            double leftDouble = Double.parseDouble(leftProp);
            double rightDouble = Double.parseDouble(rightProp);
            return Math.abs(leftDouble - rightDouble) <= config.MATCHER_TOLERANCE_LENGTHS;
        } catch (NumberFormatException eDouble) {
            try {
                // Check if dates (without time-zones)
                LocalDate leftDate = LocalDate.parse(leftProp);
                LocalDate rightDate = LocalDate.parse(rightProp);
                return leftDate.equals(rightDate);
            } catch (DateTimeParseException eDate) {
                try {
                    // Check if dates (with time-zones)
                    ZonedDateTime leftDate = ZonedDateTime.parse(leftProp);
                    ZonedDateTime rightDate = ZonedDateTime.parse(rightProp);
                    return leftDate.equals(rightDate);
                } catch (DateTimeParseException eZoned) {
                    // Literal strings
                    return leftProp.equals(rightProp);
                }
            }
        }
    }

    protected abstract Map.Entry<Node, DiffResult> findBest(Transaction tx, Relationship leftRel, Node rightParentNode);

    protected abstract boolean compareMeasurements(Object leftMeasure, Object rightMeasure);

    protected abstract ConvexPolygon3D toConvexPolygon3D(Object polygon, Precision.DoubleEquivalence precision);

    protected abstract List<Line3D> multiCurveToLines3D(Object multiCurve, Precision.DoubleEquivalence precision);

    protected abstract boolean isMultiCurveContainedInLines3D(Object multiCurve, List<Line3D> lines, Precision.DoubleEquivalence precision);

    protected abstract Plane boundarySurfacePropertyToPlane(Object boundarySurfaceProperty, Precision.DoubleEquivalence precision);

    public void exportRTreeFootprints(String folderPath) {
        try {
            for (int i = 0; i < rtrees.length; i++) {
                java.nio.file.Path filePath = Path.of(folderPath).resolve("rtree_" + i + ".png");
                Files.deleteIfExists(filePath);
                File file = Files.createFile(filePath).toFile();
                rtrees[i].visualize(1000, 1000).save(file.getAbsoluteFile().toString());
            }
            logger.info("Exported RTree footprint(s) to folder {}", folderPath);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void storeRTrees() {
        try (Transaction tx = graphDb.beginTx()) {
            logger.info("|--> Storing RTrees in the database");
            ROOT_RTREES = (Neo4jGraphRef) map(rtrees);
            Node rootRtreesNode = ROOT_RTREES.getRepresentationNode(tx);
            rootRtreesNode.addLabel(NodeLabels.__ROOT_RTREES__);
            Node rootNode = ROOT.getRepresentationNode(tx);
            rootNode.createRelationshipTo(rootRtreesNode, AuxEdgeTypes.RTREES);
            // TODO Set explicit edges to connect RTrees and CityGML nodes?
            tx.commit();
            logger.info("-->| Stored RTrees in the database");
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    @Override
    public void summarize() {
        super.summarize();
        if (config.NEO4J_RTREE_STORE) {
            // Store all RTree layers in the database for later use/import
            storeRTrees();
        }
    }

    public RTree<Neo4jGraphRef, Geometry>[] getRtrees() {
        return rtrees;
    }

    public void setRtrees(RTree<Neo4jGraphRef, Geometry>[] rtrees) {
        this.rtrees = rtrees;
    }
}
