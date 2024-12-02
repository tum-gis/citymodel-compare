package jgraf.base.reverseMapping;

import jgraf.citygml.CityGMLNeo4jDB;
import jgraf.citygml.CityGMLNeo4jDBConfig;
import jgraf.citygml.CityGMLNeo4jDBV2;
import jgraf.neo4j.Neo4jDB;
import jgraf.neo4j.factory.AuxEdgeTypes;
import org.citygml4j.model.citygml.building.Building;
import org.citygml4j.model.citygml.core.CityModel;
import org.citygml4j.model.citygml.core.CityObjectMember;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.Direction;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CityGMLv2Test {
    private static CityGMLNeo4jDB cityGMLNeo4jDB;
    private static GraphDatabaseService graphDb;
    private final static Logger logger = LoggerFactory.getLogger(CityGMLv2Test.class);

    @BeforeAll
    static void init() throws NoSuchFieldException, IllegalAccessException {
        cityGMLNeo4jDB = new CityGMLNeo4jDBV2(new CityGMLNeo4jDBConfig("config/citygmlv2.conf"));
        cityGMLNeo4jDB.open();
        Field graphDbField = Neo4jDB.class.getDeclaredField("graphDb");
        graphDbField.setAccessible(true);
        graphDb = (GraphDatabaseService) graphDbField.get(cityGMLNeo4jDB);
    }

    @Test
    void testCityGMLv2() {
        cityGMLNeo4jDB.mapFromConfig();
        try (Transaction tx = graphDb.beginTx()) {
            Node rootNode = cityGMLNeo4jDB.getRootMapperRef().getRepresentationNode(tx);
            List<Node> cityModelNodes = new ArrayList<>();
            rootNode.getRelationships(Direction.OUTGOING, AuxEdgeTypes.COLLECTION_MEMBER).forEach(rel -> {
                cityModelNodes.add(rel.getEndNode());
            });
            for (Node cityModelNode : cityModelNodes) {
                Object cityModel = cityGMLNeo4jDB.toObject(cityModelNode, null);
                assertTrue(cityModel instanceof CityModel);
                List<CityObjectMember> cityObjectMembers = ((CityModel) cityModel).getCityObjectMember();
                assertEquals(cityObjectMembers.size(), 1);
                assertTrue(cityObjectMembers.get(0).getCityObject() instanceof Building);
                Building building = (Building) cityObjectMembers.get(0).getCityObject();
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    @AfterAll
    static void close() throws InterruptedException {
        // Thread.sleep(1000 * 3600);
        cityGMLNeo4jDB.close();
    }
}
