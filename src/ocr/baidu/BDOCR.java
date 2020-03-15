package ocr.baidu;


import com.alibaba.fastjson.JSON;
import jdk.nashorn.internal.parser.JSONParser;
import ocr.OCRUtil;
import ocr.baidu.bean.OCRResult;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 图像文字识别
 */

public class BDOCR {
    private static final String POST_URL = "https://aip.baidubce.com/rest/2.0/ocr/v1/general_basic?access_token=" + AuthService.getAuth();

    /**
     * 识别本地图片的文字
     */
    public static String checkFile(String path) throws URISyntaxException, IOException {
        File file = new File(path);
        if (!file.exists()) {
            throw new NullPointerException("图片不存在");
        }
        String image = BaseImg64.encodeImgageToBase64(file);
        String param = "image=" + image;
        return post(param);
    }

    /**
     * 识别内存图片的文字
     */
    public static String checkFile(BufferedImage bufferedImage) throws URISyntaxException, IOException {
        if (bufferedImage == null) {
            throw new NullPointerException("图片不存在");
        }
        String image = BaseImg64.encodeImgageToBase64(bufferedImage);
        String param = "image=" + image;
        return post(param);
    }

    /**
     * 图片url
     * 识别结果，为json格式
     */
    public static String checkUrl(String url) throws IOException, URISyntaxException {
        String param = "url=" + url;
        return post(param);
    }

    /**
     * 通过传递参数：url和image进行文字识别
     */
    private static String post(String param) throws URISyntaxException, IOException {
        //开始搭建post请求
        HttpClient httpClient = new DefaultHttpClient();
        HttpPost post = new HttpPost();
        URI url = new URI(POST_URL);
        post.setURI(url);
        //设置请求头，请求头必须为application/x-www-form-urlencoded，因为是传递一个很长的字符串，不能分段发送
        post.setHeader("Content-Type", "application/x-www-form-urlencoded");
        StringEntity entity = new StringEntity(param);
        post.setEntity(entity);
        HttpResponse response = httpClient.execute(post);
        System.out.println(response.toString());
        if (response.getStatusLine().getStatusCode() == 200) {
            String str;
            try {
                //读取服务器返回过来的json字符串数据
                str = EntityUtils.toString(response.getEntity());
                return str;
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }
        }
        return null;
    }

    public static void main(String[] args) {
        String path = "data/tmp/test.png";
        try {
            long now = System.currentTimeMillis();
            String result = checkFile(path);
            System.out.println("耗时：" + (System.currentTimeMillis() - now) / 1000 + "s");
            OCRResult jsonObject = JSON.parseObject(result, OCRResult.class);
            jsonObject.getWords_result().stream().forEach(str -> {
                System.out.println(str.getWords());
            });
        } catch (URISyntaxException | IOException e) {
            e.printStackTrace();
        }
    }
}