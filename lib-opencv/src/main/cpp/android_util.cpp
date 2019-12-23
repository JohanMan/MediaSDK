#include <face_detector.h>
#include "android_util.h"

#define JAVA_FACE_DETECT_RESULT_CLASS "com/johan/opencv/FaceDetectResult"
#define JAVA_FEATURE_POINT_CLASS "com/johan/opencv/FaceDetectResult$FeaturePoint"

namespace autil {

    /**
     * 转换人脸检测结果
     * @param faces
     * @return
     */
    jobjectArray convertFaces(JNIEnv *env, std::vector<Face> faces) {
        jclass faceResultClass = env->FindClass(JAVA_FACE_DETECT_RESULT_CLASS);
        jmethodID initMethodId = env->GetMethodID(faceResultClass, "<init>", "()V");
        jfieldID xField = env->GetFieldID(faceResultClass, "x", "I");
        jfieldID yField = env->GetFieldID(faceResultClass, "y", "I");
        jfieldID widthField = env->GetFieldID(faceResultClass, "width", "I");
        jfieldID heightField = env->GetFieldID(faceResultClass, "height", "I");
        jfieldID pitchField = env->GetFieldID(faceResultClass, "pitch", "D");
        jfieldID yawField = env->GetFieldID(faceResultClass, "yaw", "D");
        jfieldID rollField = env->GetFieldID(faceResultClass, "roll", "D");
        jfieldID featuresField = env->GetFieldID(faceResultClass, "features", "[Lcom/johan/opencv/FaceDetectResult$FeaturePoint;");
        jclass featurePointClass = env->FindClass(JAVA_FEATURE_POINT_CLASS);
        jmethodID init2MethodId = env->GetMethodID(featurePointClass, "<init>", "()V");
        jfieldID x2Field = env->GetFieldID(featurePointClass, "x", "I");
        jfieldID y2Field = env->GetFieldID(featurePointClass, "y", "I");
        jobjectArray faceResults = env->NewObjectArray(faces.size(), faceResultClass, NULL);
        for (int i = 0; i < faces.size(); i++) {
            Face face = faces[i];
            jobject faceResult = env->NewObject(faceResultClass, initMethodId);
            env->SetIntField(faceResult, xField, face.x);
            env->SetIntField(faceResult, yField, face.y);
            env->SetIntField(faceResult, widthField, face.width);
            env->SetIntField(faceResult, heightField, face.height);
            env->SetDoubleField(faceResult, pitchField, face.pitch);
            env->SetDoubleField(faceResult, yawField, face.yaw);
            env->SetDoubleField(faceResult, rollField, face.roll);
            jobjectArray featurePoints = env->NewObjectArray(68, featurePointClass, NULL);
            for (int j = 0; j < 68; j++) {
                FPoint point = face.points[j];
                jobject featurePoint = env->NewObject(featurePointClass, init2MethodId);
                env->SetIntField(featurePoint, x2Field, point.x);
                env->SetIntField(featurePoint, y2Field, point.y);
                env->SetObjectArrayElement(featurePoints, j, featurePoint);
            }
            env->SetObjectField(faceResult, featuresField, featurePoints);
            env->SetObjectArrayElement(faceResults, i, faceResult);
        }
        return faceResults;
    }

}

