//
// Created by Administrator on 2019/12/10.
//

#ifndef MEDIASDK_OPENCV_UTIL_H
#define MEDIASDK_OPENCV_UTIL_H

#include <opencv2/core/mat.hpp>

using namespace cv;

namespace cvutil {

    /**
     * 计算相似度
     * @param mat1
     * @param mat2
     * @return
     */
    double calculateSimilarity(Mat mat1, Mat mat2);

}

#endif //MEDIASDK_OPENCV_UTIL_H
