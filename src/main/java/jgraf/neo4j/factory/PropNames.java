package jgraf.neo4j.factory;

import java.util.Arrays;

public enum PropNames {
    href,
    id,
    x,
    y,
    z,
    ARRAY_MEMBER, // Prefix followed by [index]

    value;

    public static boolean isIn(String name) {
        return Arrays.stream(values()).anyMatch(p -> name.startsWith(p.toString()) || name.endsWith(p.toString()));
    }
}
