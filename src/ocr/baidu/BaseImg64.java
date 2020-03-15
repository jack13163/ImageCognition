package ocr.baidu;


import sun.misc.BASE64Decoder;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * 图片转化base64后再UrlEncode结果
 */

public class BaseImg64 {

    /**
     * 将网络图片进行Base64位编码
     *
     * @param imageUrl 图片的url路径，如http://.....xx.jpg
     * @return
     */
    public static String encodeImgageToBase64(URL imageUrl, String format) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageUrl);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, format, outputStream);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串
        base64 = base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        String image_base64 = "data:image/" + format + ";base64,"+ base64;
        return image_base64;
    }

    /**
     * 将本地图片进行Base64位编码
     *
     * @param imageFile 本地图片的url路径
     * @return
     */
    public static String encodeImgageToBase64(File imageFile, String format) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream outputStream = null;
        try {
            BufferedImage bufferedImage = ImageIO.read(imageFile);
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, format, outputStream);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串
        base64 = base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        String image_base64 = "data:image/" + format + ";base64,"+ base64;
        return image_base64;
    }

    /**
     * 将内存中的图片进行Base64位编码
     *
     * @param bufferedImage 内存图片
     * @return
     */
    public static String encodeImgageToBase64(BufferedImage bufferedImage, String format) {// 将图片文件转化为字节数组字符串，并对其进行Base64编码处理
        ByteArrayOutputStream outputStream = null;
        try {
            outputStream = new ByteArrayOutputStream();
            ImageIO.write(bufferedImage, format, outputStream);
        } catch (MalformedURLException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        // 对字节数组Base64编码
        BASE64Encoder encoder = new BASE64Encoder();
        String base64 = encoder.encode(outputStream.toByteArray());// 返回Base64编码过的字节数组字符串
        base64 = base64.replaceAll("\n", "").replaceAll("\r", "");//删除 \r\n
        String image_base64 = "data:image/" + format + ";base64,"+ base64;
        return image_base64;
    }

    /**
     * 将Base64位编码的图片进行解码，并保存到指定目录
     *
     * @param base64 base64编码的图片信息
     * @return
     */
    public static void decodeBase64ToImage(String base64, String path, String imgName) {
        BASE64Decoder decoder = new BASE64Decoder();
        try {
            FileOutputStream write = new FileOutputStream(new File(path + imgName));
            byte[] decoderBytes = decoder.decodeBuffer(base64);
            write.write(decoderBytes);
            write.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}