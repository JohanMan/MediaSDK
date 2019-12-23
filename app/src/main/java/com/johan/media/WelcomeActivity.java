package com.johan.media;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.johan.media.helper.PermissionHelper;
import com.johan.video.record.helper.ScreenHelper;

/**
 * Created by johan on 2019/11/26.
 */

public class WelcomeActivity extends AppCompatActivity implements PermissionHelper.OnPermissionCallback {

    private static final String[] PERMISSIONS = {
            Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.READ_PHONE_STATE
    };

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_welcome);
        ScreenHelper.translucentNavigation(this);
        PermissionHelper.requestPermission(this, PERMISSIONS, this);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        PermissionHelper.handlePermissionResult(requestCode, permissions, grantResults, this);
    }

    @Override
    public void onPermissionAccept(int requestCode, String... permissions) {
        findViewById(android.R.id.content).postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(WelcomeActivity.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        }, 3000);
    }

    @Override
    public void onPermissionRefuse(int requestCode, String... permission) {
        Toast.makeText(this, "权限没通过", Toast.LENGTH_LONG).show();
    }

}
