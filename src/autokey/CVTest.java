package autokey;

import org.opencv.core.*;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.ArrayList;
import java.util.List;

public class CVTest {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        String filePath = "data/images/";
        String fileName = "idcard.png";
        CVTest util = new CVTest();
        util.cutImage(filePath, fileName);
    }

    public void cutImage(String filePath, String fileName) {
        Mat src = Imgcodecs.imread(filePath + fileName, 3);
        Imgcodecs.imwrite(filePath + "gray1.jpg", src);
        Mat gray = grayImg(src);
        Imgcodecs.imwrite(filePath + "gray.jpg", gray);
        Mat binary = threshold(gray);
        Imgcodecs.imwrite(filePath + "binary.jpg", binary);
        List<MatOfPoint> contours = new ArrayList<MatOfPoint>();
        Mat hierarchy = new Mat();
        // 查找轮廓
        Imgproc.findContours(binary, contours, hierarchy, Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE);
        MatOfPoint mp = getMaxContours(contours);
        MatOfPoint2f mt2f = new MatOfPoint2f(mp.toArray());
        RotatedRect rotatedRect = Imgproc.minAreaRect(mt2f);
        Mat result = new Mat();
        Imgproc.boxPoints(rotatedRect, result);
        System.out.println(result.dump());
        System.out.println(result.get(0, 1)[0]);
        int x0 = min(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]);
        int y0 = min(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]);
        int width = max(result.get(0, 0)[0], result.get(1, 0)[0], result.get(2, 0)[0],
                result.get(3, 0)[0]) - x0;
        int height = max(result.get(0, 1)[0], result.get(1, 1)[0], result.get(2, 1)[0],
                result.get(3, 1)[0]) - y0;
        System.out.println("x0:" + x0 + ", y0:" + y0 + ", width:" + width + ", height:" + height);
        Rect roi = new Rect(x0, y0, width, height);
        Mat dst = new Mat(src, roi);
        Imgcodecs.imwrite(filePath + "Trimming.jpg", dst);
    }

    public Mat grayImg(Mat Image) {
        Mat gray = new Mat();
        Imgproc.cvtColor(Image, gray, Imgproc.COLOR_BGR2GRAY);
        return gray;
    }

    public Mat threshold(Mat Image) {
        Mat binary = new Mat();
        Imgproc.threshold(Image, binary, 200, 255, Imgproc.THRESH_TOZERO);
        return binary;
    }

    public MatOfPoint getMaxContours(List<MatOfPoint> contours) {
        int size = contours.size();
        int i = 0;
        int max = 0;
        int result = 0;
        for (i = 0; i < size; i++) {
            int temp = contours.get(i).toArray().length;
            if (temp > max) {
                max = temp;
                result = i;
            }
        }
//        System.out.println("max is :" + max + "order is :" + result);
        return contours.get(result);
    }

    public int min(double d1, double d2, double d3, double d4) {
        return (int) Math.min(Math.min(d1, d2), Math.min(d3, d4));
    }

    public int max(double d1, double d2, double d3, double d4) {
        return (int) Math.max(Math.max(d1, d2), Math.max(d3, d4));
    }
}
