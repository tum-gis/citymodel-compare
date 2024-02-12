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
import org.apache.commons.lang3.function.TriConsumer;
import org.apache.commons.numbers.core.Precision;
import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.builder.jaxb.CityGMLBuilderException;
import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.CityGMLReadException;
import org.citygml4j.xml.io.reader.CityGMLReader;
import org.citygml4j.xml.io.reader.FeatureReadMode;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.traversal.Evaluation;
import org.neo4j.graphdb.traversal.Evaluators;
import org.neo4j.graphdb.traversal.Traverser;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.ZonedDateTime;
import java.time.format.DateTimeParseException;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicLong;
import java.util.function.BiConsumer;
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
            logger.error(e.getMessage() + "A\n" + Arrays.toString(e.getStackTrace()));
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

    // Map small files from a directory -> each file is loaded into one thread
    // TODO Momentarily for CityGML v2.0 only
    protected void mapDirCityGML(Path path, int partitionIndex) {
        dbStats.startTimer();

        CityGMLNeo4jDBConfig cityGMLConfig = (CityGMLNeo4jDBConfig) config;
        if (cityGMLConfig.CITYGML_VERSION != CityGMLVersion.v2_0) {
            logger.warn("Found CityGML version {}, expected version {}",
                    cityGMLConfig.CITYGML_VERSION, CityGMLVersion.v2_0);
        }

        // Multi-threading
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());

        // Set provides constant time for adds and removes of huge lists
        Set<Neo4jGraphRef> cityModelRefs = Collections.synchronizedSet(new HashSet<>());
        AtomicLong tlCountDir = new AtomicLong(0);
        try (Stream<Path> st = Files.walk(path)) {
            st.filter(Files::isRegularFile).parallel().forEach(file -> {
                // One file one thread
                executorService.submit((Callable<Void>) () -> {
                    try {
                        CityGMLContext ctx = CityGMLContext.getInstance();
                        CityGMLBuilder builder = ctx.createCityGMLBuilder();
                        CityGMLInputFactory in = builder.createCityGMLInputFactory();
                        in.setProperty(CityGMLInputFactory.FEATURE_READ_MODE, FeatureReadMode.SPLIT_PER_COLLECTION_MEMBER);
                        try (CityGMLReader reader = in.createCityGMLReader(file.toFile())) {
                            logger.info("Reading tiled CityGML v2.0 file {} chunk-wise into main memory", file.toString());
                            long tlCountFile = 0;
                            while (reader.hasNext()) {
                                CityGML chunk = reader.nextFeature();
                                tlCountFile++;

                                Neo4jGraphRef graphRef = (Neo4jGraphRef) this.map(chunk,
                                        AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex);

                                partitionMapPostProcessing(chunk, graphRef, partitionIndex, false);
                                logger.debug("Mapped {} top-level features", tlCountFile);

                                if (chunk instanceof CityModel) {
                                    cityModelRefs.add(graphRef);
                                }
                            }
                            tlCountDir.addAndGet(tlCountFile);
                        }
                    } catch (CityGMLBuilderException | CityGMLReadException e) {
                        throw new RuntimeException(e);
                    }

                    return null;
                });
            });
        } catch (IOException e) {
            throw new RuntimeException(e);
        } finally {
            logger.info("Waiting for all threads to finish");
            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(config.MAPPER_CONCURRENT_TIMEOUT, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
            logger.info("All threads finished");
        }
        logger.info("Mapped {} top-level features from directory {}", tlCountDir, path.toString());
        dbStats.stopTimer("Map all input tiled files in " + path.toString());

        dbStats.startTimer();
        logger.info("Resolve links of tiled files in input directory {} {},", partitionIndex, path.toString());
        setIndexesIfNew();
        resolveXLinks(resolveLinkRules(), correctLinkRules(), partitionIndex);
        dbStats.stopTimer("Resolve links of tiled files in input directory " + path.toString());

        // Merge all CityModel objects to one
        dbStats.startTimer();
        try (Transaction tx = graphDb.beginTx()) {
            Node mapperRoot = ROOT_MAPPER.getRepresentationNode(tx);
            Node mergedCityModelNode = null;
            for (Neo4jGraphRef cityModelRef : cityModelRefs) {
                Node cityModelNode = cityModelRef.getRepresentationNode(tx);
                if (mergedCityModelNode == null) {
                    mergedCityModelNode = GraphUtils.clone(tx, cityModelNode, true);
                    // root -[rel]-> createdNode
                    Relationship rel = mapperRoot.createRelationshipTo(mergedCityModelNode, AuxEdgeTypes.COLLECTION_MEMBER);
                    rel.setProperty(AuxPropNames.COLLECTION_INDEX.toString(), partitionIndex);
                    rel.setProperty(AuxPropNames.COLLECTION_MEMBER_TYPE.toString(), getCityModelClass().getName());
                    logger.debug("Connected a city model to the database");
                    continue;
                }
                GraphUtils.cloneRelationships(tx, cityModelNode, mergedCityModelNode, true);
                Lock lockCityModelNode = tx.acquireWriteLock(cityModelNode);
                cityModelNode.delete();
                lockCityModelNode.release();
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "B\n" + Arrays.toString(e.getStackTrace()));
        }
        dbStats.stopTimer("Merge all CityModel objects to one [" + partitionIndex + "]");

        logger.info("Finished mapping directory {}", path.toString());
    }

    public void resolveXLinks(TriConsumer<Transaction, Node, Node> resolveLinkRules,
                              BiConsumer<Transaction, Node> correctLinkRules,
                              int partitionIndex) {
        logger.info("|--> Resolving XLinks");
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<String> nodeIds = Collections.synchronizedList(new ArrayList<>());

        // Store all node IDs in a list first
        ExecutorService finalExecutorService = executorService;
        mappedClassesSaved.stream()
                .filter(clazz -> ClazzUtils.isSubclass(clazz, hrefClasses))
                .parallel()
                .forEach(hrefClass -> finalExecutorService.submit((Callable<Void>) () -> {
                    try (Transaction tx = graphDb.beginTx()) {
                        logger.info("Collecting node index {}", hrefClass.getName());
                        tx.findNodes(Label.label(hrefClass.getName())).stream()
                                .filter(hrefNode -> hrefNode.hasLabel(
                                        Label.label(AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex)))
                                .filter(hrefNode -> hrefNode.hasProperty(PropNames.href.toString()))
                                .forEach(hrefNode -> {
                                    nodeIds.add(hrefNode.getElementId());
                                });
                        tx.commit();
                    } catch (Exception e) {
                        logger.error(e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
                    }
                    return null;
                }));
        logger.info("Waiting for all threads to finish");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(config.MAPPER_CONCURRENT_TIMEOUT, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        logger.info("All threads finished");

        // Batch transactions
        List<List<String>> batches = new ArrayList<>();
        int batchSize = config.DB_BATCH_SIZE;
        for (int i = 0; i < nodeIds.size(); i += batchSize) {
            batches.add(nodeIds.subList(i, Math.min(i + batchSize, nodeIds.size())));
        }
        logger.info("Initiated {} batches for resolving XLinks", batches.size());

        // Resolve XLinks
        AtomicInteger transactionCount = new AtomicInteger();
        ExecutorService esBatch = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        batches.parallelStream().forEach(batch -> esBatch.submit((Callable<Void>) () -> {
            try (Transaction tx = graphDb.beginTx()) {
                batch.forEach(nodeId -> {
                    Node hrefNode = tx.getNodeByElementId(nodeId);
                    correctLinkRules.accept(tx, hrefNode);
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
                                resolveLinkRules.accept(tx, idNode, hrefNode);
                                idCount.getAndIncrement();
                                transactionCount.getAndIncrement();
                            }));
                    if (idCount.intValue() == 0) {
                        logger.warn("No element with referenced ID = {} found", id);
                    } else if (idCount.intValue() >= 2) {
                        logger.warn("{} elements of the same ID = {} detected", idCount, id);
                    }
                });
                if (transactionCount.get() > 0)
                    logger.info("Found and resolved {} XLink(s)", transactionCount);
                tx.commit();
            } catch (Exception e) {
                logger.error(e.getMessage() + " " + Arrays.toString(e.getStackTrace()));
            }
            return null;
        }));
        logger.info("Waiting for all threads to finish");
        esBatch.shutdown();
        try {
            if (!esBatch.awaitTermination(config.MAPPER_CONCURRENT_TIMEOUT, TimeUnit.SECONDS)) {
                esBatch.shutdownNow();
            }
        } catch (InterruptedException e) {
            esBatch.shutdownNow();
        }
        logger.info("All threads finished");

        logger.info("-->| Resolved XLinks");
    }

    // Returns true if the given node is reachable from the CityModel node assigned with a partition index
    // Use this AFTER AT LEAST one city model has been fully mapped and its links resolved
    protected Function<Node, Boolean> idFilterRules() {
        return (Node idNode) -> GraphUtils.isReachable(getCityModelClass().getName(), idNode);
    }

    // Define rules how to link an ID and referenced node (such as href)
    protected TriConsumer<Transaction, Node, Node> resolveLinkRules() {
        return (Transaction tx, Node idNode, Node linkedNode) -> {
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
            Lock lockLinkedNode = tx.acquireWriteLock(linkedNode);
            Lock lockIdNode = tx.acquireWriteLock(idNode);
            linkedNode.createRelationshipTo(idNode, EdgeTypes.object);
            linkedNode.removeProperty(PropNames.href.toString());
            lockLinkedNode.release();
            lockIdNode.release();
        };
    }

    protected BiConsumer<Transaction, Node> correctLinkRules() {
        return (Transaction tx, Node node) -> {
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
                Lock lock = tx.acquireWriteLock(node);
                node.setProperty(PropNames.href.toString(), "#" + valueHref);
                lock.release();
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
            logger.error(e.getMessage() + "C\n" + Arrays.toString(e.getStackTrace()));
        }

        // Multi-threading
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        String finalLeftListNodeId = leftListNodeId;
        String finalRightListNodeId = rightListNodeId;
        final long NR_OF_TASKS = leftIdList.size();
        AtomicLong TASKS_DONE = new AtomicLong(0);

        leftIdList.parallelStream().forEach(leftRelNodeId -> executorService.submit((Callable<Void>) () -> {
            try (Transaction tx = graphDb.beginTx()) {
                Node leftListNode = tx.getNodeByElementId(finalLeftListNodeId);
                Node rightListNode = tx.getNodeByElementId(finalRightListNodeId);

                Node leftRelNode = tx.getNodeByElementId(leftRelNodeId);
                Relationship leftRel = leftRelNode.getRelationships(Direction.INCOMING).stream()
                        .filter(r -> r.getStartNode().getElementId().equals(finalLeftListNodeId))
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
                } else if (diffResult.getLevel() == SimilarityLevel.SPLIT_TOPLEVEL) {
                    // Multiple top-level candidates split from the old one
                    List<Node> rightNodes = ((DiffResultTopSplit) diffResult).getSplitCandidates().stream()
                            .map(c -> c.getRepresentationNode(tx).getSingleRelationship(
                                    EdgeTypes.object, Direction.INCOMING).getStartNode()).toList();
                    rightNodes.forEach(n -> rightIdList.remove(n.getElementId()));
                    Patterns.markTopSplitChange(tx, TopSplitChange.class, leftRelNode, rightNodes);
                    diffFound.set(true);
                } else {
                    // Found no match
                    diffFound.set(true);
                    new DeleteNodeChange(tx, leftListNode, rightListNode, leftRel);
                }
                TASKS_DONE.getAndIncrement();
                logger.info("MATCHED {}", new DecimalFormat("00.00%").format(TASKS_DONE.get() * 1. / NR_OF_TASKS));
                tx.commit();
            } catch (Exception e) {
                logger.error(e.getMessage() + "D\n" + Arrays.toString(e.getStackTrace()));
            }
            return null;
        }));
        // Wait for all threads to finish
        logger.info("Waiting for all threads to finish");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(config.MATCHER_CONCURRENT_TIMEOUT, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
        }
        logger.info("All threads finished");

        // Remaining multi-relationships in right
        if (!rightIdList.isEmpty()) {
            diffFound.set(true);

            // Batch transactions
            List<List<String>> rightBatches = new ArrayList<>();
            int rightBatchSize = config.DB_BATCH_SIZE;
            for (int i = 0; i < rightIdList.size(); i += rightBatchSize) {
                rightBatches.add(rightIdList.subList(i, Math.min(i + rightBatchSize, rightIdList.size())));
            }
            logger.info("Initiated {} batches for remaining multi-relationships in right", rightBatches.size());

            ExecutorService es = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            String finalLeftListNodeId1 = leftListNodeId;
            String finalRightListNodeId1 = rightListNodeId;
            rightBatches.parallelStream().forEach(batch -> es.submit((Callable<Void>) () -> {
                try (Transaction tx = graphDb.beginTx()) {
                    Node leftListNode = tx.getNodeByElementId(finalLeftListNodeId1);
                    Node rightListNode = tx.getNodeByElementId(finalRightListNodeId1);
                    batch.forEach(rightRelNodeId -> {
                        try {
                            Node rightRelNode = tx.getNodeByElementId(rightRelNodeId);
                            Relationship rightRel = rightRelNode.getRelationships(Direction.INCOMING).stream()
                                    .filter(r -> r.getStartNode().getElementId().equals(finalRightListNodeId1))
                                    .collect(Collectors.toSet()).iterator().next();
                            new InsertNodeChange(tx, leftListNode, rightListNode, rightRel);
                        } catch (Exception e) {
                            logger.error(e.getMessage() + "E\n" + Arrays.toString(e.getStackTrace()));
                        }
                    });
                    tx.commit();
                } catch (Exception e) {
                    logger.error(e.getMessage() + "E\n" + Arrays.toString(e.getStackTrace()));
                }
                return null;
            }));
            logger.info("Waiting for all threads to finish");
            es.shutdown();
            try {
                if (!es.awaitTermination(config.MATCHER_CONCURRENT_TIMEOUT, TimeUnit.SECONDS)) {
                    es.shutdownNow();
                }
            } catch (InterruptedException e) {
                es.shutdownNow();
            }
            logger.info("All threads finished");
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
                        // logger.debug("As instructed, skipped label {}", l.name());
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
                    case SIMILAR_GEOMETRY_TRANSLATION_SIZE_CHANGE -> {
                        // Found a geometric match but translated by a vector != 0 and with a different size
                        // First create an interpretation node for later analyses
                        attachGeomChanges(tx, diffResult, leftRelNode, rightRelNode);
                        // Then treat it as usual matched geometry
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
                        attachGeomChanges(tx, diffResult, leftRelNode, rightRelNode);
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
                        attachGeomChanges(tx, diffResult, leftRelNode, rightRelNode);
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
                    .filter(rightRel -> !GraphUtils.isGeomValid(rightRel.getEndNode()))
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
        dbStats.startTimer();
        Patterns.createRuleNetwork(
                graphDb,
                ((CityGMLNeo4jDBConfig) config).INTERPRETATION_RULES_PATH,
                config.MATCHER_CONCURRENT_TIMEOUT
        );
        Patterns.interpret(
                graphDb,
                ((CityGMLNeo4jDBConfig) config).INTERPRETATION_FUNCTIONS_PATH,
                rtrees[0], // old city model
                config.DB_BATCH_SIZE,
                config.MATCHER_TOLERANCE_LENGTHS,
                config.MATCHER_CONCURRENT_TIMEOUT
        );
        dbStats.stopTimer("Create rule network and match change patterns");
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

    private void attachGeomChanges(Transaction tx, DiffResult diffResult, Node leftRelNode, Node rightRelNode) {
        if (!(diffResult instanceof DiffResultGeo res)) return;
        Label anchor = res.getAnchor();
        if (anchor != null) {
            Node leftAnchorNode = getAnchorNode(tx, leftRelNode, anchor);
            Node rightAnchorNode = getAnchorNode(tx, rightRelNode, anchor);
            if (diffResult instanceof DiffResultGeoSize resSize) {
                // Found a geometric match with a different size
                Patterns.markGeoChange(tx, leftAnchorNode, rightAnchorNode, Map.of(SizeChange.class, resSize.getDelta()));
            } else if (diffResult instanceof DiffResultGeoTranslation resTranslation) {
                // Found a geometric match but translated by a vector != 0
                Patterns.markGeoChange(tx, leftAnchorNode, rightAnchorNode, Map.of(TranslationChange.class, resTranslation.getVector()));
            } else if (diffResult instanceof DiffResultGeoTranslationResize resSizeTranslation) {
                // Found a geometric match but translated by a vector != 0 and with a different size
                Patterns.markGeoChange(tx, leftAnchorNode, rightAnchorNode, Map.of(
                        SizeChange.class, resSizeTranslation.getDelta(),
                        TranslationChange.class, resSizeTranslation.getVector()
                ));
            }
        }
    }

    private Node getAnchorNode(Transaction tx, Node node, Label anchor) {
        Traverser traverser = tx.traversalDescription()
                .depthFirst()
                .expand(PathExpanders.forDirection(Direction.OUTGOING))
                .evaluator(Evaluators.fromDepth(0))
                .evaluator(path -> {
                    if (path.endNode().hasLabel(anchor))
                        return Evaluation.INCLUDE_AND_PRUNE;
                    return Evaluation.EXCLUDE_AND_CONTINUE;
                })
                .traverse(node);
        List<Node> anchorNodes = new ArrayList<>();
        traverser.forEach(path -> anchorNodes.add(path.endNode()));
        if (anchorNodes.isEmpty()) {
            logger.error("Found no anchor node {}, attaching to source node, where change occurred", anchor.name());
            return node;
        }
        if (anchorNodes.size() > 1) {
            logger.error("Found more than one anchor node {}, selecting one", anchor.name());
        }
        return anchorNodes.get(0);
    }

    protected abstract Map.Entry<Node, DiffResult> findBest(Transaction tx, Relationship leftRel, Node rightParentNode);

    protected abstract boolean compareMeasurements(Object leftMeasure, Object rightMeasure);

    protected abstract ConvexPolygon3D toConvexPolygon3D(Object polygon, Precision.DoubleEquivalence precision);

    protected abstract double[] multiCurveBBox(Object multiCurve);

    protected abstract List<Line3D> multiCurveToLines3D(Object multiCurve, Precision.DoubleEquivalence precision);

    protected abstract boolean isMultiCurveContainedInLines3D(Object multiCurve, List<Line3D> lines, Precision.DoubleEquivalence precision);

    protected abstract Plane boundarySurfacePropertyToPlane(Object boundarySurfaceProperty, Precision.DoubleEquivalence precision);

    protected abstract double[] polygonBBox(Object polygon);

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
            throw new RuntimeException("Could not export RTree footprints " + e);
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
            logger.error(e.getMessage() + "F\n" + Arrays.toString(e.getStackTrace()));
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
