package com.johan.video.record.gl.util;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.FloatBuffer;

/**
 * Created by johan on 2018/12/11.
 */

public class DataUtil {

    /**
     * 顶点坐标
     */
    private static final float[] VERTEX_DATA = {
            1f, 1f,
            -1f, 1f,
            -1f, -1f,
            1f, 1f,
            -1f, -1f,
            1f, -1f
    };

    /**
     * 纹理坐标
     */
    private static final float[] TEXTURE_DATA = {
            1f, 1f,
            0f, 1f,
            0f, 0f,
            1f, 1f,
            0f, 0f,
            1f, 0f
    };


    /**
     * 解析 float 数组
     * @param data
     * @return
     */
    public static FloatBuffer parseFloatBuffer(float[] data) {
        ByteBuffer byteBuffer = ByteBuffer.allocateDirect(data.length * 4);
        byteBuffer.order(ByteOrder.nativeOrder());
        FloatBuffer floatBuffer = byteBuffer.asFloatBuffer();
        floatBuffer.put(data);
        floatBuffer.position(0);
        return floatBuffer;
    }

    /**
     * 加载顶点坐标
     * @return
     */
    public static FloatBuffer loadVertexData() {
        return parseFloatBuffer(VERTEX_DATA);
    }

    /**
     * 加载纹理坐标
     * @return
     */
    public static FloatBuffer loadTextureData() {
        return parseFloatBuffer(TEXTURE_DATA);
    }

    /**
     * 加载纹理坐标
     * @param flipHorizontal
     * @param flipVertical
     * @return
     */
    public static FloatBuffer loadTextureData(boolean flipHorizontal, boolean flipVertical) {
        float[] data = TEXTURE_DATA;
        float[] newData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i];
        }
        if (flipHorizontal) {
            for (int i = 0; i < data.length; i++) {
                if (i % 2 == 0) {
                    newData[i] = flip(newData[i]);
                }
            }
        }
        if (flipVertical) {
            for (int i = 0; i < data.length; i++) {
                if (i % 2 != 0) {
                    newData[i] = flip(newData[i]);
                }
            }
        }
        return parseFloatBuffer(newData);
    }

    /**
     * 翻转
     * @param number
     * @return
     */
    private static float flip(float number) {
        if (number == 0.0f) {
            return 1.0f;
        }
        return 0.0f;
    }

    /**
     * 加载纹理坐标
     * @param scale
     * @return
     */
    public static FloatBuffer loadTextureData(float scale) {
        float[] data = TEXTURE_DATA;
        float[] newData = new float[data.length];
        for (int i = 0; i < data.length; i++) {
            newData[i] = data[i] * scale;
        }
        return parseFloatBuffer(newData);
    }

}
