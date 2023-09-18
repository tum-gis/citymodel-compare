package jgraf.utils;

import org.apache.commons.geometry.euclidean.threed.ConvexPolygon3D;
import org.apache.commons.geometry.euclidean.threed.Planes;
import org.apache.commons.geometry.euclidean.threed.RegionBSPTree3D;
import org.apache.commons.geometry.euclidean.threed.Vector3D;
import org.apache.commons.numbers.core.Precision;

import java.util.List;

public class GeometryUtils {
    public static RegionBSPTree3D toRegion3D(List<Double> lower, List<Double> upper, Precision.DoubleEquivalence precision) {
        // Extract values
        double lX = lower.get(0);
        double lY = lower.get(1);
        double lZ = lower.get(2);
        double uX = upper.get(0);
        double uY = upper.get(1);
        double uZ = upper.get(2);
        // Vertex indices
        int bll = 0;
        int blr = 1;
        int bul = 2;
        int bur = 3;
        int tll = 4;
        int tlr = 5;
        int tul = 6;
        int tur = 7;
        // Vertex array
        Vector3D[] vertices = new Vector3D[8];
        vertices[bll] = Vector3D.of(lX, lY, lZ); // bottom lower left
        vertices[blr] = Vector3D.of(uX, lY, lZ); // bottom lower right
        vertices[bul] = Vector3D.of(lX, uY, lZ); // bottom upper left
        vertices[bur] = Vector3D.of(uX, uY, lZ); // bottom upper right
        vertices[tll] = Vector3D.of(lX, lY, uZ); // top lower left
        vertices[tlr] = Vector3D.of(uX, lY, uZ); // top lower right
        vertices[tul] = Vector3D.of(lX, uY, uZ); // top upper left
        vertices[tur] = Vector3D.of(uX, uY, uZ); // top upper right
        // Define boundary faces using vertex indices above
        int[][] faceIndices = {
                {bll, bul, bur, blr}, // bottom face
                {tur, tul, tll, tlr}, // top face
                {bll, blr, tlr, tll}, // front face
                {tur, tlr, blr, bur}, // right face
                {tur, bur, bul, tul}, // back face
                {bll, tll, tul, bul} // left face
        };
        // Create region
        List<ConvexPolygon3D> faces = Planes.indexedConvexPolygons(vertices, faceIndices, precision);
        return RegionBSPTree3D.from(faces);
    }
}
