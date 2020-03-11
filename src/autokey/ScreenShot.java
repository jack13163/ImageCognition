package autokey;


import javax.imageio.ImageIO;
import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.awt.image.BufferedImage;
import java.awt.image.RescaleOp;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

//继承jwindow，无边框，与jframe同等级
public class ScreenShot extends JWindow {
    public JFrame jf;
    private int startx, starty, endx, endy;
    // 最大截图标记，保存的最大范围
    public int xx, yy, ww, hh;
    // 第一次取到的屏幕
    private BufferedImage image = null;
    // 缓存加深颜色的主屏幕
    private BufferedImage tempImage = null;
    // 需要保存的图片
    private BufferedImage saveImage = null;
    // 是否开始画笔
    public boolean isdraw = false;
    // 缓存而已
    public BufferedImage tempImage2 = null;

    // 剪切板
    Clipboard clipboard;
    // 工具类
    public SetupMsg sm;
    public updateUI update;

    public interface updateUI{
        void display(String filepath);
    }

    // 普通初始化
    public ScreenShot(updateUI update) throws AWTException {
        this.jf = new JFrame();
        this.sm = new SetupMsg();
        this.update = update;
        init();
        initPro();
    }

    private void init() throws AWTException {
        this.setVisible(true);
        // 获取屏幕尺寸
        Dimension d = Toolkit.getDefaultToolkit().getScreenSize();
        this.setBounds(0, 0, d.width, d.height);
        // 截取最大屏幕
        Robot robot = new Robot();
        image = robot.createScreenCapture(new Rectangle(0, 0, d.width, d.height));
        // 初始化就给全屏
        saveImage = image;

        // 将图片全屏显示浮于上方

        // 监听鼠标点击松开
        this.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                // 记录鼠标点击行为 坐标
                isdraw = true;

                // 记录画框的坐标
                startx = e.getX();
                starty = e.getY();
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                // 鼠标松开时，保存图片
                String filepath = saveImage();
                update.display(filepath);
                isdraw = false;
            }
        });

        // 监听鼠标拖动
        this.addMouseMotionListener(new MouseMotionAdapter() {

            @Override
            public void mouseDragged(MouseEvent e) {

                // 鼠标拖动时，记录坐标
                endx = e.getX();
                endy = e.getY();
                int x = Math.min(startx, endx);
                int y = Math.min(starty, endy);
                int width = Math.abs(endx - startx) + 1;
                int height = Math.abs(endy - starty) + 1;
                // 创建缓存图片
                tempImage2 = (BufferedImage) createImage(ScreenShot.this.getWidth(), ScreenShot.this.getHeight());

                // 获取画布画笔
                Graphics g = tempImage2.getGraphics();
                // 将加深颜色的整个屏幕加入
                g.drawImage(tempImage, 0, 0, null);

                g.setColor(Color.BLUE);
                // 画截图的范围
                g.drawRect(x - 1, y - 1, width + 1, height + 1);
                // 使用刚开始没有任何变化的屏幕根据坐标截取范围
                saveImage = image.getSubimage(x, y, width, height);
                // 加入（即截取的部分颜色又显示正常）
                g.drawImage(saveImage, x, y, null);
                // 显示在屏幕上
                ScreenShot.this.getGraphics().drawImage(tempImage2, 0, 0, ScreenShot.this);
                // 记录最后的截图范围坐标
                xx = x;
                yy = y;
                ww = width;
                hh = height;

            }
        });

        // 置顶
        this.setAlwaysOnTop(true);
    }

    // 绘制图片
    @Override
    public void paint(Graphics g) {
        // 绘制一个全屏幕加深的图片
        RescaleOp ro = new RescaleOp(0.7f, 0, null);
        tempImage = ro.filter(image, null);
        g.drawImage(tempImage, 0, 0, this);
    }

    // 保存图片
    public String saveImage() {
        // 时间戳
        SimpleDateFormat sdf = new SimpleDateFormat("yyyymmddHHmmss");
        String fileName = sdf.format(new Date());
        String imgFormat = sm.getImgFormat() != null ? sm.getImgFormat() : "png";

        // 直接默认保存
        File f = new File(sm.getCustomSavePath());
        if (!f.exists()) {
            f.mkdirs();
        }
        String filePath = sm.getCustomSavePath() + "\\" + fileName + "." + imgFormat;

        // 写入文件
        // 点击一下表示截全屏
        if (saveImage == null) {
            saveImage = image;
        }

        try {
            ImageIO.write(saveImage, imgFormat, new File(filePath));
        } catch (Exception ex) {
            return "";
        }

        // 释放资源
        dispose();
        return filePath;
    }

    // 初始化配置
    public void initPro() {
        // 初始化按键对照表
        SshotUtils su = new SshotUtils();
        su.initKeyMap();
        // 原理：首先判断exe所在目录是否存在隐藏的配置文件screentshotsetup.properties，
        // 如果存在，则读取此配置文件，如果不存在则生成一个默认的隐藏的配置文件
        // 实例化一个参数对象
        // 这个是用于设置具体参数
        sm = new SetupMsg();
        // 这个是用于显示具体参数
        SetupParams sp = new SetupParams();
        File f = new File("screentshotsetup.properties");
        // 判断是否存在文件
        boolean fileExit = su.isFileExit(f);
        if (fileExit) {
            // 获取默认配置信息
            try {
                // 获取显示参数
                sp = su.getDefaultMsg(f);
                // 转化为使用参数
                sm = su.trunSm(sp);
            } catch (Exception e1) {
                sm = new SetupMsg();
                e1.printStackTrace();
            }
        }
    }
}