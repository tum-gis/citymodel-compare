package jgraf.citygml;


import com.github.davidmoten.rtree.geometry.Geometries;
import jgraf.neo4j.Neo4jGraphRef;
import jgraf.neo4j.factory.AuxNodeLabels;
import jgraf.neo4j.factory.AuxPropNames;
import org.apache.commons.geometry.euclidean.threed.ConvexPolygon3D;
import org.apache.commons.geometry.euclidean.threed.Plane;
import org.apache.commons.geometry.euclidean.threed.line.Line3D;
import org.apache.commons.numbers.core.Precision;
import org.citygml4j.core.model.CityGMLVersion;
import org.citygml4j.core.model.core.AbstractCityObject;
import org.citygml4j.core.model.core.AbstractFeature;
import org.citygml4j.core.model.core.CityModel;
import org.citygml4j.core.model.core.EngineeringCRSProperty;
import org.citygml4j.core.util.reference.DefaultReferenceResolver;
import org.citygml4j.xml.CityGMLContext;
import org.citygml4j.xml.CityGMLContextException;
import org.citygml4j.xml.reader.*;
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
import org.xmlobjects.gml.util.EnvelopeOptions;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;
import java.util.Set;
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

            // Multi-threading
            ExecutorService executorService = Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors());
            AtomicLong tlCount = new AtomicLong();
            try (CityGMLReader reader = in.createCityGMLReader(file)) {
                while (reader.hasNext()) {
                    executorService.submit((Callable<Void>) () -> {
                        AbstractFeature feature = reader.next();
                        tlCount.getAndIncrement();
                        Neo4jGraphRef graphRef = (Neo4jGraphRef) this.map(feature,
                                AuxNodeLabels.__PARTITION_INDEX__.name() + partitionIndex);
                        partitionMapPostProcessing(feature, graphRef, partitionIndex, connectToRoot);
                        logger.info("Mapped {} top-level features", tlCount.get());

                        if (feature instanceof CityModel) {
                            if (cityModelRef[0] != null)
                                throw new RuntimeException("Found multiple CityModel objects in one file");
                            cityModelRef[0] = graphRef;
                        }
                        return null;
                    });
                }
            }

            executorService.shutdown();
            try {
                if (!executorService.awaitTermination(config.MAPPER_CONCURRENT_TIMEOUT, TimeUnit.SECONDS)) {
                    executorService.shutdownNow();
                }
            } catch (InterruptedException e) {
                executorService.shutdownNow();
            }
            dbStats.stopTimer("Map input file [" + partitionIndex + "]");

            dbStats.startTimer();
            setIndexesIfNew();
            resolveXLinks(resolveLinkRules(), correctLinkRules(), partitionIndex);
            dbStats.stopTimer("Resolve links of input file [" + partitionIndex + "]");

            logger.info("Finished mapping file {}", filePath);
        } catch (CityGMLContextException | CityGMLReadException e) {
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

    @Override
    protected void setBoundingShape(Object cityObject) {
        if (cityObject instanceof org.xmlobjects.gml.model.feature.AbstractFeature c) {
            c.setBoundedBy(new BoundingShape(
                    c.computeEnvelope(EnvelopeOptions.defaults().reuseExistingEnvelopes(true))));
        }
    }

    @Override
    protected Node getTopLevelListNode(Node cityModelNode) {
        // TODO
        return null;
    }

    @Override
    protected boolean isTopLevel(Node node) {
        // TODO
        return false;
    }

    @Override
    protected Map.Entry<Node, DiffResult> findBest(Transaction tx, Relationship leftRel, Node rightParentNode) {
        // TODO
        return null;
    }

    @Override
    protected boolean compareMeasurements(Object leftMeasure, Object rightMeasure) {
        // TODO
        return false;
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
    protected Plane boundarySurfacePropertyToPlane(Object boundarySurfaceProperty, Precision.DoubleEquivalence precision) {
        // TODO
        return null;
    }

    @Override
    protected double[] polygonBBox(Object polygon) {
        return null;
    }
}
