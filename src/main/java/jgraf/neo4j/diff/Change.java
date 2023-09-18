package jgraf.neo4j.diff;

import jgraf.utils.GraphUtils;
import org.neo4j.graphdb.Label;
import org.neo4j.graphdb.Node;

public abstract class Change {
    public static Label END_NODE_LABEL;

    public String toString(Node node) {
        if (!node.hasLabel(Label.label(getClass().getName()))) return null;
        return GraphUtils.shortestPathString(node, END_NODE_LABEL);
    }
}
