package jgraf.base;

import com.github.davidmoten.rtree.RTree;
import com.github.davidmoten.rtree.geometry.Geometries;
import com.github.davidmoten.rtree.geometry.Geometry;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RTreeTest {
    private final static Logger logger = LoggerFactory.getLogger(RTreeTest.class);

    @Test
    void testSimpleObject() {
        RTree<String, Geometry> rtree = RTree.star().create();
        rtree = rtree.add("A", Geometries.rectangle(0, 0, 3, 3));
        rtree = rtree.add("B", Geometries.rectangle(1, 1, 7, 7));
        rtree.visualize(500, 500).save("output/img/rtree_test.png");
    }
}
