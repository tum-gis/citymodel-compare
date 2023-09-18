package jgraf.neo4j.diff;

import jgraf.utils.ClazzUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;

public abstract class NodeChange extends Change {
    protected String parentNodeLabel;
    protected String nodeLabel;
    protected String relType;

    public NodeChange(Node leftParentNode, Node rightParentNode, Relationship rel) {
        // Both left and right parent node should have the same labels
        parentNodeLabel = ClazzUtils.getSimpleClassName(leftParentNode);
        nodeLabel = ClazzUtils.getSimpleClassName(rel.getEndNode());
        relType = rel.getType().name();
    }
}
