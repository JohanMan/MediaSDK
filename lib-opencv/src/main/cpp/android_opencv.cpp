#include <jni.h>
#include <android/bitmap.h>
#include <opencv2/core/mat.hpp>
#include <face_detector.h>
#include <opencv2/imgproc.hpp>
#include <android_util.h>

#define JNI_CLASS "com/johan/opencv/AndroidOpencv"

static jlong jni_face_detect_create(JNIEnv *env, jclass clazz, jstring sdAssetPath_) {
    const char *sdAssetPath = env->GetStringUTFChars(sdAssetPath_, NULL);
    FaceDetector *faceDetector = new FaceDetector(sdAssetPath);
    env->ReleaseStringUTFChars(sdAssetPath_, sdAssetPath);
    return reinterpret_cast<long>(faceDetector);
}

static jobjectArray jni_face_detect(JNIEnv *env, jclass clazz, jlong ptr, jbyteArray nv21_, jint width, jint height, jint targetWidth, jint targetHeight, jboolean isFront) {
    jbyte *nv21 = env->GetByteArrayElements(nv21_, NULL);
    FaceDetector *faceDetector = reinterpret_cast<FaceDetector*>(ptr);
    std::vector<Face> faces = faceDetector->detect((char *) nv21, width, height, targetWidth, targetHeight, isFront);
    jobjectArray results = autil::convertFaces(env, faces);
    env->ReleaseByteArrayElements(nv21_, nv21, 0);
    return results;
}

static void jni_face_detect_destroy(JNIEnv *env, jclass clazz, jlong ptr) {
    FaceDetector *faceDetector = reinterpret_cast<FaceDetector*>(ptr);
    delete faceDetector;
}

static JNINativeMethod nativeMethods[] = {
        {"faceDetectCreate", "(Ljava/lang/String;)J", (void *) jni_face_detect_create},
        {"faceDetect", "(J[BIIIIZ)[Lcom/johan/opencv/FaceDetectResult;", (void *) jni_face_detect},
        {"faceDetectDestroy", "(J)V", (void *) jni_face_detect_destroy}
};

static jint registerNativeMethods(JNIEnv *env) {
    jclass clazz = env->FindClass(JNI_CLASS);
    if (clazz == NULL) {
        return JNI_FALSE;
    }
    if (env->RegisterNatives(clazz, nativeMethods, sizeof(nativeMethods) / sizeof(nativeMethods[0])) < 0) {
        return JNI_FALSE;
    }
    return JNI_TRUE;
}

jint JNI_OnLoad(JavaVM *vm, void* reserved) {
    JNIEnv *env;
    if (vm->GetEnv((void**) &env, JNI_VERSION_1_4) != JNI_OK) {
        return -1;
    }
    if (!registerNativeMethods(env)) {
        return -1;
    }
    return JNI_VERSION_1_4;
}