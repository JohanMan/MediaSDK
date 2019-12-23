package com.johan.video.record.gl.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * Created by johan on 2018/12/11.
 */

public class ResourceUtil {

    /**
     * 读取着色器脚本
     * @param context
     * @param resourceId
     * @return
     */
    public static String readShader(Context context, int resourceId) {
        StringBuilder builder = new StringBuilder();
        InputStream inputStream = null;
        InputStreamReader streamReader = null;
        BufferedReader bufferedReader = null;
        try {
            inputStream = context.getResources().openRawResource(resourceId);
            streamReader = new InputStreamReader(inputStream);
            bufferedReader = new BufferedReader(streamReader);
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                builder.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }
                if (streamReader != null) {
                    streamReader.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return builder.toString();
    }

    /**
     * 加载Bitmap
     * @param context
     * @param assetPath
     * @return
     */
    public static Bitmap loadBitmap(Context context, String assetPath) {
        Bitmap image = null;
        AssetManager assetManager = context.getResources().getAssets();
        try{
            InputStream inputStream = assetManager.open(assetPath);
            image = BitmapFactory.decodeStream(inputStream);
            inputStream.close();
        } catch (IOException e){
            e.printStackTrace();
        }
        return image;
    }

    /**
     * 周边填充空白
     * @param source
     * @param padding
     * @return
     */
    public static Bitmap paddingBitmap(Bitmap source, int padding) {
        Bitmap bitmap = Bitmap.createBitmap(source.getWidth() + padding * 2, source.getHeight() + padding * 2, source.getConfig());
        Canvas canvas = new Canvas(bitmap);
        canvas.drawARGB(0, 0, 0, 0);
        Paint paint = new Paint();
        paint.setStyle(Paint.Style.FILL);
        paint.setAntiAlias(true);
        canvas.drawBitmap(source, padding, padding, paint);
        return bitmap;
    }

}
