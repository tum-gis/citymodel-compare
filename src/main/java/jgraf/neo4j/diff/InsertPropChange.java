package jgraf.neo4j.diff;

import jgraf.neo4j.factory.AuxEdgeTypes;
import jgraf.neo4j.factory.AuxPropNames;
import jgraf.utils.ChangeUtils;
import org.neo4j.graphdb.Node;
import org.neo4j.graphdb.Transaction;

import java.util.Map;

public class InsertPropChange extends PropChange {
    private final String propValue;

    public InsertPropChange(Transaction tx, Node leftNode, Node rightNode, String propName, String propValue) {
        super(leftNode, rightNode, propName);
        this.propValue = propValue;
        ChangeUtils.addChange(tx, getClass(),
                Map.of(
                        AuxPropNames.PROPERTY_NAME, propName,
                        AuxPropNames.PROPERTY_VALUE, propValue
                ),
                Map.of(
                        AuxEdgeTypes.TANDEM, leftNode,
                        AuxEdgeTypes.RIGHT_NODE, rightNode
                )
        );
    }
}
