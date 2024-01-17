package jgraf.citygml;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import jgraf.neo4j.Neo4jGraphRef;
import jgraf.neo4j.factory.AuxNodeLabels;
import jgraf.neo4j.factory.AuxPropNames;
import jgraf.neo4j.factory.EdgeTypes;
import jgraf.neo4j.factory.PropNames;
import jgraf.utils.ClazzUtils;
import jgraf.utils.GeometryUtils;
import jgraf.utils.GraphUtils;
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
import org.citygml4j.model.citygml.building.BoundarySurfaceProperty;
import org.citygml4j.model.citygml.building.BuildingPartProperty;
import org.citygml4j.model.citygml.core.AbstractCityObject;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.citygml4j.model.citygml.generics.*;
import org.citygml4j.model.gml.base.AbstractGML;
import org.citygml4j.model.gml.base.AssociationByRepOrRef;
import org.citygml4j.model.gml.base.StringOrRef;
import org.citygml4j.model.gml.basicTypes.Measure;
import org.citygml4j.model.gml.feature.BoundingShape;
import org.citygml4j.model.gml.geometry.aggregates.MultiCurve;
import org.citygml4j.model.gml.geometry.primitives.*;
import org.citygml4j.model.gml.measures.Length;
import org.citygml4j.util.bbox.BoundingBoxOptions;
import org.citygml4j.xml.io.CityGMLInputFactory;
import org.citygml4j.xml.io.reader.*;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.*;
import java.util.concurrent.ExecutionException;
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
    protected void setBoundingShape(Object cityObject) {
        if (cityObject instanceof AbstractCityObject object) {
            object.setBoundedBy(object.calcBoundedBy(BoundingBoxOptions.defaults().useExistingEnvelopes(true)));
        }
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
                        Neo4jGraphRef graphRef = (Neo4jGraphRef) this.map(object,
                                AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex);
                        partitionMapPostProcessing(object, graphRef, partitionIndex, connectToRoot);
                        logger.info("Mapped {} top-level features", finalTlCount);

                        if (object instanceof CityModel) {
                            if (cityModelRef[0] != null)
                                throw new RuntimeException("Found multiple CityModel objects in one file");
                            cityModelRef[0] = graphRef;
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
            logger.info("Finished mapping file {}", filePath);
            dbStats.stopTimer("Map input file [" + partitionIndex + "]");

            dbStats.startTimer();
            setIndexesIfNew();
            resolveXLinks(resolveLinkRules(), correctLinkRules(), partitionIndex);
            dbStats.stopTimer("Resolve links of input file [" + partitionIndex + "]");
        } catch (CityGMLBuilderException | CityGMLReadException e) {
            throw new RuntimeException(e);
        }
        return cityModelRef[0];
    }

    @Override
    protected Class<?> getCityModelClass() {
        return CityModel.class;
    }

    @Override
    protected void partitionMapPostProcessing(Object chunk, Neo4jGraphRef graphRef, int partitionIndex, boolean connectToRoot) {
        if (chunk instanceof AbstractCityObject) {
            // Add top-level feature to RTree index
            BoundingShape boundingShape = ((AbstractCityObject) chunk).getBoundedBy();
            if (boundingShape == null) return;
            Envelope envelope = boundingShape.getEnvelope();
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
        } else if (connectToRoot && chunk instanceof CityModel) {
            //  Connect MAPPER root node with this CityModel
            connectCityModelToRoot(graphRef, Map.of(
                    AuxPropNames.COLLECTION_INDEX.toString(), partitionIndex,
                    AuxPropNames.COLLECTION_MEMBER_TYPE.toString(), CityModel.class.getName()
            ));
        }
    }

    protected Node getTopLevelListNode(Node cityModelNode) {
        return cityModelNode
                .getSingleRelationship(EdgeTypes.cityObjectMember, Direction.OUTGOING).getEndNode()
                .getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode();
    }

    @Override
    protected boolean isTopLevel(Node node) {
        return node.hasLabel(Label.label(CityObjectMember.class.getName()));
    }

    // Given a parent node and a relationship, find a corresponding match
    // TODO Same for CityGML v3
    @Override
    protected Map.Entry<Node, DiffResult> findBest(Transaction tx, Relationship leftRel, Node rightNode) {
        Node leftRelNode = leftRel.getEndNode();

        // Error tolerance for matching numerics and geometries
        Precision.DoubleEquivalence precision = Precision.doubleEquivalenceOfEpsilon(config.MATCHER_TOLERANCE_LENGTHS);
        Precision.DoubleEquivalence translationPrecision = Precision.doubleEquivalenceOfEpsilon(config.MATCHER_TRANSLATION_DISTANCE);
        Precision.DoubleEquivalence anglePrecision = Precision.doubleEquivalenceOfEpsilon(config.MATCHER_TOLERANCE_ANGLES);

        // Top-level features
        if (isTopLevel(leftRelNode)) {
            Node leftTLNode = leftRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode();
            int leftPartitionIndex = Integer.parseInt(StreamSupport
                    .stream(leftTLNode.getLabels().spliterator(), false)
                    .filter(label -> label.name().startsWith(AuxNodeLabels.__PARTITION_INDEX__.name()))
                    .collect(Collectors.toSet())
                    .iterator().next().name().replace(AuxNodeLabels.__PARTITION_INDEX__.name(), ""));
            int rightPartitionIndex = Integer.parseInt(StreamSupport
                    .stream(rightNode.getLabels().spliterator(), false)
                    .filter(label -> label.name().startsWith(AuxNodeLabels.__PARTITION_INDEX__.name()))
                    .collect(Collectors.toSet())
                    .iterator().next().name().replace(AuxNodeLabels.__PARTITION_INDEX__.name(), ""));
            String leftUuid = leftTLNode.getProperty(AuxPropNames.__UUID__.toString()).toString();
            double[] tmpBBox = GraphUtils.getBoundingBox(leftTLNode);
            Rectangle leftRectangle = Geometries.rectangle(
                    tmpBBox[0], tmpBBox[1],
                    tmpBBox[3], tmpBBox[4]
            );
            double leftArea = leftRectangle.area();
            double leftWidth = leftRectangle.x2() - leftRectangle.x1();
            double leftHeight = leftRectangle.y2() - leftRectangle.y1();
            AtomicReference<Double> maxOverlapRatio = new AtomicReference<>(config.MATCHER_TOLERANCE_SURFACES);
            AtomicReference<Double> minDistance = new AtomicReference<>(config.MATCHER_TRANSLATION_DISTANCE);
            AtomicReference<Neo4jGraphRef> rightRefOverlap = new AtomicReference<>(null);
            AtomicReference<Neo4jGraphRef> rightRefDistance = new AtomicReference<>(null);
            rtrees[rightPartitionIndex].search(leftRectangle).forEach(entry -> {
                Rectangle rightRectangle = (Rectangle) entry.geometry();

                // Check for overlapping
                double overlap = leftRectangle.intersectionArea(rightRectangle);
                double rightArea = rightRectangle.area();
                double overlapRatio = overlap / Math.sqrt(leftArea * rightArea);
                if (overlapRatio > maxOverlapRatio.get()) {
                    // Choose only top-level feature with the biggest overlapping footprint area
                    maxOverlapRatio.set(overlapRatio);
                    rightRefOverlap.set(entry.value());
                } else {
                    // Overlap is too small but may still be relevant in case of translation
                    // Also check for translation of same sized bboxes
                    double rightWidth = rightRectangle.x2() - rightRectangle.x1();
                    double rightHeight = rightRectangle.y2() - rightRectangle.y1();
                    if (precision.eq(leftWidth, rightWidth) && precision.eq(leftHeight, rightHeight)) {
                        double distance = leftRectangle.distance(rightRectangle);
                        if (distance < minDistance.get()) {
                            // Choose only top-level feature with the smallest distance between bboxes
                            // (in case of translation, since overlapping is too small)
                            minDistance.set(distance);
                            rightRefDistance.set(entry.value());
                        }
                    }
                }
            });
            if (rightRefOverlap.get() != null) {
                logger.debug("Found the best matching candidate for {} with {}% >= {}% overlapping area using RTree",
                        ClazzUtils.getSimpleClassName(leftTLNode),
                        Math.round(maxOverlapRatio.get() * 100),
                        Math.round(config.MATCHER_TOLERANCE_SURFACES * 100));
                Node rightTLNode = rightRefOverlap.get().getRepresentationNode(tx);
                // Return parent node
                return new AbstractMap.SimpleEntry<>(rightTLNode.getSingleRelationship(
                        EdgeTypes.object, Direction.INCOMING).getStartNode(),
                        new DiffResultGeo(
                                SimilarityLevel.SIMILAR_GEOMETRY,
                                maxOverlapRatio.get(),
                                // Most Solids are defined by using XLinks
                                // Do not match Solids (to avoid rematching their boundary surfaces)
                                // TODO Comment this line out if the input datasets do NOT use XLinks for Solids
                                Arrays.asList(Label.label(Solid.class.getName())) // TODO Label list to skip for top-level features
                        ));
            }
            if (rightRefDistance.get() != null) {
                logger.debug("Found the best matching candidate for {} with min distance {} <= {} allowed threshold",
                        ClazzUtils.getSimpleClassName(leftTLNode),
                        Math.round(minDistance.get()),
                        Math.round(config.MATCHER_TRANSLATION_DISTANCE));
                Node rightTLNode = rightRefDistance.get().getRepresentationNode(tx);
                // Return parent node
                return new AbstractMap.SimpleEntry<>(rightTLNode.getSingleRelationship(
                        EdgeTypes.object, Direction.INCOMING).getStartNode(),
                        // TODO Set to SIMILAR_GEOMETRY_TRANSLATED instead?
                        new DiffResultGeo(
                                SimilarityLevel.SIMILAR_GEOMETRY,
                                maxOverlapRatio.get(),
                                // Most Solids are defined by using XLinks
                                // Do not match Solids (to avoid rematching their boundary surfaces)
                                // TODO Comment this line out if the input datasets do NOT use XLinks for Solids
                                Arrays.asList(Label.label(Solid.class.getName())) // TODO Label list to skip for top-level features
                        ));
            }
            // Check for ObjectSplit changes (whether this old top-level feature has been split)
            Map<Neo4jGraphRef, Rectangle> partCandidates = new HashMap<>();
            rtrees[rightPartitionIndex].search(leftRectangle).forEach(entry -> {
                Rectangle rightRectangle = (Rectangle) entry.geometry();
                // Check for overlapping
                double overlap = leftRectangle.intersectionArea(rightRectangle);
                double rightArea = rightRectangle.area();
                double overlapRatio = overlap / Math.sqrt(leftArea * rightArea);
                if (overlapRatio > 0.2) { // TODO Define a config variable for this
                    // Choose top-level features that may have been split from the reference one
                    partCandidates.put(entry.value(), rightRectangle);
                }
            });
            if (!partCandidates.isEmpty()) { // Multiple overlapping candidates -> sum their area
                double sumRightArea = 0;
                for (Map.Entry<Neo4jGraphRef, Rectangle> e : partCandidates.entrySet()) {
                    sumRightArea += e.getValue().area();
                }
                if (sumRightArea / leftArea > config.MATCHER_TOLERANCE_SURFACES) {
                    logger.debug("Found the best matching split candidates for {} with {}% >= {}% overlapping area using RTree",
                            ClazzUtils.getSimpleClassName(leftTLNode),
                            Math.round(sumRightArea / leftArea * 100),
                            Math.round(config.MATCHER_TOLERANCE_SURFACES * 100));
                    // Return parent node
                    return new AbstractMap.SimpleEntry<>(null, // this first variable is not important
                            new DiffResultTopSplit(
                                    SimilarityLevel.SPLIT_TOPLEVEL,
                                    sumRightArea / leftArea,
                                    partCandidates.keySet().stream().toList() // this is important
                            ));
                }
            }
            return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
        }

        // BuildingPartProperties or Solid
        if (leftRelNode.hasLabel(Label.label(BuildingPartProperty.class.getName()))
                || leftRelNode.hasLabel(Label.label(Solid.class.getName()))) {
            boolean isBuildingPartProperty = leftRelNode.hasLabel(Label.label(BuildingPartProperty.class.getName()));
            double[] tmpLeftBBox = GraphUtils.getBoundingBox(isBuildingPartProperty ?
                    leftRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode()
                    : leftRelNode);
            RegionBSPTree3D leftRegion = GeometryUtils.toRegion3D(
                    List.of(tmpLeftBBox[0], tmpLeftBBox[1], tmpLeftBBox[2]),
                    List.of(tmpLeftBBox[3], tmpLeftBBox[4], tmpLeftBBox[5]),
                    precision
            );
            double leftVolume = leftRegion.getSize();
            Vector3D leftCentroid = leftRegion.getCentroid();

            AtomicReference<Double> maxOverlapRatio = new AtomicReference<>(config.MATCHER_TOLERANCE_SOLIDS);
            AtomicReference<Double> minCentroidTranslation = new AtomicReference<>(config.MATCHER_TRANSLATION_DISTANCE);
            AtomicBoolean centroidUsed = new AtomicBoolean(false);
            AtomicReference<Node> candidateRef = new AtomicReference<>();
            rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).stream()
                    .forEach(rightRel -> {
                        Node rightRelNode = rightRel.getEndNode();
                        double[] tmpRightBBox = GraphUtils.getBoundingBox(isBuildingPartProperty ?
                                rightRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode()
                                : rightRelNode);
                        RegionBSPTree3D rightRegion = GeometryUtils.toRegion3D(
                                List.of(tmpRightBBox[0], tmpRightBBox[1], tmpRightBBox[2]),
                                List.of(tmpRightBBox[3], tmpRightBBox[4], tmpRightBBox[5]),
                                precision
                        );
                        double rightVolume = rightRegion.getSize();
                        Vector3D rightCentroid = rightRegion.getCentroid();

                        // rightRegion will be altered
                        rightRegion.intersection(leftRegion);
                        double overlapSize = rightRegion.getSize();
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
                                // Choose the candidate with shortest distance
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
                                    null // TODO Label list to skip for solids
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
                                null // TODO Label list to skip for solids
                        ));
            }
            return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
        }

        // TODO BridgePartProperty, TunnelPartProperty

        // TODO Consider change in SRS/CRS?

        // TODO Aggregation surfaces (multisurfaces, composite surfaces)

        // Vector init norm = sqrt(3a^2) <= t => a <= t/sqrt(3)
        double maxAllowed = config.MATCHER_TRANSLATION_DISTANCE / Math.sqrt(3);

        // BoundarySurfaceProperties
        if (leftRelNode.hasLabel(Label.label(BoundarySurfaceProperty.class.getName()))) {
            // TODO BoundarySurfaceProperty of Bridge and Tunnel
            BoundarySurfaceProperty leftBsp = (BoundarySurfaceProperty) toObject(leftRelNode);
            double[] tmpLeftBBox = GraphUtils.getBoundingBox(
                    leftRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode());
            Vector3D leftCentroid = Vector3D.of(
                    0.5 * (tmpLeftBBox[0] + tmpLeftBBox[3]),
                    0.5 * (tmpLeftBBox[1] + tmpLeftBBox[4]),
                    0.5 * (tmpLeftBBox[2] + tmpLeftBBox[5])
            );
            Plane leftPlane = boundarySurfacePropertyToPlane(leftBsp, precision);
            if (leftPlane == null) {
                logger.warn("Could not calculate plane for BoundarySurfaceProperty, node ID {}", leftRelNode.getElementId());
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            }
            Vector3D.Unit leftNormal = leftPlane.getNormal();

            Node candidate = null;
            Vector3D minTranslation = Vector3D.of(maxAllowed, maxAllowed, maxAllowed);
            double minTranslationNorm = config.MATCHER_TRANSLATION_DISTANCE;
            for (Relationship rightRel : rightNode.getRelationships(Direction.OUTGOING, leftRel.getType())) {
                Node rightRelNode = rightRel.getEndNode();
                BoundarySurfaceProperty rightBsp = (BoundarySurfaceProperty) toObject(rightRelNode);
                double[] tmpRightBBox = GraphUtils.getBoundingBox(
                        rightRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode());
                Vector3D rightCentroid = Vector3D.of(
                        0.5 * (tmpRightBBox[0] + tmpRightBBox[3]),
                        0.5 * (tmpRightBBox[1] + tmpRightBBox[4]),
                        0.5 * (tmpRightBBox[2] + tmpRightBBox[5])
                );
                Plane rightPlane = boundarySurfacePropertyToPlane(rightBsp, precision);
                if (rightPlane == null) {
                    logger.warn("Could not calculate plane for BoundarySurfaceProperty, node ID {}", rightRelNode.getElementId());
                    continue;
                }
                Vector3D.Unit rightNormal = rightPlane.getNormal();

                // Check for surface orientation
                double angle = leftNormal.angle(rightNormal);
                if (anglePrecision.eqZero(angle)) {
                    logger.debug("Found {} with matching orientation (angle {}), checking for translation...",
                            ClazzUtils.getSimpleClassName(leftRelNode), angle);
                    // Then pick min centroid translation
                    Vector3D translation = rightCentroid.subtract(leftCentroid);
                    if (translation.eq(Vector3D.ZERO, precision)) {
                        // Zero translation
                        logger.debug("Found the best matching candidate for {} without translation",
                                ClazzUtils.getSimpleClassName(leftRelNode));
                        return new AbstractMap.SimpleEntry<>(rightRelNode,
                                new DiffResultGeo(
                                        SimilarityLevel.SIMILAR_GEOMETRY,
                                        1,
                                        null // TODO Label list to skip for polygons
                                ));
                    }
                    // Non-zero translation
                    // Pick one with the least translation
                    double translationNorm = translation.norm();
                    if (translationNorm < minTranslationNorm) {
                        minTranslation = translation;
                        minTranslationNorm = translationNorm;
                        candidate = rightRelNode;
                    }
                }
            }

            if (candidate != null) {
                logger.debug(
                        "Found the best matching candidate for {} with translation {}",
                        ClazzUtils.getSimpleClassName(leftRelNode),
                        minTranslationNorm);
                return new AbstractMap.SimpleEntry<>(candidate,
                        new DiffResultGeo(
                                SimilarityLevel.SIMILAR_GEOMETRY,
                                -minTranslationNorm,
                                null // TODO Label list to skip for BoundarySurfaceProperty
                        ));
            }
            return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
        }

        // SurfaceProperties that contain Polygons
        // TODO Support other geometries than Polygons (such as CompositeSurface)?
        if (leftRelNode.hasLabel(Label.label(SurfaceProperty.class.getName()))
                && leftRelNode.hasRelationship(Direction.OUTGOING, EdgeTypes.object)
                && leftRelNode.getSingleRelationship(EdgeTypes.object, Direction.OUTGOING).getEndNode()
                .hasLabel(Label.label(Polygon.class.getName()))) {
            SurfaceProperty leftSurfaceProperty = (SurfaceProperty) toObject(leftRelNode);
            Polygon leftPolygon = (Polygon) leftSurfaceProperty.getObject();

            // Using bbox for convex and non-convex polygons
            double[] tmpLeftBBox = polygonBBox(leftPolygon);
            Vector3D leftSizes = Vector3D.of(
                    tmpLeftBBox[3] - tmpLeftBBox[0],
                    tmpLeftBBox[4] - tmpLeftBBox[1],
                    tmpLeftBBox[5] - tmpLeftBBox[2]
            );
            Vector3D leftCentroid = Vector3D.of(
                    0.5 * (tmpLeftBBox[0] + tmpLeftBBox[3]),
                    0.5 * (tmpLeftBBox[1] + tmpLeftBBox[4]),
                    0.5 * (tmpLeftBBox[2] + tmpLeftBBox[5])
            );

            List<Double> leftPoints = toDoubleList(leftPolygon.getExterior().getRing().toList3d());
            Node candidate = null;
            Vector3D minTranslation = Vector3D.of(maxAllowed, maxAllowed, maxAllowed);
            double minTranslationNorm = config.MATCHER_TRANSLATION_DISTANCE;
            for (Relationship rightRel : rightNode.getRelationships(Direction.OUTGOING, leftRel.getType())) {
                Node rightRelNode = rightRel.getEndNode();
                SurfaceProperty rightSurfaceProperty = (SurfaceProperty) toObject(rightRelNode);
                Polygon rightPolygon = (Polygon) rightSurfaceProperty.getObject(); // TODO Other geometries than Polygons?
                double[] tmpRightBBox = polygonBBox(rightPolygon);
                Vector3D rightSizes = Vector3D.of(
                        tmpRightBBox[3] - tmpRightBBox[0],
                        tmpRightBBox[4] - tmpRightBBox[1],
                        tmpRightBBox[5] - tmpRightBBox[2]
                );
                Vector3D rightCentroid = Vector3D.of(
                        0.5 * (tmpRightBBox[0] + tmpRightBBox[3]),
                        0.5 * (tmpRightBBox[1] + tmpRightBBox[4]),
                        0.5 * (tmpRightBBox[2] + tmpRightBBox[5])
                );

                // First, test if same size
                if (leftSizes.eq(rightSizes, precision)) {
                    Vector3D translation = rightCentroid.subtract(leftCentroid);
                    if (translation.eq(Vector3D.ZERO, precision)) {
                        // Zero translation -> Exact geometric match
                        // TODO Additionally check if all points of one polyon are contained in the other?
                        logger.debug("Found the best matching candidate for {} without translation",
                                ClazzUtils.getSimpleClassName(leftRelNode));
                        return new AbstractMap.SimpleEntry<>(rightRelNode,
                                new DiffResultGeo(
                                        SimilarityLevel.SIMILAR_GEOMETRY,
                                        1,
                                        Arrays.asList(Label.label(LinearRing.class.getName())) // TODO Label list to skip for polygons
                                ));
                    }
                    // Non-zero translation
                    // Test whether the translation is correct by
                    // translating the left polygon and compare it with the right polygon
                    Matrix translationMatrix = new Matrix(new double[][]{
                            {1, 0, 0, translation.getX()},
                            {0, 1, 0, translation.getY()},
                            {0, 0, 1, translation.getZ()},
                            {0, 0, 0, 1}
                    });
                    int k = 0;
                    for (; k < leftPoints.size(); k += 3) {
                        org.citygml4j.geometry.Point p = new org.citygml4j.geometry.Point(
                                leftPoints.get(k), leftPoints.get(k + 1), leftPoints.get(k + 2));
                        p.transform3D(translationMatrix);
                        // TODO Check if this translation is along only one axis (side moving, vertical raise, etc.)
                        if (!(precision.gte(p.getX(), tmpRightBBox[0])
                                && precision.gte(p.getY(), tmpRightBBox[1])
                                && precision.gte(p.getZ(), tmpRightBBox[2])
                                && precision.lte(p.getX(), tmpRightBBox[3])
                                && precision.lte(p.getY(), tmpRightBBox[4])
                                && precision.lte(p.getZ(), tmpRightBBox[5]))
                        ) {
                            break;
                        }
                    }
                    if (k == leftPoints.size()) {
                        // All vertices of translated left polygon is contained in the right polygon
                        double translationNorm = translation.norm();
                        if (translationNorm < minTranslationNorm) {
                            // Search for the min non-zero translation
                            minTranslation = translation;
                            minTranslationNorm = translationNorm;
                            candidate = rightRelNode;
                        }
                    }
                } else {
                    // Same orientation, close spatial location, but different sizes
                    // TODO Classify further which size change this is
                    Vector3D sizeChange = rightSizes.subtract(leftSizes);
                    logger.debug("Detected a change in size of {}, magnitude {}",
                            ClazzUtils.getSimpleClassName(leftRelNode), sizeChange);
                    return new AbstractMap.SimpleEntry<>(rightRelNode,
                            new DiffResultGeoSize(sizeChange.toArray(),
                                    Arrays.asList(Label.label(LinearRing.class.getName())) // TODO Label list to skip for polygons
                            ));
                }
            }

            // Finally, check if candidate has been found
            if (candidate == null) return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
            logger.debug("Found the best matching candidate for {} with translation vector {}",
                    ClazzUtils.getSimpleClassName(leftRelNode), minTranslation);
            return new AbstractMap.SimpleEntry<>(candidate,
                    new DiffResultGeoTranslation(
                            minTranslation.toArray(),
                            Arrays.asList(Label.label(LinearRing.class.getName()))) // TODO Label list to skip for polygons
            );

            // TODO Further checks for rotation, etc. (remember to use continue; to skip other checks below)
        }

        // MultiCurves that contain LineStrings
        if (leftRelNode.hasLabel(Label.label(MultiCurve.class.getName()))) {
            MultiCurve leftMultiCurve = (MultiCurve) toObject(leftRelNode);
            double[] tmpLeftBbox = multiCurveBBox(leftMultiCurve);
            if (tmpLeftBbox == null) {
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
            List<Line3D> leftLines = multiCurveToLines3D(leftMultiCurve, precision);
            if (leftLines == null) {
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            }
            Node candidate = null;
            Vector3D minTranslation = Vector3D.of(maxAllowed, maxAllowed, maxAllowed);
            double minTranslationNorm = minTranslation.norm();
            for (Relationship rightRel : rightNode.getRelationships(Direction.OUTGOING, leftRel.getType())) {
                Node rightRelNode = rightRel.getEndNode();
                MultiCurve rightMultiCurve = (MultiCurve) toObject(rightRelNode);
                double[] tmpRightBbox = multiCurveBBox(rightMultiCurve);
                if (tmpRightBbox == null) {
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
                List<Line3D> rightLines = multiCurveToLines3D(rightMultiCurve, precision);
                if (rightLines == null) {
                    continue;
                }

                // First, test if same size
                if (leftSizes.eq(rightSizes, precision)) {
                    Vector3D translation = rightCentroid.subtract(leftCentroid);
                    if (translation.eq(Vector3D.ZERO, precision)) {
                        // Zero translation -> Proceed to check if all points of one curve are contained in the other
                        if (isMultiCurveContainedInLines3D(leftMultiCurve, rightLines, precision)
                                && isMultiCurveContainedInLines3D(rightMultiCurve, leftLines, precision)) {
                            logger.debug("Found the best matching candidate for {} without translation",
                                    ClazzUtils.getSimpleClassName(leftRelNode));
                            return new AbstractMap.SimpleEntry<>(rightRelNode,
                                    new DiffResultGeo(
                                            SimilarityLevel.SIMILAR_GEOMETRY,
                                            1,
                                            Arrays.asList(Label.label(LineString.class.getName())) // TODO Label list to skip for MultiCurves
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
                    if (isMultiCurveContainedInLines3D(leftMultiCurve, translatedRightLines, precision)
                            && isMultiCurveContainedInLines3D(rightMultiCurve, translatedLeftLines, precision)) {
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
                            Arrays.asList(Label.label(LineString.class.getName()))) // TODO Label list to skip for polygons
            );
        }

        // TODO Points
        if (leftRelNode.hasLabel(Label.label(Point.class.getName()))) {
            Point leftPoint = (Point) toObject(leftRelNode);
            AtomicReference<Node> candidateRef = null;
            AtomicReference<Double> minDistance = new AtomicReference<>(Double.MAX_VALUE);
            rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).stream()
                    .forEach(rightRel -> {
                        Node rightRelNode = rightRel.getEndNode();
                        Point rightPoint = (Point) toObject(rightRelNode);
                        List<Double> leftPos = leftPoint.getPos().getValue();
                        List<Double> rightPos = rightPoint.getPos().getValue();
                        double distance = 0;
                        for (int i = 0; i < leftPos.size(); i++) {
                            distance += Math.pow(leftPos.get(i) - rightPos.get(i), 2);
                        }
                        distance = Math.sqrt(distance);
                        if (distance <= config.MATCHER_TOLERANCE_LENGTHS) {
                            if (minDistance.get() > distance) {
                                minDistance.set(distance);
                                candidateRef.set(rightNode);
                            }
                        }
                    });
            if (candidateRef.get() == null) return new AbstractMap.SimpleEntry<>(null,
                    new DiffResult(SimilarityLevel.NONE, 0));
            return new AbstractMap.SimpleEntry<>(candidateRef.get(),
                    new DiffResultGeo(
                            SimilarityLevel.SIMILAR_GEOMETRY,
                            minDistance.get(),
                            null // TODO Label list to skip for points
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
        if (probeGen.size() > 0) {
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
                        if (leftObj instanceof IntAttribute) {
                            IntAttribute leftGen = (IntAttribute) leftObj;
                            IntAttribute rightGen = (IntAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && leftGen.getValue().equals(rightGen.getValue());
                        }
                        if (leftObj instanceof DoubleAttribute) {
                            DoubleAttribute leftGen = (DoubleAttribute) leftObj;
                            DoubleAttribute rightGen = (DoubleAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && precision.compare(leftGen.getValue(), rightGen.getValue()) == 0;
                        }
                        if (leftObj instanceof DateAttribute) {
                            DateAttribute leftGen = (DateAttribute) leftObj;
                            DateAttribute rightGen = (DateAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && leftGen.getValue().equals(rightGen.getValue());
                        }
                        if (leftObj instanceof MeasureAttribute) {
                            MeasureAttribute leftGen = (MeasureAttribute) leftObj;
                            MeasureAttribute rightGen = (MeasureAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && compareMeasurements(leftGen.getValue(), rightGen.getValue());
                        }
                        if (leftObj instanceof StringAttribute) {
                            StringAttribute leftGen = (StringAttribute) leftObj;
                            StringAttribute rightGen = (StringAttribute) rightObj;
                            return leftGen.getName().equals(rightGen.getName())
                                    && leftGen.getValue().equals(rightGen.getValue());
                        }
                        if (leftObj instanceof UriAttribute) {
                            UriAttribute leftGen = (UriAttribute) leftObj;
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
            if (filteredCandidates.size() >= 1) {
                logger.debug("Found {} exact candidates for generic attribute {}",
                        filteredCandidates.size(), leftObj.getName());
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
            if (filteredNamedCandidates.size() >= 1) {
                logger.debug("Found {} candidates for generic attribute of the same name {}",
                        filteredNamedCandidates.size(), leftObj.getName());
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
                        return compareMeasurements(leftMeasure, rightMeasure);
                    })
                    .toList();
            if (candidates.size() == 0) {
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

        // Non-geometric non-generic nodes -> Match using array / collection index
        String indexType;
        if (leftRel.hasProperty(AuxPropNames.ARRAY_INDEX.toString()))
            indexType = AuxPropNames.ARRAY_INDEX.toString();
        else if (leftRel.hasProperty(AuxPropNames.COLLECTION_INDEX.toString()))
            indexType = AuxPropNames.COLLECTION_INDEX.toString();
        else indexType = null;
        if (indexType != null) {
            final int index = Integer.parseInt(leftRel.getProperty(indexType).toString());
            Set<Relationship> rightRels = rightNode.getRelationships(Direction.OUTGOING, leftRel.getType()).stream()
                    .filter(r -> r.hasProperty(indexType)
                            && Integer.parseInt(r.getProperty(indexType).toString()) == index)
                    .collect(Collectors.toSet());
            if (rightRels.isEmpty())
                return new AbstractMap.SimpleEntry<>(null,
                        new DiffResult(SimilarityLevel.NONE, 0));
            if (rightRels.size() > 1)
                throw new RuntimeException(
                        "Found multiple relationships of the same " + indexType + " = " + index);
            // Found a node using its array/collection index
            return new AbstractMap.SimpleEntry<>(rightRels.iterator().next().getEndNode(),
                    new DiffResult(SimilarityLevel.SAME_LABELS, 0));
        }

        // Non-array and non-collection nodes -> Match by gmlid
        String id;
        if (leftRelNode.hasProperty(PropNames.id.toString()))
            id = leftRelNode.getProperty(PropNames.id.toString()).toString();
        else id = null;
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
            if (rightRels.size() > 1)
                throw new RuntimeException(
                        "Found multiple nodes of the same partition having the same ID = " + id);
            // Found a node using its array/collection index
            if (rightRels.size() == 1) {
                return new AbstractMap.SimpleEntry<>(rightRels.iterator().next().getEndNode(),
                        new DiffResultProp(SimilarityLevel.SAME_ID, 0, List.of("id")));
            }
        }

        // Non-indexed nodes -> Find match having the most common kvps
        /*
        if (leftRelNode.getAllProperties().size() > 0) {
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

    @Override
    protected boolean compareMeasurements(Object leftMeasure, Object rightMeasure) {
        if (!(leftMeasure instanceof Measure) || !(rightMeasure instanceof Measure)) return false;
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
            if (!right.getUom().matches("^urn:adv:uom:(m|mm|km)$")) return false;
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
            return Math.abs(leftMetre - rightMetre) <= config.MATCHER_TOLERANCE_LENGTHS;
        }
        if (left.getUom().matches("^urn:adv:uom:(grad|gon|rad)$")) {
            if (!right.getUom().matches("^urn:adv:uom:(grad|gon|rad)$")) return false;
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
            return Math.abs(leftRad - rightRad) <= config.MATCHER_TOLERANCE_ANGLES;
        }

        // Same but without prefix
        if (left.getUom().matches("^(m|mm|km)$")) {
            if (!right.getUom().matches("^(m|mm|km)$")) return false;
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
            return Math.abs(leftMetre - rightMetre) <= config.MATCHER_TOLERANCE_LENGTHS;
        }
        if (left.getUom().matches("^(grad|gon|rad)$")) {
            if (!right.getUom().matches("^(grad|gon|rad)$")) return false;
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
            return Math.abs(leftRad - rightRad) <= config.MATCHER_TOLERANCE_ANGLES;
        }

        return left.getUom().equals(right.getUom())
                && Math.abs(left.getValue() - right.getValue()) <= config.MATCHER_TOLERANCE_LENGTHS;
    }

    @Override
    protected ConvexPolygon3D toConvexPolygon3D(Object polygon, Precision.DoubleEquivalence precision) {
        if (!(polygon instanceof Polygon poly)) return null;
        if (!poly.isSetExterior()) return null;

        List<Vector3D> vectorList = new ArrayList<>(); // path must be closed (last = first)
        List<Double> points = toDoubleList(poly.getExterior().getRing().toList3d());

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
            List<Double> tmpPoints = toDoubleList(ls.toList3d());
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
            List<Double> points = toDoubleList(ls.toList3d());
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
            List<Double> points = toDoubleList(ls.toList3d());
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
    protected Plane boundarySurfacePropertyToPlane(Object boundarySurfaceProperty, Precision.DoubleEquivalence precision) {
        // TODO BoundarySurfaceProperty of Bridge and Tunnel
        if (!(boundarySurfaceProperty instanceof BoundarySurfaceProperty bsp)) return null;
        List<SurfaceProperty> sm = bsp.getBoundarySurface().getLod2MultiSurface() // TODO LOD3, LOD4
                .getMultiSurface().getSurfaceMember();
        List<Double> points = new ArrayList<>();
        for (SurfaceProperty sp : sm) {
            Polygon poly = (Polygon) sp.getSurface(); // TODO Other types than Polygon?
            points.addAll(toDoubleList(poly.getExterior().getRing().toList3d()));
        }
        List<Vector3D> vectors = new ArrayList<>();
        for (int i = 0; i < points.size(); i += 3) {
            Vector3D v = Vector3D.of(points.get(i), points.get(i + 1), points.get(i + 2));
            vectors.add(v);
        }
        try {
            return Planes.fromPoints(vectors, precision);
        } catch (IllegalArgumentException e) {
            logger.warn("Could not create plane from points: {} {}", Arrays.toString(vectors.toArray()), e.getMessage());
            return null;
        }
    }

    @Override
    protected double[] polygonBBox(Object polygon) {
        if (!(polygon instanceof Polygon poly)) return null;
        if (!poly.isSetExterior()) return null;
        List<Double> points = toDoubleList(poly.getExterior().getGeometry().toList3d());

        double[] bbox = new double[6];
        double minX = Double.MAX_VALUE;
        double minY = Double.MAX_VALUE;
        double minZ = Double.MAX_VALUE;
        double maxX = Double.MIN_VALUE;
        double maxY = Double.MIN_VALUE;
        double maxZ = Double.MIN_VALUE;
        for (int i = 0; i < points.size(); i += 3) {
            double vX = Double.parseDouble(points.get(i) + "");
            double vY = Double.parseDouble(points.get(i + 1) + "");
            double vZ = Double.parseDouble(points.get(i + 2) + "");
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

    // TODO This is used to convert list strings to list of doubles -> Check if this is still needed
    private List<Double> toDoubleList(List<Double> list) {
        List<Double> result = new ArrayList<>();
        for (int i = 0; i < list.size(); i++) {
            result.add(Double.parseDouble(list.get(i) + ""));
        }
        return result;
    }
}
