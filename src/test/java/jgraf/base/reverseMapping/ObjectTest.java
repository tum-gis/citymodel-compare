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
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class Person {
    private int age;
    private final String name;

    public Person(int age, String name) {
        this.age = age;
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getName() {
        return name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Person)) return false;
        return age == ((Person) obj).age
                && name.equals(((Person) obj).name);
    }
}

class Student extends Person {
    private List<Exam> exams;

    public Student(int age, String name, List<Exam> exams) {
        super(age, name);
        this.exams = exams;
    }

    public List<Exam> getExams() {
        return exams;
    }

    public void setExams(List<Exam> exams) {
        this.exams = exams;
    }

    @Override
    public boolean equals(Object obj) {
        if (!super.equals(obj)) return false;
        if (exams.size() != ((Student) obj).exams.size()) return false;
        for (int i = 0; i < exams.size(); i++) {
            if (!(exams.get(i).equals(((Student) obj).exams.get(i)))) return false;
        }
        return true;
    }
}

class Exam {
    private String name;

    public Exam(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Exam)) return false;
        return name.equals(((Exam) obj).name);
    }
}

public class ObjectTest {
    private static Neo4jDB neo4jDB;
    private static GraphDatabaseService graphDb;
    private final static Logger logger = LoggerFactory.getLogger(ObjectTest.class);

    @BeforeAll
    static void init() throws NoSuchFieldException, IllegalAccessException {
        neo4jDB = new Neo4jDB(new Neo4jDBConfig("config/base.conf"));
        neo4jDB.open();
        Field graphDbField = neo4jDB.getClass().getDeclaredField("graphDb");
        graphDbField.setAccessible(true);
        graphDb = (GraphDatabaseService) graphDbField.get(neo4jDB);
    }

    @Test
    void testSimpleObject() {
        List<Exam> exams = new ArrayList<>();
        exams.add(new Exam("EXAM_1"));
        exams.add(new Exam("EXAM_2"));
        exams.add(new Exam("EXAM_3"));
        Student s = new Student(20, "John Doe", exams);
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(s);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(s, object);
    }

    @Test
    void testSimpleObjectInline() {
        Student s = new Student(20, "John Doe", Arrays.asList(
                new Exam("EXAM_1"),
                new Exam("EXAM_2"),
                new Exam("EXAM_3")
        ));
        Neo4jGraphRef neo4JGraphRef = (Neo4jGraphRef) neo4jDB.map(s);
        Object object = null;
        try (Transaction tx = graphDb.beginTx()) {
            Node graph = neo4JGraphRef.getRepresentationNode(tx);
            object = neo4jDB.toObject(graph);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        assertEquals(s, object);
    }

    @Test
    void testMultipleObjectsInline() {
        Student[] students = {
                new Student(20, "Apple", Arrays.asList(
                        new Exam("EXAM_1"),
                        new Exam("EXAM_2"),
                        new Exam("EXAM_3")
                )),
                new Student(18, "Banana", Arrays.asList(
                        new Exam("EXAM_2"),
                        new Exam("EXAM_3")
                )),
                new Student(25, "Cranberry", Arrays.asList(
                        new Exam("EXAM_1"),
                        new Exam("EXAM_2")
                ))
        };
        Neo4jGraphRef[] neo4jGraphRefs = new Neo4jGraphRef[students.length];
        for (int i = 0; i < neo4jGraphRefs.length; i++) {
            neo4jGraphRefs[i] = (Neo4jGraphRef) neo4jDB.map(students[i]);
        }
        for (int i = 0; i < neo4jGraphRefs.length; i++) {
            Object object = null;
            try (Transaction tx = graphDb.beginTx()) {
                Neo4jGraphRef neo4JGraphRef = neo4jGraphRefs[i];
                Node graph = neo4JGraphRef.getRepresentationNode(tx);
                object = neo4jDB.toObject(graph);
                tx.commit();
            } catch (Exception e) {
                logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
            }
            assertEquals(students[i], object);
        }
    }

    @AfterAll
    static void close() throws InterruptedException {
        // Thread.sleep(1000 * 3600);
        neo4jDB.close();
    }
}
