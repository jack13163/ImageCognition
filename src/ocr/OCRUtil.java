package ocr;

import com.recognition.software.jdeskew.ImageDeskew;
import net.sourceforge.tess4j.ITessAPI;
import net.sourceforge.tess4j.ITesseract;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.Word;
import net.sourceforge.tess4j.util.ImageHelper;
import net.sourceforge.tess4j.util.Utils;
import org.opencv.core.Rect;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class OCRUtil {

    private static String testResourcesLanguagePath = "data/tessdata";
    private static int MINIMUM_DESKEW_THRESHOLD = 10;
    public static ITesseract instance = new Tesseract();

    static {
        //set language
        instance.setDatapath(testResourcesLanguagePath);
        instance.setLanguage("chi_sim_vert");
    }

    /**
     * Test of doOCR method, of class Tesseract.
     * 根据图片文件进行识别
     * @throws Exception while processing image.
     */
    public String doOCR_File(String filepath) throws Exception {
        File imageFile = new File(filepath);
        String result = instance.doOCR(imageFile);
        return result;
    }

    /**
     * Test of doOCR method, of class Tesseract.
     * 根据图片流进行识别
     * @throws Exception while processing image.
     */
    public String doOCR_BufferedImage(BufferedImage bi) throws Exception {
        String result = instance.doOCR(bi);
        return result;
    }

    /**
     * Test of doOCR method, of class Tesseract.
     * 根据图片流进行识别
     * @throws Exception while processing image.
     */
    public String doOCR_BufferedImage(BufferedImage bi, Rect rectangle) throws Exception {
        String result = instance.doOCR(bi, new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height));
        return result;
    }

    /**
     * Test of doOCR method, of class Tesseract.
     * 根据图片流进行识别
     * @throws Exception while processing image.
     */
    public List<String> doOCR_BufferedImage(BufferedImage bi, List<Rect> rectangles) throws Exception {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < rectangles.size(); i++) {
            String tmp = doOCR_BufferedImage(bi, rectangles.get(i));
            result.add(tmp);
        }
        return result;
    }

    /**
     * Test of getSegmentedRegions method, of class Tesseract.
     * 得到每一个划分区域的具体坐标
     * @throws java.lang.Exception
     */
    public List<Rectangle> getSegmentedRegions(String filepath) throws Exception {
        BufferedImage bi = ImageIO.read(new File(filepath));
        int level = ITessAPI.TessPageIteratorLevel.RIL_SYMBOL;
        System.out.println("PageIteratorLevel: " + Utils.getConstantName(level, ITessAPI.TessPageIteratorLevel.class));
        List<Rectangle> result = instance.getSegmentedRegions(bi, level);
        for (int i = 0; i < result.size(); i++) {
            Rectangle rect = result.get(i);
            System.out.println(String.format("Box[%d]: x=%d, y=%d, w=%d, h=%d", i, rect.x, rect.y, rect.width, rect.height));
        }
        return result;
    }


    /**
     * Test of doOCR method, of class Tesseract.
     * 根据定义坐标范围进行识别
     * @throws Exception while processing image.
     */
    public String doOCR_File_Rectangle(String filepath, Rect rectangle) throws Exception {
        File imageFile = new File(filepath);
        //划定区域
        // x,y是以左上角为原点，width和height是以xy为基础
        Rectangle rect = new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        String result = instance.doOCR(imageFile, rect);
        return result;
    }

    /**
     * Test of doOCR method, of class Tesseract.
     * 根据图片流进行识别
     * @throws Exception while processing image.
     */
    public List<String> doOCR_File_Rectangle(String filepath, List<Rect> rectangles) throws Exception {
        List<String> result = new ArrayList<>();
        for (int i = 0; i < rectangles.size(); i++) {
            String tmp = doOCR_File_Rectangle(filepath, rectangles.get(i));
            result.add(tmp);
        }
        return result;
    }

    /**
     * Test of createDocuments method, of class Tesseract.
     * 存储结果
     * @throws java.lang.Exception
     */
    public void createDocuments(String filepath) throws Exception {
        File imageFile = new File(filepath);
        String outputbase = "data/tmp";
        List<ITesseract.RenderedFormat> formats = new ArrayList<>(Arrays.asList(ITesseract.RenderedFormat.HOCR, ITesseract.RenderedFormat.TEXT));
        instance.createDocuments(new String[]{imageFile.getPath()}, new String[]{outputbase}, formats);
    }

    /**
     * Test of getWords method, of class Tesseract.
     * 取词方法
     * @throws java.lang.Exception
     */
    public List<Word> getWords(String filepath) throws Exception {
        File imageFile = new File(filepath);

        //按照每个字取词
        int pageIteratorLevel = ITessAPI.TessPageIteratorLevel.RIL_SYMBOL;
        System.out.println("PageIteratorLevel: " + Utils.getConstantName(pageIteratorLevel, ITessAPI.TessPageIteratorLevel.class));
        BufferedImage bi = ImageIO.read(imageFile);
        List<Word> result = instance.getWords(bi, pageIteratorLevel);

        //print the complete result
        for (Word word : result) {
            System.out.println(word.toString());
        }
        return  result;
    }

    /**
     * Test of Invalid memory access.
     * 处理倾斜
     * @throws Exception while processing image.
     */
    public String doOCR_SkewedImage(String filepath) throws Exception {
        File imageFile = new File(filepath);
        BufferedImage bi = ImageIO.read(imageFile);
        ImageDeskew id = new ImageDeskew(bi);
        double imageSkewAngle = id.getSkewAngle(); // determine skew angle
        if ((imageSkewAngle > MINIMUM_DESKEW_THRESHOLD || imageSkewAngle < -(MINIMUM_DESKEW_THRESHOLD))) {
            bi = ImageHelper.rotateImage(bi, -imageSkewAngle); // deskew image
        }

        String result = instance.doOCR(bi);
        return result;
    }
}
