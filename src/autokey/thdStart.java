package autokey;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.File;
import java.util.Vector;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import utis.Bezier;
import utis.ClipboardHelper;

public class thdStart implements Runnable {
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

    public thdStart() throws Exception {
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
                NamedNodeMap loopattributes = childNodes.item(j)
                        .getAttributes();
                Node loopattribute = loopattributes.item(0);
                attributename = loopattribute.getNodeName();
                attributevalue = loopattribute.getNodeValue();

                if ("delay".equals(nodename)) {
                    robot.delay(Integer.parseInt(attributevalue));
                }
                if (("mouseMove".equals(nodename) || "move".equals(nodename)) && ("x".equals(attributename))) {
                    // 获取鼠标当前位置
                    Point p = MouseInfo.getPointerInfo().getLocation();
                    int start_x = p.x;
                    int start_y = p.y;

                    // 目标位置
                    int end_x = Integer.parseInt(loopattributes.item(0).getNodeValue());
                    int end_y = Integer.parseInt(loopattributes.item(1).getNodeValue());

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
                if ("mousePress".equals(nodename) || "press".equals(nodename)) {
                    if ("left".equals(attributevalue)) {
                        robot.mousePress(16);
                    }
                    if ("right".equals(attributevalue)) {
                        robot.mousePress(8);
                    }
                    if ("center".equals(attributevalue)) {
                        robot.mousePress(8);
                    }
                }
                if ("mouseRelease".equals(nodename) || "release".equals(nodename)) {
                    if ("left".equals(attributevalue)) {
                        robot.mouseRelease(16);
                    }
                    if ("right".equals(attributevalue)) {
                        robot.mouseRelease(8);
                    }
                    if ("center".equals(attributevalue)) {
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
}
