package com.johan.video.record.gl.filter;

import android.content.Context;
import android.opengl.EGL14;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;

import com.johan.video.record.R;
import com.johan.video.record.gl.util.DataUtil;
import com.johan.video.record.gl.util.GLESUtil;
import com.johan.video.record.gl.util.GLLocation;

import java.nio.FloatBuffer;

/**
 * Created by johan on 2019/3/29.
 */

public abstract class Filter {

    // 上下文
    private Context context;

    // 窗口大小
    private int width;
    private int height;

    // 顶点数据
    private FloatBuffer vertexBuffer;
    // 纹理数据
    private FloatBuffer textureBuffer;

    // gl程序索引
    private int program = -1;
    // 位置索引
    private GLLocation location;

    public Filter(Context context) {
        this.context = context;
    }

    /**
     * 创建
     */
    public void create() {
        if (program != -1) return;
        program = GLESUtil.loadProgram(context, getVertexShader(), getFrameShader());
        vertexBuffer = getVertexBuffer();
        textureBuffer = getTextureBuffer();
        location = new GLLocation();
        configLocation(program, location);
    }

    /**
     * 更新窗口大小
     * @param width
     * @param height
     */
    public void updateSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    /**
     * 销毁
     */
    public void destroy() {

    }

    /**
     * 获取窗口宽度
     * @return
     */
    public int getWidth() {
        return width;
    }

    /**
     * 获取窗口高度
     * @return
     */
    public int getHeight() {
        return height;
    }

    /**
     * 绘制到FBO
     * @param texture
     * @param frameBuffer
     * 需要在GL线程绘制
     */
    public void draw(int texture, int frameBuffer) {
        draw(width, height, location, texture, frameBuffer, vertexBuffer, textureBuffer);
    }

    /**
     * 绘制输出
     * @param texture
     * 需要在GL线程绘制
     */
    public void draw(int texture) {
        draw(texture, 0);
    }

    /**
     * 配置位置索引
     * @param program
     * @param location
     */
    protected abstract void configLocation(int program, GLLocation location);

    /**
     * 绘制
     * @param width
     * @param height
     * @param location
     * @param texture
     * @param frameBuffer
     * @param vertexBuffer
     * @param textureBuffer
     */
    protected abstract void draw(int width, int height, GLLocation location, int texture, int frameBuffer, FloatBuffer vertexBuffer, FloatBuffer textureBuffer);

    /**
     * 顶点着色器
     * 子类可复写
     * @return
     */
    protected int getVertexShader() {
        return R.raw.vertex_shader;
    }

    /**
     * 片元着色器
     * 子类可复写
     * @return
     */
    protected int getFrameShader() {
        return R.raw.fragment_shader;
    }

    /**
     * 顶点坐标数据
     * 子类可复写
     * @return
     */
    protected FloatBuffer getVertexBuffer() {
        return DataUtil.loadVertexData();
    }

    /**
     * 纹理坐标数据
     * 子类可复写
     * @return
     */
    protected FloatBuffer getTextureBuffer() {
        return DataUtil.loadTextureData();
    }

    /**
     * ===================================== GL 绘制 =====================================
     */

    /**
     * 使用gl程序
     */
    protected void useProgram() {
        GLES30.glUseProgram(program);
    }

    /**
     * 设置窗口大小
     * @param x
     * @param y
     * @param width
     * @param height
     */
    protected void viewPort(int x, int y, int width, int height) {
        GLES30.glViewport(x, y, width, height);
    }

    /**
     * 清屏
     */
    protected void clear() {
        GLES30.glClearColor(0, 0, 0 ,1);
        GLES30.glClear(GLES30.GL_COLOR_BUFFER_BIT);
    }

