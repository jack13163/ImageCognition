package autokey;

import org.jb2011.lnf.beautyeye.BeautyEyeLNFHelper;

import java.awt.*;

import javax.swing.*;

public class kettest extends JFrame {
    public static Dimension tk = Toolkit.getDefaultToolkit().getScreenSize();

    public kettest() {
        super("AutoKey");
        System.out.println("屏幕大小为：" + tk.getWidth() + " " + tk.getHeight());
        JPanel jp = new GamePanel();
        add(jp);

        int width = 420;
        int height = 500;
        this.setBounds((tk.width - width) / 2, (tk.height - height) / 2, width, height);
        this.setVisible(true);
        setAlwaysOnTop(true);
    }

    /**
     * @param args
     */
    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            public void run() {

                // 国人牛逼主题，值得学习
                // 设置本属性将改变窗口边框样式定义
                // 系统默认样式 osLookAndFeelDecorated
                // 强立体半透明 translucencyAppleLike
                // 弱立体感半透明 translucencySmallShadow
                // 普通不透明 generalNoTranslucencyShadow
                BeautyEyeLNFHelper.frameBorderStyle = BeautyEyeLNFHelper.FrameBorderStyle.generalNoTranslucencyShadow;
                // 设置主题为BeautyEye
                try {
                    BeautyEyeLNFHelper.launchBeautyEyeLNF();
                } catch (Exception e) {
                    e.printStackTrace();
                }
                // 隐藏“设置”按钮
                UIManager.put("RootPane.setupButtonVisible", false);
                // 开启/关闭窗口在不活动时的半透明效果
                // 设置此开关量为false即表示关闭之，BeautyEye LNF中默认是true
                BeautyEyeLNFHelper.translucencyAtFrameInactive = false;
                // 设置BeantuEye外观下JTabbedPane的左缩进
                // 改变InsetsUIResource参数的值即可实现
                UIManager.put("TabbedPane.tabAreaInsets", new javax.swing.plaf.InsetsUIResource(3, 20, 2, 20));
                // UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                new kettest().setDefaultCloseOperation(EXIT_ON_CLOSE);
            }
        });
    }

}
