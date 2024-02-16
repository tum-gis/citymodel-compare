package jgraf.neo4j;

import jgraf.core.BaseDBConfig;
import jgraf.core.DBStats;
import jgraf.core.GraphDB;
import jgraf.core.GraphRef;
import jgraf.neo4j.diff.*;
import jgraf.neo4j.factory.AuxEdgeTypes;
import jgraf.neo4j.factory.AuxNodeLabels;
import jgraf.neo4j.factory.AuxPropNames;
import jgraf.neo4j.factory.NodeLabels;
import jgraf.utils.ClazzUtils;
import jgraf.utils.GraphUtils;
import org.apache.commons.lang3.ClassUtils;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.neo4j.dbms.api.DatabaseManagementService;
import org.neo4j.dbms.api.DatabaseManagementServiceBuilder;
import org.neo4j.graphdb.*;
import org.neo4j.graphdb.schema.Schema;
import org.neo4j.io.fs.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.jemos.podam.api.PodamFactory;
import uk.co.jemos.podam.api.PodamFactoryImpl;

import java.io.IOException;
import java.lang.invoke.MethodHandles;
import java.lang.reflect.*;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

public abstract class Neo4jDB implements GraphDB {
    protected Neo4jDBConfig config;
    protected DatabaseManagementService managementService;
    protected GraphDatabaseService graphDb;
    protected Neo4jGraphRef ROOT, ROOT_MAPPER, ROOT_MATCHER;
    protected final Set<Class<?>> excludeVertexClasses;
    protected final Set<String> excludeEdgeTypes;
    protected Set<Class<?>> mappedClassesSaved;
    protected Set<Class<?>> mappedClassesTmp;
    protected final DBStats dbStats;
    private final static Logger logger = LoggerFactory.getLogger(Neo4jDB.class);

    public Neo4jDB(Neo4jDBConfig config) {
        this.config = config;
        excludeVertexClasses = config.MAPPER_EXCLUDE_VERTEX_CLASSES;
        excludeEdgeTypes = config.MAPPER_EXCLUDE_EDGE_TYPES;
        mappedClassesSaved = ConcurrentHashMap.newKeySet();
        mappedClassesTmp = ConcurrentHashMap.newKeySet();
        dbStats = new DBStats();
    }

