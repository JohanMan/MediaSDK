package com.johan.video.record.gl.filter;

import android.content.Context;

import com.johan.video.record.gl.util.GLLocation;

import java.nio.FloatBuffer;

/**
 * Created by johan on 2019/3/29.
 * 屏幕滤镜
 * 用于最后显示在屏幕上
 * 用于绘制到EGLSurface
 */

public class ScreenFilter extends Filter {

    public ScreenFilter(Context context) {
        super(context);
    }

    @Override
    protected void configLocation(int program, GLLocation location) {
        location.configAPosition(program);
        location.configATextureCoordinate(program);
        location.configUTextureSampler(program);
    }

    @Override
    protected void draw(int width, int height, GLLocation location, int texture, int frameBuffer, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        useProgram();
        viewPort(0, 0, width, height);
        enableVertexPointer(location.aPosition, vertexBuffer);
        enableVertexPointer(location.aTextureCoordinate, textureBuffer);
        bindTexture(location.uTextureSampler, texture);
        drawArrays();
        unbindTexture();
        disableVertexPointer(location.aTextureCoordinate);
        disableVertexPointer(location.aPosition);
    }

}
