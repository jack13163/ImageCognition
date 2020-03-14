package imageprocess;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.ArrayList;
import java.util.List;

public class CVTest {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat image = Imgcodecs.imread("data/images/pcwx.png");
        CVHelper util = new CVHelper();
        int start_x = 60;
        int start_y = 0;
        Rect cutRect = new Rect(start_x, start_y, image.width() - start_x, image.height());

        List<Rect> selects = util.findRects(image, cutRect, 200, 50, 300, 150);
        List<Rect> icons = util.findRects(image, cutRect, 30, 30, 60, 60);
        List<Rect> rects = new ArrayList<>();
        rects.addAll(selects);
        rects.addAll(icons);

        util.drawRects(image, rects, start_x, start_y);
        HighGui.imshow("矩形检测", image);
        HighGui.waitKey(0);
    }
}