    @Override
    public void open() {
        // Clean previous database
        Path db = Path.of(config.DB_PATH);
        try {
            FileUtils.deleteDirectory(db);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }

        // Init database
        dbStats.startTimer();
        Path a = Path.of(config.NEO4J_PLUGIN_PATH);
        managementService = (new DatabaseManagementServiceBuilder(db))
                .loadPropertiesFromFile(Path.of(config.NEO4J_CONFIG_FILE))
                //.setConfig(GraphDatabaseSettings.auth_enabled, false)
                //.setConfig(BoltConnector.enabled, true)
                //.setConfig(BoltConnector.listen_address, new SocketAddress("0.0.0.0", 7687)) // 0.0.0.0 allows outside access (such as via Docker)
                //.setConfig(GraphDatabaseSettings.plugin_dir, Path.of(config.NEO4J_PLUGIN_PATH).toAbsolutePath())
                //.setConfig(GraphDatabaseSettings.procedure_unrestricted, List.of("apoc.*"))
                //.setConfig(GraphDatabaseSettings.procedure_allowlist, List.of("apoc.coll.*", "apoc.load.*"))
                .build();
        graphDb = managementService.database(config.DB_NAME);

        registerShutdownHook(managementService);
        logger.info("Opened neo4j database on " + config.DB_PATH);

        // Init root nodes
        try (Transaction tx = graphDb.beginTx()) {
            Node rootNode = tx.createNode(NodeLabels.__ROOT__);
            Node rootMapperNode = tx.createNode(NodeLabels.__ROOT_MAPPER__);
            Node rootMatcherNode = tx.createNode(NodeLabels.__ROOT_MATCHER__);

            ROOT = new Neo4jGraphRef(rootNode);
            ROOT_MAPPER = new Neo4jGraphRef(rootMapperNode);
            ROOT_MATCHER = new Neo4jGraphRef(rootMatcherNode);

            rootNode.createRelationshipTo(rootMapperNode, AuxEdgeTypes.MAPPER);
            rootNode.createRelationshipTo(rootMatcherNode, AuxEdgeTypes.MATCHER);

            rootMapperNode.setProperty(AuxPropNames.COLLECTION_SIZE.toString(), config.MAPPER_DATASET_PATHS.size());
            // TODO Matcher root node

            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
        dbStats.stopTimer("Initialize database");
    }

    public void setIndex(Class<?> vertexClass, String propName) {
        try (Transaction tx = graphDb.beginTx()) {
            // Create automatic indexing while creating nodes
            Schema schema = tx.schema();
            schema.indexFor(Label.label(vertexClass.getName())).on(propName).create();
            logger.info("Created index on {}.{}", vertexClass.getSimpleName(), propName);
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public void waitForIndexes() {
        try (Transaction tx = graphDb.beginTx()) {
            Schema schema = tx.schema();
            schema.getIndexes().forEach(index -> schema.awaitIndexOnline(index, 600, TimeUnit.SECONDS));
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public GraphRef map(Object source, String... partitionLabels) {
        Neo4jGraphRef neo4JGraphRef = null;
        try (Transaction tx = graphDb.beginTx()) { // One transaction per input object
            Node node = map(tx, source, new IdentityHashMap<>(), partitionLabels);
            neo4JGraphRef = new Neo4jGraphRef(node);
            tx.commit();
            // logger.info("Mapped {}", source.getClass().getSimpleName());
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
        return neo4JGraphRef;
    }

    private Node map(Transaction tx, Object source, IdentityHashMap<Object, Node> mapped, String... partitionLabels) throws IllegalAccessException {
        if (source == null) return null;
        Class<?> clazz = source.getClass();
        for (Class<?> cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
            if (excludeVertexClasses.contains(cl)) return null;
        }
        // logger.debug("Mapping {}", clazz.getSimpleName());

        // Calculate bounding box if available
        setBoundingShape(source);

        // Check if this object has been mapped before, if yes return this mapped node instead of creating a new one
        // The scope of this cycle detection check is only within the source object TODO
        Node mappedNode = mapped.get(source);
        if (mappedNode != null) return mappedNode;

        Node node = tx.createNode();
        mapped.put(source, node);
        if (!mappedClassesSaved.contains(clazz)) mappedClassesTmp.add(clazz);

        // Check if arrays
        if (clazz.isArray()) {
            node.addLabel(NodeLabels.__ARRAY__);
            node.setProperty(AuxPropNames.ARRAY_MEMBER_TYPE.toString(), clazz.getComponentType().getName());
            int size = 0;
            for (int i = 0; i < Array.getLength(source); i++) {
                if (Array.get(source, i) != null) {
                    size++;
                }
            }
            node.setProperty(AuxPropNames.ARRAY_SIZE.toString(), size); // Count only non-null elements
            int index = 0;
            for (int i = 0; i < Array.getLength(source); i++) {
                if (Array.get(source, i) == null) continue;
                if (ClazzUtils.isPrintable(Array.get(source, i).getClass())) {
                    // Store printable members as strings to avoid exploding number of nodes
                    node.setProperty(AuxPropNames.ARRAY_MEMBER + "[" + index + "]", Array.get(source, i).toString());
                } else {
                    // Map each array member as a subsequent node
                    Node vNode = map(tx, Array.get(source, i), mapped, partitionLabels);
                    if (vNode == null) continue;
                    // Edge properties
                    Relationship rel = node.createRelationshipTo(vNode, AuxEdgeTypes.ARRAY_MEMBER);
                    rel.setProperty(AuxPropNames.ARRAY_INDEX.toString(), index);
                }
                index++; // index ensures continuous indexing, while i may have gap due to placeholders in Java, etc.
            }
        }
        // Other object types
        else {
            node.addLabel(Label.label(clazz.getName()));
            // Set properties and relationships
            for (Class<?> cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
                Field[] fields = cl.getDeclaredFields();
                for (Field field : fields) {
                    if (excludeEdgeTypes.contains(field.getName())) continue;
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    String fieldName = field.getName();
                    Object fieldValue = FieldUtils.readField(field, source, true); // Primitives will be wrapped
                    if (fieldValue == null) continue;
                    if (ClazzUtils.isPrintable(fieldValue.getClass())) {
                        node.setProperty(fieldName, fieldValue.toString());
                        node.setProperty(fieldName + AuxPropNames.__TYPE__, fieldValue.getClass().getName());
                    } else {
                        Node vNode = map(tx, fieldValue, mapped, partitionLabels);
                        if (vNode == null) continue;
                        node.createRelationshipTo(vNode, RelationshipType.withName(fieldName));
                    }
                }
            }
        }

        if (partitionLabels != null) for (String label : partitionLabels) node.addLabel(Label.label(label));

        return node;
    }

    public Object toObject(Node node) {
        Object object = null;
        try {
            object = toObject(node, new HashMap<>());
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | InstantiationException |
                 IllegalAccessException | NoSuchFieldException e) {
            throw new RuntimeException(e);
        }
        // logger.info("Reverse-mapped {}", object.getClass().getName());
        return object;
    }

    private Object toObject(Node graphNode, HashMap<String, Object> reverseMapped) throws ClassNotFoundException, NoSuchMethodException, InvocationTargetException, InstantiationException, IllegalAccessException, NoSuchFieldException {
        if (graphNode == null) return null;

        // Check if this object has been mapped before, if yes return this mapped node instead of creating a new one
        // The scope of this cycle detection check is only within the source object TODO
        Object reverseMappedObject = reverseMapped.get(graphNode.getElementId());
        if (reverseMappedObject != null) return reverseMappedObject;

        // Retrieve labels
        Set<Label> labels = new HashSet<>();
        graphNode.getLabels().forEach(labels::add);
        if (labels.isEmpty()) return null;
        // logger.debug("Reverse-mapping {}", ClazzUtils.getSimpleClassName(graphNode));

        // Init return object
        Object object = null;

        // Check if arrays
        if (GraphUtils.labelContains(labels, NodeLabels.__ARRAY__)) {
            Class<?> componentType
                    = ClassUtils.getClass(graphNode.getProperty(AuxPropNames.ARRAY_MEMBER_TYPE.toString()).toString());
            // Class.forName(graphNode.getProperty(PropNames.ARRAY_MEMBER_TYPE.toString()).toString());
            int size = Integer.parseInt(graphNode.getProperty(AuxPropNames.ARRAY_SIZE.toString()).toString());
            object = Array.newInstance(componentType, size);
            // Immediately add object to reverseMapped BEFORE recursive calls
            reverseMapped.put(graphNode.getElementId(), object);
            // Check if array elements are of printable type or Object due to Java's way of handling generic types in runtime
            // In the latter case, if there are no outgoing edges, then the array elements are of (generic) printable types
            if (!graphNode.getRelationships(Direction.OUTGOING).stream().iterator().hasNext()) {
                // Array members are stored as properties
                Map<String, Object> properties = graphNode.getAllProperties();

                if (componentType.equals(Object.class)) {
                    // Check whether this is an array of Double, String, or Object
                    int keyCount = 0;
                    boolean allDoubles = true;
                    boolean allIntegers = true;
                    for (Map.Entry<String, Object> entry : properties.entrySet()) {
                        keyCount++;
                        if (!entry.getValue().toString().matches("[0-9]+[.,][0-9]+")) {
                            if (!entry.getValue().toString().matches("[0-9]+")) {
                                allDoubles = false;
                                allIntegers = false;
                                break;
                            }
                            allDoubles = false;
                        } else {
                            allIntegers = false;
                        }
                    }
                    if (allDoubles) {
                        componentType = Double.class;
                    } else if (allIntegers) {
                        componentType = Integer.class;
                    }
                }

                for (Map.Entry<String, Object> entry : properties.entrySet()) {
                    if (entry.getKey().matches(AuxPropNames.ARRAY_MEMBER + "\\[[0-9]+\\]")) {
                        int index = Integer.parseInt(entry.getKey()
                                .replace(AuxPropNames.ARRAY_MEMBER + "[", "")
                                .replace("]", ""));
                        Object arrayElement;
                        if (componentType.equals(Object.class)) {
                            // Convert this value to either Double or String
                            arrayElement = ClazzUtils.toPrintableObjectSimplified(entry.getValue().toString());
                        } else {
                            arrayElement = ClazzUtils.castPrintableObject(componentType, entry.getValue().toString());
                        }
                        Array.set(object, index, arrayElement);
                    }
                }
            } else {
                // Array members are stored using edges
                try (ResourceIterator<Relationship> it = graphNode.getRelationships(Direction.OUTGOING).iterator()) {
                    while (it.hasNext()) {
                        Relationship rel = it.next();
                        if (rel.getType().name().equals(AuxPropNames.ARRAY_MEMBER.toString())) {
                            Object vNode = toObject(rel.getEndNode(), reverseMapped);
                            if (vNode == null) continue;
                            int index = Integer.parseInt(rel.getProperty(AuxPropNames.ARRAY_INDEX.toString()).toString());
                            Array.set(object, index, componentType.cast(vNode));
                        }
                    }
                }
            }
        } else {
            // Other object types
            if (labels.stream().filter(l -> !AuxNodeLabels.isIn(l)).count() != 1)
                throw new RuntimeException("Each node must have only one class label");
            Class<?> clazz = Class.forName(labels.stream().filter(l -> !AuxNodeLabels.isIn(l)).iterator().next().name());
            // Determine type parameters (such as String in Set<String>)
            try {
                object = clazz.getDeclaredConstructor().newInstance();
            } catch (NoSuchMethodException e) {
                TypeVariable<? extends Class<?>>[] typeParameters = clazz.getTypeParameters();
                Class<?>[] genericTypes = new Class<?>[typeParameters.length];
                for (int i = 0; i < typeParameters.length; i++) {
                    // In cases where <T extends A, B, C>; A, B, C can be ONE class or interfaces
                    // -> Select the first type A to instantiate a placeholder object
                    genericTypes[i] = Class.forName(((Class<?>) typeParameters[i].getBounds()[0]).getName());
                }
                PodamFactory factory = new PodamFactoryImpl();
                object = factory.manufacturePojo(clazz, genericTypes);
            }
            // Immediately add object to reverseMapped BEFORE recursive calls
            reverseMapped.put(graphNode.getElementId(), object);
            for (Class<?> cl = clazz; cl != Object.class; cl = cl.getSuperclass()) {
                Field[] fields = cl.getDeclaredFields();
                for (Field field : fields) {
                    if (Modifier.isStatic(field.getModifiers())) continue;
                    if (Modifier.isFinal(field.getModifiers())) {
                        // Overwrite final
                        MethodHandles.privateLookupIn(Field.class, MethodHandles.lookup())
                                .findVarHandle(Field.class, "modifiers", int.class)
                                .set(field, field.getModifiers() & ~Modifier.FINAL);
                    }
                    // Overwrite private
                    field.setAccessible(true);
                    String fieldName = field.getName();
                    Object fieldValue = null;
                    if (graphNode.hasProperty(fieldName)) {
                        if (graphNode.hasProperty(fieldName + AuxPropNames.__TYPE__)) {
                            // If a type exists
                            fieldValue = ClazzUtils.castPrintableObject(Class.forName(
                                            graphNode.getProperty(fieldName + AuxPropNames.__TYPE__).toString()),
                                    graphNode.getProperty(fieldName).toString());
                        } else {
                            // Printable values stored as properties
                            fieldValue = ClazzUtils.toPrintableObjectSimplified(graphNode.getProperty(fieldName).toString()); // TODO Use toPrintableObject(...) instead?
                        }
                    } else if (graphNode.hasRelationship(Direction.OUTGOING, RelationshipType.withName(fieldName))) {
                        // Complex values stored using edges // TODO Multiple edges of same name?
                        Relationship rel = graphNode.getSingleRelationship(
                                RelationshipType.withName(fieldName), Direction.OUTGOING);
                        fieldValue = toObject(rel.getEndNode(), reverseMapped);
                        if (fieldValue == null) continue;
                    }
                    try {
                        field.set(object, fieldValue);
                    } catch (IllegalArgumentException e) {
                        // Could not set e.g. float to Double
                        field.set(object, ClazzUtils.castPrintableObject(field.getType(), fieldValue.toString()));
                    }
                }
            }
        }

        return object;
    }

    public void summarize() {
        // Mapped nodes
        if (!mappedClassesTmp.isEmpty()) throw new RuntimeException("Mapping still in progress");
        dbStats.startTimer();
        logger.info("|--> Retrieving node and label stats");
        Map<String, Long> mappedLabelCount = new HashMap<>();
        AtomicLong mappedNodeCount = new AtomicLong();
        mappedClassesSaved.stream()
                .filter(clazz -> !AuxNodeLabels.isIn(Label.label(clazz.getName())))
                .forEach(clazz -> fillCount(Label.label(clazz.getName()), mappedNodeCount, mappedLabelCount));
        dbStats.setMappedLabelCount(mappedLabelCount);
        dbStats.setMappedNodeCount(mappedNodeCount.get());

        // Nodes per partition
        Map<String, Long> partitionLabelCount = new HashMap<>();
        AtomicLong partitionNodeCount = new AtomicLong();
        for (int i = 0; i < config.MAPPER_DATASET_PATHS.size(); i++) {
            fillCount(Label.label(AuxNodeLabels.__PARTITION_INDEX__.name() + i),
                    partitionNodeCount, partitionLabelCount);
        }
        dbStats.setPartitionLabelCount(partitionLabelCount);
        dbStats.setPartitionNodeCount(partitionNodeCount.get());

        // Change nodes
        Map<String, Long> matchedLabelCount = new HashMap<>();
        AtomicLong matchedNodeCount = new AtomicLong();
        Set.of(
                InsertPropChange.class,
                DeletePropChange.class,
                UpdatePropChange.class,
                InsertNodeChange.class,
                DeleteNodeChange.class,
                TranslationChange.class,
                SizeChange.class,
                TopSplitChange.class
        ).forEach(clazz -> fillCount(Label.label(clazz.getName()), matchedNodeCount, matchedLabelCount));
        dbStats.setMatchedLabelCount(matchedLabelCount);
        dbStats.setMatchedNodeCount(matchedNodeCount.get());

        logger.info("-->| Retrieved node and label stats");
        dbStats.stopTimer("Retrieve node and label stats");
        logger.info(dbStats.toString());

        dbStats.exportChanges(graphDb, config.MATCHER_EXPORT_PATH);
        logger.info("Exported changes to {}", config.MATCHER_EXPORT_PATH);
    }

    private void fillCount(Label label, AtomicLong nodeCount, Map<String, Long> labelCount) {
        try (Transaction tx = graphDb.beginTx()) {
            tx.findNodes(label).forEachRemaining(node -> {
                nodeCount.getAndIncrement();
                Long labelCountValue = labelCount.get(label.name());
                if (labelCountValue != null) {
                    labelCount.put(label.name(), labelCountValue + 1);
                } else {
                    labelCount.put(label.name(), 1L);
                }
            });
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

    public void remainOpen() {
        logger.info("Neo4j database is now running via neo4j://localhost:7687");
    }

    @Override
    public void close() {
        // Shutdown database at the end (if it is not being published via hostname:port
        managementService.shutdown();
        logger.info("Closed neo4j database");
    }

    private static void registerShutdownHook(final DatabaseManagementService managementService) {
        // Registers a shutdown hook for the Neo4j instance so that it
        // shuts down nicely when the VM exits (even if you "Ctrl-C" the
        // running application).
        Runtime.getRuntime().addShutdownHook(new Thread(managementService::shutdown));
    }

    protected abstract void setBoundingShape(Object cityObject);

    public BaseDBConfig getConfig() {
        return config;
    }

    public Set<Class<?>> getExcludeVertexClasses() {
        return excludeVertexClasses;
    }

    public Set<String> getExcludeEdgeTypes() {
        return excludeEdgeTypes;
    }

    public Neo4jGraphRef getRootRef() {
        return ROOT;
    }

    public Neo4jGraphRef getRootMapperRef() {
        return ROOT_MAPPER;
    }

    public Neo4jGraphRef getRootMatcherRef() {
        return ROOT_MATCHER;
    }

    public static void finishThreads(ExecutorService executorService, long seconds) {
        logger.info("Waiting for all threads to finish");
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(seconds, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            logger.error("awaitTermination interrupted: {}\n{}", e.getMessage(), Arrays.toString(e.getStackTrace()));
            executorService.shutdownNow();
        }
        logger.info("All threads finished");
    }
}
