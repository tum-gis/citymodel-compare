package jgraf.citygml;

import org.neo4j.graphdb.Label;

import java.util.List;

// A DiffResult object is used to evaluate candidates while matching
public class DiffResultGeoSize extends DiffResultGeo {
    private double[] delta;

    public DiffResultGeoSize(double[] delta, List<Label> skip, Label anchor) {
        // Switch sign of deltas so that higher value means higher confidence
        super(SimilarityLevel.SIMILAR_GEOMETRY_SIZE_CHANGE, -1 * (delta[0] + delta[1] + delta[2]), skip, anchor);
        // Avoid multiplication of delta[i] since they can be 0
        this.delta = delta;
    }

    public double[] getDelta() {
        return delta;
    }

    public void setDelta(double[] delta) {
        this.delta = delta;
    }
}
