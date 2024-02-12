package jgraf.utils;

import jgraf.neo4j.diff.*;
import jgraf.neo4j.factory.AuxEdgeTypes;
import jgraf.neo4j.factory.AuxPropNames;
import org.neo4j.graphdb.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.stream.Collectors;

public class ChangeUtils {
    private enum _PropNames {
        CHANGE_TYPE("change_type"),
        REF("ref");

        private final String value;

        _PropNames(String value) {
            this.value = value;
        }
    }

    private final static Logger logger = LoggerFactory.getLogger(ChangeUtils.class);

    /*
    public static void setIndexes(GraphDatabaseService graphDb) {
        try (Transaction tx = graphDb.beginTx()) {
            // Create automatic indexing while creating nodes
            Schema schema = tx.schema();
            Class<?>[] classes = {
//                    InsertPropChange.class,
//                    DeletePropChange.class,
//                    UpdatePropChange.class,
//                    InsertNodeChange.class,
//                    DeleteNodeChange.class,
                    Change.class
            };
            for (Class<?> cl : classes) {
                schema.indexFor(Label.label(cl.getName())).create();
                logger.info("Created index on {}", cl.getSimpleName());
            }
            tx.commit();
        } catch (Exception e) {
            logger.error(e.getMessage() + "\n" + Arrays.toString(e.getStackTrace()));
        }
    }

     */

    public static void addChange(Transaction tx, Class<? extends Change> changeType,
                                 Map<AuxPropNames, Object> props, Map<AuxEdgeTypes, Node> nodes) {
        // Do not create a change node if an exact node like it already exists
        if (exists(changeType, props, nodes)) return;

        Node changeNode = tx.createNode(Label.label(changeType.getName()), Label.label(Change.class.getName()));
        changeNode.setProperty(_PropNames.CHANGE_TYPE.value, changeType.getSimpleName());
        if (props != null) {
            for (Map.Entry<AuxPropNames, Object> entry : props.entrySet()) {
                changeNode.setProperty(entry.getKey().toString(), entry.getValue());
            }
        }
        if (nodes != null) {
            for (Map.Entry<AuxEdgeTypes, Node> entry : nodes.entrySet()) {
                Node target = entry.getValue();
                Lock lock = tx.acquireWriteLock(target);
                changeNode.createRelationshipTo(target, entry.getKey());
                lock.release();
            }
        }
        // logger.debug("Detected {}", changeType.getSimpleName());
    }

    private static boolean exists(Class<? extends Change> changeType,
                                  Map<AuxPropNames, Object> props, Map<AuxEdgeTypes, Node> nodes) {
        if (nodes == null || nodes.isEmpty()) return false;
        // Take first node and check
        Map.Entry<AuxEdgeTypes, Node> entry = nodes.entrySet().iterator().next();
        if (!entry.getValue().hasRelationship(Direction.INCOMING, entry.getKey())) {
            return false;
        }
        // The change node, if exists, must be among nodes that have edges to entry.value() node
        Node changeNode = null;
        for (Relationship rel : entry.getValue().getRelationships(Direction.INCOMING, entry.getKey())) {
            if (!rel.getStartNode().hasLabel(Label.label(changeType.getName()))) continue;
            boolean jumped = false;
            for (Map.Entry<AuxEdgeTypes, Node> r : nodes.entrySet()) {
                if (!rel.getStartNode().hasRelationship(Direction.OUTGOING, r.getKey())) {
                    jumped = true;
                    break;
                }
                if (!rel.getStartNode().getSingleRelationship(r.getKey(), Direction.OUTGOING).getEndNode().equals(r.getValue())) {
                    jumped = true;
                    break;
                }
            }
            if (!jumped) {
                changeNode = rel.getStartNode();
                break;
            }
        }
        if (changeNode == null) return false;
        if (props != null) {
            for (Map.Entry<AuxPropNames, Object> prop : props.entrySet()) {
                if (!changeNode.hasProperty(prop.getKey().toString())) return false;
                if (!changeNode.getProperty(prop.getKey().toString()).equals(prop.getValue())) return false;
            }
        }

        return true;
    }

