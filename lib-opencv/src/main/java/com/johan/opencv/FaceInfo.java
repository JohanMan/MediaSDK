package com.johan.opencv;

/**
 * Created by johan on 2019/12/4.
 */

public class FaceInfo {

    public int x;
    public int y;
    public int width;
    public int height;
    public double pitch;
    public double yaw;
    public double roll;
    public FeaturePoint[] features;

    public static class FeaturePoint {
        public int x;
        public int y;
    }

    @Override
    public String toString() {
        return "FaceInfo{" +
                "x=" + x +
                ", y=" + y +
                ", width=" + width +
                ", height=" + height +
                ", pitch=" + pitch +
                ", yaw=" + yaw +
                ", roll=" + roll +
                ", features=" + features.length +
                '}';
    }

}
