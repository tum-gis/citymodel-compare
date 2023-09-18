package jgraf.neo4j.diff;

import jgraf.neo4j.factory.AuxEdgeTypes;
import jgraf.neo4j.factory.AuxPropNames;
import jgraf.utils.ChangeUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Relationship;
import org.neo4j.graphdb.Transaction;

import java.util.Map;

public class DeleteNodeChange extends NodeChange {
    public DeleteNodeChange(Transaction tx, Node leftParentNode, Node rightParentNode, Relationship leftRel) {
        super(leftParentNode, rightParentNode, leftRel);
        ChangeUtils.addChange(tx, getClass(),
                Map.of(
                        AuxPropNames.RELATIONSHIP_NAME, relType
                ),
                Map.of(
                        AuxEdgeTypes.TANDEM, leftParentNode,
                        AuxEdgeTypes.RIGHT_PARENT, rightParentNode,
                        AuxEdgeTypes.LEFT_NODE, leftRel.getEndNode()
                )
        );
    }
}
