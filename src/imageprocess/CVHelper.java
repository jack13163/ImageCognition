package imageprocess;

import org.opencv.core.*;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

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
        Mat imgROI = new Mat(image, rect);
        imgROI.copyTo(imgDesc);
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
    public static List<Rect> findRects(Mat src, Rect rect, int width_low, int height_low, int width_up, int height_up) {
        CVHelper util = new CVHelper();

        // 1.裁剪
        Mat cut = util.cutImage(src, rect);

        // 2.替换白色背景为黑色
        Mat change = util.replaceWhiteBackground(cut);

        // 3.灰度化
        Mat gray = util.grayImg(change);

        // 4.腐蚀
        Mat dst = gray.clone();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
        Imgproc.erode(gray, dst, element, new Point(-1, -1), 3);

        // 5.二值化
        Mat black = util.threshold(dst);

        // 6.边缘处理
        Mat canny = black.clone();
        Imgproc.Canny(black, canny, 100, 200);

        // 7.查找矩形轮廓
        List<Rect> contours = util.findRects(canny);
        List<Rect> rects = util.getSmallContours(contours, width_low, height_low, width_up, height_up);
        return rects;
    }
}
