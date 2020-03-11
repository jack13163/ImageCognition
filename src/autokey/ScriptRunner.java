package autokey;


import javax.swing.*;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import confnition.CoordBean;
import confnition.ImageCognition;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utis.Bezier;
import utis.ClipboardHelper;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.util.Vector;

public class ScriptRunner implements Runnable {
    Robot robot;
    static boolean state;
    Document doc = null;
    String nodename = null;
    String attributename = null;
    String attributevalue = null;
    long loop;

    public void init(String xmlFile) throws Exception {
        DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
        DocumentBuilder db = dbf.newDocumentBuilder();
        doc = db.parse(new File(xmlFile));
    }

    public ScriptRunner() throws Exception {
        init("info.xml");

        try {
            robot = new Robot();
        } catch (AWTException e1) {
            e1.printStackTrace();
        }
        state = false;
        new Thread(this).start();
    }

    public void run() {

        NodeList nodeList = doc.getElementsByTagName("loop");
        Node fatherNode = nodeList.item(0);
        NamedNodeMap attributes = fatherNode.getAttributes();

        try {
            loop = Long.parseLong(attributes.item(0).getNodeValue());
        } catch (Exception e) {
            loop = -1;
        }
        if (loop == -1) {
            while (true) {
                synchronized (this) {
                    if (state) {
                        state = false;
                        break;
                    }
                }
                doLoop(fatherNode);
            }
        } else if (loop > 0) {
            for (int i = 0; i < loop; i++) {
                synchronized (this) {
                    if (state) {
                        state = false;
                        break;
                    }
                }
                doLoop(fatherNode);
            }
            GamePanel.btnStart.setEnabled(true);
            GamePanel.btnStop.setEnabled(false);
        }
    }

    private void doLoop(Node fatherNode) {
        NodeList childNodes = fatherNode.getChildNodes();
        // System.out.println(childNodes.getLength());
        for (int j = 0; j < childNodes.getLength(); j++) {
            Node childNode = childNodes.item(j);

            if (childNode instanceof Element) {
                nodename = childNode.getNodeName();
                NamedNodeMap loopattributes = childNodes.item(j).getAttributes();
                Node loopattribute = loopattributes.item(0);
                attributename = loopattribute.getNodeName();
                attributevalue = loopattribute.getNodeValue();

                if ("delay".equals(nodename)) {
                    robot.delay(Integer.parseInt(attributevalue));
                }
                if ("image".equals(nodename)) {
                    try {
                        // 屏幕截图
                        FindImage.captureScreen("screen.png", "data/images/");
                        // 找图
                        java.util.List<CoordBean> results = FindImage.findImage4FullScreen("data/images/screen.png", attributevalue, ImageCognition.SIM_ACCURATE_VERY);
                        if (results != null) {
                            for (int i = 0; i < results.size(); i++) {
                                // 找到图片中心
                                CoordBean loc = results.get(i);
                                int end_x = loc.getX() + loc.getWidth() / 2;
                                int end_y = loc.getY() + loc.getHeight() / 2;
                                moveMouseSlowly(end_x, end_y);
                                robot.delay(1000);// 延时1秒
                            }
                        } else {
                            JOptionPane.showMessageDialog(null, "无法找到图片", "提示", JOptionPane.WARNING_MESSAGE);
                            return;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (("mouseMove".equals(nodename) || "move".equals(nodename)) && ("x".equals(attributename))) {
                    // 目标位置
                    int end_x = Integer.parseInt(loopattributes.item(0).getNodeValue());
                    int end_y = Integer.parseInt(loopattributes.item(1).getNodeValue());

                    moveMouseSlowly(end_x, end_y);
                }
                if ("mousePress".equals(nodename) || "press".equals(nodename)) {
                    if ("left".equals(attributevalue)) {
                        robot.mousePress(InputEvent.BUTTON1_MASK);// 左键
                    }
                    if ("right".equals(attributevalue)) {
                        robot.mousePress(InputEvent.BUTTON3_MASK);// 右键
                    }
                    if ("center".equals(attributevalue)) {
                        robot.mousePress(8);
                    }
                }
                if ("mouseRelease".equals(nodename) || "release".equals(nodename)) {
                    if ("left".equals(attributevalue)) {
                        robot.mouseRelease(InputEvent.BUTTON1_MASK);
                    }
                    if ("right".equals(attributevalue)) {
                        robot.mouseRelease(InputEvent.BUTTON3_MASK);
                    }
                    if ("double".equals(attributevalue)) {
                        robot.mouseRelease(4);
                    }
                }
                if ("mouseWheel".equals(nodename) || "wheel".equals(nodename)) {
                    // 此方法中的wheelAmt指的是滑动滚轮上的刻度数.
                    //    如果此参数小于0,则表示向上滚动滑轮
                    //    如果此参数大于0,则表示向下滚动滑轮
                    //    例如,向上滚动5个刻度滑轮:
                    //    robot.mouseWheel(-5);
                    robot.mouseWheel(Integer.parseInt(attributevalue));
                }
                if ("keyPress".equals(nodename)) {
                    robot.keyPress(Integer.parseInt(attributevalue));
                }
                if ("keyRelease".equals(nodename)) {
                    robot.keyRelease(Integer.parseInt(attributevalue));
                }
                if ("input".equals(nodename)) {
                    // 复制内容到剪贴板
                    ClipboardHelper.setClipboardString(attributevalue);
                    ClipboardHelper.keyPressWithCtrl(KeyEvent.VK_V);
                }
            }
        }
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
     * @return
     */
    public Icon captureFullScreen() {
        BufferedImage fullScreenImage = robot.createScreenCapture(new Rectangle(Toolkit.getDefaultToolkit().getScreenSize()));
        ImageIcon icon = new ImageIcon(fullScreenImage);
        return icon;
    }

    /**
     * 捕捉屏幕的一个矫形区域
     *
     * @param x      x坐标位置
     * @param y      y坐标位置
     * @param width  矩形的宽
     * @param height 矩形的高
     * @return
     */
    public Icon capturePartScreen(int x, int y, int width, int height) {
        robot.mouseMove(x, y);
        BufferedImage fullScreenImage = robot.createScreenCapture(new Rectangle(width, height));
        ImageIcon icon = new ImageIcon(fullScreenImage);
        return icon;
    }
}
