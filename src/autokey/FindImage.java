package autokey;

import confnition.CoordBean;
import confnition.ImageCognition;
import marvin.image.MarvinImage;
import marvin.image.MarvinSegment;
import marvin.io.MarvinImageIO;
import utis.PerceptualHash;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import static marvin.MarvinPluginCollection.findSubimage;
import static marvin.MarvinPluginCollection.findTextRegions;

public class FindImage {

    /**
     * 精确找图
     *
     * @param src
     * @param tofind
     * @param sim
     * @throws Exception
     */
    public static List<CoordBean> findImage4FullScreen(String src, String tofind, int sim) throws Exception {
        // 将要查找的本地图读到BufferedImage
        InputStream srcStream = new FileInputStream(src);
        BufferedImage screenImg = ImageIO.read(srcStream);
        InputStream in = new FileInputStream(tofind);
        BufferedImage searchImg = ImageIO.read(in);

        //图片识别工具类
        ImageCognition ic = new ImageCognition();

        List<CoordBean> list = ic.imageSearch(screenImg, searchImg, sim);
        return list;// g.drawRect(coordBean.getX(), coordBean.getY(), searchImg.getWidth(), searchImg.getHeight());
    }

    /**
     * 根据缩略图找图
     *
     * @param src
     * @param tofind
     * @return
     */
    public static boolean findImageByThumbnail(String src, String tofind) throws IOException {
        BufferedImage image1 = ImageIO.read(new File(src));
        BufferedImage image2 = ImageIO.read(new File(tofind));
        boolean code = PerceptualHash.perceptualHashSimilarity(image1, image2);
        return code;
    }

    /**
     * 根据Marvin实现找图功能
     *
     * @param src
     * @param tofind
     * @param similarity
     * @return
     */
    public static MarvinSegment findImageByMarvin(String src, String tofind, double similarity) {
        MarvinImage window = MarvinImageIO.loadImage(src);
        MarvinImage eclipse = MarvinImageIO.loadImage(tofind);

        MarvinSegment seg1 = findSubimage(eclipse, window, 0, 0, similarity);
        return seg1;//        System.out.println("Found:" + seg1.x1 + " " + seg1.y1 + " width:" + (seg1.x2 - seg1.x1) + " height:" + (seg1.y2 - seg1.y1));
    }


    /**
     * 根据Marvin实现文字功能
     *
     * @param src
     * @return
     */
    public static List<MarvinSegment> findTextRegionByMarvin(String src) {
        MarvinImage window = MarvinImageIO.loadImage(src);

        int maxWhiteSpace = 10;
        int maxFontLineWidth = 10;
        int minTextWidth = 30;
        int grayScaleThreshold = 127;

        List<MarvinSegment> segs = findTextRegions(window, maxWhiteSpace, maxFontLineWidth, minTextWidth, grayScaleThreshold);
        return segs;
    }

    /**
     * 截屏
     *
     * @param fileName
     * @param folder
     * @throws Exception
     */
    public static BufferedImage captureScreen(String fileName, String folder) throws Exception {

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Rectangle screenRectangle = new Rectangle(screenSize);
        Robot robot = new Robot();
        BufferedImage image = robot.createScreenCapture(screenRectangle);
        // 截图保存的路径
        File screenFile = new File(folder);
        // 如果路径不存在,则创建
        if (!screenFile.getParentFile().exists()) {
            screenFile.getParentFile().mkdirs();
        }
        //判断文件是否存在，不存在就创建文件
        if (!screenFile.exists() && !screenFile.isDirectory()) {
            screenFile.mkdir();
        }

        File f = new File(screenFile, fileName);
        ImageIO.write(image, "png", f);

        return image;
    }
}
