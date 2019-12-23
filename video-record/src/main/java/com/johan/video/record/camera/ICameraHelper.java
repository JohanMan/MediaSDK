package com.johan.video.record.camera;

import android.content.Context;
import android.graphics.SurfaceTexture;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import java.io.IOException;

/**
 * Created by johan on 2019/11/26.
 */

public interface ICameraHelper {

    // 前摄
    int CAMERA_ID_FRONT = 1;
    // 后摄
    int CAMERA_ID_BACKGROUND = 0;

    /**
     * 打开摄像头
     * @param cameraId
     */
    void open(int cameraId) throws CameraException;

    /**
     * 设置屏幕方向
     * @param orientation
     */
    void setOrientation(int orientation);

    /**
     * 设置帧率
     * @param fps
     */
    void setFps(int fps);

    /**
     * 预览
     * @param width
     * @param height
     * @param surfaceTexture
     */
    void preview(int width, int height, SurfaceTexture surfaceTexture) throws IOException;

    /**
     * 设置预览回调
     * @param width
     * @param height
     * @param previewCallback
     */
    void setPreviewCallback(int width, int height, PreviewCallback previewCallback);

    /**
     * 预览回调
     */
    interface PreviewCallback {
        /**
         * 回调
         * @param data 预览数据 格式nv21
         * @param previewWidth 预览宽度 非显示view宽度
         * @param previewHeight 预览高度 非显示view高度
         * @param isFront 是否前摄
         */
        void onPreview(byte[] data, int previewWidth, int previewHeight, boolean isFront);
    }

    /**
     * 关闭摄像头
     */
    void close();

    /**
     * 切换摄像头
     */
    void shift(int width, int height, SurfaceTexture surfaceTexture) throws IOException;;

}
