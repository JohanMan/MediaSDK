package com.johan.video.record.gl.filter;

import android.content.Context;

import com.johan.video.record.R;
import com.johan.video.record.gl.util.GLLocation;

import java.nio.FloatBuffer;

/**
 * Created by johan on 2019/4/1.
 * 美肤滤镜 属于滤镜组
 * 磨皮：本质是像素点模糊，使用双边滤波模糊
 * 亮白：本质是调高图片亮度
 * 红润：本质是改变图片的色调
 */

public class BeautyFilter extends Filter {

    // 测试得出 0.5 效果不错 (0~1)
    private float opacity = 0f;
    // 测试得出 0.1 效果不错 (0~0.2)
    private float brightness = 0f;
    // 测试得出 0.5 效果不错（0~1）
    private float tone = 0f;

    public BeautyFilter(Context context) {
        super(context);
    }

    @Override
    protected int getFrameShader() {
        return R.raw.fragment_beauty_shader;
    }

    @Override
    protected void configLocation(int program, GLLocation location) {
        location.configAPosition(program);
        location.configATextureCoordinate(program);
        location.configUTextureSampler(program);
        location.configUWidth(program);
        location.configUHeight(program);
        location.configUOpacity(program);
        location.configUBrightness(program);
        location.configUTone(program);
    }

    @Override
    protected void draw(int width, int height, GLLocation location, int texture, int frameBuffer, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        useProgram();
        clear();
        viewPort(0, 0, width, height);
        bindFrameBuffer(frameBuffer);
        enableVertexPointer(location.aPosition, vertexBuffer);
        enableVertexPointer(location.aTextureCoordinate, textureBuffer);
        bindTexture(location.uTextureSampler, texture);
        setUniformFloat(location.uWidth, width);
        setUniformFloat(location.uHeight, height);
        setUniformFloat(location.uOpacity, opacity);
        setUniformFloat(location.uBrightness, brightness);
        setUniformFloat(location.uTone, tone);
        drawArrays();
        unbindTexture();
        disableVertexPointer(location.aTextureCoordinate);
        disableVertexPointer(location.aPosition);
        unbindFrameBuffer();
    }

    /**
     * 设置模糊程度 0-10
     * @param level
     */
    public void setOpacity(int level) {
        opacity = 0.1f * level;
    }

    /**
     * 设置明亮程度
     * @param level
     */
    public void setBrightness(int level) {
        brightness = 0.02f * level;
    }

    /**
     * 设置红润程度
     * @param level
     */
    public void setTone(int level) {
        tone = 0.1f * level;
    }

}
