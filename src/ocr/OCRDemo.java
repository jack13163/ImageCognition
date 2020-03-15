package ocr;

import imageprocess.CVHelper;
import org.opencv.core.Core;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import java.util.List;


public class OCRDemo {
    public static void main(String args[]) throws Exception {
        try {
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            CVHelper cvHelper = new CVHelper();
            OCRUtil ocrUtil = new OCRUtil();
            String filepath = "data/tmp/test.png";

            // 检测矩形
            Mat image = Imgcodecs.imread(filepath);
            List<Rect> rects = cvHelper.findRects(image, 30, 12, 60, 25);
            cvHelper.drawRects(image, rects);
            HighGui.imshow("矩形检测", image);
            HighGui.waitKey(0);
            List<String> result = ocrUtil.doOCR_File_Rectangle(filepath, rects);
            for (int i = 0; i < result.size(); i++) {
                System.out.println(result.get(i));
            }

        } catch (Exception e) {
            System.out.println(e.toString());//打印图片内容
        }
    }
}