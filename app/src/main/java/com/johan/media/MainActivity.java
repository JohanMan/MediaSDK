package com.johan.media;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.OrientationEventListener;
import android.view.View;
import android.widget.ImageView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.johan.media.window.BeautySettingWindow;
import com.johan.video.record.helper.ScreenHelper;
import com.johan.video.record.view.CameraView;

import java.io.IOException;
import java.io.InputStream;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    private CameraView cameraView;
    private View optionItemLayout;
    private TextView[] optionItemViews = new TextView[1];

    private BeautySettingWindow beautySettingWindow;

    private OrientationEventListener orientationEventListener;
    private int lastOrientation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ScreenHelper.translucentNavigation(this);
        cameraView = findViewById(R.id.camera_view);
        optionItemLayout = findViewById(R.id.option_item_layout);
        optionItemViews[0] = findViewById(R.id.option_item_beauty);
        initOrientationEventListener();
        orientationEventListener.enable();
    }

    /**
     * 初始化角度事件监听器
     */
    private void initOrientationEventListener() {
        orientationEventListener = new OrientationEventListener(this, SensorManager.SENSOR_DELAY_NORMAL) {
            @Override
            public void onOrientationChanged(int orientation) {
                if (orientation == OrientationEventListener.ORIENTATION_UNKNOWN) {
                    return;
                }
                int newOrientation = 0;
                if (orientation > 350 || orientation < 10) {
                    newOrientation = 0;
                } else if (orientation > 80 && orientation < 100) {
                    newOrientation = 90;
                } else if (orientation > 170 && orientation < 190) {
                    newOrientation = 180;
                } else if (orientation > 260 && orientation < 280) {
                    newOrientation = 270;
                }
                if (lastOrientation != newOrientation) {
                    Log.e(TAG, lastOrientation + " -> " + newOrientation);
                    cameraView.setOrientation(newOrientation);
                    lastOrientation = newOrientation;
                }
            }
        };
    }

    @Override
    protected void onDestroy() {
        orientationEventListener.disable();
        super.onDestroy();
    }

    /**
     * 选择美颜功能
     * @param view
     */
    public void selectBeauty(View view) {
        selectOptionItem(0);
        if (beautySettingWindow == null) {
            beautySettingWindow = new BeautySettingWindow(this);
            beautySettingWindow.setOnSettingChangedListener(new BeautySettingWindow.OnSettingChangedListener() {
                @Override
                public void onSettingChanged(int opacity, int brightness, int tone) {
                    cameraView.setBeauty(opacity, brightness, tone);
                }
            });
            beautySettingWindow.setOnDismissListener(new PopupWindow.OnDismissListener() {
                @Override
                public void onDismiss() {
                    optionItemViews[0].setSelected(false);
                }
            });
        }
        if (beautySettingWindow.isShowing()) {
            beautySettingWindow.dismiss();
            return;
        }
        beautySettingWindow.show(optionItemLayout);
    }

    /**
     * 选择贴图功能
     * @param view
     */
    public void selectWatermark(View view) {
        selectOptionItem(1);
    }

    /**
     * 选择功能
     * @param index
     */
    private void selectOptionItem(int index) {
        for (int i = 0; i < optionItemViews.length; i++) {
            optionItemViews[i].setSelected(i == index);
        }
    }

    /**
     * 切换摄像头
     * @param view
     */
    public void shift(View view) {
        cameraView.shiftCamera();
    }

    /**
     * 拍照
     * @param view
     */
    public void take(View view) {
        try {
            InputStream inputStream = getAssets().open("icon_face_rect.png");
            Bitmap bitmap = BitmapFactory.decodeStream(inputStream);
            cameraView.setFaceBitmap(bitmap);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 查看图片
     * @param view
     */
    public void picture(View view) {
        cameraView.setImageView((ImageView) findViewById(R.id.image_view));
    }

}
