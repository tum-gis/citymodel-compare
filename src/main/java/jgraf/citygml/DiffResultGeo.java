package jgraf.citygml;

import org.neo4j.graphdb.Label;

import java.util.List;

// A DiffResult object is used to evaluate candidates while matching
public class DiffResultGeo extends DiffResult {
    protected List<Label> skip; // skip nodes with these labels -> prune paths while matching
    protected Label anchor; // specify where to attach changes (other than default source node where change is detected)

    public DiffResultGeo(SimilarityLevel level, double value, List<Label> skip, Label anchor) {
        super(level, value);
        this.skip = skip;
        this.anchor = anchor;
    }

    public List<Label> getSkip() {
        return skip;
    }

    public void setSkip(List<Label> skip) {
        this.skip = skip;
    }

    public Label getAnchor() {
        return anchor;
    }

    public void setAnchor(Label anchor) {
        this.anchor = anchor;
    }
}
