package imageprocess;

import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;

public class CVTest {

    public static void main(String[] args) {
        System.loadLibrary(Core.NATIVE_LIBRARY_NAME);

        Mat image = Imgcodecs.imread("data/images/pcwx3.png");
        CVHelper util = new CVHelper();
        int start_x = 60;
        int start_y = 0;
        Rect cutRect = new Rect(start_x, start_y, image.width() - start_x, image.height());

        List<Rect> selects = util.findRects(image, cutRect, 200, 50, 300, 150);
        Mat selectImage = image.clone();
        util.drawRects(selectImage, selects, start_x, start_y);
        HighGui.imshow("选中检测", selectImage);

        List<Rect> icons = util.findRects(image, cutRect, 30, 30, 60, 60);
        Mat iconImage = image.clone();
        util.drawRects(iconImage, icons, start_x, start_y);
        HighGui.imshow("头像检测", iconImage);
        HighGui.waitKey(0);
    }
}