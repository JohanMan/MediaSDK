//
// Created by Administrator on 2019/12/6.
//

#ifndef MEDIASDK_OPENCV_LOG_H
#define MEDIASDK_OPENCV_LOG_H

#include <android/log.h>
#include <jni.h>
#include "face_detector.h"

#define TAG "OpenCV"
#define LOGE(...) __android_log_print(ANDROID_LOG_ERROR, TAG, __VA_ARGS__)

namespace autil {

    /**
     * 转换人脸检测结果
     * @param faces
     * @return
     */
    jobjectArray convertFaces(JNIEnv *env, std::vector<Face> faces);

}

#endif //MEDIASDK_OPENCV_LOG_H
