package com.johan.video.record.gl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.opengl.GLES30;
import android.opengl.Matrix;

import com.johan.video.record.R;
import com.johan.video.record.gl.util.DataUtil;
import com.johan.video.record.gl.util.GLESUtil;
import com.johan.video.record.gl.util.GLLocation;

import java.nio.FloatBuffer;

/**
 * Created by johan on 2019/3/29.
 * 水印绘制 也是滤镜 不过只负责绘制 并不做绑定和解绑
 * see {@link WatermarkFilter}
 */

public class WatermarkDrawer extends Filter {

    private float[] matrix = new float[16];
    private Bitmap bitmap;
    private int watermarkTexture = 0;
    private int locationLeft, locationTop;
    private int locationWidth, locationHeight;

    public WatermarkDrawer(Context context) {
        super(context);
        resetMatrix();
    }

    @Override
    public void destroy() {
        watermarkTexture = -1;
    }

    @Override
    protected int getVertexShader() {
        return R.raw.vertex_watermark_shader;
    }

    @Override
    protected FloatBuffer getTextureBuffer() {
        return DataUtil.loadTextureData(false, true);
    }

    @Override
    protected void configLocation(int program, GLLocation location) {
        location.configUPositionMatrix(program);
        location.configAPosition(program);
        location.configATextureCoordinate(program);
        location.configUTextureSampler(program);
    }

    @Override
    protected void draw(int width, int height, GLLocation location, int texture, int frameBuffer, FloatBuffer vertexBuffer, FloatBuffer textureBuffer) {
        if (bitmap != null) {
            if (watermarkTexture != 0) {
                GLESUtil.destroyTexture(watermarkTexture);
            }
            watermarkTexture = GLESUtil.createTexture(bitmap);
            bitmap = null;
        }
        if (watermarkTexture == 0) return;
        useProgram();
        viewPort(locationLeft, height - locationHeight - locationTop, locationWidth, locationHeight);
        // 加上以下两行代码后 水印的图片才能恢复透明 否则以黑底呈现
        GLES30.glEnable(GLES30.GL_BLEND);                                   // 打开混合功能
        GLES30.glBlendFunc(GLES30.GL_ONE, GLES30.GL_ONE_MINUS_SRC_ALPHA);   // 指定混合模式
        setUniformMatrix4(location.uPositionMatrix, matrix);
        enableVertexPointer(location.aPosition, vertexBuffer);
        enableVertexPointer(location.aTextureCoordinate, textureBuffer);
        bindTexture(location.uTextureSampler, watermarkTexture);
        drawArrays();
        unbindTexture();
        disableVertexPointer(location.aTextureCoordinate);
        disableVertexPointer(location.aPosition);
    }

    /**
     * 设置图片
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    /**
     * 更新位置
     * @param left
     * @param top
     * @param width
     * @param height
     * @param angle
     */
    public void updateLocation(int left, int top, int width, int height, int angle) {
        // 重置
        resetMatrix();
        // 赋值
        locationLeft = left;
        locationTop = top;
        locationWidth = width;
        locationHeight = height;
        // 旋转
        // 注意 是以z轴旋转 而且角度是逆时针的
        Matrix.rotateM(matrix, 0, -angle, 0, 0, 1.0f);
    }

    /**
     * 重置矩阵
     */
    private void resetMatrix() {
        // row 1
        matrix[0] = 1;
        matrix[1] = 0;
        matrix[2] = 0;
        matrix[3] = 0;
        // row 2
        matrix[4] = 0;
        matrix[5] = 1;
        matrix[6] = 0;
        matrix[7] = 0;
        // row 3
        matrix[8] = 0;
        matrix[9] = 0;
        matrix[10] = 1;
        matrix[11] = 0;
        // row 4
        matrix[12] = 0;
        matrix[13] = 0;
        matrix[14] = 0;
        matrix[15] = 1;
    }

}
