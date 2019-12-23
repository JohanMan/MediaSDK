//
// Created by Administrator on 2019/12/6.
//

#ifndef MEDIASDK_FACE_DETECTOR_H
#define MEDIASDK_FACE_DETECTOR_H

#include <opencv2/opencv.hpp>
#include <opencv2/dnn.hpp>
#include <dlib/opencv/cv_image.h>
#include <dlib/image_processing/shape_predictor.h>
#include <ncnn/net.h>
#include "mtcnn.h"

using namespace cv;

/**
 * 特征点
 */
typedef struct {
    int x;
    int y;
} FPoint;

/**
 * 人脸检测结果
 */
typedef struct {
    int x;
    int y;
    int width;
    int height;
    FPoint points[68];
    double pitch;
    double yaw;
    double roll;
} Face;

/**
 * 四元数
 */
typedef struct {
    double w;
    double x;
    double y;
    double z;
} Quaterniond;

// 定义PI
#define PI 3.141592

class FaceDetector {
private:
    MTCNN *mtcnn;
    dlib::shape_predictor shapePredictor;
    Mat last;
    std::vector<Face> lastFaces;
    /**
     * 是否与上一帧相似
     * @param bgr
     * @return
     */
    bool isSimilarity(Mat &bgr);
    /**
     * 人脸检测
     * @param bgr
     * @return
     */
    std::vector<Face> detectFace(Mat &bgr);
    /**
     * 68关键点提取
     * @param bgr
     * @param faces
     * @param index
     */
    void landmarkFace(Mat &bgr, std::vector<Face> &faces, int index);
    /**
     * 计算角度
     * @param bgr
     * @param faces
     * @param index
     */
    void calculateDegree(Mat &bgr, std::vector<Face> &faces, int index);
public:
    /**
     * 构造函数
     * @param sdAssetPath
     */
    FaceDetector(const char *sdAssetPath);
    /**
     * 人脸检测
     * @param nv21
     * @param width
     * @param height
     * @param targetWidth
     * @param targetHeight
     * @param isFront
     * @return
     */
    std::vector<Face> detect(char *nv21, int width, int height, int targetWidth, int targetHeight, bool isFront);
};

#endif //MEDIASDK_FACE_DETECTOR_H
