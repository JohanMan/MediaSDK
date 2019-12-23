package com.johan.opencv;

import java.util.Arrays;

/**
 * Created by johan on 2019/12/4.
 */

public class FaceDetectResult {

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
        return "FaceDetectResult{" +
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
