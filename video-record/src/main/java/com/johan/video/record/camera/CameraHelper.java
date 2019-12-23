package com.johan.video.record.camera;

import android.graphics.ImageFormat;
import android.graphics.SurfaceTexture;
import android.hardware.Camera;
import android.util.Log;
import android.view.Surface;

import java.io.IOException;
import java.util.List;

/**
 * Created by johan on 2019/11/26.
 */

public class CameraHelper implements ICameraHelper {

    private int cameraId;
    private Camera camera;
    private int orientation;
    private int fps = 30 * 1000;
    private PreviewCallback previewCallback;

    @Override
    public void open(int cameraId) throws CameraException {
        if (cameraId >= Camera.getNumberOfCameras()) {
            throw new CameraException(CameraException.CODE_NOT_SUPPORT_CAMERA);
        }
        if (camera != null) return;
        this.cameraId = cameraId;
        camera = Camera.open(cameraId);
    }

    @Override
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        Log.e("sdd", "or : " + orientation);
        if (camera != null) {
            camera.setDisplayOrientation(getDisplayOrientation(orientation));
        }
    }

    @Override
    public void setFps(int fps) {
        if (camera == null) return;
        this.fps = fps * 1000;
        Camera.Parameters parameters = camera.getParameters();
        int[] fs = findBestFpsRange(parameters.getSupportedPreviewFpsRange(), fps);
        parameters.setPreviewFpsRange(fs[0], fs[1]);
    }

    @Override
    public void preview(int width, int height, SurfaceTexture surfaceTexture) throws IOException {
        if (camera == null) return;
        config(width, height);
        camera.setPreviewTexture(surfaceTexture);
        camera.startPreview();
    }

    /**
     * 设置预览回调
     * @param callback
     */
    @Override
    public void setPreviewCallback(final int width, final int height, PreviewCallback callback) {
        if (camera == null) return;
        if (callback == null) {
            camera.setPreviewCallback(null);
            return;
        }
        this.previewCallback = callback;
        camera.setPreviewCallbackWithBuffer(new Camera.PreviewCallback() {
            @Override
            public void onPreviewFrame(byte[] data, Camera camera) {
                if (camera != null) {
                    Camera.Size size = camera.getParameters().getPreviewSize();
                    previewCallback.onPreview(data, size.width, size.height, cameraId == CAMERA_ID_FRONT);
                    camera.addCallbackBuffer(data);
                }
            }
        });
        Camera.Parameters parameters = camera.getParameters();
        // 资料说是 PreviewSize 的宽高
        // 结果却出现 Callback buffer was too small! Expected 1382400 bytes, but got 460800 bytes!
        // 实践得出其实 View 的宽高
        // Camera.Size previewSize = parameters.getPreviewSize();
        int bufferSize = width * height * ImageFormat.getBitsPerPixel(parameters.getPreviewFormat()) / 8;
        byte[] buffer = new byte[bufferSize];
        camera.addCallbackBuffer(buffer);
    }

    @Override
    public void shift(int width, int height, SurfaceTexture surfaceTexture) throws IOException {
        if (Camera.getNumberOfCameras() == 1) return;
        if (cameraId == CAMERA_ID_BACKGROUND) {
            cameraId = CAMERA_ID_FRONT;
        } else {
            cameraId = CAMERA_ID_BACKGROUND;
        }
        if (camera != null) {
            close();
        }
        camera = Camera.open(cameraId);
        if (previewCallback != null) {
            setPreviewCallback(width, height, previewCallback);
        }
        preview(width, height, surfaceTexture);
    }

    @Override
    public void close() {
        if (camera == null) return;
        camera.setPreviewCallbackWithBuffer(null);
        camera.stopPreview();
        camera.release();
        camera = null;
    }

    /**
     * 配置
     * @param width 预览宽度
     * @param height 预览高度
     */
    private void config(int width, int height) {
        if (camera == null) return;
        Camera.Parameters parameters = camera.getParameters();
        // 设置聚焦模式
        if (parameters.getSupportedFocusModes().contains(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE)) {
            parameters.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);
        }
        // 设置预览图片格式 NV21
        parameters.setPreviewFormat(ImageFormat.NV21);
        // 设置预览图片大小
        Camera.Size size = findBestSize(parameters.getSupportedPreviewSizes(), width, height);
        parameters.setPreviewSize(size.width, size.height);
        // 设置帧率
        int[] fs = findBestFpsRange(parameters.getSupportedPreviewFpsRange(), fps);
        parameters.setPreviewFpsRange(fs[0], fs[1]);
        // 设置参数
        camera.setParameters(parameters);
        // 设置预览显示的方向
        camera.setDisplayOrientation(90);
    }

    /**
     * 根据屏幕的旋转角度 获取相机显示的角度
     * @return
     */
    public int getDisplayOrientation(int rotation) {
        Camera.CameraInfo info = new Camera.CameraInfo();
        Camera.getCameraInfo(cameraId, info);
        int degrees = 0;
        switch (rotation) {
            case Surface.ROTATION_0:
                degrees = 0;
                break;
            case Surface.ROTATION_90:
                degrees = 90;
                break;
            case Surface.ROTATION_180:
                degrees = 180;
                break;
            case Surface.ROTATION_270:
                degrees = 270;
                break;
        }
        int result;
        if (info.facing == Camera.CameraInfo.CAMERA_FACING_FRONT) {
            result = (info.orientation + degrees) % 360;
            result = (360 - result) % 360;
        } else {
            result = (info.orientation - degrees + 360) % 360;
        }
        return result;
    }

    /**
     * 找到最适合的Size
     * @param sizeList
     * @param width
     * @param height
     * @return
     */
    private Camera.Size findBestSize(List<Camera.Size> sizeList, int width, int height) {
        Camera.Size bestSize = null;
        // 设置短边为宽 长边为高
        if (width > height) {
            int temp = height;
            height = width;
            width = temp;
        }
        // 先判断是否存和Target大小大概相等的Size
        // 可能不一定完全相等 如果宽度和高度查的不是很多的话 变形还在接受范围之类
        // 如果要完全相等的话 等到下面计算宽高比 可能得到的是一个很小的预览大小
        for (Camera.Size size : sizeList) {
            if (isSameSize(size, width, height)) {
                bestSize = size;
            }
        }
        if (bestSize != null) {
            return bestSize;
        }
        // 如果没有找到大小相同Size的，就找比例最接近的Size
        bestSize = sizeList.get(0);
        float ratio = (float) width / height;
        float bestRatio = 0;
        for (Camera.Size size : sizeList) {
            float sizeRatio = calculateRatio(size);
            if (Math.abs(sizeRatio - ratio) < Math.abs(bestRatio - ratio)) {
                bestRatio = sizeRatio;
                bestSize = size;
            }
        }
        return bestSize;
    }

    /**
     * 判断是否相同的Size
     * @param size
     * @param width
     * @param height
     * @return
     */
    private boolean isSameSize(Camera.Size size, int width, int height) {
        // 设置短边为宽 长边为高
        int sizeWidth = Math.min(size.width, size.height);
        int sizeHeight = Math.max(size.width, size.height);
        return Math.abs(width - sizeWidth) + Math.abs(height - sizeHeight) < 100;
    }

    /**
     * 计算Size比例
     * @param size
     * @return
     */
    private float calculateRatio(Camera.Size size) {
        int sizeWidth = Math.min(size.width, size.height);
        int sizeHeight = Math.max(size.width, size.height);
        float ratio = (float) sizeWidth / sizeHeight;
        return ratio;
    }

    /**
     * 查找最合适的 FPS Range
     * @param fpsRangeList
     * @return
     */
    private int[] findBestFpsRange(List<int[]> fpsRangeList, int fps) {
        int[] bestFpsRange = fpsRangeList.get(0);
        for (int i = 1; i < fpsRangeList.size(); i++) {
            int[] fpsRange = fpsRangeList.get(i);
            if (fpsRange[0] != fpsRange[1]) {
                continue;
            }
            if (Math.abs(fpsRange[0] - fps) < Math.abs(bestFpsRange[0] - fps)) {
                bestFpsRange = fpsRange;
            }
        }
        return bestFpsRange;
    }

}
