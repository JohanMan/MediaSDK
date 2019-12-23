package com.johan.video.record.gl.util;

import android.opengl.GLES30;

/**
 * Created by johan on 2018/12/12.
 */

public class GLLocation {

    // 顶点坐标（顶点着色器）
    private static final String POSITION_ATTRIBUTE = "aPosition";
    // 顶点变换矩阵（顶点着色器）
    private static final String POSITION_MATRIX_UNIFORM = "uPositionMatrix";
    // 纹理坐标（顶点着色器）
    private static final String TEXTURE_COORDINATE_ATTRIBUTE = "aTextureCoordinate";
    // 纹理变换矩阵（顶点着色器）
    private static final String TEXTURE_MATRIX_UNIFORM = "uTextureMatrix";
    // 纹理（片元着色器）
    private static final String TEXTURE_SAMPLER_UNIFORM = "uTextureSampler";
    // 纹理（片元着色器）
    private static final String TEXTURE2_SAMPLER_UNIFORM = "uTexture2Sampler";
    // Curve 曲线纹理（片元着色器）
    private static final String CURVE_TEXTURE_SAMPLER_UNIFORM = "uCurveTextureSampler";
    // Curve 曲线纹理（片元着色器）
    private static final String CURVE2_TEXTURE_SAMPLER_UNIFORM = "uCurve2TextureSampler";
    // Grey 灰度纹理（片元着色器）
    private static final String GREY_TEXTURE_SAMPLER_UNIFORM = "uGreyTextureSampler";
    // Grey 灰度纹理（片元着色器）
    private static final String GREY2_TEXTURE_SAMPLER_UNIFORM = "uGrey2TextureSampler";
    // Grey 灰度纹理（片元着色器）
    private static final String GREY3_TEXTURE_SAMPLER_UNIFORM = "uGrey3TextureSampler";
    // Mask 覆盖纹理（片元着色器）
    private static final String MASK_TEXTURE_SAMPLER_UNIFORM = "uMaskTextureSampler";
    // Layer 覆盖纹理（片元着色器）
    private static final String LAYER_TEXTURE_SAMPLER_UNIFORM = "uLayerTextureSampler";
    // 纹理宽度 (片元着色器)（一般为宽度）
    private static final String WIDTH_UNIFORM = "uWidth";
    // 纹理高度 (片元着色器)（一般为高度）
    private static final String HEIGHT_UNIFORM = "uHeight";
    // 模糊程度 (片元着色器) (由低到高: 0.01 ~ 0.99)
    private static final String OPACITY_UNIFORM = "uOpacity";
    // 明亮程度 (片元着色器) (由低到高: 0.01 ~ 0.10)
    private static final String BRIGHTNESS_UNIFORM = "uBrightness";
    // 红润程度 (片元着色器) (由低到高: 0.01 ~ 0.10)
    private static final String TONE_UNIFORM = "uTone";
    // 纹理宽度差值（片元着色器）（一般为1/宽度）
    private static final String TEXEL_WIDTH_OFFSET_UNIFORM = "uTexelWidthOffset";
    // 纹理高度差值（片元着色器）（一般为1/高度）
    private static final String TEXEL_HEIGHT_OFFSET_UNIFORM = "uTexelHeightOffset";
    // 纹理糊化大小（片元着色器）
    private static final String BLUR_SIZE_UNIFORM = "uBlurSize";
    // 低性能（片元着色器）（0或1）
    private static final String LOW_PERFORMANCE_UNIFORM = "uLowPerformance";
    // 单步距离（片元着色器）（一般为1/高度）
    private static final String SINGLE_STEP_OFFSET_UNIFORM = "uSingleStepOffset";
    // 蜡笔力度（片元着色器）
    private static final String STRENGTH_UNIFORM = "uStrength";

    // 属性索引
    public int aPosition = -1;
    public int uPositionMatrix = -1;
    public int aTextureCoordinate = -1;
    public int uTextureMatrix = -1;
    public int uTextureSampler = -1;
    public int uTexture2Sampler = -1;
    public int uCurveTextureSampler = -1;
    public int uCurve2TextureSampler = -1;
    public int uGreyTextureSampler = -1;
    public int uGrey2TextureSampler = -1;
    public int uGrey3TextureSampler = -1;
    public int uMaskTextureSampler = -1;
    public int uLayerTextureSampler = -1;
    public int uWidth = -1;
    public int uHeight = -1;
    public int uOpacity = -1;
    public int uBrightness = -1;
    public int uTone = -1;
    public int uTexelWidthOffset = -1;
    public int uTexelHeightOffset = -1;
    public int uBlurSize = -1;
    public int uLowPerformance = -1;
    public int uSingleStepOffset = -1;
    public int uStrength = -1;

