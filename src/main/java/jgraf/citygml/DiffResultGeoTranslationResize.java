package jgraf.citygml;

import org.neo4j.graphdb.Label;

import java.util.List;

public class DiffResultGeoTranslationResize extends DiffResultGeo {
    private double[] vector;
    private double[] delta;

    public DiffResultGeoTranslationResize(double[] vector, double[] delta,
                                          List<Label> skip, Label anchor) {
        super(SimilarityLevel.SIMILAR_GEOMETRY_TRANSLATION_SIZE_CHANGE,
                -1 * Math.sqrt(vector[0] * vector[0] + vector[1] * vector[1] + vector[2] * vector[2])
                        - 1 * (delta[0] + delta[1] + delta[2]),
                skip,
                anchor);
        this.vector = vector;
        this.delta = delta;
    }

    public double[] getVector() {
        return vector;
    }

    public void setVector(double[] vector) {
        this.vector = vector;
    }

    public double[] getDelta() {
        return delta;
    }

    public void setDelta(double[] delta) {
        this.delta = delta;
    }
}
