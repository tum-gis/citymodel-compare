package jgraf.citygml;

import jgraf.neo4j.Neo4jGraphRef;

import java.util.List;

public class DiffResultTopSplit extends DiffResult {
    List<Neo4jGraphRef> splitCandidates;

    public DiffResultTopSplit(SimilarityLevel level, double value, List<Neo4jGraphRef> splitCandidates) {
        super(level, value);
        this.splitCandidates = splitCandidates;
    }

    public List<Neo4jGraphRef> getSplitCandidates() {
        return splitCandidates;
    }

    public void setSplitCandidates(List<Neo4jGraphRef> splitCandidates) {
        this.splitCandidates = splitCandidates;
    }
}