    /**
     * 配置属性
     */

    public void configAPosition(int program) {
        aPosition = GLES30.glGetAttribLocation(program, POSITION_ATTRIBUTE);
    }

    public void configUPositionMatrix(int program) {
        uPositionMatrix = GLES30.glGetUniformLocation(program, POSITION_MATRIX_UNIFORM);
    }

    public void configATextureCoordinate(int program) {
        aTextureCoordinate = GLES30.glGetAttribLocation(program, TEXTURE_COORDINATE_ATTRIBUTE);
    }

    public void configUTextureMatrix(int program) {
        uTextureMatrix = GLES30.glGetUniformLocation(program, TEXTURE_MATRIX_UNIFORM);
    }

    public void configUTextureSampler(int program) {
        uTextureSampler = GLES30.glGetUniformLocation(program, TEXTURE_SAMPLER_UNIFORM);
    }

    public void configUTexture2Sampler(int program) {
        uTexture2Sampler = GLES30.glGetUniformLocation(program, TEXTURE2_SAMPLER_UNIFORM);
    }

    public void configUCurveTextureSampler(int program) {
        uCurveTextureSampler = GLES30.glGetUniformLocation(program, CURVE_TEXTURE_SAMPLER_UNIFORM);
    }

    public void configUCurve2TextureSampler(int program) {
        uCurve2TextureSampler = GLES30.glGetUniformLocation(program, CURVE2_TEXTURE_SAMPLER_UNIFORM);
    }

    public void configUGreyTextureSampler(int program) {
        uGreyTextureSampler = GLES30.glGetUniformLocation(program, GREY_TEXTURE_SAMPLER_UNIFORM);
    }

    public void configUGrey2TextureSampler(int program) {
        uGrey2TextureSampler = GLES30.glGetUniformLocation(program, GREY2_TEXTURE_SAMPLER_UNIFORM);
    }

    public void configUGrey3TextureSampler(int program) {
        uGrey3TextureSampler = GLES30.glGetUniformLocation(program, GREY3_TEXTURE_SAMPLER_UNIFORM);
    }

    public void configUMaskTextureSampler(int program) {
        uMaskTextureSampler = GLES30.glGetUniformLocation(program, MASK_TEXTURE_SAMPLER_UNIFORM);
    }

    public void configULayerTextureSampler(int program) {
        uLayerTextureSampler = GLES30.glGetUniformLocation(program, LAYER_TEXTURE_SAMPLER_UNIFORM);
    }

    public void configUWidth(int program) {
        uWidth = GLES30.glGetUniformLocation(program, WIDTH_UNIFORM);
    }

    public void configUHeight(int program) {
        uHeight = GLES30.glGetUniformLocation(program, HEIGHT_UNIFORM);
    }

    public void configUOpacity(int program) {
        uOpacity = GLES30.glGetUniformLocation(program, OPACITY_UNIFORM);
    }

    public void configUBrightness(int program) {
        uBrightness = GLES30.glGetUniformLocation(program, BRIGHTNESS_UNIFORM);
    }

    public void configUTone(int program) {
        uTone = GLES30.glGetUniformLocation(program, TONE_UNIFORM);
    }

    public void configUTexelWidthOffset(int program) {
        uTexelWidthOffset = GLES30.glGetUniformLocation(program, TEXEL_WIDTH_OFFSET_UNIFORM);
    }

    public void configUTexelHeightOffset(int program) {
        uTexelHeightOffset = GLES30.glGetUniformLocation(program, TEXEL_HEIGHT_OFFSET_UNIFORM);
    }

    public void configUBlurSize(int program) {
        uBlurSize = GLES30.glGetUniformLocation(program, BLUR_SIZE_UNIFORM);
    }

    public void configULowPerformance(int program) {
        uLowPerformance = GLES30.glGetUniformLocation(program, LOW_PERFORMANCE_UNIFORM);
    }

    public void configUSingleStepOffset(int program) {
        uSingleStepOffset = GLES30.glGetUniformLocation(program, SINGLE_STEP_OFFSET_UNIFORM);
    }

    public void configUStrength(int program) {
        uStrength = GLES30.glGetUniformLocation(program, STRENGTH_UNIFORM);
    }

}
