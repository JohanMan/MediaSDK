package com.johan.opencv;

import java.util.List;

/**
 * Created by johan on 2019/11/29.
 */

public interface IFaceDetector {

    /**
     * 设置监听器
     * @param listener
     */
    void setFaceDetectedListener(FaceDetectedListener listener);


    /**
     * 人脸检测
     * @param nv21 摄像头预览数据
     * @param width 摄像头参数预览宽度
     * @param height 摄像头参数预览高度
     * @param targetWidth 界面显示预览宽度
     * @param targetHeight 界面显示预览高度
     * @param isFront 是否是前摄
     */
    void detect(byte[] nv21, int width, int height, int targetWidth, int targetHeight, boolean isFront);

    /**
     * 销毁
     */
    void release();

    /**
     * 人脸检测监听器
     */
    interface  FaceDetectedListener {
        void onDetected(FaceDetectResult[] results);
    }

}
