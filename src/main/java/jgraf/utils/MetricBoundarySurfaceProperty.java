package jgraf.utils;

import org.apache.commons.geometry.euclidean.threed.Vector3D;

import java.util.List;

public class MetricBoundarySurfaceProperty {
    Vector3D normalVector;
    Vector3D[] bbox;
    List<Vector3D> exteriorPoints;
    Class<?> surfaceType;
    int highestLOD;

    public MetricBoundarySurfaceProperty(
            Class<?> surfaceType,
            Vector3D normalVector,
            Vector3D[] bbox,
            List<Vector3D> exteriorPoints,
            int highestLOD
    ) {
        this.surfaceType = surfaceType;
        this.normalVector = normalVector;
        this.bbox = bbox;
        this.exteriorPoints = exteriorPoints;
        this.highestLOD = highestLOD;
    }

    public Class<?> getSurfaceType() {
        return surfaceType;
    }

    public void setSurfaceType(Class<?> surfaceType) {
        this.surfaceType = surfaceType;
    }

    public Vector3D getNormalVector() {
        return normalVector;
    }

    public Vector3D[] getBbox() {
        return bbox;
    }

    public void setNormalVector(Vector3D normalVector) {
        this.normalVector = normalVector;
    }

    public void setBbox(Vector3D[] bbox) {
        this.bbox = bbox;
    }

    public List<Vector3D> getExteriorPoints() {
        return exteriorPoints;
    }

    public void setExteriorPoints(List<Vector3D> exteriorPoints) {
        this.exteriorPoints = exteriorPoints;
    }

    public int getHighestLOD() {
        return highestLOD;
    }

    public void setHighestLOD(int highestLOD) {
        this.highestLOD = highestLOD;
    }
}