    public static String toString(Node changeNode) {
        StringBuilder result = new StringBuilder();
        if (changeNode.hasLabel(Label.label(InsertNodeChange.class.getName()))) {
            Node rightParentNode = changeNode.getSingleRelationship(
                    AuxEdgeTypes.RIGHT_PARENT, Direction.OUTGOING).getEndNode();
            Node rightNode = changeNode.getSingleRelationship(
                    AuxEdgeTypes.RIGHT_NODE, Direction.OUTGOING).getEndNode();
            Relationship rightRel = rightNode.getRelationships(Direction.INCOMING).stream()
                    .filter(r -> r.getStartNode().equals(rightParentNode))
                    .collect(Collectors.toSet()).iterator().next();
            result.append(String.format("""
                            %s\s
                            \t> Inserted node: %s
                            \t> From right parent node: %s
                            \t> Via relationship: %s
                            """,
                    InsertNodeChange.class.getSimpleName(),
                    GraphUtils.getStringLabels(rightNode),
                    GraphUtils.getStringLabels(rightParentNode),
                    rightRel.getType()));
        } else if (changeNode.hasLabel(Label.label(DeleteNodeChange.class.getName()))) {
            Node leftParentNode = changeNode.getSingleRelationship(
                    AuxEdgeTypes.TANDEM, Direction.OUTGOING).getEndNode();
            Node leftNode = changeNode.getSingleRelationship(
                    AuxEdgeTypes.LEFT_NODE, Direction.OUTGOING).getEndNode();
            Relationship leftRel = leftNode.getRelationships(Direction.INCOMING).stream()
                    .filter(r -> r.getStartNode().equals(leftParentNode))
                    .collect(Collectors.toSet()).iterator().next();
            result.append(String.format("""
                            %s\s
                            \t> Deleted node: %s
                            \t> From left parent node: %s
                            \t> Via relationship: %s
                            """,
                    DeleteNodeChange.class.getSimpleName(),
                    GraphUtils.getStringLabels(leftNode),
                    GraphUtils.getStringLabels(leftParentNode),
                    leftRel.getType()));
        } else if (changeNode.hasLabel(Label.label(InsertPropChange.class.getName()))) {
            Node leftNode = changeNode.getSingleRelationship(
                    AuxEdgeTypes.RIGHT_NODE, Direction.OUTGOING).getEndNode();
            String propName = changeNode.getProperty(AuxPropNames.PROPERTY_NAME.toString()).toString();
            String propValue = changeNode.getProperty(AuxPropNames.PROPERTY_VALUE.toString()).toString();
            result.append(String.format("""
                            %s\s
                            \t> Inserted property: %s = %s
                            \t> From right node: %s
                            """,
                    InsertPropChange.class.getSimpleName(),
                    propName, propValue,
                    GraphUtils.getStringLabels(leftNode)));
        } else if (changeNode.hasLabel(Label.label(DeletePropChange.class.getName()))) {
            Node rightNode = changeNode.getSingleRelationship(
                    AuxEdgeTypes.TANDEM, Direction.OUTGOING).getEndNode();
            String propName = changeNode.getProperty(AuxPropNames.PROPERTY_NAME.toString()).toString();
            String propValue = changeNode.getProperty(AuxPropNames.PROPERTY_VALUE.toString()).toString();
            result.append(String.format("""
                            %s\s
                            \t> Deleted property: %s = %s
                            \t> From left node: %s
                            """,
                    DeletePropChange.class.getSimpleName(),
                    propName, propValue,
                    GraphUtils.getStringLabels(rightNode)));
        } else if (changeNode.hasLabel(Label.label(UpdatePropChange.class.getName()))) {
            Node leftNode = changeNode.getSingleRelationship(
                    AuxEdgeTypes.TANDEM, Direction.OUTGOING).getEndNode();
            Node rightNode = changeNode.getSingleRelationship(
                    AuxEdgeTypes.RIGHT_NODE, Direction.OUTGOING).getEndNode();
            String propName = changeNode.getProperty(AuxPropNames.PROPERTY_NAME.toString()).toString();
            String leftPropValue = changeNode.getProperty(AuxPropNames.LEFT_PROPERTY_VALUE.toString()).toString();
            String rightPropValue = changeNode.getProperty(AuxPropNames.RIGHT_PROPERTY_VALUE.toString()).toString();
            result.append(String.format("""
                            %s\s
                            \t> Updated property: %s = %s ->  %s
                            \t> From left node: %s
                            \t> From right node: %s
                            """,
                    UpdatePropChange.class.getSimpleName(),
                    propName, leftPropValue, rightPropValue,
                    GraphUtils.getStringLabels(leftNode),
                    GraphUtils.getStringLabels(rightNode)));
        }
        return result.toString();
    }
}
