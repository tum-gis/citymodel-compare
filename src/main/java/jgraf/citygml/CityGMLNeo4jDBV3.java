package jgraf.citygml;


import com.github.davidmoten.rtree.geometry.Geometries;
import jgraf.neo4j.Neo4jDB;
import jgraf.neo4j.Neo4jGraphRef;
import jgraf.neo4j.factory.AuxNodeLabels;
import jgraf.neo4j.factory.AuxPropNames;
import jgraf.neo4j.factory.EdgeTypes;
import jgraf.utils.BatchUtils;
import jgraf.utils.MetricBoundarySurfaceProperty;
import org.apache.commons.geometry.euclidean.threed.ConvexPolygon3D;
import org.apache.commons.geometry.euclidean.threed.line.Line3D;
import org.apache.commons.numbers.core.Precision;
import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.core.AbstractCityObject;
import org.citygml4j.core.model.core.AbstractFeature;
import org.citygml4j.core.model.core.CityModel;
import org.citygml4j.core.model.core.EngineeringCRSProperty;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.reader.*;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xmlobjects.gml.model.base.AbstractGML;
import org.xmlobjects.gml.model.base.AbstractInlineOrByReferenceProperty;
import org.xmlobjects.gml.model.base.AbstractReference;
import org.xmlobjects.gml.model.feature.BoundingShape;
import org.xmlobjects.gml.model.geometry.Envelope;
import org.xmlobjects.gml.model.geometry.primitives.Solid;
import org.xmlobjects.gml.util.EnvelopeOptions;

import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicLong;

public class CityGMLNeo4jDBV3 extends CityGMLNeo4jDB {
    protected final static Logger logger = LoggerFactory.getLogger(CityGMLNeo4jDBV3.class);

    public CityGMLNeo4jDBV3(CityGMLNeo4jDBConfig config) {
        super(config);
        uuidClasses = Set.of(AbstractFeature.class);
        idClasses = Set.of(AbstractGML.class);
        hrefClasses = Set.of(AbstractReference.class, EngineeringCRSProperty.class,
                AbstractInlineOrByReferenceProperty.class);
    }

