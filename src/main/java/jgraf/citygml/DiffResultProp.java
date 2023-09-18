package jgraf.citygml;

import java.util.List;

// A DiffResult object is used to evaluate candidates while matching
public class DiffResultProp extends DiffResult {
    private List<String> skip;

    public DiffResultProp(SimilarityLevel level, double value, List<String> skip) {
        super(level, value);
        this.skip = skip;
    }

    public List<String> getSkip() {
        return skip;
    }

    public void setSkip(List<String> skip) {
        this.skip = skip;
    }
}
