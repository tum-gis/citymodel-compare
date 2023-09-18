package jgraf.citygml;

import org.neo4j.graphdb.Label;

import java.util.List;

// A DiffResult object is used to evaluate candidates while matching
public class DiffResultGeo extends DiffResult {
    private List<Label> skip;

    public DiffResultGeo(SimilarityLevel level, double value, List<Label> skip) {
        super(level, value);
        this.skip = skip;
    }

    public List<Label> getSkip() {
        return skip;
    }

    public void setSkip(List<Label> skip) {
        this.skip = skip;
    }
}
