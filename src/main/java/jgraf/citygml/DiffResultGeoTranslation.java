package jgraf.citygml;

import org.neo4j.graphdb.Label;

import java.util.List;

// A DiffResult object is used to evaluate candidates while matching
public class DiffResultGeoTranslation extends DiffResultGeo {
    private double[] vector;

    public DiffResultGeoTranslation(double[] vector, int[] lods, List<Label> skip, Label anchor) {
        // Switch sign of norm of vector so that higher value means higher confidence
        super(SimilarityLevel.SIMILAR_GEOMETRY_TRANSLATION,
                -1 * Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2]),
                lods, skip, anchor);
        this.vector = vector;
    }

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }
}