    /**
     * 绑定帧缓存
     * 离屏渲染
     * @param frameBuffer
     */
    protected void bindFrameBuffer(int frameBuffer) {
         GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffer);
    }

    /**
     * 解除绑定帧缓存
     */
    protected void unbindFrameBuffer() {
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
    }

    /**
     * 允许设置顶点坐标属性
     * @param index
     * @param buffer
     */
    protected void enableVertexPointer(int index, FloatBuffer buffer) {
        buffer.position(0);
        GLES30.glEnableVertexAttribArray(index);
        GLES30.glVertexAttribPointer(index, 2, GLES30.GL_FLOAT, false, 0, buffer);
    }

    /**
     * 禁止设置顶点坐标属性
     * @param index
     */
    protected void disableVertexPointer(int index) {
        GLES30.glDisableVertexAttribArray(index);
    }

    /**
     * 指定Uniform浮点变量值
     * @param location
     * @param value
     */
    protected void setUniformFloat(int location, float value) {
        GLES30.glUniform1f(location, value);
    }

    /**
     * 指定Uniform浮点变量值
     * @param location
     * @param value
     */
    protected void setUniformFloat2(int location, float[] value) {
        GLES30.glUniform2fv(location, 1, FloatBuffer.wrap(value));
    }

    /**
     * 指定Uniform整数变量值
     * @param location
     * @param value
     */
    protected void setUniformInteger(int location, int value) {
        GLES30.glUniform1i(location, value);
    }

    /**
     * 指定Uniform矩阵变量值
     * @param location
     * @param matrix
     */
    protected void setUniformMatrix4(int location, float[] matrix) {
        GLES30.glUniformMatrix4fv(location, 1, false, matrix, 0);
    }


    /**
     * 下一个活动的纹理ID
     */
    private int nextActiveTextureId;

    /**
     * 绑定摄像机纹理
     * @param location
     * @param texture
     */
    protected void bindCameraTexture(int location, int texture) {
        // 激动纹理
        GLES30.glActiveTexture(getNextActiveTexture());
        // 自动绑定到激活纹理中
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, texture);
        // 激动纹理ID(nextActiveTextureId)赋值给samplerExternalOES或者sampler2D
        // 注意不是用生成的纹理ID(texture)来赋值
        GLES30.glUniform1i(location, nextActiveTextureId);
        // 累加
        nextActiveTextureId++;
    }

    /**
     * 解除绑定摄像机纹理
     */
    protected void unbindCameraTexture() {
        nextActiveTextureId = 0;
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
    }

    /**
     * 绑定普通纹理
     * @param location
     * @param texture
     */
    protected void bindTexture(int location, int texture) {
        // 激动纹理
        GLES30.glActiveTexture(getNextActiveTexture());
        // 自动绑定到激活纹理中
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
        // 激动纹理ID(nextActiveTextureId)赋值给samplerExternalOES或者sampler2D
        // 注意不是用生成的纹理ID(texture)来赋值
        GLES30.glUniform1i(location, nextActiveTextureId);
        // 累加
        nextActiveTextureId++;
    }

    /**
     * 解除绑定普通纹理
     */
    protected void unbindTexture() {
        nextActiveTextureId = 0;
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    /**
     * 获取下一个活动的纹理
     * 最多可绑定10个纹理
     * @return
     */
    private int getNextActiveTexture() {
        switch (nextActiveTextureId) {
            case 0 : return GLES30.GL_TEXTURE0;
            case 1 : return GLES30.GL_TEXTURE1;
            case 2 : return GLES30.GL_TEXTURE2;
            case 3 : return GLES30.GL_TEXTURE3;
            case 4 : return GLES30.GL_TEXTURE4;
            case 5 : return GLES30.GL_TEXTURE5;
            case 6 : return GLES30.GL_TEXTURE6;
            case 7 : return GLES30.GL_TEXTURE7;
            case 8 : return GLES30.GL_TEXTURE8;
            case 9 : return GLES30.GL_TEXTURE9;
            default: return GLES30.GL_TEXTURE10;
        }
    }

    /**
     * 绘制图形
     */
    protected void drawArrays() {
        GLES30.glDrawArrays(GLES30.GL_TRIANGLES, 0, 6);
    }

}
