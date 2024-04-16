package jgraf.core;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public class DBStats {
    private long mappedNodeCount;
    private long partitionNodeCount;
    private long matchedNodeCount;
    private Map<String, Long> mappedLabelCount;
    private Map<String, Long> partitionLabelCount;
    private Map<String, Long> matchedLabelCount;
    private final List<Map.Entry<String, Long>> timeLogs;
    private Long startTime;

    private final static Logger logger = LoggerFactory.getLogger(DBStats.class);

    public DBStats() {
        mappedNodeCount = 0;
        partitionNodeCount = 0;
        matchedNodeCount = 0;
        mappedLabelCount = new ConcurrentHashMap<>();
        partitionLabelCount = new ConcurrentHashMap<>();
        matchedLabelCount = new ConcurrentHashMap<>();
        timeLogs = new ArrayList<>();
        startTime = null;
    }

    public void startTimer() {
        if (startTime != null) throw new RuntimeException("A previous timer is still running");
        startTime = System.nanoTime();
    }

    private long getTimeSeconds() {
        if (startTime == null) throw new RuntimeException("A timer has not been started");
        long tmp = startTime;
        startTime = null;
        return Math.round((System.nanoTime() - tmp) * 1e-9);
    }

    public void stopTimer(String msg) {
        timeLogs.add(new AbstractMap.SimpleEntry<>(msg, getTimeSeconds()));
    }

    @Override
    public String toString() {
        String horizontalBorder = new String(new char[102]).replace("\0", "-");
        StringBuilder result = new StringBuilder("\n+ " + horizontalBorder + " +\n");
        String stringFormat = "| %-70s : %,15d : %10.3f%% |\n";

        // Sort mapped label hash map
        result.append(String.format(stringFormat, "TOTAL NUMBER OF MAPPED NODES", mappedNodeCount, 100f));
        mappedLabelCount = sort(mappedLabelCount);
        for (Map.Entry<String, Long> entry : mappedLabelCount.entrySet()) {
            result.append(String.format(stringFormat, toSimpleClassName(entry.getKey()), entry.getValue(),
                    Double.parseDouble(entry.getValue().toString()) * 100f / mappedNodeCount));
        }
        result.append("+ ").append(horizontalBorder).append(" +\n");

        // Aux. label hash map
        result.append(String.format(stringFormat, "TOTAL NUMBER OF NODES PER PARTITION", partitionNodeCount, 100f));
        partitionLabelCount = sort(partitionLabelCount);
        for (Map.Entry<String, Long> entry : partitionLabelCount.entrySet()) {
            result.append(String.format(stringFormat, toSimpleClassName(entry.getKey()), entry.getValue(),
                    Double.parseDouble(entry.getValue().toString()) * 100f / partitionNodeCount));
        }
        result.append("+ ").append(horizontalBorder).append(" +\n");

        // Matched label hash map
        result.append(String.format(stringFormat, "TOTAL NUMBER OF CHANGE NODES", matchedNodeCount, 100f));
        for (Map.Entry<String, Long> entry : matchedLabelCount.entrySet()) {
            result.append(String.format(stringFormat, toSimpleClassName(entry.getKey()), entry.getValue(),
                    Double.parseDouble(entry.getValue().toString()) * 100f / matchedNodeCount));
        }
        result.append("+ ").append(horizontalBorder).append(" +\n");

        // Time logs
        long totalTime = 0;
        for (Map.Entry<String, Long> entry : timeLogs) {
            totalTime += entry.getValue();
        }
        result.append(String.format(stringFormat, "TOTAL ELAPSED TIME (s)", totalTime, 100f));
        for (Map.Entry<String, Long> entry : timeLogs) {
            result.append(String.format(stringFormat, entry.getKey(), entry.getValue(),
                    entry.getValue() * 100f / totalTime));
        }
        result.append("+ ").append(horizontalBorder).append(" +\n");

        return result.toString();
    }

    private HashMap<String, Long> sort(Map<String, Long> map) {
        return map.entrySet()
                .stream()
                // Sort desc by values, then asc by keys
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed()
                        .thenComparing((Comparator<Map.Entry<String, Long>> & Serializable) (e1, e2) ->
                                toSimpleClassName(e1.getKey()).compareTo(toSimpleClassName(e2.getKey()))))
                .collect(Collectors.toMap(
                        Map.Entry::getKey,
                        Map.Entry::getValue,
                        (e1, e2) -> e1,
                        LinkedHashMap::new)
                );
    }

    private String toSimpleClassName(String className) {
        String result = "";
        try {
            result = Class.forName(className).getSimpleName();
        } catch (ClassNotFoundException e) {
            result = className;
        }
        return result;
    }

    public long getMappedNodeCount() {
        return mappedNodeCount;
    }

    public void setMappedNodeCount(long mappedNodeCount) {
        this.mappedNodeCount = mappedNodeCount;
    }

    public long getPartitionNodeCount() {
        return partitionNodeCount;
    }

    public void setPartitionNodeCount(long partitionNodeCount) {
        this.partitionNodeCount = partitionNodeCount;
    }

    public long getMatchedNodeCount() {
        return matchedNodeCount;
    }

    public void setMatchedNodeCount(long matchedNodeCount) {
        this.matchedNodeCount = matchedNodeCount;
    }

    public Map<String, Long> getMappedLabelCount() {
        return mappedLabelCount;
    }

    public Map<String, Long> getMatchedLabelCount() {
        return matchedLabelCount;
    }

    public void setMappedLabelCount(Map<String, Long> mappedLabelCount) {
        this.mappedLabelCount = mappedLabelCount;
    }

    public Map<String, Long> getPartitionLabelCount() {
        return partitionLabelCount;
    }

    public void setPartitionLabelCount(Map<String, Long> partitionLabelCount) {
        this.partitionLabelCount = partitionLabelCount;
    }

    public void setMatchedLabelCount(Map<String, Long> matchedLabelCount) {
        this.matchedLabelCount = matchedLabelCount;
    }

    public List<Map.Entry<String, Long>> getTimeLogs() {
        return timeLogs;
    }
}
