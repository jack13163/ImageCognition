package utis;

import java.awt.*;
import java.awt.image.BufferedImage;

public class PerceptualHash {

    /**
     * ”感知哈希算法”（Perceptual hash algorithm）
     * 作用是对每张图片生成一个”指纹”（fingerprint）字符串，然后比较不同图片的指纹。
     * 结果越接近，就说明图片越相似。
     * @param src1
     * @param src2
     * @return
     */
    public static boolean perceptualHashSimilarity(BufferedImage src1, BufferedImage src2) {
        String code1 = perceptualHashSimilarity(src1);
        String code2 = perceptualHashSimilarity(src2);
        char[] ch1 = code1.toCharArray();
        char[] ch2 = code2.toCharArray();
        int diffCount = 0;
        for (int i = 0; i < 64; i++) {
            if (ch1[i] != ch2[i]) {
                diffCount++;
            }
        }
        return diffCount <= 5;
    }

    /**
     * 计算哈希值
     * @param src
     * @return
     */
    private static String perceptualHashSimilarity(BufferedImage src) {
        int width = 8;
        int height = 8;
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_BGR);
        Graphics graphics = image.createGraphics();
        graphics.drawImage(src, 0, 0, 8, 8, null);
        int total = 0;
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = image.getRGB(j, i);
                int gray = gray(pixel);
                total = total + gray;
            }
        }
        StringBuffer res = new StringBuffer();
        // 计算所有64个像素的灰度平均值。
        int grayAvg = total / (width * height);
        // 二值化
        for (int i = 0; i < height; i++) {
            for (int j = 0; j < width; j++) {
                int pixel = image.getRGB(j, i);
                int gray = gray(pixel);
                if (gray >= grayAvg) {
                    res.append("1");
                } else {
                    res.append("0");
                }
            }
        }
        return res.toString();
    }

    /**
     * 简化色彩。所有像素点总共只有64种颜色。
     * @param rgb
     * @return
     */
    private static int gray(int rgb) {
        int a = rgb & 0xff000000;//将最高位（24-31）的信息（alpha通道）存储到a变量
        int r = (rgb >> 16) & 0xff;//取出次高位（16-23）红色分量的信息
        int g = (rgb >> 8) & 0xff;//取出中位（8-15）绿色分量的信息
        int b = rgb & 0xff;//取出低位（0-7）蓝色分量的信息
        rgb = (r * 77 + g * 151 + b * 28) >> 8;    // NTSC luma，算出灰度值
        return a | (rgb << 16) | (rgb << 8) | rgb;//将灰度值送入各个颜色分量
    }
}
