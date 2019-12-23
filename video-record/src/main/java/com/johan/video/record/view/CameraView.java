package com.johan.video.record.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.SurfaceTexture;
import android.opengl.GLSurfaceView;
import android.util.AttributeSet;
import android.util.Log;
import android.widget.ImageView;

import com.johan.opencv.FaceDetectResult;
import com.johan.opencv.FaceDetector;
import com.johan.opencv.FaceInfo;
import com.johan.opencv.IFaceDetector;
import com.johan.video.record.camera.CameraException;
import com.johan.video.record.camera.CameraHelper;
import com.johan.video.record.camera.ICameraHelper;
import com.johan.video.record.gl.GLDrawer;
import com.johan.video.record.gl.filter.BeautyFilter;
import com.johan.video.record.gl.filter.CameraFilter;
import com.johan.video.record.gl.filter.FilterManager;
import com.johan.video.record.gl.filter.ScreenFilter;
import com.johan.video.record.gl.filter.WatermarkFilter;

import java.io.IOException;
import java.util.List;

import javax.microedition.khronos.egl.EGLConfig;
import javax.microedition.khronos.opengles.GL10;

/**
 * Created by johan on 2019/11/25.
 */

public class CameraView extends GLSurfaceView implements GLSurfaceView.Renderer, SurfaceTexture.OnFrameAvailableListener {

    private static final String TAG = "CameraView";

    private SurfaceTexture surfaceTexture;
    private ICameraHelper cameraHelper;
    private GLDrawer glDrawer;
    private FilterManager filterManager;
    private IFaceDetector faceDetector;

    private int orientation;
    private float[] transformMatrix = new float[16];
    private int faceDetectFailCount;

    public CameraView(Context context) {
        super(context);
    }

    public CameraView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setEGLContextClientVersion(3);
        setRenderer(this);
        setRenderMode(GLSurfaceView.RENDERMODE_WHEN_DIRTY);
        cameraHelper = new CameraHelper();
        glDrawer = new GLDrawer();
        filterManager = new FilterManager();
        filterManager.add(new CameraFilter(getContext().getApplicationContext()));
        filterManager.add(new ScreenFilter(getContext().getApplicationContext()));
        faceDetector = new FaceDetector(getContext().getApplicationContext());
        faceDetector.setFaceDetectedListener(faceDetectedListener);
    }

    @Override
    public void onSurfaceCreated(GL10 gl, EGLConfig config) {
        int texture = glDrawer.createCameraTexture();
        surfaceTexture = new SurfaceTexture(texture);
        surfaceTexture.setOnFrameAvailableListener(this);
        try {
            cameraHelper.open(ICameraHelper.CAMERA_ID_FRONT);
            cameraHelper.setPreviewCallback(getWidth(), getHeight(), previewCallback);
            cameraHelper.preview(getWidth(), getHeight(), surfaceTexture);
        } catch (CameraException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onSurfaceChanged(GL10 gl, int width, int height) {
        Log.e(TAG, "Surface Change : " + width + "," + height);
    }

    @Override
    public void onDrawFrame(GL10 gl) {
        surfaceTexture.updateTexImage();
        surfaceTexture.getTransformMatrix(transformMatrix);
        CameraFilter cameraFilter = filterManager.findFilter(CameraFilter.class);
        if (cameraFilter != null) {
            cameraFilter.setMatrix(transformMatrix);
        }
        glDrawer.draw(filterManager.getFilters(), getWidth(), getHeight());
    }

    @Override
    public void onFrameAvailable(SurfaceTexture surfaceTexture) {
        requestRender();
    }

    /**
     * 预览回调
     */
    private ICameraHelper.PreviewCallback previewCallback = new ICameraHelper.PreviewCallback() {
        @Override
        public void onPreview(byte[] data, int width, int height, boolean isFront) {
            // 人脸检测
            faceDetector.detect(data, width, height, getWidth(), getHeight(), isFront);
        }
    };

    /**
     * 人脸检测监听器
     */
    private IFaceDetector.FaceDetectedListener faceDetectedListener = new IFaceDetector.FaceDetectedListener() {
        @Override
        public void onDetected(FaceDetectResult[] results) {
            WatermarkFilter watermarkFilter = filterManager.findFilter(WatermarkFilter.class);
            if (watermarkFilter == null) return;
            int size = results.length;
            if (size == 0) {
                faceDetectFailCount ++;
                if (faceDetectFailCount > 5) {
                    watermarkFilter.setWorkDrawer(0);
                }
                return;
            }
            watermarkFilter.setWorkDrawer(size);
            faceDetectFailCount = 0;
            for (int i = 0; i < size; i++) {
                FaceDetectResult result = results[i];
                Log.e(TAG, result.toString());
                watermarkFilter.updateLocation(i, result.x, result.y, result.width, result.height, (int) result.roll);
            }
        }
    };

    /**
     * 设置角度
     * @param orientation
     */
    public void setOrientation(int orientation) {
        this.orientation = orientation;
        Log.e(TAG, "Orientation Change : " + orientation);
    }

    /**
     * 切换摄像头
     */
    public void shiftCamera() {
        if (cameraHelper != null) {
            try {
                cameraHelper.shift(getWidth(), getHeight(), surfaceTexture);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 美颜
     * @param opacity 磨皮程度 1-10
     * @param brightness 美白程度 1-10
     * @param tone 红润程度 1-10
     */
    public void setBeauty(int opacity, int brightness, int tone) {
        Log.e(TAG, "set beauty : opacity(" + opacity + ") brightness(" + brightness + ") tone(" + tone + ")");
        if (opacity == 0 && brightness == 0 && tone == 0) {
            filterManager.remove(BeautyFilter.class);
            return;
        }
        BeautyFilter beautyFilter = filterManager.findFilter(BeautyFilter.class);
        if (beautyFilter == null) {
            beautyFilter = new BeautyFilter(getContext().getApplicationContext());
            filterManager.addBefore(beautyFilter, ScreenFilter.class);
        }
        beautyFilter.setOpacity(opacity);
        beautyFilter.setBrightness(brightness);
        beautyFilter.setTone(tone);
    }

    /**
     * 设置人脸贴图
     * @param bitmap
     */
    public void setFaceBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            filterManager.remove(WatermarkFilter.class);
        } else {
            WatermarkFilter watermarkFilter = new WatermarkFilter(getContext().getApplicationContext(), 5);
            watermarkFilter.setBitmap(bitmap);
            filterManager.addBefore(watermarkFilter, ScreenFilter.class);
        }
    }

    public void setImageView(ImageView imageView) {

    }

}
