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
}
