#include <face_detector.h>
#include <vector>
#include <android_util.h>
#include <model_utils.hpp>
#include <opencv_util.h>
#include <c_util.h>

/**
* 是否与上一帧相似
* @param bgr
* @return
*/
bool FaceDetector::isSimilarity(Mat &bgr) {
    if (!last.empty()) {
        double similarity = cvutil::calculateSimilarity(last, bgr);
        return similarity >= 0.95;
    }
    return false;
}

/**
 * 人脸检测
 * @param bgr
 * @return
 */
std::vector<Face> FaceDetector::detectFace(Mat &bgr) {
    unsigned char *data = bgr.data;
    int width = bgr.cols;
    int height = bgr.rows;
    // 转为NCNN的Mat
    ncnn::Mat image = ncnn::Mat::from_pixels(data, ncnn::Mat::PIXEL_RGB, width, height);
    // 设置检测最小脸部大小
    mtcnn->SetMinFace(40);
    std::vector<Bbox> finalBbox;
    // NCNN人脸检测
    mtcnn->detect(image, finalBbox);
    std::vector<Face> faces;
    for (int i = 0; i < finalBbox.size(); i++) {
        Bbox bbox = finalBbox[i];
        Face face;
        face.x = bbox.x1;
        face.y = bbox.y1;
        face.width = bbox.x2 - bbox.x1;
        face.height = bbox.y2 - bbox.y1;
        faces.push_back(face);
    }
    return faces;
}

/**
 * 获取68个特征点
 * @param pixel
 * @param faces
 * @param index
 * @param shapePredictor
 */
void FaceDetector::landmarkFace(Mat &bgr, std::vector<Face> &faces, int index) {
    Face *face = &faces[index];
    // 转为dlib需要的格式
    dlib::cv_image<dlib::bgr_pixel> pixel(bgr);
    // 转为dlib需要的边框
    int left = face->x;
    int top = face->y;
    int right = face->x + face->width;
    int bottom = face->y + face->height;
    dlib::rectangle faceRectangle(left, top, right, bottom);
    // 提取特征点
    dlib::full_object_detection landmarkDetection = shapePredictor(pixel, faceRectangle);
    for (int j = 0; j < 68; j++) {
        FPoint point;
        point.x = (int) landmarkDetection.part(j).x();
        point.y = (int) landmarkDetection.part(j).y();
        face->points[j] = point;
    }
}

/**
 * 计算角度
 * @param faces
 * @param index
 */
void FaceDetector::calculateDegree(Mat &bgr, std::vector<Face> &faces, int index) {
    Face *face = &faces[index];
    // 转为dlib需要的格式
    dlib::cv_image<dlib::bgr_pixel> pixel(bgr);
    // 后续需要用OpenCV的函数进行旋转向量运算 而Dlib在检测时会对图片进行一定的缩放
    // 所以需要计算一个缩放比例ratio 以便将关键点的坐标还原为原图中的坐标
    float ratio = int(pixel.nc()) * 1.0f / bgr.cols;
    // 图片的点集
    std::vector<cv::Point2d> imagePoints;
    // 下巴(8) 鼻尖(30) 左眼角(36) 右眼角(45) 左嘴角(48) 右嘴角(54)
    imagePoints.push_back(Point2d(face->points[30].x / ratio, face->points[30].y / ratio));     // Nose tip
    imagePoints.push_back(Point2d(face->points[8].x / ratio, face->points[8].y / ratio));       // Chin
    imagePoints.push_back(Point2d(face->points[36].x / ratio, face->points[36].y / ratio));     // Left eye left corner
    imagePoints.push_back(Point2d(face->points[45].x / ratio, face->points[45].y / ratio));     // Right eye right corner
    imagePoints.push_back(Point2d(face->points[48].x / ratio, face->points[48].y / ratio));     // Left Mouth corner
    imagePoints.push_back(Point2d(face->points[54].x / ratio, face->points[54].y / ratio));     // Right mouth corner
    // 3D model 图片的点集
    std::vector<Point3d> modelPoints;
    modelPoints.push_back(Point3d(0.0f, 0.0f, 0.0f));               // Nose tip
    modelPoints.push_back(Point3d(0.0f, -330.0f, -65.0f));          // Chin
    modelPoints.push_back(Point3d(-225.0f, 170.0f, -135.0f));       // Left eye left corner
    modelPoints.push_back(Point3d(225.0f, 170.0f, -135.0f));        // Right eye right corner
    modelPoints.push_back(Point3d(-150.0f, -150.0f, -125.0f));      // Left Mouth corner
    modelPoints.push_back(Point3d(150.0f, -150.0f, -125.0f));       // Right mouth corner
    // 计算旋转向量
    // Camera internals
    double focalLength = bgr.cols; // Approximate focal length.
    Point2d center = Point2d(bgr.cols / 2, bgr.rows / 2);
    Mat cameraMatrix(3, 3, cv::DataType<double>::type);
    double cameraMatrixData[] = {focalLength, 0, center.x, 0, focalLength, center.y, 0, 0, 1};
    memcpy(cameraMatrix.data, cameraMatrixData, sizeof(double) * 9);
    cv::Mat distCoeffs = cv::Mat::zeros(4, 1, cv::DataType<double>::type); // Assuming no lens distortion
    cv::Mat rotationVector; // Rotation in axis-angle form
    cv::Mat translationVector;
    // Solve for pose
    // 从3D model到图片中人脸的仿射变换矩阵 它包含旋转和平移的信息
    // solvePnP函数输出结果包括旋转向量(roatation vector)和平移向量(translation vector)
    cv::solvePnP(modelPoints, imagePoints, cameraMatrix, distCoeffs, rotationVector, translationVector);
    // 旋转向量转换为欧拉角
    // pitch围绕X轴旋转 叫俯仰角
    // yaw围绕Y轴旋转 叫偏航角
    // roll围绕Z轴旋转 叫翻滚角
    // calculate rotation angles
    double theta = norm(rotationVector, CV_L2);
    // transformed to quaterniond
    Quaterniond q;
    q.w = cos(theta / 2);
    q.x = sin(theta / 2) * rotationVector.at<double>(0, 0) / theta;
    q.y = sin(theta / 2) * rotationVector.at<double>(0, 1) / theta;
    q.z = sin(theta / 2) * rotationVector.at<double>(0, 2) / theta;
    double ysqr = q.y * q.y;
    // pitch (x-axis rotation)
    double t0 = +2.0 * (q.w * q.x + q.y * q.z);
    double t1 = +1.0 - 2.0 * (q.x * q.x + ysqr);
    face->pitch = std::atan2(t0, t1) * 180 / PI;
    // yaw (y-axis rotation)
    double t2 = +2.0 * (q.w * q.y - q.z * q.x);
    t2 = t2 > 1.0 ? 1.0 : t2;
    t2 = t2 < -1.0 ? -1.0 : t2;
    face->yaw = std::asin(t2) * 180 / PI;
    // roll (z-axis rotation)
    double t3 = +2.0 * (q.w * q.z + q.x * q.y);
    double t4 = +1.0 - 2.0 * (ysqr + q.z * q.z);
    face->roll = std::atan2(t3, t4) * 180 / PI;
//    LOGE("pitch : %f, yaw: %f, roll: %f", face->pitch, face->yaw, face->roll);
}

