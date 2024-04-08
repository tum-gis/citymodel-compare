package jgraf.neo4j.factory;

import org.neo4j.graphdb.RelationshipType;

import java.util.Arrays;

public enum AuxEdgeTypes implements RelationshipType {
    MAPPER,
    MATCHER,
    RTREES,
    RTREE_ARRAY,
    RTREE_DATA,
    ARRAY_MEMBER,
    COLLECTION_MEMBER,
    MAP_MEMBER,

    TANDEM, //LEFT_PARENT
    RIGHT_PARENT,
    LEFT_NODE,
    RIGHT_NODE,

    boundedBy_old;

    public static boolean isIn(RelationshipType type) {
        return Arrays.stream(values()).anyMatch(t -> type.name().startsWith(t.name()) || type.name().endsWith(t.name()));
    }
}
