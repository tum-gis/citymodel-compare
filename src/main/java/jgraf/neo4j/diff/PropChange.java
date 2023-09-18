package jgraf.neo4j.diff;

import jgraf.utils.ClazzUtils;
import org.neo4j.graphdb.Node;

public abstract class PropChange extends Change {
    protected String nodeLabel;
    protected String propName;

    public PropChange(Node leftNode, Node rightNode, String propName) {
        // Both left and right node should have the same labels
        nodeLabel = ClazzUtils.getSimpleClassName(leftNode);
        this.propName = propName;
    }
}
