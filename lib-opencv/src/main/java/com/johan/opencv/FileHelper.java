package com.johan.opencv;

import android.content.Context;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by johan on 2019/11/28.
 */

public class FileHelper {

    /**
     * 获取SD卡Asset路径
     * @return
     */
    public static String getSDAssetPath(Context context) {
        return context.getExternalFilesDir("OpenCV") + "/";
    }

    /**
     * 将Asset文件复制到SD卡
     * @param context
     * @param assetFile
     * @param targetFile
     * @param isCover
     * @throws IOException
     */
    public static void copyAssetFile(Context context, String assetFile, String targetFile, boolean isCover) throws IOException {
        // 检测文件是否存在
        File file = new File(targetFile);
        if (file.exists()) {
            if (isCover) {
                file.delete();
            } else {
                return;
            }
        }
        // 拷贝
        InputStream inputStream = context.getAssets().open(assetFile);
        OutputStream outputStream = new FileOutputStream(targetFile);
        byte[] buffer = new byte[1024];
        int read;
        while ((read = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, read);
        }
        inputStream.close();
        outputStream.flush();
        outputStream.close();
    }

}
