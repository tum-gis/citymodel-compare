package jgraf.base.reverseMapping;

import jgraf.neo4j.Neo4jDB;
import jgraf.neo4j.Neo4jDBConfig;
import jgraf.neo4j.Neo4jGraphRef;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.neo4j.graphdb.GraphDatabaseService;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class AggregationTest {
    private static Neo4jDB neo4jDB;
    private static GraphDatabaseService graphDb;
    private final static Logger logger = LoggerFactory.getLogger(AggregationTest.class);

    @BeforeAll
    static void init() throws NoSuchFieldException, IllegalAccessException {
        neo4jDB = new Neo4jDB(new Neo4jDBConfig("config/base.conf"));
        neo4jDB.open();
        Field graphDbField = neo4jDB.getClass().getDeclaredField("graphDb");
        graphDbField.setAccessible(true);
        graphDb = (GraphDatabaseService) graphDbField.get(neo4jDB);
    }

    @Test
    void testStringArray() {
        String[] array = {"A", "B", "C"};
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(array);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertTrue(object instanceof String[]);
        for (int i = 0; i < array.length; i++) {
            assertEquals(Array.get(object, i), array[i]);
        }
    }

    @Test
    void testIntegerArray() {
        Integer[] array = {0, 1, 2};
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(array);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertTrue(object instanceof Integer[]);
        for (int i = 0; i < array.length; i++) {
            assertEquals(Array.get(object, i), array[i]);
        }
    }

    @Test
    void testPrimitiveIntArray() {
        int[] array = {0, 1, 2};
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(array);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertTrue(object.getClass().isArray());
        assertEquals(object.getClass().getComponentType(), int.class);
        for (int i = 0; i < array.length; i++) {
            assertEquals(Array.get(object, i), array[i]);
        }
    }

    @Test
    void testDoubleArray() {
        Double[] array = {(double) 0, 1.0, 2.0};
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(array);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertTrue(object.getClass().isArray());
        assertEquals(object.getClass().getComponentType(), Double.class);
        for (int i = 0; i < array.length; i++) {
            assertEquals(Array.get(object, i), array[i]);
        }
    }

    @Test
    void testPrimitiveDoubleArray() {
        double[] array = {0, 1.0, 2.0};
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(array);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertTrue(object.getClass().isArray());
        assertEquals(object.getClass().getComponentType(), double.class);
        for (int i = 0; i < array.length; i++) {
            assertEquals(Array.get(object, i), array[i]);
        }
    }

    @Test
    void testStringList() {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(list);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(object.getClass(), ArrayList.class);
        for (int i = 0; i < list.size(); i++) {
            assertEquals(((ArrayList<?>) object).get(i), list.get(i));
        }
    }

    @Test
    void testIntegerList() {
        List<Integer> list = new ArrayList<>();
        list.add(0);
        list.add(1);
        list.add(2);
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(list);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(object.getClass(), ArrayList.class);
        for (int i = 0; i < list.size(); i++) {
            assertEquals(((ArrayList<?>) object).get(i), list.get(i));
        }
    }

    @Test
    void testDoubleList() {
        List<Double> list = new ArrayList<>();
        list.add(0.0);
        list.add(1.0);
        list.add(2.0);
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(list);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(object.getClass(), ArrayList.class);
        for (int i = 0; i < list.size(); i++) {
            assertEquals(((ArrayList<?>) object).get(i), list.get(i));
        }
    }

    @Test
    @SuppressWarnings("unchecked")
    void testStringIntegerHashMap() {
        HashMap<String, Integer> map = new HashMap<>(Map.of(
                "A", 0,
                "B", 1,
                "C", 2
        ));
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(map);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(object.getClass(), HashMap.class);
        HashMap<String, Integer> objectCast = (HashMap<String, Integer>) object;
        assertEquals(objectCast.size(), map.size());
        assertEquals(objectCast, map);
        // TODO Do NOT use objectCast.get(key) to compare the values since this can sometimes return null due to rehashing
    }

    @Test
    @SuppressWarnings("unchecked")
    void testStringDoublerHashMap() {
        HashMap<String, Double> map = new HashMap<>(Map.of(
                "A", 0.0,
                "B", 1.0,
                "C", 2.0
        ));
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(map);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(object.getClass(), HashMap.class);
        HashMap<String, Double> objectCast = (HashMap<String, Double>) object;
        assertEquals(objectCast.size(), map.size());
        assertEquals(objectCast, map);
        // TODO Do NOT use objectCast.get(key) to compare the values since this can sometimes return null due to rehashing
    }

    @Test
    @SuppressWarnings("unchecked")
    void testStringStringHashMap() {
        HashMap<String, String> map = new HashMap<>(Map.of(
                "A", "a",
                "B", "b",
                "C", "c"
        ));
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(map);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(object.getClass(), HashMap.class);
        HashMap<String, String> objectCast = (HashMap<String, String>) object;
        assertEquals(objectCast.size(), map.size());
        assertEquals(objectCast, map);
        // TODO Do NOT use objectCast.get(key) to compare the values since this can sometimes return null due to rehashing
    }

    @Test
    @SuppressWarnings("unchecked")
    void testStringNumStringHashMap() {
        HashMap<String, String> map = new HashMap<>(Map.of(
                "A", "1",
                "B", "2",
                "C", "3"
        ));
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(map);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(object.getClass(), HashMap.class);
        HashMap<String, String> objectCast = (HashMap<String, String>) object;
        assertEquals(objectCast.size(), map.size());
        assertEquals(objectCast, map);
        // TODO Do NOT use objectCast.get(key) to compare the values since this can sometimes return null due to rehashing
    }

    @AfterAll
    static void close() throws InterruptedException {
        // Thread.sleep(1000 * 3600);
        neo4jDB.close();
    }
}
