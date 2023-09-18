package jgraf.base.reverseMapping;

import jgraf.citygml.CityGMLNeo4jDB;
import jgraf.citygml.CityGMLNeo4jDBConfig;
import jgraf.citygml.CityGMLNeo4jDBV3;
import jgraf.neo4j.Neo4jDB;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Field;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CityGMLv3Test {
    private static CityGMLNeo4jDB cityGMLNeo4jDB;
    private static GraphDatabaseService graphDb;
    private final static Logger logger = LoggerFactory.getLogger(CityGMLv3Test.class);

    @BeforeAll
    static void init() throws NoSuchFieldException, IllegalAccessException {
        cityGMLNeo4jDB = new CityGMLNeo4jDBV3(new CityGMLNeo4jDBConfig("config/citygmlv3.conf"));
        cityGMLNeo4jDB.open();
        Field graphDbField = Neo4jDB.class.getDeclaredField("graphDb");
        graphDbField.setAccessible(true);
        graphDb = (GraphDatabaseService) graphDbField.get(cityGMLNeo4jDB);
    }

    @Test
    void testCityGMLv3() {
        cityGMLNeo4jDB.mapFromConfig();
        /*
        try (Transaction tx = graphDb.beginTx()) {
            Node rootNode = cityGMLNeo4jDB.getRootMapperRef().getRepresentationNode(tx);
            Node cityModelNode = rootNode.getSingleRelationship(AuxEdgeTypes.COLLECTION_MEMBER, Direction.OUTGOING)
                    .getEndNode();
            Object cityModel = cityGMLNeo4jDB.toObject(cityModelNode);
            assertTrue(cityModel instanceof CityModel);
            List<AbstractCityObjectProperty> cityObjectMembers = ((CityModel) cityModel).getCityObjectMembers();
            assertEquals(cityObjectMembers.size(), 1);
            assertTrue(cityObjectMembers.get(0).getObject() instanceof Building);
            Building building = (Building) cityObjectMembers.get(0).getObject();
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        */
    }

    @AfterAll
    static void close() throws InterruptedException {
        // Thread.sleep(1000 * 3600);
        cityGMLNeo4jDB.close();
    }
}
