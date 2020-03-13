package imageprocess;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;
import org.opencv.imgproc.Imgproc;

import java.util.List;

public class CVTest {
    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
        findSelect("data/images/", "pcwx2.png");
    }

    /**
     * 头像识别
     *
     * @return:void
     * @date: 2019年1月18日 上午9:18:08
     */
    public static void findUserImage(String filePath, String fileName) {
        Mat src = Imgcodecs.imread(filePath + fileName);
        CVHelper util = new CVHelper();

        // 灰度化
        Mat gray = util.grayImg(src);

        //1.边缘处理
        Imgproc.Canny(src, gray, 50, 200);
        HighGui.imshow("边缘检测", gray);

        // 2.查找矩形轮廓
        List<Rect> contours = util.findRects(gray);
        List<Rect> rects = util.getSmallContours(contours, 30, 30, 400, 300);
        for (int i = 0, len = rects.size(); i < len; i++) {
            Rect rect = rects.get(i);
            Imgproc.rectangle(src, rect, new Scalar(0, 0, 255));
        }
        HighGui.imshow("轮廓检测", src);
        HighGui.waitKey(0);
    }

    /**
     * 查找用户点击位置
     *
     * @param filePath
     * @param fileName
     */
    public static void findSelect(String filePath, String fileName) {
        Mat src = Imgcodecs.imread(filePath + fileName);
        CVHelper util = new CVHelper();

        // 替换白色背景为黑色
        Mat change = util.replaceWhiteBackground(src);
        HighGui.imshow("替换白色背景为黑色", change);
        HighGui.waitKey(0);

        // 灰度化
        Mat gray = util.grayImg(change);
        HighGui.imshow("灰度化", gray);

        // 腐蚀
        Mat dst = gray.clone();
        Mat element = Imgproc.getStructuringElement(Imgproc.MORPH_RECT, new Size(4, 4));
        Imgproc.erode(gray, dst, element, new Point(-1, -1), 3);
        HighGui.imshow("腐蚀", dst);

        // 二值化
        Mat black = util.threshold(dst);
        HighGui.imshow("二值化", black);

        // 1.边缘处理
        Mat canny = black.clone();
        Imgproc.Canny(black, canny, 50, 200);
        HighGui.imshow("边缘检测", canny);

        // 2.查找矩形轮廓
        List<Rect> contours = util.findRects(canny);
        List<Rect> rects = util.getSmallContours(contours, 30, 30, 400, 300);
        for (int i = 0, len = rects.size(); i < len; i++) {
            Rect rect = rects.get(i);
            Imgproc.rectangle(src, rect, new Scalar(0, 0, 255));
        }
        HighGui.imshow("轮廓检测", src);
        HighGui.waitKey(0);
    }
}
