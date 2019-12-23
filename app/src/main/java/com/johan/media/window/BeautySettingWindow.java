package com.johan.media.window;

import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupWindow;

import com.johan.media.R;
import com.johan.media.view.CustomSeekBar;

/**
 * Created by johan on 2019/11/27.
 */

public class BeautySettingWindow extends PopupWindow {

    private int height;
    private OnSettingChangedListener onSettingChangedListener;

    private int opacity, brightness, tone;

    public BeautySettingWindow(Context context) {
        super(context);
        setWidth(ViewGroup.LayoutParams.MATCH_PARENT);
        setHeight(ViewGroup.LayoutParams.WRAP_CONTENT);
        View layout = LayoutInflater.from(context).inflate(R.layout.window_beauty_setting, null);
        CustomSeekBar opacitySeekBar = layout.findViewById(R.id.opacity_seek_bar);
        opacitySeekBar.setOnValueChangedListener(opacityValueChangedListener);
        CustomSeekBar brightnessSeekBar = layout.findViewById(R.id.brightness_seek_bar);
        brightnessSeekBar.setOnValueChangedListener(brightnessValueChangedListener);
        CustomSeekBar toneSeekBar = layout.findViewById(R.id.tone_seek_bar);
        toneSeekBar.setOnValueChangedListener(toneValueChangedListener);
        layout.measure(View.MeasureSpec.UNSPECIFIED, View.MeasureSpec.UNSPECIFIED);
        height = layout.getMeasuredHeight();
        setContentView(layout);
        setOutsideTouchable(true);
        setBackgroundDrawable(new BitmapDrawable());
    }

    private CustomSeekBar.OnValueChangedListener opacityValueChangedListener = new CustomSeekBar.OnValueChangedListener() {
        @Override
        public void onValueChanged(int oldValue, int newValue) {
            opacity = newValue;
            notifyClient();
        }
    };

    private CustomSeekBar.OnValueChangedListener brightnessValueChangedListener = new CustomSeekBar.OnValueChangedListener() {
        @Override
        public void onValueChanged(int oldValue, int newValue) {
            brightness = newValue;
            notifyClient();
        }
    };

    private CustomSeekBar.OnValueChangedListener toneValueChangedListener = new CustomSeekBar.OnValueChangedListener() {
        @Override
        public void onValueChanged(int oldValue, int newValue) {
            tone = newValue;
            notifyClient();
        }
    };

    private void notifyClient() {
        if (onSettingChangedListener != null) {
            onSettingChangedListener.onSettingChanged(opacity, brightness, tone);
        }
    }

    public void show(View parent) {
        int[] locations = new int[2];
        parent.getLocationInWindow(locations);
        int left = 0;
        int top = locations[1] - height;
        showAtLocation(parent, Gravity.NO_GRAVITY, left, top);
    }

    public void setOnSettingChangedListener(OnSettingChangedListener onSettingChangedListener) {
        this.onSettingChangedListener = onSettingChangedListener;
    }

    public interface OnSettingChangedListener {
        void onSettingChanged(int opacity, int brightness, int tone);
    }

}
