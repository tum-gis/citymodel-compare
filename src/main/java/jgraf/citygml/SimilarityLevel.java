package jgraf.citygml;

public enum SimilarityLevel {
    EQUIVALENCE(10),
    SIMILAR_GEOMETRY(9),
    SPLIT_TOPLEVEL(8),
    SIMILAR_GEOMETRY_TRANSLATION_SIZE_CHANGE(7),
    SIMILAR_GEOMETRY_TRANSLATION(6),
    SIMILAR_GEOMETRY_SIZE_CHANGE(5),
    SAME_PROPS(4),
    SAME_ID(3),
    SIMILAR_STRUCTURE(2),
    SAME_LABELS(1),
    NONE(0);

    private int value;

    SimilarityLevel(int value) {
        this.value = value;
    }

    public int getValue() {
        return value;
    }
}
