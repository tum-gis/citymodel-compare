package jgraf.citygml;

// A DiffResult object is used to evaluate candidates while matching
public class DiffResult {
    protected SimilarityLevel level;
    protected double value; // different values within the same level

    public DiffResult(SimilarityLevel level, double value) {
        this.level = level;
        this.value = value;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DiffResultGeo that)) return false;

        if (value != that.value) return false;
        return level == that.level;
    }

    public double compare(DiffResultGeo that) {
        if (that == null) throw new RuntimeException("Could not compare null DiffResult object");
        if (this.level == that.level) return this.value - that.value;
        return this.level.getValue() - that.level.getValue();
    }

    public SimilarityLevel getLevel() {
        return level;
    }

    public void setLevel(SimilarityLevel level) {
        this.level = level;
    }

    public double getValue() {
        return value;
    }

    public void setValue(double value) {
        this.value = value;
    }
}
