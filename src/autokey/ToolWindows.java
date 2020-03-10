package autokey;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

/*
 * 操作窗口
 */
public class ToolWindows extends JWindow {
    private ScreenShot parent;
    ImageIcon copyImage = new ImageIcon("data/images/copy.png");
    ImageIcon saveImage = new ImageIcon("data/images/save.png");
    ImageIcon cancelImage = new ImageIcon("data/images/cancel.png");
    ImageIcon drawImage = new ImageIcon("data/images/draw.png");

    public ToolWindows(ScreenShot parent, int x, int y) {
        this.parent = parent;
        this.init();
        this.setLocation(x, y);
        this.pack();
        this.setVisible(true);

    }

    private void init() {
        this.setLayout(new BorderLayout());
        JToolBar toolBar = new JToolBar("a");

        //
        JButton copyClipButton = new JButton(copyImage);
        copyClipButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    parent.copyClipImage();
                    dispose();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        toolBar.add(copyClipButton);

        // 保存按钮
        JButton saveButton = new JButton(saveImage);
        saveButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                try {
                    parent.saveImage();
                    dispose();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        });
        toolBar.add(saveButton);

        // 标记
        JButton drawButton = new JButton(drawImage);
        drawButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.drawImage();
                // dispose();
            }
        });
        toolBar.add(drawButton);

        // 关闭按钮
        JButton closeButton = new JButton(cancelImage);
        closeButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                parent.cancel();
                dispose();
            }
        });
        toolBar.add(closeButton);
        this.add(toolBar, BorderLayout.NORTH);

        // 置顶
        this.setAlwaysOnTop(true);
    }

}