/**
 * FaceDetector 构造
 * @param sdAssetPath
 */
FaceDetector::FaceDetector(const char *sdAssetPath) {
    mtcnn = new MTCNN(sdAssetPath);
    const char *landmark = "shape_predictor_68_face_landmarks_small.dat";
    char* landmarkDat = cutil::splicingString(sdAssetPath, landmark);
    med::load_shape_predictor_model(shapePredictor, landmarkDat);
}

/**
 * 人脸检测
 * @param nv21_
 * @param width
 * @param height
 * @param targetWidth
 * @param targetHeight
 * @param isFront
 * @return
 */
std::vector<Face> FaceDetector::detect(char *nv21_, int width, int height, int targetWidth, int targetHeight, bool isFront) {
    // 加载数据
    Mat nv21 = Mat::zeros(height + height / 2, width, CV_8UC1);
    nv21.data = (uchar *) nv21_;
    // BGR
    Mat bgr;
    cvtColor(nv21, bgr, CV_YUV2BGR_NV21);
    // 1.预处理
    // 放缩到目标的1/8
    resize(bgr, bgr, Size(bgr.cols / 8, bgr.rows / 8));
    // 前摄需要关于Y翻转
    if (isFront) {
        flip(bgr, bgr, 1);
    }
    // 顺时针旋转90度
    rotate(bgr, bgr, ROTATE_90_CLOCKWISE);
    // 放缩到目标的1/8
    resize(bgr, bgr, Size(targetWidth / 8, targetHeight / 8));
    // 2.图像对比 对比上一帧 是否相似
    bool similarity = isSimilarity(bgr);
    if (similarity) {
        return lastFaces;
    }
    // 3.人脸检测
    std::vector<Face> faces = detectFace(bgr);
    // 4.特征点检测
    for (int i = 0; i < faces.size(); i++) {
        landmarkFace(bgr, faces, i);
    }
    // 5.计算角度
    for (int i = 0; i < faces.size(); i++) {
        calculateDegree(bgr, faces, i);
    }
    // 6.最后坐标放缩为原来大小
    for (int i = 0; i < faces.size(); i++) {
        Face *face = &faces[i];
        face->x = face->x * 8;
        face->y = face->y * 8;
        face->width = face->width * 8;
        face->height = face->height * 8;
        for (int j = 0;j < 68; j++) {
            FPoint *point = &(face[i].points[j]);
            point->x = point->x * 8;
            point->y = point->y * 8;
        }
    }
    // 保存这次检测结果
    if (faces.size() > 0) {
        last = bgr;
        lastFaces = faces;
    }
    return faces;
}