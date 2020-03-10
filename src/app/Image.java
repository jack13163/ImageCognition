package app;

import confnition.CoordBean;
import confnition.ImageCognition;
import marvin.image.MarvinImage;
import marvin.image.MarvinSegment;
import marvin.io.MarvinImageIO;
import utis.PerceptualHash;
import utis.TimeHelper;

import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import javax.imageio.ImageIO;

import static marvin.MarvinPluginCollection.findSubimage;
import static marvin.MarvinPluginCollection.findTextRegions;

public class Image {
    public static void main(String[] args) throws Exception {
//        // 屏幕截图
//        captureScreen("screen.png", "data/images/");
//
//        // 文字区域查找
//        TimeHelper.startWatch(new TimeHelper.Job() {
//            @Override
//            public void run() {
//                findTextRegionByMarvin("data/images/screen.png");
//            }
//        });


        TimeHelper.startWatch(new TimeHelper.Job() {
            @Override
            public void run() {
                findImageByMarvin("data/images/screen.png", "data/images/wx_max.png", 0.97);
            }
        });

        TimeHelper.startWatch(new TimeHelper.Job() {
            @Override
            public void run() {
                try {
                    findImage4FullScreen("data/images/screen.png",
                            "data/images/wx_max.png",
                            ImageCognition.SIM_ACCURATE_VERY);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });

        TimeHelper.startWatch(new TimeHelper.Job() {
            @Override
            public void run() {
                try {
                    if (findImageByThumbnail("data/images/normal.png", "data/images/small.png")) {
                        System.out.println("两个图片相似");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }

    /**
     * 精确找图
     *
     * @param src
     * @param tofind
     * @param sim
     * @throws Exception
     */
    public static void findImage4FullScreen(String src, String tofind, int sim) throws Exception {
        // 将要查找的本地图读到BufferedImage
        InputStream srcStream = new FileInputStream(src);
        BufferedImage screenImg = ImageIO.read(srcStream);
        InputStream in = new FileInputStream(tofind);
        BufferedImage searchImg = ImageIO.read(in);

        //图片识别工具类
        ImageCognition ic = new ImageCognition();

        List<CoordBean> list = ic.imageSearch(screenImg, searchImg, sim);
        for (CoordBean coordBean : list) {
            System.out.println("找到图片,坐标是" + coordBean.getX() + "," + coordBean.getY());

            //标注找到的图的位置
            Graphics g = screenImg.getGraphics();
            g.setColor(Color.BLACK);
            g.drawRect(coordBean.getX(), coordBean.getY(), searchImg.getWidth(), searchImg.getHeight());
            g.setFont(new Font(null, Font.BOLD, 20));
            g.drawString("←找到的图片在这里", coordBean.getX() + searchImg.getWidth() + 5, coordBean.getY() + 10 + searchImg.getHeight() / 2);
            OutputStream out = new FileOutputStream("data/images/result.png");
            ImageIO.write(screenImg, "png", out);
        }
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
    public static boolean findImageByMarvin(String src, String tofind, double similarity) {
        MarvinImage window = MarvinImageIO.loadImage(src);
        MarvinImage eclipse = MarvinImageIO.loadImage(tofind);

        MarvinSegment seg1 = findSubimage(eclipse, window, 0, 0, similarity);
        if (seg1 != null) {
            System.out.println("Found:" + seg1.x1 + " " + seg1.y1 + " width:" + (seg1.x2 - seg1.x1) + " height:" + (seg1.y2 - seg1.y1));
            drawRect(window, seg1.x1, seg1.y1, seg1.x2 - seg1.x1, seg1.y2 - seg1.y1);
            MarvinImageIO.saveImage(window, "data/images/result.png");
            return true;
        } else {
            return false;
        }
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
        for (int i = 0; i < segs.size(); i++) {
            MarvinSegment segment = segs.get(i);
            if (segment != null) {
                System.out.println("Found:" + segment.x1 + " " + segment.y1 + " width:" + (segment.x2 - segment.x1) + " height:" + (segment.y2 - segment.y1));
                drawRect(window, segment.x1, segment.y1, segment.x2 - segment.x1, segment.y2 - segment.y1);
                MarvinImageIO.saveImage(window, "data/images/findtextregion.png");
            }
        }
        return segs;
    }

    private static void drawRect(MarvinImage image, int x, int y, int width, int height) {
        image.drawRect(x, y, width, height, Color.red);
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

        //自动打开
        if (Desktop.isDesktopSupported() && Desktop.getDesktop().isSupported(Desktop.Action.OPEN)) {
            Desktop.getDesktop().open(f);
        }
        return image;
    }
}
