package jgraf.neo4j.factory;

import org.neo4j.graphdb.Label;

import java.util.Arrays;

public enum NodeLabels implements Label {
    __ROOT__,
    __ROOT_MAPPER__,
    __ROOT_MATCHER__,
    __ROOT_RTREES__,

    __ARRAY__;

    public static boolean isIn(Label label) {
        return Arrays.stream(values()).anyMatch(t -> label.name().startsWith(t.name()) || label.name().endsWith(t.name()));
    }
}
