package com.johan.opencv;

import android.content.Context;

import java.io.IOException;

/**
 * Created by johan on 2019/12/4.
 */

public class FaceDetector implements IFaceDetector {

    private static final String TAG = "FaceDetector";

    private Context context;
    private long ptr;
    private FaceDetectedListener listener;

    public FaceDetector(Context appContext) {
        this.context = appContext;
        OpencvThread.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    String sdAssetPath = FileHelper.getSDAssetPath(context);
                    FileHelper.copyAssetFile(context, "det1.bin", sdAssetPath + "det1.bin", false);
                    FileHelper.copyAssetFile(context, "det1.param", sdAssetPath + "det1.param", false);
                    FileHelper.copyAssetFile(context, "det2.bin", sdAssetPath + "det2.bin", false);
                    FileHelper.copyAssetFile(context, "det2.param", sdAssetPath + "det2.param", false);
                    FileHelper.copyAssetFile(context, "det3.bin", sdAssetPath + "det3.bin", false);
                    FileHelper.copyAssetFile(context, "det3.param", sdAssetPath + "det3.param", false);
                    FileHelper.copyAssetFile(context, "shape_predictor_68_face_landmarks_small.dat", sdAssetPath + "shape_predictor_68_face_landmarks_small.dat", false);
                    ptr = AndroidOpencv.faceDetectCreate(sdAssetPath);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
    }

    @Override
    public void setFaceDetectedListener(FaceDetectedListener listener) {
        this.listener = listener;
    }

    @Override
    public void detect(final byte[] nv21, final int width, final int height, final int targetWidth, final int targetHeight, final boolean isFront) {
        if (ptr == 0) return;
        OpencvThread.execute(new Runnable() {
            @Override
            public void run() {
                FaceDetectResult[] results = AndroidOpencv.faceDetect(ptr, nv21, width, height, targetWidth, targetHeight, isFront);
                listener.onDetected(results);
            }
        });
    }

    @Override
    public void release() {

    }

}
