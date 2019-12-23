package com.johan.video.record.gl.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES11Ext;
import android.opengl.GLES30;
import android.opengl.GLUtils;

import java.nio.ByteBuffer;

/**
 * Created by johan on 2018/12/6.
 * 函数执行前提
 * 1.搭建 GL 环境
 * 2.GL 线程执行
 */

public class GLESUtil {

    /**
     * 加载 GL 程序
     * @param context
     * @param vertexShader 顶点着色器脚本 ID
     * @param frameShader 片元着色器脚本 ID
     * @return
     */
    public static int loadProgram(Context context, int vertexShader, int frameShader) {
        // 加载顶点着色器
        int verticesShader = loadShader(GLES30.GL_VERTEX_SHADER, ResourceUtil.readShader(context, vertexShader));
        // 加载顶点着色器失败
        if (verticesShader == 0) {
            return 0;
        }
        // 加载片元着色器
        int fragmentShader = loadShader(GLES30.GL_FRAGMENT_SHADER, ResourceUtil.readShader(context, frameShader));
        // 加载片元着色器失败
        if (fragmentShader == 0) {
            return 0;
        }
        // 创建 GL 程序
        int program = GLES30.glCreateProgram();
        // 创建失败
        if (program == 0) {
            return 0;
        }
        // 添加着色器
        GLES30.glAttachShader(program, verticesShader);
        GLES30.glAttachShader(program, fragmentShader);
        // 链接程序
        GLES30.glLinkProgram(program);
        // 创建数组 保存链接结果
        int[] linkStatus = new int[1];
        // 获取链接结果
        GLES30.glGetProgramiv(program, GLES30.GL_LINK_STATUS, linkStatus, 0);
        // 链接失败
        if (linkStatus[0] == 0) {
            // 删除程序
            GLES30.glDeleteProgram(program);
            return 0;
        }
        // 链接成功
        return program;
    }

    /**
     * 卸载 GL 程序
     * @param program
     */
    public static void unloadProgram(int program) {
        GLES30.glDeleteProgram(program);
    }

    /**
     * 加载着色器
     * @param type
     * @param source
     */
    private static int loadShader(int type, String source) {
        // 创建着色器
        int shader = GLES30.glCreateShader(type);
        // 创建失败
        if (shader == 0) return 0;
        // 创建成功
        // 加载源码
        GLES30.glShaderSource(shader, source);
        // 编译源码
        GLES30.glCompileShader(shader);
        // 创建数组 保存编译状态
        int[] compiled = new int[1];
        // 获取编译状态
        GLES30.glGetShaderiv(shader, GLES30.GL_COMPILE_STATUS, compiled, 0);
        // 编译失败
        if (compiled[0] == 0) {
            // 删除着色器
            GLES30.glDeleteShader(shader);
            return 0;
        }
        // 编译成功
        return shader;
    }

    /**
     * 创建 OES 纹理
     * @return
     */
    public static int createOESTexture() {
        int[] textures = new int[1];
        // 生成一个纹理
        GLES30.glGenTextures(1, textures, 0);
        // 绑定 GL_TEXTURE_EXTERNAL_OES 纹理
        // 指定接下来设置 GL_TEXTURE_EXTERNAL_OES 纹理属性 都是针对 textures[0] 的
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, textures[0]);
        // 设置属性
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_NEAREST);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        // 解除绑定
        GLES30.glBindTexture(GLES11Ext.GL_TEXTURE_EXTERNAL_OES, 0);
        return textures[0];
    }

    /**
     * 创建空纹理
     * @param width
     * @param height
     * @return
     */
    public static int createTexture(int width, int height) {
        int[] textures = new int[1];
        // 生成纹理
        GLES30.glGenTextures(1, textures, 0);
        // 绑定 GL_TEXTURE_2D 纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        // 设置 GL_TEXTURE_2D 纹理属性
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        // 设置 GL_TEXTURE_2D 纹理数据
        // 相当于平时使用 GLUtils.texImage2D 填充纹理数据 现在数据暂时为空
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, width, height, 0,
                GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, null);
        // 解除绑定
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        return textures[0];
    }

    /**
     * 创建纹理
     * @param bitmap
     * @return
     */
    public static int createTexture(Bitmap bitmap) {
        int[] textures = new int[1];
        // 生成纹理
        GLES30.glGenTextures(1, textures, 0);
        // 绑定 GL_TEXTURE_2D 纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        // 设置 GL_TEXTURE_2D 纹理属性
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        // 设置 GL_TEXTURE_2D 纹理数据
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        // 解除绑定
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        return textures[0];
    }

    /**
     * 创建纹理
     * @param data
     * @return
     */
    public static int createTexture(byte[] data) {
        int[] textures = new int[1];
        // 生成纹理
        GLES30.glGenTextures(1, textures, 0);
        // 绑定 GL_TEXTURE_2D 纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, textures[0]);
        // 设置 GL_TEXTURE_2D 纹理属性
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MAG_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_MIN_FILTER, GLES30.GL_LINEAR);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_S, GLES30.GL_CLAMP_TO_EDGE);
        GLES30.glTexParameterf(GLES30.GL_TEXTURE_2D, GLES30.GL_TEXTURE_WRAP_T, GLES30.GL_CLAMP_TO_EDGE);
        // 设置 GL_TEXTURE_2D 纹理数据
        GLES30.glTexImage2D(GLES30.GL_TEXTURE_2D, 0, GLES30.GL_RGBA, 256, 1, 0, GLES30.GL_RGBA, GLES30.GL_UNSIGNED_BYTE, ByteBuffer.wrap(data));
        // 解除绑定
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
        return textures[0];
    }

    /**
     * 更新纹理
     * @param bitmap
     * @param texture
     * @return
     */
    public static void updateTexture(Bitmap bitmap, int texture) {
        // 绑定 GL_TEXTURE_2D 纹理
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, texture);
        // 设置 GL_TEXTURE_2D 纹理数据
        GLUtils.texImage2D(GLES30.GL_TEXTURE_2D, 0, bitmap, 0);
        // 解除绑定
        GLES30.glBindTexture(GLES30.GL_TEXTURE_2D, 0);
    }

    /**
     * 销毁纹理
     * @param texture
     */
    public static void destroyTexture(int texture) {
        int[] textures = new int[1];
        textures[0] = texture;
        GLES30.glDeleteTextures(1, textures, 0);
    }

    /**
     * 创建 MediaFrame Buffer
     * @param textureId
     * @return
     */
    public static int createFrameBuffer(int textureId) {
        int[] frameBuffers = new int[1];
        // 生成 MediaFrame Buffer
        GLES30.glGenFramebuffers(1, frameBuffers, 0);
        // 绑定 GL_FRAMEBUFFER
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, frameBuffers[0]);
        // 关联 MediaFrame Buffer 和 Texture 纹理
        GLES30.glFramebufferTexture2D(GLES30.GL_FRAMEBUFFER, GLES30.GL_COLOR_ATTACHMENT0,
                GLES30.GL_TEXTURE_2D, textureId, 0);
        // 解除绑定
        GLES30.glBindFramebuffer(GLES30.GL_FRAMEBUFFER, 0);
        return frameBuffers[0];
    }

    /**
     * 销毁 MediaFrame Buffer
     * @param frameBuffer
     */
    public static void destroyFrameBuffer(int frameBuffer) {
        int[] frameBuffers = new int[1];
        frameBuffers[0] = frameBuffer;
        GLES30.glDeleteFramebuffers(1, frameBuffers, 0);
    }

}
