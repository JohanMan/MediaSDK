package com.johan.video.record.camera;

/**
 * Created by johan on 2019/11/26.
 */

public class CameraException extends Exception {

    public static final int CODE_NOT_SUPPORT_CAMERA = -1;

    private int code;

    public CameraException(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }

    public String toMessage() {
        switch (code) {
            case CODE_NOT_SUPPORT_CAMERA :
                return "open camera fail : not support camera";
        }
        return "";
    }

}
