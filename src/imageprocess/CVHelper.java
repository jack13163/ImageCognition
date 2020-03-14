package imageprocess;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CVHelper {

    /**
     * 替换白色背景为黑色
     *
     * @param image
     * @return
     */
    public Mat replaceWhiteBackground(Mat image) {
        Mat result = image.clone();

        for (int i = 0; i < result.rows(); i++) {
            for (int j = 0; j < result.cols(); j++) {
                // 将白色替换为黑色，消除背景的影响
                int th = 245;
                if (image.get(i, j)[0] > th && image.get(i, j)[1] > th && image.get(i, j)[2] > th) {
                    result.put(i, j, new double[]{0, 0, 0});
                }
            }
        }

        return result;
    }

    /**
     * 图片裁剪
     *
     * @param image
     * @param rect
     * @return
     */
    public Mat cutImage(Mat image, Rect rect) {

        //从ROI中剪切图片
        Mat imgDesc = new Mat(rect.height, rect.width, image.type());
        try {
            Mat imgROI = new Mat(image, rect);
            imgROI.copyTo(imgDesc);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return imgDesc;
    }

    /**
     * 查找矩形轮廓
     *
     * @param image
     * @return
     */
    public List<Rect> findRects(Mat image) {

        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        // 查找轮廓
        Imgproc.findContours(image, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);

        List<Rect> result = new ArrayList<>();
        for (int i = 0; i < contours.size(); i++) {
            Rect rect = pointToRect(contours.get(i));
            result.add(rect);
        }

        return result;
    }

    /**
     * 绘制矩形轮廓
     *
     * @param image
     * @return
     */
    public void drawRects(Mat image, List<Rect> rects) {
        drawRects(image, rects, 0, 0);
    }

    /**
     * 绘制矩形轮廓
     *
     * @param image
     * @return
     */
    public void drawRects(Mat image, List<Rect> rects, int off_x, int off_y) {

        for (int i = 0, len = rects.size(); i < len; i++) {
            Rect rect = rects.get(i);
            if (off_x != 0 || off_y != 0) {
                Rect newRect = new Rect(rect.x + off_x, rect.y + off_y, rect.width, rect.height);
                Imgproc.rectangle(image, newRect, new Scalar(0, 0, 255));
            } else {
                Imgproc.rectangle(image, rect, new Scalar(0, 0, 255));
            }
        }
    }

    /**
     * 灰度化
     *
     * @param Image
     * @return
     */
    public Mat grayImg(Mat Image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(Image, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }

    /**
     * 二值化
     *
     * @param Image
     * @return
     */
    public Mat threshold(Mat Image) {
        Mat binary = new Mat();
        Imgproc.threshold(Image, binary, 200, 255, Imgproc.THRESH_TOZERO);
        return binary;
    }

    /**
     * 获取最大的轮廓
     *
     * @param contours
     * @return
     */
    public Rect getMaxContours(List<Rect> contours) {
        int size = contours.size();
        int i = 0;
        int max = 0;
        int result = 0;
        for (i = 0; i < size; i++) {
            int temp = contours.get(i).height * contours.get(i).width;
            if (temp > max) {
                max = temp;
                result = i;
            }
        }
        return contours.get(result);
    }

    /**
     * 获取小于指定宽高的矩形
     *
     * @param contours
     * @param width_low
     * @param height_low
     * @param width_up
     * @param height_up
     * @return
     */
    public List<Rect> getSmallContours(List<Rect> contours, int width_low, int height_low, int width_up, int height_up) {
        List<Rect> rects = new ArrayList<>();

        for (int i = 0; i < contours.size(); i++) {

            Rect rect = contours.get(i);

            if (rect.width <= width_up && rect.height <= height_up && rect.width >= width_low && rect.height >= height_low) {
                rects.add(rect);
                System.out.println("x0:" + rect.x + ", y0:" + rect.y + ", width:" + rect.width + ", height:" + rect.height);
            }
        }

        return rects;
    }

    /**
     * 转换为边框
     *
     * @param point
     * @return
     */
    public Rect pointToRect(MatOfPoint point) {
        MatOfPoint2f mt2f = new MatOfPoint2f(point.toArray());
        RotatedRect rotatedRect = Imgproc.minAreaRect(mt2f);
        Mat result = new Mat();
        Imgproc.boxPoints(rotatedRect, result);
        int x0 = min(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]);
        int y0 = min(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]);
        int width = max(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]) - x0;
        int height = max(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]) - y0;

        Rect roi = new Rect(x0, y0, width, height);
        return roi;
    }

    public int min(double d1, double d2, double d3, double d4) {
        return (int) Math.min(Math.min(d1, d2), Math.min(d3, d4));
    }

    public int max(double d1, double d2, double d3, double d4) {
        return (int) Math.max(Math.max(d1, d2), Math.max(d3, d4));
    }

    /**
     * 查找图片某一区域中的矩形
     *
     * @param src
     * @return
     */
    public List<Rect> findRects(Mat src, Rect rect, int width_low, int height_low, int width_up, int height_up) {

        // 1.裁剪
        Mat cut = cutImage(src, rect);

        // 2.替换白色背景为黑色
        Mat change = replaceWhiteBackground(cut);

        // 3.灰度化
        Mat gray = grayImg(change);

        // 4.腐蚀
        Mat dst = gray.clone();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
        Imgproc.erode(gray, dst, element, new Point(-1, -1), 3);

        // 5.二值化
        Mat black = threshold(dst);

        // 6.边缘处理
        Mat canny = black.clone();
        Imgproc.Canny(black, canny, 100, 200);

        // 7.查找矩形轮廓
        List<Rect> contours = findRects(canny);
        List<Rect> rects = getSmallContours(contours, width_low, height_low, width_up, height_up);
        return rects;
    }

    /**
     * @param src
     * @param tomatch
     * @param th
     * @return
     */
    public Rect match(Mat src, Mat tomatch, double th) {
        return match(src, tomatch, Imgproc.TM_SQDIFF_NORMED, th);
    }

    /**
     * 模板匹配
     * <p>
     * TM_SQDIFF 平方差匹配法：该方法采用平方差来进行匹配；最好的匹配值为0；匹配越差，匹配值越大。
     * TM_CCORR 相关匹配法：该方法采用乘法操作；数值越大表明匹配程度越好。
     * TM_CCOEFF 相关系数匹配法：1表示完美的匹配；-1表示最差的匹配。
     * TM_SQDIFF_NORMED 归一化平方差匹配法。
     * TM_CCORR_NORMED 归一化相关匹配法。
     * TM_CCOEFF_NORMED 归一化相关系数匹配法。
     *
     * @param src
     * @param tomatch
     * @param method
     * @param th
     * @return
     */
    public Rect match(Mat src, Mat tomatch, int method, double th) {

        int width = src.cols() - tomatch.cols() + 1;
        int height = src.rows() - tomatch.rows() + 1;
        // 3 创建32位模板匹配结果Mat
        Mat result = new Mat(width, height, src.type());
        // 4 调用 模板匹配函数
        Imgproc.matchTemplate(src, tomatch, result, method);
        // 5 归一化
        Core.normalize(result, result, 0, 1, Core.NORM_MINMAX, -1, new Mat());
        // 6 获取模板匹配结果
        Core.MinMaxLocResult mmr = Core.minMaxLoc(result);
        // 7 绘制匹配到的结果
        int x, y;
        if (method == Imgproc.TM_SQDIFF_NORMED || method == Imgproc.TM_SQDIFF) {
            x = (int) mmr.minLoc.x;
            y = (int) mmr.minLoc.y;
        } else {
            x = (int) mmr.maxLoc.x;
            y = (int) mmr.maxLoc.y;
        }

        // 8 计算图像的相似度
        Rect rect = new Rect(x, y, tomatch.width(), tomatch.height());
        Mat tmp = cutImage(src, rect);
        if (compareHist(tomatch, tmp) >= th) {
            return rect;
        } else {
            return null;
        }
    }

    /**
     * 直方图比较图像的相似度
     *
     * @param src_1
     * @param src_2
     */
    public double compareHist(Mat src_1, Mat src_2) {
        Mat hvs_1 = new Mat();
        Mat hvs_2 = new Mat();
        //图片转HSV
        Imgproc.cvtColor(src_1, hvs_1, Imgproc.COLOR_BGR2HSV);
        Imgproc.cvtColor(src_2, hvs_2, Imgproc.COLOR_BGR2HSV);

        Mat hist_1 = new Mat();
        Mat hist_2 = new Mat();

        //直方图计算
        Imgproc.calcHist(Stream.of(hvs_1).collect(Collectors.toList()), new MatOfInt(0), new Mat(), hist_1, new MatOfInt(255), new MatOfFloat(0, 256));
        Imgproc.calcHist(Stream.of(hvs_2).collect(Collectors.toList()), new MatOfInt(0), new Mat(), hist_2, new MatOfInt(255), new MatOfFloat(0, 256));

        //图片归一化
        Core.normalize(hist_1, hist_1, 1, hist_1.rows(), Core.NORM_MINMAX, -1, new Mat());
        Core.normalize(hist_2, hist_2, 1, hist_2.rows(), Core.NORM_MINMAX, -1, new Mat());

        //直方图比较
        double result = Imgproc.compareHist(hist_1, hist_2, Imgproc.CV_COMP_CORREL);
        return result;
    }
}