    @Override
    protected Neo4jGraphRef mapFileCityGML(String filePath, int partitionIndex, boolean connectToRoot) {
        final Neo4jGraphRef[] cityModelRef = {null};
        try {
            CityGMLNeo4jDBConfig cityGMLConfig = (CityGMLNeo4jDBConfig) config;
            if (cityGMLConfig.CITYGML_VERSION != CityGMLVersion.v3_0) {
                logger.warn("Found CityGML version {}, expected version {}",
                        cityGMLConfig.CITYGML_VERSION, CityGMLVersion.v3_0);
            }
            dbStats.startTimer();
            CityGMLContext context = CityGMLContext.newInstance();
            CityGMLInputFactory in
                    = context.createCityGMLInputFactory()
                    .withChunking(ChunkOptions.defaults());
            Path file = Path.of(filePath);
            logger.info("Reading CityGML v3.0 file {} chunk-wise into main memory", filePath);

            // Ids of top-level features with no existing bounding shapes
            List<Neo4jGraphRef> topLevelNoBbbox = Collections.synchronizedList(new ArrayList<>());

            // Multi-threading
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            AtomicLong tlCount = new AtomicLong();
            try (CityGMLReader reader = in.createCityGMLReader(file)) {
                while (reader.hasNext()) {
                    CityGMLChunk chunk = reader.nextChunk();

                    executorService.submit((Callable<Void>) () -> {
                        AbstractFeature feature = chunk.build();
                        tlCount.getAndIncrement();
                        boolean toUpdateBboxTL = preProcessMapping(feature);
                        Neo4jGraphRef graphRef = (Neo4jGraphRef) this.map(feature,
                                AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex);
                        postProcessMapping(toUpdateBboxTL, feature, graphRef, partitionIndex, topLevelNoBbbox);
                        logger.info("Mapped {} top-level features", tlCount.get());

                        if (feature instanceof CityModel) {
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
                        return null;
                    });
                }
            }

            Neo4jDB.finishThreads(executorService, config.MAPPER_CONCURRENT_TIMEOUT);
            dbStats.stopTimer("Map input file [" + partitionIndex + "]");

            dbStats.startTimer();
            setIndexesIfNew();
            resolveXLinks(resolveLinkRules(), correctLinkRules(), partitionIndex);
            dbStats.stopTimer("Resolve links of input file [" + partitionIndex + "]");

            dbStats.startTimer();
            logger.info("Calculate and map bounding boxes of top-level features");
            calcTLBbox(topLevelNoBbbox, partitionIndex);
            dbStats.stopTimer("Calculate and map bounding boxes of top-level features");

            logger.info("Finished mapping file {}", filePath);
        } catch (CityGMLContextException | CityGMLReadException e) {
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
                || aco.getGeometryInfo(true).hasImplicitGeometries()
                || aco.getGeometryInfo(true).hasLodImplicitGeometries();
    }

    @Override
    protected void calcTLBbox(List<Neo4jGraphRef> topLevelNoBbox, int partitionIndex) {
        if (topLevelNoBbox == null) return;
        ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
        List<List<Neo4jGraphRef>> batches = BatchUtils.toBatches(topLevelNoBbox, 10 * config.MATCHER_TOPLEVEL_BATCH_SIZE);
        batches.forEach(batch -> executorService.submit((Callable<Void>) () -> {
            try (Transaction tx = graphDb.beginTx()) {
                batch.forEach(graphRef -> {
                    // Calculate bounding shape
                    Node topLevelNode = graphRef.getRepresentationNode(tx);
                    AbstractCityObject aco = (AbstractCityObject) toObject(topLevelNode);
                    aco.computeEnvelope(EnvelopeOptions.defaults().setEnvelopeOnFeatures(true));
                    BoundingShape boundingShape = aco.getBoundedBy();
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

    @Override
    protected Node getTopLevelListNode(Node cityModelNode) {
        // TODO
        return null;
    }

    @Override
    protected boolean isCOMTopLevel(Node cityObjectMemberNode) {
        // TODO
        return false;
    }

    @Override
    protected boolean isTopLevel(Node node) {
        // TODO
        return false;
    }

    @Override
    protected boolean isTopLevel(Object object) {
        return object instanceof AbstractCityObject;
    }

    @Override
    protected String getCOMElementId(Transaction tx, Neo4jGraphRef topLevelRef) {
        // TODO
        return null;
    }

    /*
    @Override
    protected Node getAnchorNode(Transaction tx, Node node, Label anchor) {
        // TODO
        return null;
    }
    */

    @Override
    protected List<Label> skipLabelsForTopLevel() {
        return List.of(Label.label(Solid.class.getName()));
    }

    protected PriorityQueue<Map.Entry<String, Double>> findBestTopLevel(Transaction tx, Relationship leftRel, Node rightNode) {
        // TODO
        return null;
    }

    @Override
    protected Map.Entry<Node, DiffResult> findBest(Transaction tx, Relationship leftRel, Node rightParentNode) {
        // TODO
        return null;
    }

    @Override
    protected boolean isPartProperty(Node node) {
        // TODO
        return false;
    }

    @Override
    protected boolean isBoundarySurfaceProperty(Node node) {
        // TODO
        return false;
    }

    @Override
    protected Double compareMeasurements(Object leftMeasure, Object rightMeasure) {
        // TODO
        return null;
    }

    @Override
    protected ConvexPolygon3D toConvexPolygon3D(Object polygon, Precision.DoubleEquivalence precision) {
        // TODO
        return null;
    }

    @Override
    protected double[] multiCurveBBox(Object multiCurve) {
        return null;
    }

    @Override
    protected List<Line3D> multiCurveToLines3D(Object multiCurve, Precision.DoubleEquivalence precision) {
        // TODO
        return null;
    }

    @Override
    protected boolean isMultiCurveContainedInLines3D(Object multiCurve, List<Line3D> lines, Precision.DoubleEquivalence precision) {
        // TODO
        return false;
    }

    @Override
    protected MetricBoundarySurfaceProperty metricFromBoundarySurfaceProperty(Node node, Precision.DoubleEquivalence lengthPrecision, Precision.DoubleEquivalence anglePrecision) {
        // TODO
        return null;
    }

    @Override
    public void testImportAndExport(String importFilePath, String exportFilePath) {
        // TODO
    }
}
