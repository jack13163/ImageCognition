package pcwx;

import autokey.RobotUtil;
import imageprocess.CVHelper;
import org.opencv.core.*;
import org.opencv.highgui.HighGui;
import org.opencv.imgcodecs.Imgcodecs;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

public class Main {
    public static Robot robot;
    public static RobotUtil robotUtil;
    public static CVHelper cvHelper;

    private static Mat fullscreenMat;
    private static boolean isRun = true;
    private static boolean debug = false;

    public static void main(String[] args) {

        try {
            robot = new Robot();
            robotUtil = new RobotUtil(robot);
            cvHelper = new CVHelper();
            System.loadLibrary(Core.NATIVE_LIBRARY_NAME);
            Rect tempRect1;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        while (isRun) {
                            fullscreenMat = robotUtil.captureFullScreenMat();
                            Thread.sleep(300);
                        }
                    } catch (Exception ex) {
                        ex.printStackTrace();
                    }
                }
            }).start();
            Thread.sleep(3000);

            // 点击微信logo
            clickWxlogo();
            Thread.sleep(1000);

            // 识别微信区域
            Rect wxRect = findWxForm();
            if (wxRect == null) {
                // 最大化微信
                maxWxForm();
            }

            // 调试识别微信区域
            if (debug) {
                Mat mat = cvHelper.cutImage(fullscreenMat, wxRect);
                HighGui.imshow("矩形检测", mat);
                HighGui.waitKey(0);
            }

            // 点击联系人
            Rect people = new Rect(wxRect.x + 15, wxRect.y + 130, 30, 30);
            Rect empty = new Rect(wxRect.x, wxRect.y + 500, 60, 30);
            Rect first = new Rect(wxRect.x + 60, wxRect.y + 100, 250, 60);
            Rect listtop = new Rect(wxRect.x + 306, wxRect.y + 70, 4, 4);
            robotUtil.clickRectCenter(people);

            // 翻到最前
            for (int i = 0; i < 10; i++) {
                robotUtil.clickRectCenter(listtop);
            }
            robot.mouseWheel(-3);
            robotUtil.clickRectCenter(first);
            robotUtil.clickRectCenter(empty);

            // 查找下一个需要发送消息的联系人
            Rect node = findNextNode(wxRect);
            while (node != null) {
                while (node != null) {
                    robotUtil.clickRectCenter(node);
                    doJob();
                    node = findNextNode(wxRect);
                }

                robot.mouseWheel(3);
                robotUtil.clickRectCenter(empty);
                Thread.sleep(1000);
                node = findNextNode(wxRect);
            }

            // 结束任务
            isRun = false;
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 执行其他操作
     */
    public static void doJob() {
        // 个人资料
        Mat info = Imgcodecs.imread("data/images/pcwx_personinfo.png");
        Rect rect = cvHelper.match(fullscreenMat, info, 0.95);
        if (rect != null) {
            robotUtil.clickRectCenter(rect);
        } else {
            System.out.println("群");
        }
    }

    /**
     * 点击微信logo
     */
    public static void clickWxlogo() {
        clickImage("data/images/pcwx_logo.png");
    }

    /**
     * 最大化微信
     */
    public static void maxWxForm(){
        clickImage("data/images/wx_max.png");
    }

    /**
     * 点击图片
     * @param imagepath
     */
    public static void clickImage(String imagepath) {
        Mat logo = Imgcodecs.imread(imagepath);
        Rect tempRect1 = cvHelper.match(fullscreenMat, logo, 0.95);
        while (tempRect1 == null) {
            tempRect1 = cvHelper.match(fullscreenMat, logo, 0.95);
        }
        robotUtil.clickRectCenter(tempRect1);
    }

    /**
     * 找到微信窗口
     *
     * @throws Exception
     */
    public static Rect findWxForm() throws Exception {

        Mat newgroupImage = Imgcodecs.imread("data/images/pcwx_newgroup.png");
        Mat leftBottomImage = Imgcodecs.imread("data/images/pcwx_leftbottom.png");
        Mat close = Imgcodecs.imread("data/images/pcwx_close.png");

        Rect rect1 = cvHelper.match(fullscreenMat, newgroupImage, 0.95);
        Rect rect2 = cvHelper.match(fullscreenMat, leftBottomImage, 0.95);
        Rect rect3 = cvHelper.match(fullscreenMat, close, 0.95);

        if (rect1 != null && rect2 != null && rect3 != null) {
            int x = rect1.x - 270;
            int y = rect1.y - 22;
            int width = (rect3.x - x) + rect3.width + 10;
            int height = (rect2.y - y) + rect2.height + 14;
            return new Rect(x, y, width, height);
        } else {
            return null;
        }
    }

    /**
     * 查找列表的下一个节点
     *
     * @throws Exception
     */
    public static Rect findNextNode(Rect wxRect) throws Exception {

        CVHelper util = new CVHelper();

        // 确定检测区域
        int start_x = 60;
        int start_y = 0;
        Rect cutRect = new Rect(wxRect.x + start_x, wxRect.y + start_y, 300, wxRect.height);

        // 检测矩形
        List<Rect> selects = util.findRects(fullscreenMat, cutRect, 200, 50, 300, 110);
        List<Rect> icons = util.findRects(fullscreenMat, cutRect, 30, 30, 60, 60);
        List<Rect> rects = new ArrayList<>();
        rects.addAll(selects);
        rects.addAll(icons);

        // 查找下一个
        List<Rect> result = rects.stream().sorted(new Comparator<Rect>() {
            @Override
            public int compare(Rect o1, Rect o2) {
                return o1.y - o2.y;
            }
        }).collect(Collectors.toList());
        int index = -1;
        try {
            index = result.indexOf(selects.get(0));
        } catch (Exception ex) {
            // 没有找到选中的元素
            ex.printStackTrace();
            return null;
        }
        int nextIndex = index + 1;
        if (nextIndex == result.size()) {
            return null;
        } else {
            Rect rect = new Rect(result.get(nextIndex).x + wxRect.x + start_x, result.get(nextIndex).y + wxRect.y,
                    result.get(nextIndex).width, result.get(nextIndex).height);
            return rect;
        }
    }
}
