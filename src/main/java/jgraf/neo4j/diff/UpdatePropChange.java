package jgraf.neo4j.diff;

import jgraf.neo4j.factory.AuxEdgeTypes;
import jgraf.neo4j.factory.AuxPropNames;
import jgraf.utils.ChangeUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.Map;

public class UpdatePropChange extends PropChange {
    private final String leftPropValue;
    private final String rightPropValue;

    public UpdatePropChange(Transaction tx, Node leftNode, Node rightNode,
                            String propName, String leftPropValue, String rightPropValue) {
        super(leftNode, rightNode, propName);
        this.leftPropValue = leftPropValue;
        this.rightPropValue = rightPropValue;
        ChangeUtils.addChange(tx, getClass(),
                Map.of(
                        AuxPropNames.PROPERTY_NAME, propName,
                        AuxPropNames.LEFT_PROPERTY_VALUE, leftPropValue,
                        AuxPropNames.RIGHT_PROPERTY_VALUE, rightPropValue
                ), Map.of(
                        AuxEdgeTypes.TANDEM, leftNode,
                        AuxEdgeTypes.RIGHT_NODE, rightNode
                )
        );
    }
}
