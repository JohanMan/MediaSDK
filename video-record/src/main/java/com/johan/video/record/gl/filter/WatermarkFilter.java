package com.johan.video.record.gl.filter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Matrix;
import android.graphics.PointF;

import com.johan.video.record.gl.util.GLLocation;
import com.johan.video.record.gl.util.ResourceUtil;

import java.nio.FloatBuffer;

/**
 * Created by johan on 2019/3/29.
 * 水印滤镜 绘制水印
 */

public class WatermarkFilter extends Filter {

    private WatermarkDrawer[] drawers;
    private int workDrawer;
    private boolean canRotate;
    private float scale;

    public WatermarkFilter(Context context, int max) {
        super(context);
        drawers = new WatermarkDrawer[max];
        for (int i = 0; i < max; i++) {
            drawers[i] = new WatermarkDrawer(context);
        }
    }

    @Override
    public void updateSize(int width, int height) {
        super.updateSize(width, height);
        for (WatermarkDrawer drawer : drawers) {
            drawer.updateSize(width, height);
        }
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
        clear();
        viewPort(0, 0, width, height);
        bindFrameBuffer(frameBuffer);
        enableVertexPointer(location.aPosition, vertexBuffer);
        enableVertexPointer(location.aTextureCoordinate, textureBuffer);
        bindTexture(location.uTextureSampler, texture);
        drawArrays();
        unbindTexture();
        disableVertexPointer(location.aTextureCoordinate);
        disableVertexPointer(location.aPosition);
        if (workDrawer > 0) {
            if (workDrawer > drawers.length) {
                workDrawer = drawers.length;
            }
            for (int i = 0; i < workDrawer; i++) {
                drawers[i].create();
                drawers[i].draw(texture);
            }
        }
        unbindFrameBuffer();
    }

    /**
     * 设置水印图片
     * @param bitmap
     */
    public void setBitmap(Bitmap bitmap) {
        // 只有正方形图片可以旋转
        canRotate = bitmap.getWidth() == bitmap.getHeight();
        // 支持旋转图片预处理
        if (canRotate) {
            // 由于支持设置角度 旋转时会导致图片被切割 所以要在原来的图片周边加空白
            // 图片旋转45度时为最大 所以最后的图片大小为sqrt(width*width + height*height) 也就是对角线大小
            int size = bitmap.getWidth();
            int targetSize = (int) Math.sqrt(size * size * 2);
            int padding = (targetSize - size) / 2;
            bitmap = ResourceUtil.paddingBitmap(bitmap, padding);
            // 由于图片四周填充了空白 图片有效范围相对于原始图片缩小了 所以下面设置位置和大小时需要设置倍数
            // 计算倍数
            scale = targetSize * 1.0f / size;
        } else {
            // 不支持倍数为0
            scale = 0;
        }
        for (WatermarkDrawer drawer : drawers) {
            drawer.setBitmap(bitmap);
        }
    }

    /**
     * 设置水印的位置
     * @param index
     * @param left
     * @param top
     * @param width
     * @param height
     * @param angel
     */
    public void updateLocation(int index, int left, int top, int width, int height, int angel) {
        if (canRotate) {
            PointF tl = scaleByPoint(left, top, left + width / 2, top + height / 2, scale);
            left = (int) tl.x;
            top = (int) tl.y;
            width = (int) (width * scale);
            height = (int) (height * scale);
        } else {
            angel = 0;
        }
        drawers[index].updateLocation(left, top, width, height, angel);
    }

    /**
     * 设置可绘制个数
     * @param workDrawer
     */
    public void setWorkDrawer(int workDrawer) {
        this.workDrawer = workDrawer;
    }

    /**
     * 计算
     * @param targetPointX
     * @param targetPointY
     * @param scaleCenterX
     * @param scaleCenterY
     * @param scale
     * @return
     */
    private PointF scaleByPoint(float targetPointX, float targetPointY, float scaleCenterX, float scaleCenterY, float scale){
        Matrix matrix = new Matrix();
        // 将Matrix移到到当前原点所在的位置，
        matrix.preTranslate(targetPointX, targetPointY);
        // 再以某个点为中心进行缩放
        matrix.postScale(scale, scale, scaleCenterX, scaleCenterY);
        float[] values = new float[9];
        matrix.getValues(values);
        // x y 坐标
        float x = values[Matrix.MTRANS_X];
        float y = values[Matrix.MTRANS_Y];
        return new PointF(x, y);
    }

}
