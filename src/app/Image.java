package app;

import confnition.CoordBean;
import confnition.ImageCognition;
import utis.PerceptualHash;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.Robot;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.io.*;
import java.util.List;

import javax.imageio.ImageIO;

public class Image {
    public static void main(String[] args) throws Exception {
        findImage4FullScreen("data/images/20204408134403.png", "data/images/20204408134436.png", ImageCognition.SIM_ACCURATE_VERY);
        if(findImageByThumbnail("data/images/normal.png", "data/images/small.png")){
            System.out.println("两个图片相似");
        }
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
}
