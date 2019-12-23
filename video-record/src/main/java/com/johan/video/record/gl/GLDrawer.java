package com.johan.video.record.gl;

import android.graphics.Bitmap;

import com.johan.video.record.gl.filter.Filter;
import com.johan.video.record.gl.util.GLESUtil;

import java.util.List;

/**
 * Created by johan on 2019/11/26.
 */

public class GLDrawer {

    private int size;
    private int inputTexture;
    private int[] textures;
    private int[] frameBuffers;

    public int createBitmapTexture(Bitmap bitmap) {
        inputTexture = GLESUtil.createTexture(bitmap);
        return inputTexture;
    }

    public int createCameraTexture() {
        inputTexture = GLESUtil.createOESTexture();
        return inputTexture;
    }

    public void draw(List<Filter> filters, int width, int height) {
        // Filter有变化
        if (size != filters.size()) {
            size = filters.size();
            // 重置纹理
            if (textures != null) {
                for (int i = 1; i < textures.length; i++) {
                    GLESUtil.destroyTexture(textures[i]);
                }
            }
            textures = new int[size];
            textures[0] = inputTexture;
            for (int i = 1; i < size; i++) {
                textures[i] = GLESUtil.createTexture(width, height);
            }
            // 重置FBO
            if (frameBuffers != null) {
                for (int i = 0; i < frameBuffers.length; i++) {
                    GLESUtil.destroyFrameBuffer(frameBuffers[i]);
                }
            }
            frameBuffers = new int[size-1];
            for (int i = 0; i < size-1; i++) {
                frameBuffers[i] = GLESUtil.createFrameBuffer(textures[i + 1]);
            }
        }
        // 绘制
        for (int i = 0; i < filters.size(); i++) {
            Filter filter = filters.get(i);
            filter.create();
            filter.updateSize(width, height);
            if (i == filters.size() - 1) {
                filter.draw(textures[i]);
            } else {
                filter.draw(textures[i], frameBuffers[i]);
            }
        }
    }

}
