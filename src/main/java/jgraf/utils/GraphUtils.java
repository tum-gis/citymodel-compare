package jgraf.utils;

import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Rectangle;
import jgraf.neo4j.factory.*;
import org.citygml4j.model.common.child.ChildList;
import org.neo4j.graphdb.*;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.StreamSupport;

public class GraphUtils {
    public static void markGeomInvalid(Transaction tx, Node geomNode) {
        Lock lock = tx.acquireWriteLock(geomNode);
        geomNode.setProperty(AuxPropNames.GEOM_VALID.toString(), false);
        lock.release();
    }

    public static boolean isGeomValid(Node geomNode) {
        return !geomNode.hasProperty(AuxPropNames.GEOM_VALID.toString())
                || Boolean.parseBoolean(geomNode.getProperty(AuxPropNames.GEOM_VALID.toString()).toString());
    }

    public static double[] getBoundingBox(Node abstractCityObjectNode) { // Building or BuildingPart // TODO This is momentarily only in CityGML v2
        try {
            Node envelope = abstractCityObjectNode
                    .getSingleRelationship(EdgeTypes.boundedBy, Direction.OUTGOING).getEndNode()
                    .getSingleRelationship(EdgeTypes.envelope, Direction.OUTGOING).getEndNode();
            Node lowerCorner = envelope
                    .getSingleRelationship(EdgeTypes.lowerCorner, Direction.OUTGOING).getEndNode()
                    .getSingleRelationship(EdgeTypes.value, Direction.OUTGOING).getEndNode()
                    .getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode();
            Node upperCorner = envelope
                    .getSingleRelationship(EdgeTypes.upperCorner, Direction.OUTGOING).getEndNode()
                    .getSingleRelationship(EdgeTypes.value, Direction.OUTGOING).getEndNode()
                    .getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode();
            // TODO Compare classes Envelope and DirectPosition in CityGMl v2 and v3 here
            double iminX = Double.parseDouble(lowerCorner.getProperty(PropNames.ARRAY_MEMBER + "[0]").toString());
            double iminY = Double.parseDouble(lowerCorner.getProperty(PropNames.ARRAY_MEMBER + "[1]").toString());
            double iminZ = Double.parseDouble(lowerCorner.getProperty(PropNames.ARRAY_MEMBER + "[2]").toString());
            double imaxX = Double.parseDouble(upperCorner.getProperty(PropNames.ARRAY_MEMBER + "[0]").toString());
            double imaxY = Double.parseDouble(upperCorner.getProperty(PropNames.ARRAY_MEMBER + "[1]").toString());
            double imaxZ = Double.parseDouble(upperCorner.getProperty(PropNames.ARRAY_MEMBER + "[2]").toString());
            return new double[]{iminX, iminY, iminZ, imaxX, imaxY, imaxZ};
        } catch (NumberFormatException e) {
            throw new RuntimeException(e);
        }
    }

    public static Node getCollectionMemberNode(Node collectionNode, int memberIndex) {
        try (ResourceIterator<Relationship> it
                     = collectionNode.getRelationships(Direction.OUTGOING, AuxEdgeTypes.COLLECTION_MEMBER).iterator()) {
            while (it.hasNext()) {
                Relationship rel = it.next();
                Node memberNode = rel.getEndNode();
                if (rel.hasProperty(AuxPropNames.ARRAY_INDEX.toString())
                        && Integer.parseInt(rel.getProperty(AuxPropNames.ARRAY_INDEX.toString()).toString()) == memberIndex)
                    return memberNode;
                if (rel.hasProperty(AuxPropNames.COLLECTION_INDEX.toString())
                        && Integer.parseInt(rel.getProperty(AuxPropNames.COLLECTION_INDEX.toString()).toString()) == memberIndex)
                    return memberNode;
            }
        }
        return null;
    }

    public static Node clone(Transaction tx, Node node, boolean delete) {
        // Labels
        List<Label> ll = new ArrayList<>();
        node.getLabels().forEach(ll::add);
        Label[] labelArray = new Label[ll.size()];
        for (int i = 0; i < labelArray.length; i++) {
            labelArray[i] = ll.get(i);
        }
        Node clone = tx.createNode(labelArray);

        // Properties
        for (Map.Entry<String, Object> entry : node.getAllProperties().entrySet()) {
            clone.setProperty(entry.getKey(), entry.getValue());
        }

        // Relationships
        cloneRelationships(tx, node, clone, delete);

        if (delete) node.delete();

        return clone;
    }

