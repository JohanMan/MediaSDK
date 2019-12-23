package com.johan.media.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.annotation.Nullable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.johan.media.R;


/**
 * Created by johan on 2019/6/19.
 */

public class CustomSeekBar extends View {

    private static final int LINE_WIDTH = 4;
    private static final int SPACE = 20;

    private int min = 0;
    private int max = 10;
    private int textColor;
    private int lineColor;
    private int circleColor;
    private int circleTextColor;
    private int circleRadius;
    private int textSize;
    private int touchEffectLeft;
    private int touchEffectRight;
    private int stepLength;
    private int value;

    private Paint textPaint;
    private Paint linePaint;
    private Paint circlePaint;
    private Paint circleTextPaint;

    private OnValueChangedListener onValueChangedListener;

    public CustomSeekBar(Context context) {
        super(context);
    }

    public CustomSeekBar(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        TypedArray array = context.obtainStyledAttributes(attrs, R.styleable.CustomSeekBar);
        value = min = array.getInteger(R.styleable.CustomSeekBar_min, 0);
        max = array.getInteger(R.styleable.CustomSeekBar_max, 10);
        textColor = array.getColor(R.styleable.CustomSeekBar_text_color, Color.BLACK);
        lineColor = array.getColor(R.styleable.CustomSeekBar_line_color, Color.BLACK);
        circleColor = array.getColor(R.styleable.CustomSeekBar_circle_color, Color.BLACK);
        circleTextColor = array.getColor(R.styleable.CustomSeekBar_circle_text_color, Color.BLACK);
        circleRadius = array.getDimensionPixelOffset(R.styleable.CustomSeekBar_circle_radius, 20);
        array.recycle();
        textSize = context.getResources().getDimensionPixelSize(R.dimen.custom_seek_bar_text_size);
        textPaint = new Paint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(textSize);
        textPaint.setColor(textColor);
        linePaint = new Paint();
        linePaint.setAntiAlias(true);
        linePaint.setStrokeWidth(LINE_WIDTH);
        linePaint.setColor(lineColor);
        circlePaint = new Paint();
        circlePaint.setAntiAlias(true);
        circlePaint.setStyle(Paint.Style.FILL);
        circlePaint.setColor(circleColor);
        circleTextPaint = new Paint();
        circleTextPaint.setAntiAlias(true);
        circleTextPaint.setTextSize(textSize);
        circleTextPaint.setColor(circleTextColor);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        int width = getWidth();
        int height = getHeight();
        String minString = String.valueOf(min);
        Rect minRect = new Rect();
        textPaint.getTextBounds(minString, 0, minString.length(), minRect);
        String maxString = String.valueOf(max);
        Rect maxRect = new Rect();
        textPaint.getTextBounds(maxString, 0, maxString.length(), maxRect);
        canvas.drawText(minString, -minRect.left, -minRect.top + (height - minRect.height()) / 2, textPaint);
        canvas.drawLine(minRect.width() + SPACE, height / 2, width - maxRect.width() - SPACE, height / 2, linePaint);
        canvas.drawText(maxString, -maxRect.left + width - maxRect.width(), -maxRect.top + (height - maxRect.height()) / 2, textPaint);
        int offset = minRect.width() + SPACE;
        int lineLength = width - minRect.width() - maxRect.width() - 2 * SPACE - 2 * circleRadius;
        stepLength = lineLength / (max - min) + 1;
        touchEffectLeft = offset + circleRadius;
        touchEffectRight = offset + circleRadius + lineLength;
        drawCircle(canvas);
    }

    private void drawCircle(Canvas canvas) {
        int x = touchEffectLeft + stepLength * (value - min);
        int y = getHeight() / 2;
        canvas.drawCircle(x, y, circleRadius, linePaint);
        canvas.drawCircle(x, y, circleRadius - LINE_WIDTH, circlePaint);
        String valueString = String.valueOf(value);
        Rect rect = new Rect();
        circleTextPaint.getTextBounds(valueString, 0, valueString.length(), rect);
        canvas.drawText(valueString, -rect.left + x - rect.width() / 2, -rect.top + y - rect.height() / 2, circleTextPaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN :
            case MotionEvent.ACTION_MOVE :
                float x = event.getX();
                if (x > touchEffectLeft && x < touchEffectRight) {
                    float step = (x - touchEffectLeft) / stepLength;
                    int newValue = Math.round(step) + min;
                    if (newValue != value) {
                        if (onValueChangedListener != null) {
                            onValueChangedListener.onValueChanged(value, newValue);
                        }
                        value = newValue;
                        invalidate();
                    }
                    return true;
                } else {
                    return false;
                }
        }
        return super.onTouchEvent(event);
    }

    public void setValue(int value) {
        this.value = value;
        invalidate();
    }

    public void setOnValueChangedListener(OnValueChangedListener onValueChangedListener) {
        this.onValueChangedListener = onValueChangedListener;
    }

    public interface OnValueChangedListener {
        void onValueChanged(int oldValue, int newValue);
    }

}
