package jgraf.citygml;

import org.neo4j.graphdb.Label;

import java.util.List;

// A DiffResult object is used to evaluate candidates while matching
public class DiffResultGeo extends DiffResult {
    protected List<Label> skip; // skip nodes with these labels -> prune paths while matching
    protected Label anchor; // specify where to attach changes (other than default source node where change is detected)

    protected int[] lods; // lods[0] = original lod, lods[1] = new lod

    public DiffResultGeo(SimilarityLevel level, double value, int[] lods, List<Label> skip, Label anchor) {
        super(level, value);
        this.lods = lods;
        this.skip = skip;
        this.anchor = anchor;
    }

    public int[] getLods() {
        return lods;
    }

    public void setLods(int[] lods) {
        this.lods = lods;
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