    public static void cloneRelationships(Transaction tx, Node source, Node clone, boolean delete) {
        for (Relationship rel : source.getRelationships()) {
            Relationship cloneRel = null;
            boolean mergingArray = false;
            if (rel.getStartNode().equals(source)) {
                // node is a start node
                if (rel.getEndNode().hasLabel(Label.label(ChildList.class.getName()))) { // TODO ChildList is in CityGML 2.0
                    // (a)-[]->(ChildList)-[elementData]->(__ARRAY__ )->[ARRAY_MEMBER]->(b)
                    //         modCount,Size              ARRAY_SIZE
                    if (!clone.hasRelationship(Direction.OUTGOING, rel.getType())) {
                        Lock lockClone = tx.acquireWriteLock(clone);
                        Lock lockRel = tx.acquireWriteLock(rel);
                        cloneRel = clone.createRelationshipTo(rel.getEndNode(), rel.getType());
                        lockRel.release();
                        lockClone.release();
                    } else if (rel.getEndNode().hasLabel(Label.label(ChildList.class.getName()))) { // TODO ChildList is in CityGML 2.0
                        mergingArray = true;
                        Node sourceChildList = rel.getEndNode();
                        Node sourceArray = sourceChildList.getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode();
                        Node cloneChildList = clone.getSingleRelationship(rel.getType(), Direction.OUTGOING).getEndNode();
                        Node cloneArray = cloneChildList.getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).getEndNode();
                        Lock lockCloneArray = tx.acquireWriteLock(cloneArray);
                        AtomicInteger count = new AtomicInteger();
                        sourceArray.getRelationships(Direction.OUTGOING, AuxEdgeTypes.ARRAY_MEMBER).forEach(r -> {
                            cloneArray.createRelationshipTo(r.getEndNode(), r.getType());
                            Lock lockR = tx.acquireWriteLock(r);
                            if (delete) r.delete();
                            lockR.release();
                            count.getAndIncrement();
                        });
                        cloneArray.setProperty(AuxPropNames.ARRAY_SIZE.toString(),
                                Integer.parseInt(cloneArray.getProperty(AuxPropNames.ARRAY_SIZE.toString()).toString())
                                        + count.get());
                        Lock lockCloneChildList = tx.acquireWriteLock(cloneChildList);
                        cloneChildList.setProperty(AuxPropNames.modCount.toString(),
                                Integer.parseInt(cloneChildList.getProperty(AuxPropNames.modCount.toString()).toString())
                                        + count.get());
                        cloneChildList.setProperty(AuxPropNames.size.toString(),
                                Integer.parseInt(cloneChildList.getProperty(AuxPropNames.size.toString()).toString())
                                        + count.get());
                        lockCloneChildList.release();
                        lockCloneArray.release();
                        if (delete) {
                            Lock lockSourceChildList = tx.acquireWriteLock(sourceChildList);
                            sourceChildList.getSingleRelationship(EdgeTypes.elementData, Direction.OUTGOING).delete();

                            Lock lockSourceArray = tx.acquireWriteLock(sourceArray);
                            sourceArray.delete();
                            lockSourceArray.release();

                            Lock lockRel = tx.acquireWriteLock(rel);
                            rel.delete();
                            lockRel.release();

                            sourceChildList.delete();
                            lockSourceChildList.release();
                        }
                    }
                } else {
                    Lock lockClone = tx.acquireWriteLock(clone);
                    Lock lockRel = tx.acquireWriteLock(rel.getEndNode());
                    cloneRel = clone.createRelationshipTo(rel.getEndNode(), rel.getType());
                    lockRel.release();
                    lockClone.release();
                }
            } else {
                // node is an end node
                Lock lockStartNode = tx.acquireWriteLock(rel.getStartNode());
                Lock lockClone = tx.acquireWriteLock(clone);
                cloneRel = rel.getStartNode().createRelationshipTo(clone, rel.getType());
                lockClone.release();
                lockStartNode.release();
            }

            if (!mergingArray) {
                Lock lockCloneRel = tx.acquireWriteLock(cloneRel);
                for (Map.Entry<String, Object> entry : rel.getAllProperties().entrySet()) {
                    cloneRel.setProperty(entry.getKey(), entry.getValue());
                }
                lockCloneRel.release();

                if (delete) {
                    Lock lockRel = tx.acquireWriteLock(rel);
                    rel.delete();
                    lockRel.release();
                }
            }
        }
    }

    public static void delete(Node node) {
        node.getRelationships().forEach(Entity::delete);
        node.delete();
    }

    public static boolean isReachable(Node source, Node node) {
        return isReachable(source, node, new ArrayList<>());
    }

    private static boolean isReachable(Node source, Node node, List<Node> visited) {
        if (node == null) return false;
        if (node.equals(source)) return true;
        if (visited.contains(node)) return false;
        visited.add(node);
        // Traverse from node back to source is more efficient if source has a lot of child nodes
        for (Relationship rel : node.getRelationships(Direction.INCOMING)) {
            if (isReachable(source, rel.getStartNode(), visited)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isReachable(String sourceLabel, Node node) {
        return isReachable(sourceLabel, node, new ArrayList<>());
    }

    private static boolean isReachable(String sourceLabel, Node node, List<Node> visited) {
        if (node == null) return false;
        if (node.hasLabel(Label.label(sourceLabel))) return true;
        if (visited.contains(node)) return false;
        visited.add(node);
        // Traverse from node back to source is more efficient if source has a lot of child nodes
        for (Relationship rel : node.getRelationships(Direction.INCOMING)) {
            if (isReachable(sourceLabel, rel.getStartNode(), visited)) {
                return true;
            }
        }
        return false;
    }

    public static boolean labelContains(Collection<Label> labels, Label label) {
        return (labels.stream().anyMatch(l -> label.name().startsWith(l.name()) || label.name().endsWith(l.name())));
    }

    public static String shortestPathString(Node start, Label end) {
        StringBuilder sb = new StringBuilder();
        sb.append("Context:\n");
        List<Map.Entry<String, String>> path = shortestPath(start, end, new ArrayList<>());
        if (path == null) return null;
        path.forEach(entry -> {
            sb.append("\t> ");
            try {
                Class<?> cl = Class.forName(entry.getKey().replace("[", "").replace("]", ""));
                Class<?> tmp = cl;
                sb.append("[");
                while (!tmp.equals(Object.class)) {
                    sb.append(tmp.getSimpleName());
                    tmp = tmp.getSuperclass();
                    if (!tmp.equals(Object.class)) sb.append(" / ");
                }
                sb.append("]");
            } catch (ClassNotFoundException e) {
                sb.append(entry.getKey());
            }
            if (entry.getValue() != null)
                sb.append("\n\t\t.").append(PropNames.id).append(" = ").append(entry.getValue());
            sb.append("\n");
        });
        sb.append("\n");
        return sb.toString();
    }

    public static List<Map.Entry<String, String>> shortestPath(Node start, Label end) {
        return shortestPath(start, end, new ArrayList<>());
    }

    private static List<Map.Entry<String, String>> shortestPath(Node start, Label end,
                                                                List<Map.Entry<String, String>> current) {
        // Add the simple label + id (if available) of this node to the current path
        Set<String> labels = StreamSupport
                .stream(start.getLabels().spliterator(), false)
                .filter(label -> !AuxNodeLabels.isIn(label))
                .map(Label::name)
                .collect(Collectors.toSet());
        if (start.hasProperty(PropNames.id.toString()))
            current.add(new AbstractMap.SimpleEntry<>(
                    labels.toString(),
                    start.getProperty(PropNames.id.toString()).toString()
            ));
        else
            current.add(new AbstractMap.SimpleEntry<>(
                    labels.toString(),
                    null
            ));

        // Path reached
        if (start.hasLabel(end)) return current;

        // Unreachable path
        if (start.getDegree(Direction.INCOMING) == 0) return null;

        List<Map.Entry<String, String>> shortest = current;
        List<Map.Entry<String, String>> tmp;
        for (Relationship rel : start.getRelationships(Direction.INCOMING)) {
            tmp = shortestPath(rel.getStartNode(), end, current);
            if (tmp != null && tmp.size() < shortest.size()) shortest = tmp;
        }
        return shortest;
    }

    public static String getStringLabels(Node node) {
        return "[" +
                StreamSupport.stream(node.getLabels().spliterator(), false)
                        .map(Label::name)
                        .collect(Collectors.joining(", "))
                + "]";
    }

    public static <T> List<T> listAll(List<T> first, List<T> second) {
        List<T> result = null;
        if (first != null) {
            result = new ArrayList<>(first);
            if (second != null) {
                result.addAll(second);
                return result;
            }
            return result;
        }
        if (second != null) {
            return new ArrayList<>(second);
        }
        return null;
    }
}
