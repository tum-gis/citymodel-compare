package jgraf.citygml;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import com.google.common.collect.Maps;
import jgraf.neo4j.Neo4jDB;
import jgraf.neo4j.Neo4jGraphRef;
import jgraf.neo4j.factory.AuxNodeLabels;
import jgraf.neo4j.factory.AuxPropNames;
import jgraf.neo4j.factory.EdgeTypes;
import jgraf.neo4j.factory.PropNames;
import jgraf.utils.*;
import org.apache.commons.geometry.euclidean.threed.*;
import org.apache.commons.geometry.euclidean.threed.line.Line3D;
import org.apache.commons.geometry.euclidean.threed.line.Lines3D;
import org.apache.commons.numbers.core.Precision;
import org.citygml4j.CityGMLContext;
import org.citygml4j.builder.jaxb.CityGMLBuilder;
import org.citygml4j.builder.jaxb.CityGMLBuilderException;
import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.geometry.Matrix;
import org.citygml4j.model.citygml.CityGML;
import org.citygml4j.model.citygml.bridge.BridgePartProperty;
import org.citygml4j.model.citygml.building.*;
import org.citygml4j.model.citygml.cityobjectgroup.CityObjectGroup;
import org.citygml4j.model.citygml.core.*;
import org.citygml4j.model.citygml.generics.*;
import org.citygml4j.model.citygml.tunnel.TunnelPartProperty;
import org.citygml4j.model.citygml.vegetation.SolitaryVegetationObject;
import org.citygml4j.model.common.child.ChildList;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.gml.base.AssociationByRepOrRef;
import org.citygml4j.model.gml.base.CoordinateListProvider;
import org.citygml4j.model.gml.base.StringOrRef;
import org.citygml4j.model.gml.basicTypes.Code;
import org.citygml4j.model.gml.basicTypes.Measure;
import org.citygml4j.model.gml.feature.BoundingShape;
import org.citygml4j.model.gml.feature.FeatureProperty;
import org.citygml4j.model.gml.geometry.GeometryProperty;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurve;
import org.citygml4j.model.gml.geometry.aggregates.MultiSurface;
import org.citygml4j.model.gml.geometry.primitives.*;
import org.citygml4j.model.gml.measures.Length;
import org.citygml4j.model.module.ModuleContext;
import org.citygml4j.model.module.citygml.CityGMLModuleType;
import org.citygml4j.util.bbox.BoundingBoxOptions;
import org.citygml4j.util.gmlid.DefaultGMLIdManager;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.CityGMLOutputFactory;
import org.citygml4j.xml.io.reader.*;
import org.citygml4j.xml.io.writer.CityGMLWriteException;
import org.citygml4j.xml.io.writer.CityGMLWriter;
import org.citygml4j.xml.io.writer.FeatureWriteMode;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class CityGMLNeo4jDBV2 extends CityGMLNeo4jDB {
    private final static Logger logger = LoggerFactory.getLogger(CityGMLNeo4jDBV2.class);

    public CityGMLNeo4jDBV2(CityGMLNeo4jDBConfig config) {
        super(config);
        uuidClasses = Set.of(CityGML.class);
        idClasses = Set.of(AbstractGML.class);
        hrefClasses = Set.of(AssociationByRepOrRef.class, StringOrRef.class);
    }

    @Override
    protected Neo4jGraphRef mapFileCityGML(String filePath, int partitionIndex, boolean connectToRoot) {
        final Neo4jGraphRef[] cityModelRef = {null};
        try {
            CityGMLNeo4jDBConfig cityGMLConfig = (CityGMLNeo4jDBConfig) config;
            if (cityGMLConfig.CITYGML_VERSION != CityGMLVersion.v2_0) {
                logger.warn("Found CityGML version {}, expected version {}",
                        cityGMLConfig.CITYGML_VERSION, CityGMLVersion.v2_0);
            }
            dbStats.startTimer();
            CityGMLContext ctx = CityGMLContext.getInstance();
            CityGMLBuilder builder = ctx.createCityGMLBuilder();
            CityGMLInputFactory in = builder.createCityGMLInputFactory();
            in.setProperty(CityGMLInputFactory.FEATURE_READ_MODE, FeatureReadMode.SPLIT_PER_COLLECTION_MEMBER);
            CityGMLReader reader = in.createCityGMLReader(new File(filePath));
            logger.info("Reading CityGML v2.0 file {} chunk-wise into main memory", filePath);

            // Ids of top-level features with no existing bounding shapes
            List<Neo4jGraphRef> topLevelNoBbox = Collections.synchronizedList(new ArrayList<>());

            // Multi-threading
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            long tlCount = 0;
            while (reader.hasNext()) {
                final XMLChunk chunk = reader.nextChunk();
                tlCount++;

                long finalTlCount = tlCount;
                executorService.execute(() -> {
                    try {
                        CityGML object = chunk.unmarshal();
                        boolean toUpdateBboxTL = preProcessMapping(object);
                        Neo4jGraphRef graphRef = (Neo4jGraphRef) this.map(object,
                                AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex);
                        postProcessMapping(toUpdateBboxTL, object, graphRef, partitionIndex, topLevelNoBbox);
                        logger.info("Mapped {} top-level features", finalTlCount);

                        if (object instanceof CityModel) {
                            if (cityModelRef[0] != null)
                                throw new RuntimeException("Found multiple CityModel objects in one file");
                            cityModelRef[0] = graphRef;
                            if (connectToRoot) {
                                //  Connect MAPPER root node with this CityModel
                                connectCityModelToRoot(graphRef, Map.of(
                                        AuxPropNames.COLLECTION_INDEX.toString(), partitionIndex,
                                        AuxPropNames.COLLECTION_MEMBER_TYPE.toString(), CityModel.class.getName()
                                ));
                            }
                        }
                    } catch (UnmarshalException | MissingADESchemaException e) {
                        throw new RuntimeException(e);
                    }
                });
            }

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(config.MAPPER_CONCURRENT_TIMEOUT, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
            reader.close();
            logger.info("Mapped all {} top-level features", tlCount);
            logger.info("Finished mapping file {}", filePath);
            dbStats.stopTimer("Map input file [" + partitionIndex + "]");

            dbStats.startTimer();
            setIndexesIfNew();
            resolveXLinks(resolveLinkRules(), correctLinkRules(), partitionIndex);
            dbStats.stopTimer("Resolve links of input file [" + partitionIndex + "]");

            dbStats.startTimer();
            logger.info("Calculate and map bounding boxes of top-level features");
            calcTLBbox(topLevelNoBbox, partitionIndex);
            dbStats.stopTimer("Calculate and map bounding boxes of top-level features");

            logger.info("Finished mapping file {}", filePath);
        } catch (CityGMLBuilderException | CityGMLReadException e) {
            throw new RuntimeException(e);
        }
        return cityModelRef[0];
    }

    @Override
    protected boolean preProcessMapping(Object chunk) {
        boolean toUpdateBboxTL = toUpdateBboxTL(chunk);
        if (toUpdateBboxTL) ((AbstractCityObject) chunk).setBoundedBy(null);
        return toUpdateBboxTL;
    }

    @Override
    protected void postProcessMapping(boolean toUpdateBboxTL, Object chunk, Neo4jGraphRef graphRef, int partitionIndex, List<Neo4jGraphRef> topLevelNoBbox) {
        if (!isTopLevel(chunk)) return;
        if (toUpdateBboxTL) topLevelNoBbox.add(graphRef);
        else {
            BoundingShape boundingShape = ((AbstractCityObject) chunk).getBoundedBy();
            if (boundingShape == null) {
                logger.debug("Bounding shape does not exist for top-level feature {}, will be calculated after XLink resolution", chunk.getClass().getName());
                topLevelNoBbox.add(graphRef);
                return;
            }
            addToRtree(boundingShape, graphRef, partitionIndex);
        }
    }

    @Override
    protected Class<?> getCityModelClass() {
        return CityModel.class;
    }

    @Override
    protected boolean toUpdateBboxTL(Object chunk) {
        if (!isTopLevel(chunk)) return false;
        AbstractCityObject aco = (AbstractCityObject) chunk;
        return aco.getBoundedBy() == null
                || aco.getLodRepresentation().hasLodImplicitGeometries()
                || aco.getLodRepresentation().hasImplicitGeometries();
    }

    @Override
    protected void calcTLBbox(List<Neo4jGraphRef> topLevelNoBbox, int partitionIndex) {
        if (topLevelNoBbox == null) return;
        // Multithreaded
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        BatchUtils.toBatches(topLevelNoBbox, 10 * config.MATCHER_TOPLEVEL_BATCH_SIZE)
                .forEach(batch -> executorService.submit((Callable<Void>) () -> {
                    try (Transaction tx = graphDb.beginTx()) {
                        batch.forEach(graphRef -> {
                            // Calculate bounding shape
                            Node topLevelNode = graphRef.getRepresentationNode(tx);
                            if (topLevelNode.hasLabel(Label.label(SolitaryVegetationObject.class.getName()))) {
                                System.out.println();
                            }
                            AbstractCityObject aco = (AbstractCityObject) toObject(topLevelNode);
                            BoundingShape boundingShape = aco.calcBoundedBy(BoundingBoxOptions.defaults().assignResultToFeatures(true));
                            if (boundingShape == null) {
                                logger.warn("Bounding shape not found for top-level feature {}, ignoring", aco.getClass().getName());
                                return;
                            }
                            Node boundingShapeNode;
                            try {
                                boundingShapeNode = map(tx, boundingShape, new IdentityHashMap<>(), AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex);
                            } catch (IllegalAccessException e) {
                                logger.error("Error mapping bounding shape for top-level feature {}, {}",
                                        aco.getClass().getName(), e.getMessage());
                                throw new RuntimeException(e);
                            }
                            topLevelNode.createRelationshipTo(boundingShapeNode, EdgeTypes.boundedBy);

                            // Add top-level features to RTree index
                            addToRtree(boundingShape, graphRef, partitionIndex);
                        });
                        tx.commit();
                    } catch (Exception e) {
                        throw new RuntimeException(e);
                    }
                    return null;
                }));
        Neo4jDB.finishThreads(executorService, config.MATCHER_CONCURRENT_TIMEOUT);
    }

    @Override
    protected void addToRtree(Object boundingShape, Neo4jGraphRef graphRef, int partitionIndex) {
        if (boundingShape instanceof BoundingShape) {
            Envelope envelope = ((BoundingShape) boundingShape).getEnvelope();
            if (envelope == null) {
                logger.warn("Envelope not found for top-level feature, ignoring");
                return;
            }
            double lowerX = envelope.getLowerCorner().getValue().get(0);
            double lowerY = envelope.getLowerCorner().getValue().get(1);
            double upperX = envelope.getUpperCorner().getValue().get(0);
            double upperY = envelope.getUpperCorner().getValue().get(1);
            synchronized (rtrees[partitionIndex]) {
                rtrees[partitionIndex] = rtrees[partitionIndex].add(graphRef, Geometries.rectangle(
                        lowerX, lowerY,
                        upperX, upperY
                ));
                // TODO Also use Geometries.rectangleGeographic(..) for lat and lon values
            }
        }
    }

    protected Node getTopLevelListNode(Node cityModelNode) {
        return cityModelNode
                .getSingleRelationship(EdgeTypes.cityObjectMember, Direction.OUTGOING).getEndNode()
                .getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode();
    }

    @Override
    protected boolean isCOMTopLevel(Node cityObjectMemberNode) {
        return isTopLevel(
                cityObjectMemberNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode());
    }

    @Override
    protected boolean isTopLevel(Node node) {
        return StreamSupport.stream(node.getLabels().spliterator(), false)
                .anyMatch(label -> {
                    try {
                        if (CityObjectGroup.class.isAssignableFrom(Class.forName(label.name()))) {
                            // TODO Skip CityObjectGroup for now
                            return false;
                        }
                        return AbstractCityObject.class.isAssignableFrom(Class.forName(label.name()));
                    } catch (ClassNotFoundException e) {
                        return false;
                    }
                });
    }

    @Override
    protected boolean isTopLevel(Object obj) {
        return obj instanceof AbstractCityObject;
    }

    @Override
    protected String getCOMElementId(Transaction tx, Neo4jGraphRef topLevelRef) {
        return topLevelRef.getRepresentationNode(tx)
                .getRelationships(Direction.INCOMING, EdgeTypes.object).stream()
                // There maybe multiple incoming relationships "object" (due to CityObjectMember and CityObjectGroup)
                // -> choose the one with CityObjectMember
                // TODO Also consider CityObjectGroup?
                .filter(rel -> rel.getStartNode().hasLabel(Label.label(CityObjectMember.class.getName())))
                .findFirst().get().getStartNode().getElementId();
    }

    @Override
    protected List<Label> skipLabelsForTopLevel() {
        return List.of(Label.label(Solid.class.getName()));
    }

    // Match top-level features, return matches with descending similarity
    @Override
    protected PriorityQueue<Map.Entry<String, Double>> findBestTopLevel(Transaction tx, Relationship leftRel, Node rightNode) {
        Node leftRelNode = leftRel.getEndNode();
        if (!isCOMTopLevel(leftRelNode)) {
            throw new RuntimeException("Expected top-level feature, found " + ClazzUtils.getSimpleClassName(leftRelNode));
        }

        PriorityQueue<Map.Entry<String, Double>> maxHeap = new PriorityQueue<>(
                (r1, r2) -> r2.getValue().compareTo(r1.getValue()));

        Node leftTLNode = leftRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode();
        List<Label> leftLabels = StreamSupport.stream(leftTLNode.getLabels().spliterator(), false)
                .filter(label -> !label.name().contains(AuxNodeLabels.__PARTITION_INDEX__.name()))
                .toList();

        int rightPartitionIndex = Integer.parseInt(StreamSupport
                .stream(rightNode.getLabels().spliterator(), false)
                .filter(label -> label.name().startsWith(AuxNodeLabels.__PARTITION_INDEX__.name()))
                .collect(Collectors.toSet())
                .iterator().next().name().replace(AuxNodeLabels.__PARTITION_INDEX__.name(), ""));
        double[] leftBbox = GraphUtils.getBoundingBox(leftTLNode);
        Rectangle leftRectangle = Geometries.rectangle(
                leftBbox[0], leftBbox[1],
                leftBbox[3], leftBbox[4]
        );
        double leftArea = leftRectangle.area();

        // Preparations
        List<double[]> rightBboxes = new ArrayList<>();
        List<String> rightCOMIDs = new ArrayList<>();
        rtrees[rightPartitionIndex].search(leftRectangle).forEach(entry -> {
            // Consider only top-level features of the same type
            Node rightTLNode = entry.value().getRepresentationNode(tx);
            List<Label> rightLabels = StreamSupport.stream(rightTLNode.getLabels().spliterator(), false)
                    .filter(label -> !label.name().contains(AuxNodeLabels.__PARTITION_INDEX__.name()))
                    .toList();
            int leftInRight = leftLabels.stream().filter(rightLabels::contains).toList().size();
            int rightInLeft = rightLabels.stream().filter(leftLabels::contains).toList().size();
            if (leftInRight == 0 || leftInRight != rightInLeft) {
                return;
            }

            double[] rightBbox = GraphUtils.getBoundingBox(rightTLNode);
            rightBboxes.add(rightBbox);
            String rightCOMId = getCOMElementId(tx, entry.value());
            rightCOMIDs.add(rightCOMId);
        });

        // First check for overlapping volume
        double leftRatio = 0;
        double rightRatio = 0;
        for (int i = 0; i < rightBboxes.size(); i++) {
            String rightCOMID = rightCOMIDs.get(i);
            double[] rightBbox = rightBboxes.get(i);
            double leftVolume = GeometryUtils.volume(leftBbox);
            double rightVolume = GeometryUtils.volume(rightBbox);
            double overlapVolume = GeometryUtils.overlapVolume(leftBbox, rightBbox);
            leftRatio = overlapVolume / leftVolume;
            rightRatio = overlapVolume / rightVolume;
            if (leftRatio >= config.MATCHER_TOLERANCE_SOLIDS && rightRatio >= config.MATCHER_TOLERANCE_SOLIDS) {
                // Overlap satisfies a minimum threshold
                maxHeap.add(new AbstractMap.SimpleEntry<>(rightCOMID, leftRatio));
            }
        }

        // Trees often have similar bounding boxes -> Use additional features for matching
        if (leftTLNode.hasLabel(Label.label(SolitaryVegetationObject.class.getName()))) {
            // Class
            String leftClazz;
            if (leftTLNode.hasRelationship(Direction.OUTGOING, EdgeTypes.clazz)) {
                leftClazz = leftTLNode.getSingleRelationship(EdgeTypes.clazz, Direction.OUTGOING).getEndNode()
                        .getProperty(PropNames.value.toString()).toString();
            } else {
                leftClazz = null;
            }

            // Species
            String leftSpecies;
            if (leftTLNode.hasRelationship(Direction.OUTGOING, EdgeTypes.species)) {
                leftSpecies = leftTLNode.getSingleRelationship(EdgeTypes.species, Direction.OUTGOING).getEndNode()
                        .getProperty(PropNames.value.toString()).toString();
            } else {
                leftSpecies = null;
            }

            // Height
            Length leftHeight;
            if (leftTLNode.hasRelationship(Direction.OUTGOING, EdgeTypes.height)) {
                leftHeight = (Length) toObject(leftTLNode.getSingleRelationship(EdgeTypes.height, Direction.OUTGOING).getEndNode());
            } else {
                leftHeight = null;
            }

            // Names
            String leftNameStrings;
            if (leftTLNode.hasRelationship(Direction.OUTGOING, EdgeTypes.name)) {
                ChildList<Code> ns = (ChildList<Code>) toObject(leftTLNode.getSingleRelationship(EdgeTypes.name, Direction.OUTGOING).getEndNode());
                List<String> leftNames = new ArrayList<>(ns.stream().map(Code::getValue).toList());
                Collections.sort(leftNames);
                leftNameStrings = String.join(",", leftNames);
            } else {
                leftNameStrings = null;
            }

            Map<Map.Entry<String, Double>, Double> toUpdate = new HashMap<>(); // cannot change priority queue while iterating -> change after
            // TODO Adjust these values freely
            double weightClazz = 0.1;
            double weightSpecies = 0.1;
            double weightHeight = 0.5;
            double weightNames = 1;
            maxHeap.forEach(entry -> {
                Node rightTLNode = tx.getNodeByElementId(entry.getKey())
                        .getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode();
                double oldValue = entry.getValue();
                double newValue = oldValue;

                // Class
                String rightClazz = null;
                if (rightTLNode.hasRelationship(Direction.OUTGOING, EdgeTypes.clazz)) {
                    rightClazz = rightTLNode.getSingleRelationship(EdgeTypes.clazz, Direction.OUTGOING).getEndNode()
                            .getProperty(PropNames.value.toString()).toString();
                }
                if (leftClazz != null && leftClazz.equals(rightClazz)) {
                    newValue += weightClazz;
                }

                // Species
                String rightSpecies = null;
                if (rightTLNode.hasRelationship(Direction.OUTGOING, EdgeTypes.species)) {
                    rightSpecies = rightTLNode.getSingleRelationship(EdgeTypes.species, Direction.OUTGOING).getEndNode()
                            .getProperty(PropNames.value.toString()).toString();
                }
                if (leftSpecies != null && leftSpecies.equals(rightSpecies)) {
                    newValue += weightSpecies;
                }

                // Height
                Length rightHeight = null;
                if (rightTLNode.hasRelationship(Direction.OUTGOING, EdgeTypes.height)) {
                    rightHeight = (Length) toObject(rightTLNode.getSingleRelationship(EdgeTypes.height, Direction.OUTGOING).getEndNode());
                }
                if (leftHeight != null && rightHeight != null) {
                    Double diff = compareMeasurements(leftHeight, rightHeight);
                    if (diff == 0) {
                        newValue += weightHeight;
                    }
                }

                // Names
                String rightNameStrings = null;
                if (rightTLNode.hasRelationship(Direction.OUTGOING, EdgeTypes.name)) {
                    ChildList<Code> ns = (ChildList<Code>) toObject(rightTLNode.getSingleRelationship(EdgeTypes.name, Direction.OUTGOING).getEndNode());
                    List<String> rightNames = new ArrayList<>(ns.stream().map(Code::getValue).toList());
                    Collections.sort(rightNames);
                    rightNameStrings = String.join(",", rightNames);
                }
                if (leftNameStrings != null && leftNameStrings.equals(rightNameStrings)) {
                    newValue += weightNames;
                }

                // Save to add
                if (newValue != oldValue) {
                    toUpdate.put(entry, newValue);
                }
            });

            // Update maxHeap
            toUpdate.forEach((key, value) -> {
                maxHeap.remove(key);
                maxHeap.add(new AbstractMap.SimpleEntry<>(key.getKey(), value));
            });
        }

        if (!maxHeap.isEmpty()) return maxHeap;

        // Too small overlapping, check for translations
        // TODO Also consider 3D bboxes instead of rectangles?
        for (int i = 0; i < rightBboxes.size(); i++) {
            String rightCOMID = rightCOMIDs.get(i);
            double[] rightBbox = rightBboxes.get(i);
            Rectangle rightRectangle = Geometries.rectangle(
                    rightBbox[0], rightBbox[1],
                    rightBbox[3], rightBbox[4]
            );
            double rightArea = rightRectangle.area();

            // Accept equal widths and heights
            if (Math.abs(leftRectangle.perimeter() - rightRectangle.perimeter())
                    < 2 * config.MATCHER_TOLERANCE_LENGTHS) {
                double distance = rightRectangle.distance(leftRectangle);
                if (distance < config.MATCHER_TRANSLATION_DISTANCE) { // Accept translations within a threshold
                    // Move left rectangle to the right
                    Rectangle movedLeftRectangle = Geometries.rectangle(
                            leftRectangle.x1() + distance, leftRectangle.y1() + distance,
                            leftRectangle.x2() + distance, leftRectangle.y2() + distance
                    );

                    // Check for overlapping
                    double movedOverlap = movedLeftRectangle.intersectionArea(rightRectangle);
                    double leftMovedOverlapRatio = movedOverlap / leftArea;
                    double rightMovedOverlapRatio = movedOverlap / rightArea;
                    if (leftMovedOverlapRatio > config.MATCHER_TOLERANCE_SURFACES
                            && rightMovedOverlapRatio > config.MATCHER_TOLERANCE_SURFACES) {
                        // Overlap satisfies a minimum threshold
                        maxHeap.add(new AbstractMap.SimpleEntry<>(rightCOMID, leftMovedOverlapRatio));
                    }
                }
            }
        }

        return maxHeap;
    }

    // Given a parent node and a relationship, find a corresponding match
    // TODO Same for CityGML v3
    @Override
    protected Map.Entry<Node, DiffResult> findBest(Transaction tx, Relationship leftRel, Node rightNode) {
        Node leftRelNode = leftRel.getEndNode();

        // Error tolerance for matching numerics and geometries
        Precision.DoubleEquivalence lengthPrecision = Precision.doubleEquivalenceOfEpsilon(config.MATCHER_TOLERANCE_LENGTHS);
        Precision.DoubleEquivalence translationPrecision = Precision.doubleEquivalenceOfEpsilon(config.MATCHER_TRANSLATION_DISTANCE);
        Precision.DoubleEquivalence anglePrecision = Precision.doubleEquivalenceOfEpsilon(config.MATCHER_TOLERANCE_ANGLES);

        // PartProperties or Solid
        if (isPartProperty(leftRelNode)
                || leftRelNode.hasLabel(Label.label(Solid.class.getName()))) {
            boolean isPartProperty = isPartProperty(leftRelNode);
            double[] tmpLeftBBox = GraphUtils.getBoundingBox(isPartProperty ?
                    leftRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode()
                    : leftRelNode);
            double leftVolume = (tmpLeftBBox[3] - tmpLeftBBox[0])
                    * (tmpLeftBBox[4] - tmpLeftBBox[1])
                    * (tmpLeftBBox[5] - tmpLeftBBox[2]);
            Vector3D leftCentroid = Vector3D.of(
                    0.5 * (tmpLeftBBox[0] + tmpLeftBBox[3]),
                    0.5 * (tmpLeftBBox[1] + tmpLeftBBox[4]),
                    0.5 * (tmpLeftBBox[2] + tmpLeftBBox[5])
            );

            AtomicReference<Double> maxOverlapRatio = new AtomicReference<>(config.MATCHER_TOLERANCE_SOLIDS);
            AtomicReference<Double> minCentroidTranslation = new AtomicReference<>(config.MATCHER_TRANSLATION_DISTANCE);
            AtomicBoolean centroidUsed = new AtomicBoolean(false);
            AtomicReference<Node> candidateRef = new AtomicReference<>();
            rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).stream()
                    .forEach(rightRel -> {
                        Node rightRelNode = rightRel.getEndNode();
                        double[] tmpRightBBox = GraphUtils.getBoundingBox(isPartProperty ?
                                rightRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode()
                                : rightRelNode);
                        double rightVolume = (tmpRightBBox[3] - tmpRightBBox[0])
                                * (tmpRightBBox[4] - tmpRightBBox[1])
                                * (tmpRightBBox[5] - tmpRightBBox[2]);
                        Vector3D rightCentroid = Vector3D.of(
                                0.5 * (tmpRightBBox[0] + tmpRightBBox[3]),
                                0.5 * (tmpRightBBox[1] + tmpRightBBox[4]),
                                0.5 * (tmpRightBBox[2] + tmpRightBBox[5])
                        );

                        // rightRegion will be altered
                        double overlapX = Math.max(0, Math.min(tmpLeftBBox[3], tmpRightBBox[3]) - Math.max(tmpLeftBBox[0], tmpRightBBox[0]));
                        double overlapY = Math.max(0, Math.min(tmpLeftBBox[4], tmpRightBBox[4]) - Math.max(tmpLeftBBox[1], tmpRightBBox[1]));
                        double overlapZ = Math.max(0, Math.min(tmpLeftBBox[5], tmpRightBBox[5]) - Math.max(tmpLeftBBox[2], tmpRightBBox[2]));
                        double overlapSize = overlapX * overlapY * overlapZ;
                        double overlapRatio = overlapSize / leftVolume;
                        if (overlapRatio > maxOverlapRatio.get()) {
                            maxOverlapRatio.set(overlapRatio);
                            candidateRef.set(rightRelNode);
                        } else {
                            // Small bboxes may not overlap but they can still be a valid match -> Compare volume first
                            double ratioSizeLR = leftVolume / rightVolume;
                            double ratioSizeRL = rightVolume / leftVolume;
                            // Must consider both ratios in case one bbox is much bigger than the other
                            if ((ratioSizeLR >= config.MATCHER_TOLERANCE_SOLIDS && ratioSizeLR <= 1)
                                    || (ratioSizeRL >= config.MATCHER_TOLERANCE_SOLIDS && ratioSizeRL <= 1)) {
                                // Choose the candidate with the shortest distance
                                double centroidTranslation = rightCentroid.subtract(leftCentroid).norm();
                                if (centroidTranslation <= minCentroidTranslation.get()) {
                                    minCentroidTranslation.set(centroidTranslation);
                                    candidateRef.set(rightRelNode);
                                    centroidUsed.set(true);
                                }
                            }
                        }
                    });
            if (candidateRef.get() != null) {
                if (centroidUsed.get()) {
                    logger.debug(
                            "Found the best matching candidate for {} with distance {}",
                            ClazzUtils.getSimpleClassName(leftRelNode),
                            minCentroidTranslation.get());
                    return new AbstractMap.SimpleEntry<>(candidateRef.get(),
                            new DiffResultGeo(
                                    SimilarityLevel.SIMILAR_GEOMETRY,
                                    -minCentroidTranslation.get(),
                                    null, // TODO Label list to skip for solids
                                    null
                            ));
                }
                logger.debug(
                        "Found the best matching candidate for {} with {}% >= {}% overlapping volume",
                        ClazzUtils.getSimpleClassName(leftRelNode),
                        Math.round(maxOverlapRatio.get() * 100),
                        Math.round(config.MATCHER_TOLERANCE_SOLIDS * 100));
                return new AbstractMap.SimpleEntry<>(candidateRef.get(),
                        new DiffResultGeo(
                                SimilarityLevel.SIMILAR_GEOMETRY,
                                maxOverlapRatio.get(),
                                null, // TODO Label list to skip for solids
                                null
                        ));
            }
            return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
        }

        // TODO Consider change in SRS/CRS?

        // TODO Aggregation surfaces (composite surfaces)

        // Vector init norm = sqrt(3a^2) <= t => a <= t/sqrt(3)
        double maxAllowed = config.MATCHER_TRANSLATION_DISTANCE / Math.sqrt(3);

        // BoundarySurfaceProperties -> scout for type (roofs/walls/grounds) and centroid distance
        // -> best match with min distance -> however still have to match content, no skipping geometries
        if (isBoundarySurfaceProperty(leftRelNode)) {
            MetricBoundarySurfaceProperty leftMetricBSP = metricFromBoundarySurfaceProperty(leftRelNode, lengthPrecision, anglePrecision);
            if (leftMetricBSP == null) {
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            }

            // Surface type
            Class<?> leftBSType = leftMetricBSP.getSurfaceType();

            // Using bbox for convex and non-convex polygons
            Vector3D[] leftBBox = leftMetricBSP.getBbox();
            Vector3D leftSizes = leftBBox[1].subtract(leftBBox[0]);

            // Calculate normal vector
            Vector3D leftNormal = leftMetricBSP.getNormalVector();
            if (leftNormal == null) {
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            }
            Vector3D leftCentroid = Vector3D.centroid(leftMetricBSP.getExteriorPoints());

            // Exterior points
            List<Vector3D> leftPoints = leftMetricBSP.getExteriorPoints();

            // LOD
            int leftLOD = leftMetricBSP.getHighestLOD();

            Node candidateTranslationSameSize = null;
            Vector3D minTranslation = Vector3D.of(maxAllowed, maxAllowed, maxAllowed);
            double minTranslationNorm = config.MATCHER_TRANSLATION_DISTANCE;

            Node candidateTranslationResize = null;
            Vector3D minResize = Vector3D.of(maxAllowed, maxAllowed, maxAllowed);
            double minResizeNorm = config.MATCHER_TRANSLATION_DISTANCE;

            for (Relationship rightRel : rightNode.getRelationships(Direction.OUTGOING, leftRel.getType())) {
                Node rightRelNode = rightRel.getEndNode();
                if (!GraphUtils.isGeomValid(rightRelNode)) continue;
                MetricBoundarySurfaceProperty rightMetricBSP = metricFromBoundarySurfaceProperty(rightRelNode, lengthPrecision, anglePrecision);
                if (rightMetricBSP == null) {
                    continue;
                }

                // Surface type
                Class<?> rightBSType = rightMetricBSP.getSurfaceType();

                // Using bbox for convex and non-convex polygons
                Vector3D[] rightBBox = rightMetricBSP.getBbox();
                Vector3D rightSizes = rightBBox[1].subtract(rightBBox[0]);

                // Calculate normal vector
                Vector3D rightNormal = rightMetricBSP.getNormalVector();
                if (rightNormal == null) {
                    GraphUtils.markGeomInvalid(tx, rightRelNode);
                    continue;
                }
                Vector3D rightCentroid = Vector3D.centroid(rightMetricBSP.getExteriorPoints());

                // Exterior points
                List<Vector3D> rightPoints = rightMetricBSP.getExteriorPoints();

                // LOD
                int rightLOD = rightMetricBSP.getHighestLOD();

                // Check label
                if (!leftBSType.equals(rightBSType)) continue;

                // Check LOD
                if (leftLOD != rightLOD) continue;

                // Compute diff in orientation
                double angle = leftNormal.angle(rightNormal);
                if (!anglePrecision.eqZero(angle)) {
                    continue;
                } // TODO Support for small rotations?

                Vector3D sizeChange = rightSizes.subtract(leftSizes);

                // Compute translation
                Vector3D translation = rightCentroid.subtract(leftCentroid);
                if (translation.eq(Vector3D.ZERO, lengthPrecision)) {
                    if (sizeChange.eq(Vector3D.ZERO, lengthPrecision)) {
                        // Same orientation, same position, same size
                        logger.debug("Found the best matching candidate for {} with same orientation, same position, and same size",
                                ClazzUtils.getSimpleClassName(leftRelNode));
                        return new AbstractMap.SimpleEntry<>(rightRelNode,
                                new DiffResultGeo(
                                        SimilarityLevel.SIMILAR_GEOMETRY,
                                        1,
                                        List.of(Label.label(MultiSurface.class.getName())),
                                        null
                                ));
                    } else {
                        // Same orientation + Same position + Different size
                        logger.debug("Found the best matching candidate for {} with same orientation, same position, but different size, magnitude {}",
                                ClazzUtils.getSimpleClassName(leftRelNode), sizeChange);
                        return new AbstractMap.SimpleEntry<>(rightRelNode,
                                new DiffResultGeoSize(
                                        sizeChange.toArray(),
                                        List.of(Label.label(MultiSurface.class.getName())),
                                        Label.label(SurfaceProperty.class.getName())
                                ));
                    }
                } else if (translation.norm() < config.MATCHER_TRANSLATION_DISTANCE) {
                    // Test whether the translation is correct by
                    // translating the left polygon and compare it with the right polygon
                    Matrix translationMatrix = new Matrix(new double[][]{
                            {1, 0, 0, translation.getX()},
                            {0, 1, 0, translation.getY()},
                            {0, 0, 1, translation.getZ()},
                            {0, 0, 0, 1}
                    });
                    int k = 0;
                    for (; k < leftPoints.size(); k++) {
                        Vector3D vp = leftPoints.get(k);
                        org.citygml4j.geometry.Point p = new org.citygml4j.geometry.Point(vp.getX(), vp.getY(), vp.getZ());
                        p.transform3D(translationMatrix);
                        // TODO Check if this translation is along only one axis (side moving, vertical raise, etc.)
                        if (!(lengthPrecision.gte(p.getX(), rightBBox[0].getX())
                                && lengthPrecision.gte(p.getY(), rightBBox[0].getY())
                                && lengthPrecision.gte(p.getZ(), rightBBox[0].getZ())
                                && lengthPrecision.lte(p.getX(), rightBBox[1].getX())
                                && lengthPrecision.lte(p.getY(), rightBBox[1].getY())
                                && lengthPrecision.lte(p.getZ(), rightBBox[1].getZ()))
                        ) {
                            break;
                        }
                    }

                    boolean isSet = false;
                    double translationNorm = translation.norm();
                    if (translationNorm < minTranslationNorm) {
                        isSet = true;
                        minTranslationNorm = translationNorm;
                        minTranslation = translation;
                    }
                    if (k == leftPoints.size()) {
                        // Same orientation, same size, but with translation -> Find min translation
                        if (isSet) candidateTranslationSameSize = rightRelNode;
                    } else {
                        if (isSet) {
                            double resizeNorm = sizeChange.norm();
                            if (resizeNorm < minResizeNorm) {
                                minResizeNorm = resizeNorm;
                                minResize = sizeChange;
                                candidateTranslationResize = rightRelNode;
                            }
                        }
                    }
                }

                /*
                // Check overlapping volume
                if (matchBbox(leftBBox, rightBBox, precision, config.MATCHER_TOLERANCE_SOLIDS)) {
                    if (sizeChange.eq(Vector3D.ZERO, precision)) {
                        // Same orientation + Same position + Same size
                        logger.debug("Found the best matching candidate for {} with same orientation, same position, and same size",
                                ClazzUtils.getSimpleClassName(leftRelNode));
                        return new AbstractMap.SimpleEntry<>(rightRelNode,
                                new DiffResultGeo(
                                        SimilarityLevel.SIMILAR_GEOMETRY,
                                        1,
                                        List.of(Label.label(MultiSurface.class.getName())),
                                        null
                                ));
                    }
                } else {

                }
                */
            }

            // Finally, check if candidate has been found
            if (candidateTranslationSameSize == null && candidateTranslationResize == null)
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));

            if (candidateTranslationSameSize != null && candidateTranslationResize != null) {
                logger.warn("Found multiple best matching candidates for {} with translation {}, " +
                                "but both with and without resize {}, taking the one with translation and resize",
                        ClazzUtils.getSimpleClassName(leftRelNode), minTranslation, minResize);
                return new AbstractMap.SimpleEntry<>(candidateTranslationResize,
                        new DiffResultGeoTranslationResize(
                                minTranslation.toArray(),
                                minResize.toArray(),
                                List.of(Label.label(MultiSurface.class.getName())),
                                Label.label(SurfaceProperty.class.getName())
                        ));
            }

            if (candidateTranslationSameSize != null) {
                // Same orientation, same size, but with translation
                logger.debug("Found the best matching candidate for {} with same orientation, same size, but with translation {}",
                        ClazzUtils.getSimpleClassName(leftRelNode), minTranslation);
                return new AbstractMap.SimpleEntry<>(candidateTranslationSameSize,
                        new DiffResultGeoTranslation(
                                minTranslation.toArray(),
                                List.of(Label.label(MultiSurface.class.getName())),
                                Label.label(SurfaceProperty.class.getName())
                        ));
            }

            // Same orientation, but with translation and resize
            logger.debug("Found the best matching candidate for {} with same orientation, but with translation {} and resize {}",
                    ClazzUtils.getSimpleClassName(leftRelNode), minTranslation, minResize);
            return new AbstractMap.SimpleEntry<>(candidateTranslationResize,
                    new DiffResultGeoTranslationResize(
                            minTranslation.toArray(),
                            minResize.toArray(),
                            List.of(Label.label(MultiSurface.class.getName())),
                            Label.label(SurfaceProperty.class.getName())
                    ));

            // TODO Further checks for rotation, etc. (remember to use continue; to skip other checks below)
        }

        // Implicit geometries
        if (leftRelNode.hasLabel(Label.label(ImplicitRepresentationProperty.class.getName()))) {
            ImplicitRepresentationProperty leftIRP = (ImplicitRepresentationProperty) toObject(leftRelNode);
            String leftId = leftIRP.getImplicitGeometry().getRelativeGMLGeometry().getGeometry().getId();

            for (Relationship rightRel : rightNode.getRelationships(Direction.OUTGOING, leftRel.getType())) {
                Node rightRelNode = rightRel.getEndNode();
                ImplicitRepresentationProperty rightIRP = (ImplicitRepresentationProperty) toObject(rightRelNode);
                String rightId = leftIRP.getImplicitGeometry().getRelativeGMLGeometry().getGeometry().getId();

                // Implicit geometries are defined once and referenced using IDs -> match IDs
                if (leftId.equals(rightId)) {
                    logger.debug("Found the best matching candidate for {} with same ID",
                            ClazzUtils.getSimpleClassName(leftRelNode));
                    return new AbstractMap.SimpleEntry<>(rightRelNode,
                            new DiffResultGeo(
                                    SimilarityLevel.SIMILAR_GEOMETRY,
                                    1,
                                    List.of(Label.label(GeometryProperty.class.getName())),
                                    null
                            ));
                }
            }

            return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
        }

        // MultiCurves that contain LineStrings
        if (leftRelNode.hasLabel(Label.label(MultiCurve.class.getName()))) {
            MultiCurve leftMultiCurve = (MultiCurve) toObject(leftRelNode);
            double[] tmpLeftBbox = multiCurveBBox(leftMultiCurve);
            if (tmpLeftBbox == null) {
                GraphUtils.markGeomInvalid(tx, leftRelNode);
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            }
            Vector3D leftCentroid = Vector3D.of(
                    (tmpLeftBbox[0] + tmpLeftBbox[3]) / 2.,
                    (tmpLeftBbox[1] + tmpLeftBbox[4]) / 2.,
                    (tmpLeftBbox[2] + tmpLeftBbox[5]) / 2.
            );
            Vector3D leftSizes = Vector3D.of(
                    tmpLeftBbox[3] - tmpLeftBbox[0],
                    tmpLeftBbox[4] - tmpLeftBbox[1],
                    tmpLeftBbox[5] - tmpLeftBbox[2]
            );
            List<Line3D> leftLines = multiCurveToLines3D(leftMultiCurve, lengthPrecision);
            if (leftLines == null) {
                GraphUtils.markGeomInvalid(tx, leftRelNode);
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            }
            Node candidate = null;
            Vector3D minTranslation = Vector3D.of(maxAllowed, maxAllowed, maxAllowed);
            double minTranslationNorm = minTranslation.norm();
            for (Relationship rightRel : rightNode.getRelationships(Direction.OUTGOING, leftRel.getType())) {
                Node rightRelNode = rightRel.getEndNode();
                if (!GraphUtils.isGeomValid(rightRelNode)) continue;
                MultiCurve rightMultiCurve = (MultiCurve) toObject(rightRelNode);
                double[] tmpRightBbox = multiCurveBBox(rightMultiCurve);
                if (tmpRightBbox == null) {
                    GraphUtils.markGeomInvalid(tx, rightRelNode);
                    continue;
                }
                Vector3D rightCentroid = Vector3D.of(
                        (tmpRightBbox[0] + tmpRightBbox[3]) / 2.,
                        (tmpRightBbox[1] + tmpRightBbox[4]) / 2.,
                        (tmpRightBbox[2] + tmpRightBbox[5]) / 2.
                );
                Vector3D rightSizes = Vector3D.of(
                        tmpRightBbox[3] - tmpRightBbox[0],
                        tmpRightBbox[4] - tmpRightBbox[1],
                        tmpRightBbox[5] - tmpRightBbox[2]
                );
                List<Line3D> rightLines = multiCurveToLines3D(rightMultiCurve, lengthPrecision);
                if (rightLines == null) {
                    GraphUtils.markGeomInvalid(tx, rightRelNode);
                    continue;
                }

                // First, test if same size
                if (leftSizes.eq(rightSizes, lengthPrecision)) {
                    Vector3D translation = rightCentroid.subtract(leftCentroid);
                    if (translation.eq(Vector3D.ZERO, lengthPrecision)) {
                        // Zero translation -> Proceed to check if all points of one curve are contained in the other
                        if (isMultiCurveContainedInLines3D(leftMultiCurve, rightLines, lengthPrecision)
                                && isMultiCurveContainedInLines3D(rightMultiCurve, leftLines, lengthPrecision)) {
                            logger.debug("Found the best matching candidate for {} without translation",
                                    ClazzUtils.getSimpleClassName(leftRelNode));
                            return new AbstractMap.SimpleEntry<>(rightRelNode,
                                    new DiffResultGeo(
                                            SimilarityLevel.SIMILAR_GEOMETRY,
                                            1,
                                            List.of(Label.label(LineString.class.getName())), // TODO Label list to skip for MultiCurves
                                            null
                                    ));
                        }
                    }
                    // Non-zero translation
                    // Test whether the translation is correct by
                    // translating the left multi-curve and compare it with the right one, and vice versa
                    AffineTransformMatrix3D matLR = AffineTransformMatrix3D.createTranslation(
                            Vector3D.of(translation.getX(), translation.getY(), translation.getZ()));
                    AffineTransformMatrix3D matRL = AffineTransformMatrix3D.createTranslation(
                            Vector3D.of(-translation.getX(), -translation.getY(), -translation.getZ()));
                    List<Line3D> translatedLeftLines = leftLines.stream().map(line -> line.transform(matLR)).toList();
                    List<Line3D> translatedRightLines = rightLines.stream().map(line -> line.transform(matRL)).toList();
                    if (isMultiCurveContainedInLines3D(leftMultiCurve, translatedRightLines, lengthPrecision)
                            && isMultiCurveContainedInLines3D(rightMultiCurve, translatedLeftLines, lengthPrecision)) {
                        // Search for the min non-zero translation
                        double translationNorm = translation.norm();
                        if (translationNorm < minTranslationNorm) {
                            minTranslation = translation;
                            minTranslationNorm = translationNorm;
                            candidate = rightRelNode;
                        }
                    }
                }
            }
            if (candidate == null) {
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            }
            logger.debug("Found the best matching candidate for {} with translation vector {}",
                    ClazzUtils.getSimpleClassName(leftRelNode), minTranslation.toString());
            return new AbstractMap.SimpleEntry<>(candidate,
                    new DiffResultGeoTranslation(
                            minTranslation.toArray(),
                            List.of(Label.label(LineString.class.getName())), // TODO Label list to skip for polygons
                            null
                    ));
        }

        // Points (all instances of CoordinateListProvider are treated the same, while PointProperty individually)
        if (ClazzUtils.isInstanceOf(leftRelNode, CoordinateListProvider.class)
                || leftRelNode.hasLabel(Label.label(PointProperty.class.getName()))) {
            boolean leftIsPP;
            List<Double> leftPos;
            if (leftRelNode.hasLabel(Label.label(PointProperty.class.getName()))) {
                leftIsPP = true;
                leftPos = ((PointProperty) toObject(leftRelNode)).getPoint().toList3d();
            } else {
                leftIsPP = false;
                leftPos = ((CoordinateListProvider) toObject(leftRelNode)).toList3d();
            }

            AtomicReference<Node> candidateRef = new AtomicReference<>();
            AtomicReference<Double> minDistance = new AtomicReference<>(Double.MAX_VALUE);
            rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).stream()
                    .forEach(rightRel -> {
                        Node rightRelNode = rightRel.getEndNode();
                        if (!ClazzUtils.isInstanceOf(rightRelNode, CoordinateListProvider.class)
                                && !rightRelNode.hasLabel(Label.label(PointProperty.class.getName()))) return;
                        List<Double> rightPos;
                        if (rightRelNode.hasLabel(Label.label(PointProperty.class.getName()))) {
                            if (!leftIsPP) return;
                            rightPos = ((PointProperty) toObject(rightRelNode)).getPoint().toList3d();
                        } else {
                            if (leftIsPP) return;
                            rightPos = ((CoordinateListProvider) toObject(rightRelNode)).toList3d();
                        }
                        double distance = 0;
                        for (int i = 0; i < leftPos.size(); i++) {
                            distance += Math.pow(leftPos.get(i) - rightPos.get(i), 2);
                        }
                        distance = Math.sqrt(distance);
                        if (distance <= config.MATCHER_TOLERANCE_LENGTHS) {
                            if (minDistance.get() > distance) {
                                minDistance.set(distance);
                                candidateRef.set(rightRelNode);
                            }
                        }
                    });
            if (candidateRef.get() == null) return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
            return new AbstractMap.SimpleEntry<>(candidateRef.get(),
                    new DiffResultGeo(
                            SimilarityLevel.EQUIVALENCE,
                            minDistance.get(),
                            null, // TODO Label list to skip for points
                            null
                    ));
        }

        // Generic attributes
        List<String> probeGen = StreamSupport.stream(leftRelNode.getLabels().spliterator(), false)
                .filter(l -> {
                    try {
                        return AbstractGenericAttribute.class.isAssignableFrom(Class.forName(l.name()))
                                // GenericAttributeSet objects are simply lists of smaller generic attributes
                                && !l.name().equals(GenericAttributeSet.class.getName());
                    } catch (ClassNotFoundException e) {
                        return false;
                    }
                })
                .map(Label::name)
                .toList();
        if (!probeGen.isEmpty()) {
            String genClassLabel = probeGen.get(0);
            // Find corresponding candidates
            List<Node> candidates = rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).stream()
                    .map(Relationship::getEndNode)
                    .filter(endNode -> endNode.hasLabel(Label.label(genClassLabel)))
                    .toList();
            // Compare objects
            AbstractGenericAttribute leftObj = (AbstractGenericAttribute) toObject(leftRelNode);
            List<Node> filteredCandidates = candidates.stream()
                    .filter(rightRelNode -> {
                        AbstractGenericAttribute rightObj = (AbstractGenericAttribute) toObject(rightRelNode);
                        if (leftObj instanceof IntAttribute leftGen) {
                            IntAttribute rightGen = (IntAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && leftGen.getValue().equals(rightGen.getValue());
                        }
                        if (leftObj instanceof DoubleAttribute leftGen) {
                            DoubleAttribute rightGen = (DoubleAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && lengthPrecision.compare(leftGen.getValue(), rightGen.getValue()) == 0;
                        }
                        if (leftObj instanceof DateAttribute leftGen) {
                            DateAttribute rightGen = (DateAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && leftGen.getValue().equals(rightGen.getValue());
                        }
                        if (leftObj instanceof MeasureAttribute leftGen) {
                            MeasureAttribute rightGen = (MeasureAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && compareMeasurements(leftGen.getValue(), rightGen.getValue()) == 0;
                        }
                        if (leftObj instanceof StringAttribute leftGen) {
                            StringAttribute rightGen = (StringAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && leftGen.getValue().equals(rightGen.getValue());
                        }
                        if (leftObj instanceof UriAttribute leftGen) {
                            UriAttribute rightGen = (UriAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && leftGen.getValue().equals(rightGen.getValue());
                        }
                        // GenericAttributeSet objects will be matched later in the process
                        // as they are a list of smaller generic attributes
                        return false;
                    })
                    .toList();
            // Found one or more exact matching candidates
            if (!filteredCandidates.isEmpty()) {
                // logger.debug("Found {} exact candidates for generic attribute {}", filteredCandidates.size(), leftObj.getName());
                return new AbstractMap.SimpleEntry<>(filteredCandidates.get(0),
                        new DiffResult(SimilarityLevel.EQUIVALENCE, 0));
            }
            // Found no exact match -> proceed to find generic attributes of the same name
            List<Node> filteredNamedCandidates = candidates.stream()
                    .filter(rightRelNode -> {
                        AbstractGenericAttribute rightObj = (AbstractGenericAttribute) toObject(rightRelNode);
                        return leftObj.isSetName() && rightObj.isSetName()
                                && leftObj.getName().equals(rightObj.getName());
                    })
                    .toList();
            if (!filteredNamedCandidates.isEmpty()) {
                // logger.debug("Found {} candidates for generic attribute of the same name {}", filteredNamedCandidates.size(), leftObj.getName());
                return new AbstractMap.SimpleEntry<>(filteredNamedCandidates.get(0),
                        new DiffResult(SimilarityLevel.SAME_LABELS, 0));
            }
            return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
        }

        // Lengths, Measurements (with uom)
        if (leftRelNode.hasLabel(Label.label(Measure.class.getName()))
                || leftRelNode.hasLabel(Label.label(Length.class.getName()))) {
            // Length is a subclass of Measure without new specialized properties
            Measure leftMeasure = (Measure) toObject(leftRelNode);
            AtomicInteger count = new AtomicInteger();
            List<Node> candidates = rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).stream()
                    .map(Relationship::getEndNode)
                    .filter(rightRelNode -> {
                        count.getAndIncrement();
                        if (!rightRelNode.hasLabel(Label.label(Measure.class.getName()))
                                && !rightRelNode.hasLabel(Label.label(Length.class.getName()))) return false;
                        Measure rightMeasure = (Measure) toObject(rightRelNode);
                        return compareMeasurements(leftMeasure, rightMeasure) == 0;
                    })
                    .toList();
            if (candidates.isEmpty()) {
                // If there is only one measured height -> select it for diff instead of null/empty
                if (count.get() == 1) {
                    return new AbstractMap.SimpleEntry<>(
                            rightNode.getSingleRelationship(leftRel.getType(), Direction.OUTGOING).getEndNode(),
                            new DiffResultProp(SimilarityLevel.SAME_PROPS, 0, null)); // TODO skip needed here?
                }
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResultProp(SimilarityLevel.SAME_PROPS, 0, null)); // TODO skip needed here?
            }
            return new AbstractMap.SimpleEntry<>(candidates.get(0),
                    new DiffResult(SimilarityLevel.EQUIVALENCE, 0));
        }

        // Match by gmlid
        String id;
        if (leftRelNode.hasProperty(PropNames.id.toString())) {
            id = leftRelNode.getProperty(PropNames.id.toString()).toString();
        } else {
            id = null;
        }
        if (id != null) {
            Set<Relationship> rightRels = rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).stream()
                    .filter(r -> r.getEndNode().hasProperty(PropNames.id.toString())
                            && r.getEndNode().getProperty(PropNames.id.toString()).toString().equals(id))
                    .collect(Collectors.toSet());
            /*
            if (rightRels.isEmpty())
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            */
            if (rightRels.size() > 1) {
                logger.error("Found multiple relationships with the same gml:id = {}", id);
            }
            // Found a node using its array/collection index
            if (rightRels.size() == 1) {
                return new AbstractMap.SimpleEntry<>(rightRels.iterator().next().getEndNode(),
                        new DiffResultProp(SimilarityLevel.SAME_ID, 0, List.of("id")));
            }
        }

        // Match by common kvps with same values
        if (!leftRelNode.getAllProperties().isEmpty()) {
            Map<String, Object> leftProps = leftRelNode.getAllProperties();
            AtomicReference<Integer> minCountDiffering = new AtomicReference<>(Integer.MIN_VALUE);
            AtomicReference<Node> candidate = new AtomicReference<>(null);
            rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).forEach(rightRel -> {
                Node rightRelNode = rightRel.getEndNode();
                Map<String, Object> rightProps = rightRelNode.getAllProperties();
                int countDiffering = Maps.difference(leftProps, rightProps).entriesDiffering().size();
                if (countDiffering < minCountDiffering.get()) {
                    minCountDiffering.set(countDiffering);
                    candidate.set(rightRelNode);
                }
            });

            if (candidate.get() != null) {
                return new AbstractMap.SimpleEntry<>(candidate.get(),
                        new DiffResult(SimilarityLevel.SIMILAR_STRUCTURE, minCountDiffering.get()));
            }
        }

        // Non-geometric non-generic nodes -> Find match having the most common kvps
        /*
        if (!leftRelNode.getAllProperties().isEmpty()) {
            AtomicReference<Integer> countCommon = new AtomicReference<>();
            Optional<Relationship> optRightRel = rightNode.getRelationships(Direction.OUTGOING, leftRel.getType())
                    .stream()
                    .reduce((r1, r2) -> {
                        int countCommon1 = Maps.difference(leftRelNode.getAllProperties(), r1.getEndNode().getAllProperties())
                                .entriesInCommon().size();
                        int countCommon2 = Maps.difference(leftRelNode.getAllProperties(), r2.getEndNode().getAllProperties())
                                .entriesInCommon().size();
                        if (countCommon1 > countCommon2) {
                            countCommon.set(countCommon1);
                            return r1;
                        }
                        countCommon.set(countCommon2);
                        return r2;
                    });
            return new AbstractMap.SimpleEntry<>(
                    optRightRel.map(Relationship::getEndNode).orElse(null),
                    new DiffResult(SimilarityLevel.SIMILAR_STRUCTURE, countCommon.get()));
            // Found a node that has the most kvps in common
        }
        */

        // TODO Other objects

        // If rel type is 1:1 and nodes have the same label
        if (rightNode.getDegree(leftRel.getType(), Direction.OUTGOING) == 1) {
            Node rightRelNode = rightNode.getSingleRelationship(leftRel.getType(), Direction.OUTGOING).getEndNode();
            boolean sameLabels = true;
            for (Label l : rightRelNode.getLabels()) {
                if (!AuxNodeLabels.isIn(l) && !leftRelNode.hasLabel(l)) {
                    sameLabels = false;
                    break;
                }
            }

            if (sameLabels) {
                return new AbstractMap.SimpleEntry<>(
                        rightNode.getSingleRelationship(
                                leftRel.getType(), Direction.OUTGOING).getEndNode(),
                        new DiffResult(SimilarityLevel.SAME_LABELS, 0)
                );
            }
        }

        return new AbstractMap.SimpleEntry<>(null,
                new DiffResult(SimilarityLevel.NONE, 0));
    }

    /*
    @Override
    protected Node getAnchorNode(Transaction tx, Node node, Label anchor) {
        // BoundarySurfaceProperty -[*]-> SurfaceProperty
        return node.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode()
                .getSingleRelationship(EdgeTypes.lod2MultiSurface, Direction.OUTGOING).getEndNode()
                .getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode()
                .getSingleRelationship(EdgeTypes.surfaceMember, Direction.OUTGOING).getEndNode()
                .getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode()
                .getSingleRelationship(AuxEdgeTypes.ARRAY_MEMBER, Direction.OUTGOING).getEndNode();
    }
    */

    protected boolean isPartProperty(Node node) {
        return StreamSupport.stream(node.getLabels().spliterator(), false).anyMatch(l -> {
            try {
                return ClazzUtils.isSubclass(Class.forName(l.name()), FeatureProperty.class, BuildingPartProperty.class)
                        || ClazzUtils.isSubclass(Class.forName(l.name()), FeatureProperty.class, BridgePartProperty.class)
                        || ClazzUtils.isSubclass(Class.forName(l.name()), FeatureProperty.class, TunnelPartProperty.class);
            } catch (ClassNotFoundException e) {
                return false;
            }
        });
    }

    protected boolean isBoundarySurfaceProperty(Node node) {
        return StreamSupport.stream(node.getLabels().spliterator(), false).anyMatch(l -> {
            try {
                return ClazzUtils.isSubclass(Class.forName(l.name()), FeatureProperty.class, AbstractBoundarySurface.class)
                        || ClazzUtils.isSubclass(Class.forName(l.name()), FeatureProperty.class, org.citygml4j.model.citygml.bridge.AbstractBoundarySurface.class)
                        || ClazzUtils.isSubclass(Class.forName(l.name()), FeatureProperty.class, org.citygml4j.model.citygml.tunnel.AbstractBoundarySurface.class);
            } catch (ClassNotFoundException e) {
                return false;
            }
        });
    }

    @Override
    protected Double compareMeasurements(Object leftMeasure, Object rightMeasure) {
        if (!(leftMeasure instanceof Measure) || !(rightMeasure instanceof Measure)) return null;
        Measure left = (Measure) leftMeasure;
        Measure right = (Measure) rightMeasure;
        // Documentation on uom
        // https://www.adv-online.de/icc/extdeu/binarywriterservlet?imgUid=62370a7d-753b-8a01-e1f4-351ec0023010&uBasVariant=11111111-1111-1111-1111-111111111111&isDownload=true
        /*
        Syntax:
        uom="urn:adv:uom:<short_hand>"

        UNIT                        SHORT HAND
        Metre                       m
        Millimetre                  mm
        Kilometre                   km
        Square metre                m2
        Cubic metre                 m3
        Degree, decimal (old)       grad
        Gradian, decimal            gon
        Radians                     rad
        10-5*m/s^2                  mgal
        m^2/s^2                     m2s2
         */

        // TODO Compare m2, etc.

        if (left.getUom().matches("^urn:adv:uom:(m|mm|km)$")) {
            if (!right.getUom().matches("^urn:adv:uom:(m|mm|km)$")) return null;
            double leftMetre = switch (left.getUom()) {
                case "urn:adv:uom:mm" -> left.getValue() / 1000;
                case "urn:adv:uom:m" -> left.getValue();
                case "urn:adv:uom:km" -> left.getValue() * 1000;
                default -> Double.NaN;
            };
            double rightMetre = switch (right.getUom()) {
                case "urn:adv:uom:mm" -> right.getValue() / 1000;
                case "urn:adv:uom:m" -> right.getValue();
                case "urn:adv:uom:km" -> right.getValue() * 1000;
                default -> Double.NaN;
            };
            double diff = leftMetre - rightMetre;
            if (Math.abs(diff) > config.MATCHER_TOLERANCE_LENGTHS) return diff;
            return (double) 0;
        }
        if (left.getUom().matches("^urn:adv:uom:(grad|gon|rad)$")) {
            if (!right.getUom().matches("^urn:adv:uom:(grad|gon|rad)$")) return null;
            double leftRad = switch (left.getUom()) {
                case "urn:adv:uom:grad" -> Math.toRadians(left.getValue());
                case "urn:adv:uom:gon" -> Math.toRadians(left.getValue() * 90. / 100.);
                case "urn:adv:uom:rad" -> left.getValue();
                default -> Double.NaN;
            };
            double rightRad = switch (right.getUom()) {
                case "urn:adv:uom:grad" -> Math.toRadians(right.getValue());
                case "urn:adv:uom:gon" -> Math.toRadians(right.getValue() * 90. / 100.);
                case "urn:adv:uom:rad" -> right.getValue();
                default -> Double.NaN;
            };
            double diff = leftRad - rightRad;
            if (Math.abs(diff) > config.MATCHER_TOLERANCE_ANGLES) return diff;
            return (double) 0;
        }

        // Same but without prefix
        if (left.getUom().matches("^(m|mm|km)$")) {
            if (!right.getUom().matches("^(m|mm|km)$")) return null;
            double leftMetre = switch (left.getUom()) {
                case "mm" -> left.getValue() / 1000;
                case "m" -> left.getValue();
                case "km" -> left.getValue() * 1000;
                default -> Double.NaN;
            };
            double rightMetre = switch (right.getUom()) {
                case "mm" -> right.getValue() / 1000;
                case "m" -> right.getValue();
                case "km" -> right.getValue() * 1000;
                default -> Double.NaN;
            };
            double diff = leftMetre - rightMetre;
            if (Math.abs(diff) > config.MATCHER_TOLERANCE_LENGTHS) return diff;
            return (double) 0;
        }
        if (left.getUom().matches("^(grad|gon|rad)$")) {
            if (!right.getUom().matches("^(grad|gon|rad)$")) return null;
            double leftRad = switch (left.getUom()) {
                case "grad" -> Math.toRadians(left.getValue());
                case "gon" -> Math.toRadians(left.getValue() * 90. / 100.);
                case "rad" -> left.getValue();
                default -> Double.NaN;
            };
            double rightRad = switch (right.getUom()) {
                case "grad" -> Math.toRadians(right.getValue());
                case "gon" -> Math.toRadians(right.getValue() * 90. / 100.);
                case "rad" -> right.getValue();
                default -> Double.NaN;
            };
            double diff = leftRad - rightRad;
            if (Math.abs(diff) > config.MATCHER_TOLERANCE_ANGLES) return diff;
            return (double) 0;
        }

        return null;
    }

    @Override
    protected ConvexPolygon3D toConvexPolygon3D(Object polygon, Precision.DoubleEquivalence precision) {
        if (!(polygon instanceof Polygon poly)) return null;
        if (!poly.isSetExterior()) return null;

        List<Vector3D> vectorList = new ArrayList<>(); // path must be closed (last = first)
        List<Double> points = poly.getExterior().getRing().toList3d();

        for (int i = 0; i < points.size(); i += 3) {
            //double x = Double.parseDouble(String.valueOf(ps.get(i)));
            //double y = Double.parseDouble(String.valueOf(ps.get(i + 1)));
            //double z = Double.parseDouble(String.valueOf(ps.get(i + 2)));
            double x = points.get(i);
            double y = points.get(i + 1);
            double z = points.get(i + 2);
            vectorList.add(Vector3D.of(x, y, z));
        }

        ConvexPolygon3D result = null;
        try {
            result = Planes.convexPolygonFromVertices(vectorList, precision);
        } catch (IllegalArgumentException e) {
            logger.warn(e.getMessage());
        }

        return result;
    }

    @Override
    protected double[] multiCurveBBox(Object multiCurve) {
        if (!(multiCurve instanceof MultiCurve mc)) return null;
        List<Double> points = new ArrayList<>();
        for (CurveProperty cp : mc.getCurveMember()) { // TODO getCurveMembers()?
            LineString ls = (LineString) cp.getCurve(); // TODO Other AbstractCurve types than LineString?
            List<Double> tmpPoints = ls.toList3d();
            points.addAll(tmpPoints);
        }
        double[] bbox = new double[6];
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;
        for (int i = 0; i < points.size(); i += 3) {
            double vX = points.get(i);
            double vY = points.get(i + 1);
            double vZ = points.get(i + 2);
            if (vX < minX) {
                minX = vX;
            } else if (vX > maxX) {
                maxX = vX;
            }
            if (vY < minY) {
                minY = vY;
            } else if (vY > maxY) {
                maxY = vY;
            }
            if (vZ < minZ) {
                minZ = vZ;
            } else if (vZ > maxZ) {
                maxZ = vZ;
            }
        }
        bbox[0] = minX;
        bbox[1] = minY;
        bbox[2] = minZ;
        bbox[3] = maxX;
        bbox[4] = maxY;
        bbox[5] = maxZ;
        return bbox;
    }

    @Override
    // Create a list of lines containing non-collinear points given in a multiCurve
    protected List<Line3D> multiCurveToLines3D(Object multiCurve, Precision.DoubleEquivalence precision) {
        if (!(multiCurve instanceof MultiCurve mc)) return null;
        List<Line3D> lines = new ArrayList<>();
        for (CurveProperty cp : mc.getCurveMember()) { // TODO getCurveMembers()?
            LineString ls = (LineString) cp.getCurve(); // TODO Other AbstractCurve types than LineString?
            List<Double> points = ls.toList3d();
            if (points.size() < 6) {
                logger.warn("LineString contains too few points");
                continue;
            }
            // First line from first two points
            Vector3D point1 = Vector3D.of(points.get(0), points.get(1), points.get(2));
            Vector3D point2 = Vector3D.of(points.get(3), points.get(4), points.get(5));
            int iPoint = 3;
            while (point1.eq(point2, precision) && iPoint <= points.size() - 3) {
                iPoint += 3;
                point2 = Vector3D.of(points.get(iPoint), points.get(iPoint + 1), points.get(iPoint + 2));
            }
            if (point1.eq(point2, precision)) {
                logger.warn("LineString contains only collinear points, ignoring");
                continue;
            }
            lines.add(Lines3D.fromPoints(point1, point2, precision));

            int iSaved = 3;
            for (int i = 6; i < points.size(); i += 3) {
                Vector3D point = Vector3D.of(points.get(i), points.get(i + 1), points.get(i + 2));
                Line3D lastLine = lines.get(lines.size() - 1);
                if (lastLine.contains(point)) continue; // Collinear point to the last line
                lines.add(Lines3D.fromPoints(
                        Vector3D.of(points.get(iSaved), points.get(iSaved + 1), points.get(iSaved + 2)),
                        point, precision));
                iSaved = i;
            }
        }
        return lines;
    }

    @Override
    protected boolean isMultiCurveContainedInLines3D(Object multiCurve, List<Line3D> lines, Precision.DoubleEquivalence precision) {
        if (!(multiCurve instanceof MultiCurve mc)) return false;
        for (CurveProperty cp : mc.getCurveMember()) { // TODO getCurveMembers()?
            LineString ls = (LineString) cp.getCurve(); // TODO Other AbstractCurve types than LineString?
            List<Double> points = ls.toList3d();
            for (int i = 0; i < points.size(); i += 3) {
                boolean contained = false;
                for (Line3D line : lines) {
                    Vector3D point = Vector3D.of(points.get(i), points.get(i + 1), points.get(i + 2));
                    if (line.contains(point)) {
                        contained = true;
                        break;
                    }
                }
                if (!contained) return false;
            }
        }
        return true;
    }

    @Override
    protected MetricBoundarySurfaceProperty metricFromBoundarySurfaceProperty(
            Node node,
            Precision.DoubleEquivalence lengthPrecision,
            Precision.DoubleEquivalence anglePrecision
    ) {
        // Check types
        List<SurfaceProperty> sps;
        Class<?> surfaceType;
        int highestLOD;
        if (ClazzUtils.isInstanceOf(node, BoundarySurfaceProperty.class)) {
            BoundarySurfaceProperty bsp = (BoundarySurfaceProperty) toObject(node);
            AbstractBoundarySurface bs = bsp.getBoundarySurface();
            surfaceType = bs.getClass();
            if (bs.isSetLod4MultiSurface()) {
                sps = bs.getLod4MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 4;
            } else if (bs.isSetLod3MultiSurface()) {
                sps = bs.getLod3MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 3;
            } else if (bs.isSetLod2MultiSurface()) {
                sps = bs.getLod2MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 2;
            } else {
                logger.error("Building.BoundarySurfaceProperty, node id = {}, has no MultiSurface, ignoring", node.getElementId());
                return null;
            }
        } else if (ClazzUtils.isInstanceOf(node, org.citygml4j.model.citygml.bridge.BoundarySurfaceProperty.class)) {
            org.citygml4j.model.citygml.bridge.BoundarySurfaceProperty bsp = (org.citygml4j.model.citygml.bridge.BoundarySurfaceProperty) toObject(node);
            org.citygml4j.model.citygml.bridge.AbstractBoundarySurface bs = bsp.getBoundarySurface();
            surfaceType = bs.getClass();
            if (bs.isSetLod4MultiSurface()) {
                sps = bs.getLod4MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 4;
            } else if (bs.isSetLod3MultiSurface()) {
                sps = bs.getLod3MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 3;
            } else if (bs.isSetLod2MultiSurface()) {
                sps = bs.getLod2MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 2;
            } else {
                logger.error("Bridge.BoundarySurfaceProperty, node id = {}, has no MultiSurface, ignoring", node.getElementId());
                return null;
            }
        } else if (ClazzUtils.isInstanceOf(node, org.citygml4j.model.citygml.tunnel.BoundarySurfaceProperty.class)) {
            org.citygml4j.model.citygml.tunnel.BoundarySurfaceProperty bsp = (org.citygml4j.model.citygml.tunnel.BoundarySurfaceProperty) toObject(node);
            org.citygml4j.model.citygml.tunnel.AbstractBoundarySurface bs = bsp.getBoundarySurface();
            surfaceType = bs.getClass();
            if (bs.isSetLod4MultiSurface()) {
                sps = bs.getLod4MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 4;
            } else if (bs.isSetLod3MultiSurface()) {
                sps = bs.getLod3MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 3;
            } else if (bs.isSetLod2MultiSurface()) {
                sps = bs.getLod2MultiSurface().getMultiSurface().getSurfaceMember();
                highestLOD = 2;
            } else {
                logger.error("Tunnel.BoundarySurfaceProperty, node id = {}, has no MultiSurface, ignoring", node.getElementId());
                return null;
            }
        } else {
            logger.error("Node id = {} is not a BoundarySurfaceProperty, ignoring", node.getElementId());
            return null;
        }

        // Calculate bbox and normal vector for each surface (using its exterior)
        List<Vector3D> normals = new ArrayList<>();
        List<Vector3D[]> bboxes = new ArrayList<>();
        List<Vector3D> points = new ArrayList<>();
        for (SurfaceProperty sp : sps) {
            Polygon poly = (Polygon) sp.getSurface(); // TODO Other types than Polygon?
            List<Double> surfacePoints = poly.getExterior().getRing().toList3d();
            List<Vector3D> surfaceVectors = new ArrayList<>();
            for (int i = 0; i < surfacePoints.size(); i += 3) {
                Vector3D v = Vector3D.of(surfacePoints.get(i), surfacePoints.get(i + 1), surfacePoints.get(i + 2));
                surfaceVectors.add(v);
            }
            points.addAll(surfaceVectors);

            // Surface normal (normalized, with uniform direction -> independent of order of points)
            Vector3D surfaceNormal = Planes.fromPoints(surfaceVectors, lengthPrecision).getNormal().normalize();
            /*if (sps.size() > 1) {
                // Only bring the normal vector to the same side of the plane in case of multiple surfaces
                if (anglePrecision.lt(surfaceNormal.angle(Vector3D.of(1, 1, 1)), 0)) {
                    surfaceNormal = surfaceNormal.negate();
                }
            }*/
            normals.add(surfaceNormal);

            // Bounding box
            Vector3D[] bbox = {Vector3D.min(surfaceVectors), Vector3D.max(surfaceVectors)};
            bboxes.add(bbox);
        }

        // Calculate the normal vector of all member surfaces
        Vector3D normal = Vector3D.ZERO;
        for (Vector3D n : normals) {
            normal = normal.add(n);
        }
        normal = normal.normalize();

        // Bounding box overall
        Vector3D[] bbox = {
                Vector3D.min(bboxes.stream().map(b -> b[0]).collect(Collectors.toList())),
                Vector3D.max(bboxes.stream().map(b -> b[1]).collect(Collectors.toList()))
        };

        return new MetricBoundarySurfaceProperty(surfaceType, normal, bbox, points, highestLOD);
    }

    @Override
    public void testImportAndExport(String importFilePath, String exportFilePath) {
        try (Transaction tx = graphDb.beginTx()) {
            // IMPORT

            Neo4jGraphRef cityModelRef = mapFileCityGML(importFilePath, 0, false);
            Node cityModelNode = cityModelRef.getRepresentationNode(tx);

            // EXPORT

            CityGMLContext ctx = CityGMLContext.getInstance();
            CityGMLBuilder builder = ctx.createCityGMLBuilder();
            CityGMLInputFactory in = builder.createCityGMLInputFactory();
            //in.parseSchema(new File("datasets/schemas/CityGML-NoiseADE-2_0_0.xsd"));

            CityGMLOutputFactory out = builder.createCityGMLOutputFactory(in.getSchemaHandler());
            ModuleContext moduleContext = new ModuleContext(); // v2.0.0
            FeatureWriteMode writeMode = FeatureWriteMode.SPLIT_PER_COLLECTION_MEMBER; // SPLIT_PER_COLLECTION_MEMBER;

            // set to true and check the differences
            boolean splitOnCopy = false;

            out.setModuleContext(moduleContext);
            out.setGMLIdManager(DefaultGMLIdManager.getInstance());
            out.setProperty(CityGMLOutputFactory.FEATURE_WRITE_MODE, writeMode);
            out.setProperty(CityGMLOutputFactory.SPLIT_COPY, splitOnCopy);

            //out.setProperty(CityGMLOutputFactory.EXCLUDE_FROM_SPLITTING, ADEComponent.class);

            CityGMLWriter writer = out.createCityGMLWriter(new File(exportFilePath), "utf-8");
            writer.setPrefixes(moduleContext);
            writer.setDefaultNamespace(moduleContext.getModule(CityGMLModuleType.CORE));
            writer.setIndentString("  ");
            writer.setHeaderComment("written by citygml4j",
                    "using a CityGMLWriter instance",
                    "Split mode: " + writeMode,
                    "Split on copy: " + splitOnCopy);

            CityModel cityModel = (CityModel) toObject(cityModelNode);
            writer.write(cityModel);
            writer.close();
            logger.info("CityGML file written to {}", exportFilePath);
            tx.commit();
        } catch (CityGMLBuilderException | CityGMLWriteException e) {
            throw new RuntimeException(e);
        }
    }

    // Match two bounding boxes in 3D
    private boolean matchBbox(
            double[] bbox1,
            double[] bbox2,
            Precision.DoubleEquivalence toleranceLength, // when comparing size length with 0
            double percentVolPass // matched only if overlapping volume over vol 1 and vol 2 is greater than this
    ) {
        if (bbox1 == null || bbox2 == null) return false;
        if (bbox1.length != 6 || bbox2.length != 6) return false;

        // Volume 1
        double vol1 = 0;
        double sizeX1 = bbox1[3] - bbox1[0];
        double sizeY1 = bbox1[4] - bbox1[1];
        double sizeZ1 = bbox1[5] - bbox1[2];
        if (toleranceLength.eq(sizeX1, 0)) vol1 = sizeY1 * sizeZ1;
        else if (toleranceLength.eq(sizeY1, 0)) vol1 = sizeX1 * sizeZ1;
        else if (toleranceLength.eq(sizeZ1, 0)) vol1 = sizeX1 * sizeY1;
        else vol1 = sizeX1 * sizeY1 * sizeZ1;

        // Volume 2
        double vol2 = 0;
        double sizeX2 = bbox2[3] - bbox2[0];
        double sizeY2 = bbox2[4] - bbox2[1];
        double sizeZ2 = bbox2[5] - bbox2[2];
        if (toleranceLength.eq(sizeX2, 0)) vol2 = sizeY2 * sizeZ2;
        else if (toleranceLength.eq(sizeY2, 0)) vol2 = sizeX2 * sizeZ2;
        else if (toleranceLength.eq(sizeZ2, 0)) vol2 = sizeX2 * sizeY2;
        else vol2 = sizeX2 * sizeY2 * sizeZ2;

        // Overlapping volume
        double overlap = 0;
        double x_overlap = Math.max(0, Math.min(bbox1[3], bbox2[3]) - Math.max(bbox1[0], bbox2[0]));
        double y_overlap = Math.max(0, Math.min(bbox1[4], bbox2[4]) - Math.max(bbox1[1], bbox2[1]));
        double z_overlap = Math.max(0, Math.min(bbox1[5], bbox2[5]) - Math.max(bbox1[2], bbox2[2]));
        if (toleranceLength.eq(x_overlap, 0)) overlap = y_overlap * z_overlap;
        else if (toleranceLength.eq(y_overlap, 0)) overlap = x_overlap * z_overlap;
        else if (toleranceLength.eq(z_overlap, 0)) overlap = x_overlap * y_overlap;
        else overlap = x_overlap * y_overlap * z_overlap;

        // Evaluate
        return overlap / vol1 > percentVolPass && overlap / vol2 > percentVolPass;
    }
}
