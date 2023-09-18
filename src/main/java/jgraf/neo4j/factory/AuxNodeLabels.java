package jgraf.neo4j.factory;

import org.neo4j.graphdb.Label;

import java.util.Arrays;

public enum AuxNodeLabels implements Label {
    __PARTITION_INDEX__; // Prefix for labelling nodes from the same dataset or partition

    public static boolean isIn(Label label) {
        return Arrays.stream(values()).anyMatch(t -> label.name().startsWith(t.name()) || label.name().endsWith(t.name()));
    }
}
