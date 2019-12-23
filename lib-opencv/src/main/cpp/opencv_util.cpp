#include <opencv_util.h>
#include <opencv/cv.hpp>

namespace cvutil {

    /**
     * 计算相似度
     * @param mat1
     * @param mat2
     * @return
     */
    double calculateSimilarity(Mat mat1, Mat mat2) {
        int targetSize = 64;
        // 放缩
        Mat target1;
        resize(mat1, target1, Size(targetSize, targetSize));
        Mat target2;
        resize(mat2, target2, Size(targetSize, targetSize));
        // 灰度化
        cvtColor(target1, target1, CV_BGR2GRAY);
        cvtColor(target2, target2, CV_BGR2GRAY);
        // 均值
        int total1 = 0;
        int total2 = 0;
        for (int row = 0; row < targetSize; row ++) {
            for (int col = 0; col < targetSize; col ++) {
                total1 += target1.at<uchar>(row, col);
                total2 += target2.at<uchar>(row, col);
            }
        }
        int mean1 = total1 / (targetSize * targetSize);
        int mean2 = total2 / (targetSize * targetSize);
        // 累计
        int sameCount = 0;
        int temp1, temp2;
        for (int row = 0; row < targetSize; row ++) {
            for (int col = 0; col < targetSize; col ++) {
                temp1 = target1.at<uchar>(row, col) > mean1 ? 1 : 0;
                temp2 = target2.at<uchar>(row, col) > mean2 ? 1 : 0;
                if (temp1 == temp2) {
                    sameCount ++;
                }
            }
        }
        // 比例
        return sameCount * 1.0 / (targetSize * targetSize);
    }

}
