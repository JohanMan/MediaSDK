package com.johan.opencv;

import android.graphics.Bitmap;

/**
 * Created by johan on 2019/12/4.
 */

public class AndroidOpencv {

    static {
        System.loadLibrary("android-opencv");
    }

    /**
     * 人脸检测初始化
     * @param sdAssetPath
     * @return
     */
    public static native long faceDetectCreate(String sdAssetPath);

    /**
     * 人脸检测
     * @param ptr C层实例指针
     * @param nv21 摄像头预览数据 NV21
     * @param width 摄像头预览宽度
     * @param height 摄像头预览高度
     * @param targetWidth View显示高度
     * @param targetHeight View显示宽度
     * @param isFront 是否为前摄
     * @return
     */
    public static native FaceDetectResult[] faceDetect(long ptr, byte[] nv21, int width, int height, int targetWidth, int targetHeight, boolean isFront);

    /**
     * 人脸检测销毁
     * @param ptr
     */
    public static native void faceDetectDestroy(long ptr);

}
