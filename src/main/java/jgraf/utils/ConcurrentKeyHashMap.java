package jgraf.utils;

import jgraf.citygml.Patterns;
import org.neo4j.graphdb.Node;

import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class ConcurrentKeyHashMap<K, V> extends ConcurrentHashMap<K, V> {
    public ConcurrentKeyHashMap() {
        super();
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (!(obj instanceof ConcurrentKeyHashMap<?, ?> other)) {
            return false;
        }
        return this.fuzzyEquals((ConcurrentKeyHashMap<K, V>) other, "", "", 0.001); // TODO Precision as a variable?
    }

    private boolean fuzzyEquals(ConcurrentKeyHashMap<K, V> other, String prefix1, String prefix2, double precision) {
        // Compare if both key1 and key2 have the same keys and values
        if (this.size() != other.size())
            return false;
        for (K key : this.keySet()) {
            if (!other.containsKey(key))
                return false;
            if (!fuzzyEquals(prefix1 + this.get(key).toString(), prefix2 + other.get(key).toString(), precision))
                return false;
        }
        return true;
    }

    private static boolean fuzzyEquals(String s1, String s2, double precision) {
        // Trim strings
        String a = s1.trim();
        String b = s2.trim();

        // Numeric values
        String numericRegex = "[+-]?([0-9]+([.][0-9]*)?|[.][0-9]+)";
        if (a.matches(numericRegex) && b.matches(numericRegex)) {
            return Math.abs(Double.parseDouble(a) - Double.parseDouble(b)) <= precision;
        }

        // Zoned date-time values such as "2016-11-22T00:00+01:00[Europe/Berlin]"
        String zonedDateTimeRegex = "[0-9]{4}-[0-9]{2}-[0-9]{2}T[0-9]{2}:[0-9]{2}[+-][0-9]{2}:[0-9]{2}\\[.*]";
        if (a.matches(zonedDateTimeRegex) && b.matches(zonedDateTimeRegex)) {
            return ZonedDateTime.parse(a).equals(ZonedDateTime.parse(b));
        }

        return a.equals(b);
    }
}
