package com.johan.video.record.gl.filter;

import android.content.Context;

import com.johan.video.record.R;
import com.johan.video.record.gl.util.GLLocation;

import java.nio.FloatBuffer;

/**
 * Created by johan on 2019/3/29.
 * 相机滤镜 用于绘制预览数据作为输入滤镜
 */

public class CameraFilter extends Filter {

    private float[] matrix;

    public CameraFilter(Context context) {
        super(context);
    }

    public void setMatrix(float[] matrix) {
        this.matrix = matrix;
    }

    @Override
    protected int getVertexShader() {
        return R.raw.vertex_camera_shader;
    }

    @Override
    protected int getFrameShader() {
        return R.raw.fragment_camera_shader;
    }

    @Override
    protected void configLocation(int program, GLLocation location) {
        location.configAPosition(program);
        location.configUTextureMatrix(program);
        location.configATextureCoordinate(program);
        location.configUTextureSampler(program);
    }

    @Override
    protected void draw(int width, int height, GLLocation location, int texture, int frameBuffer, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        useProgram();
        clear();
        viewPort(0, 0, width, height);
        bindFrameBuffer(frameBuffer);
        enableVertexPointer(location.aPosition, vertexBuffer);
        setUniformMatrix4(location.uTextureMatrix, matrix);
        enableVertexPointer(location.aTextureCoordinate, textureBuffer);
        bindCameraTexture(location.uTextureSampler, texture);
        drawArrays();
        unbindCameraTexture();
        disableVertexPointer(location.aTextureCoordinate);
        disableVertexPointer(location.aPosition);
        unbindFrameBuffer();
    }

}
