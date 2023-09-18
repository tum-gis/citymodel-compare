package jgraf.base.forwardMapping;

import jgraf.neo4j.Neo4jDB;
import jgraf.neo4j.Neo4jDBConfig;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class AggregationTest {
    private static Neo4jDB neo4jDB;
    private final static Logger logger = LoggerFactory.getLogger(AggregationTest.class);

    @BeforeAll
    static void init() {
        neo4jDB = new Neo4jDB(new Neo4jDBConfig("config/base.conf"));
        neo4jDB.open();
    }

    @Test
    void testArray() {
        String[] array = {"A", "B", "C"};
        neo4jDB.map(array);
    }

    @Test
    void testList() {
        List<String> list = new ArrayList<>();
        list.add("A");
        list.add("B");
        list.add("C");
        neo4jDB.map(list);
    }

    @Test
    void testHashMap() {
        HashMap<String, Integer> map = new HashMap<>(Map.of(
                "A", 0,
                "B", 1,
                "C", 2
        ));
        neo4jDB.map(map);
    }

    @AfterAll
    static void close() throws InterruptedException {
        // Thread.sleep(1000 * 3600);
        neo4jDB.close();
    }
}
