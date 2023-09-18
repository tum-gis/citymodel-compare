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

import java.lang.reflect.Field;
import java.util.Arrays;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class CycleTest {

    class A {
        private B b;

        public A(B b) {
            this.b = b;
        }

        public B getB() {
            return b;
        }
    }

    class B {
        private C c;

        public B(C c) {
            this.c = c;
        }

        public C getC() {
            return c;
        }
    }

    class C {
        private A a;

        public C() {
        }

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }

    private static Neo4jDB neo4jDB;
    private static GraphDatabaseService graphDb;
    private final static Logger logger = LoggerFactory.getLogger(CycleTest.class);

    @BeforeAll
    static void init() throws NoSuchFieldException, IllegalAccessException {
        neo4jDB = new Neo4jDB(new Neo4jDBConfig("config/base.conf"));
        neo4jDB.open();
        Field graphDbField = neo4jDB.getClass().getDeclaredField("graphDb");
        graphDbField.setAccessible(true);
        graphDb = (GraphDatabaseService) graphDbField.get(neo4jDB);
    }

    @Test
    void testCyclePrevention() {
        C c = new C();
        B b = new B(c);
        A a = new A(b);
        c.setA(a);
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(a);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertTrue(object instanceof A);
        assertEquals(a.getB(), b);
        assertEquals(a.getB().getC(), c);
        assertEquals(a.getB().getC().getA(), a);
    }

    @AfterAll
    static void close() throws InterruptedException {
        // Thread.sleep(1000 * 3600);
        neo4jDB.close();
    }
}
