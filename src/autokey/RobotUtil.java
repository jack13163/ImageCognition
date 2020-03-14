package autokey;

import imageprocess.Mat2BufImg;
import org.opencv.core.Mat;
import org.opencv.core.Rect;
import utis.Bezier;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Vector;

public class RobotUtil {

    public Robot robot;

    public RobotUtil(Robot robot) {
        this.robot = robot;
    }

    /**
     * 缓慢移动鼠标
     *
     * @param end_x
     * @param end_y
     */
    public void moveMouseSlowly(int end_x, int end_y) {

        // 获取鼠标当前位置
        Point p = MouseInfo.getPointerInfo().getLocation();
        int start_x = p.x;
        int start_y = p.y;

        int step = 500;
        Vector<Point> points = Bezier.generatePoints(p,
                new Point((start_x + end_x) * 2 / 3, (start_y + end_y) / 3),
                new Point(end_x, end_y),
                500);
        for (int i = 0; i < step; i++) {
            robot.mouseMove(points.get(i).x, points.get(i).y);
            robot.delay(1);//停顿1毫秒
        }
    }

    /**
     * 返回到桌面
     */
    public void backToDesktop() {
        robot.keyPress(KeyEvent.VK_WINDOWS);
        robot.keyPress(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_D);
        robot.keyRelease(KeyEvent.VK_WINDOWS);
    }

    /**
     * 获取指定位置颜色
     *
     * @param x
     * @param y
     * @return
     */
    public Color getLocationColor(int x, int y) {
        // 获取指定位置颜色
        return robot.getPixelColor(x, y);
    }

    //Shift组合键
    public void keyPressWithShift(int key) {
        //按下Shift
        robot.keyPress(KeyEvent.VK_SHIFT);
        //按下某个键
        robot.keyPress(key);

        //释放某个键
        robot.keyRelease(key);
        //释放Shift
        robot.keyRelease(KeyEvent.VK_SHIFT);
        //等待100ms
        robot.delay(100);
    }

    //Ctrl组合键
    public void keyPressWithCtrl(int key) {
        robot.keyPress(KeyEvent.VK_CONTROL);
        robot.keyPress(key);

        robot.keyRelease(key);
        robot.keyRelease(KeyEvent.VK_CONTROL);

        robot.delay(100);
    }

    //Alt组合键
    public void keyPressWithAlt(int key) {
        robot.keyPress(KeyEvent.VK_ALT);
        robot.keyPress(key);

        robot.keyRelease(key);
        robot.keyRelease(KeyEvent.VK_ALT);
        robot.delay(100);
    }

    // 输入字符串
    public void keyPressString(String str) {
        //获取剪切板
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        //将传入字符串封装下
        Transferable tText = new StringSelection(str);
        //将字符串放入剪切板
        clip.setContents(tText, null);
        //按下Ctrl+V实现粘贴文本
        keyPressWithCtrl(KeyEvent.VK_V);
        robot.delay(100);
    }

    //输入数字
    public void keyPressNumber(int number) {
        //将数字转成字符串
        String str = Integer.toString(number);
        //调用字符串的方法
        keyPressString(str);
    }

    //实现按一次某个按键
    public void keyPress(int key) {
        //按下键
        robot.keyPress(key);
        //释放键
        robot.keyRelease(key);
        robot.delay(1000);
    }

    /**
     * 利用快捷键关机
     */
    public void shutdown() {
        // 首先切换到桌面
        backToDesktop();

        //然后alt+f4
        keyPressWithAlt(KeyEvent.VK_F4);

        //最后按下enter键
        robot.keyPress(KeyEvent.VK_ENTER);
        robot.delay(20);
        robot.keyRelease(KeyEvent.VK_ENTER);
    }

    /**
     * 鼠标单击（左击）,要双击就连续调用
     *
     * @param x     x坐标位置
     * @param y     y坐标位置
     * @param delay 该操作后的延迟时间
     */
    public void clickLMouse(int x, int y, int delay) {
        moveMouseSlowly(x, y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(10);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(delay);
    }

    /**
     * 鼠标双击
     *
     * @param x     x坐标位置
     * @param y     y坐标位置
     * @param delay 该操作后的延迟时间
     */
    public void doubleClickLMouse(int x, int y, int delay) {
        moveMouseSlowly(x, y);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(10);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(10);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(10);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(delay);
    }

    /**
     * 鼠标右击,要双击就连续调用
     *
     * @param x     x坐标位置
     * @param y     y坐标位置
     * @param delay 该操作后的延迟时间
     */
    public void clickRMouse(int x, int y, int delay) {
        moveMouseSlowly(x, y);
        robot.mousePress(InputEvent.BUTTON3_MASK);
        robot.delay(10);
        robot.mouseRelease(InputEvent.BUTTON3_MASK);
        robot.delay(delay);
    }

    /**
     * 键盘输入（一次只能输入一个字符）
     *
     * @param ks    键盘输入的字符数组
     * @param delay 输入一个键后的延迟时间
     */
    public void pressKeys(int[] ks, int delay) {
        for (int i = 0; i < ks.length; i++) {
            keyPress(ks[i]);
            robot.delay(delay);
        }
    }

    /**
     * 全选
     */
    void doSelectAll() {
        keyPressWithCtrl(KeyEvent.VK_A);
    }

    /**
     * 复制
     */
    void doCopy() {
        keyPressWithCtrl(KeyEvent.VK_C);
    }

    /**
     * 粘贴
     */
    void doParse() {
        keyPressWithCtrl(KeyEvent.VK_V);
    }

    /**
     * 捕捉全屏慕
     *
     * @return
     */
    public BufferedImage captureFullScreen() {
        BufferedImage fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        return fullScreenImage;
    }

    /**
     * 捕捉全屏慕
     *
     * @return
     */
    public Mat captureFullScreenMat() throws IOException {
        BufferedImage fullScreen = captureFullScreen();
        Mat fullscreenMat = Mat2BufImg.BufferedImage2Mat(fullScreen);
        return fullscreenMat;
    }

    /**
     * 捕捉屏幕的一个矫形区域
     *
     * @param rect
     * @return
     */
    public BufferedImage capturePartScreen(Rect rect) {
        moveMouseSlowly(rect.x, rect.y);
        BufferedImage partScreenImage = robot.createScreenCapture(new Rectangle(rect.width, rect.height));
        return partScreenImage;
    }

    /**
     * 捕捉屏幕的一个矫形区域
     *
     * @return
     */
    public Mat capturePartScreenMat(Rect rect) throws IOException {
        BufferedImage fullScreen = capturePartScreen(rect);
        Mat fullscreenMat = Mat2BufImg.BufferedImage2Mat(fullScreen);
        return fullscreenMat;
    }

    /**
     * 点击矩形的中心
     *
     * @param rect
     */
    public void clickRectCenter(Rect rect) {
        moveMouseSlowly(rect.x + rect.width / 2, rect.y + rect.height / 2);
        robot.mousePress(InputEvent.BUTTON1_MASK);
        robot.delay(10);
        robot.mouseRelease(InputEvent.BUTTON1_MASK);
        robot.delay(500);
    }
}
