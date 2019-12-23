package com.johan.opencv;

import android.os.Handler;
import android.os.HandlerThread;

/**
 * Created by johan on 2019/12/4.
 */

public class OpencvThread {

    private static HandlerThread handlerThread;
    private static Handler handler;

    static {
        handlerThread = new HandlerThread("OpenCV");
        handlerThread.start();
        handler = new Handler(handlerThread.getLooper());
    }

    public static void execute(Runnable command) {
        handler.post(command);
    }

}
