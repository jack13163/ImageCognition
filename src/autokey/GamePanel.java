package autokey;

import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import java.util.Vector;

import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GamePanel extends JPanel implements Runnable, ActionListener, MouseListener, MouseMotionListener {
    JLabel label1 = new JLabel("X:");
    JTextField jtf1 = new JTextField(5);
    JLabel label2 = new JLabel("Y:");
    JTextField jtf2 = new JTextField(5);
    JButton btn1 = new JButton("定位");
    JLabel label3 = new JLabel("（按ESC停止）");
    JButton btn2 = new JButton("添加");

    Vector<String> vector = new Vector<String>();
    JLabel label6 = new JLabel("点击方式:");
    JComboBox jcb = new JComboBox(vector);

    JLabel label5 = new JLabel("延时(ms):");
    JTextField jtf3 = new JTextField("2000", 5);
    JButton btnDelay = new JButton("添加");

    JLabel label7 = new JLabel("按键方式:");
    Vector<String> vector2 = new Vector<String>();
    JComboBox jcb2 = new JComboBox(vector2);
    JLabel label8 = new JLabel("0~9|A~Z:");
    JTextField jtf4 = new JTextField(10);
    JButton btn6 = new JButton("添加");

    JLabel lblInputContent = new JLabel("输入内容:");
    JTextArea txtInputContent = new JTextArea();
    JScrollPane jspContent = new JScrollPane(txtInputContent);
    JButton btnInputContent = new JButton("添加");

    JLabel lblScript = new JLabel("脚本内容:");
    JTextArea jta = new JTextArea("<loop id=\"1\">\n</loop>");
    JScrollPane jsp = new JScrollPane(jta);

    JButton btn7 = new JButton("导入");
    JButton btn8 = new JButton("导出");
    static JButton btnStart = new JButton("开始");
    JLabel label4 = new JLabel("（按ESC停止）");
    static JButton btn4 = new JButton("停止");

    public static boolean xystate = false;

    public GamePanel() {
        super();

        vector.add("左击");
        vector.add("右击");
        vector.add("双击");

        vector2.add("键入");
        vector2.add("键出");
        vector2.add("键入键出");

        JPanel contentPanel = new JPanel();
        contentPanel.setLayout(new BorderLayout());
        add(contentPanel);

        JPanel northPanel = new JPanel();
        JPanel centerPanel = new JPanel();
        JPanel southPanel = new JPanel();
        northPanel.setPreferredSize(new Dimension(400,200));
        centerPanel.setPreferredSize(new Dimension(400,200));
        southPanel.setPreferredSize(new Dimension(400,100));
        contentPanel.add(northPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(southPanel, BorderLayout.SOUTH);

        northPanel.setLayout(new GridLayout(4, 1));
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(label1);
        panel1.add(jtf1);
        panel1.add(label2);
        panel1.add(jtf2);
        panel1.add(btn1);
        panel1.add(label3);
        panel1.add(btn2);
        northPanel.add(panel1);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel2.add(label6);
        panel2.add(jcb);
        northPanel.add(panel2);

        JPanel panel7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel7.add(label5);
        panel7.add(jtf3);
        panel7.add(btnDelay);
        northPanel.add(panel7);

        JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel4.add(label7);
        panel4.add(jcb2);
        panel4.add(jtf4);
        panel4.add(label8);
        panel4.add(btn6);
        northPanel.add(panel4);

        centerPanel.setLayout(new GridLayout(2, 1));
        JPanel panel3 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel3.add(lblInputContent);
        jspContent.setPreferredSize(new Dimension(200, 90));
        panel3.add(jspContent);
        panel3.add(btnInputContent);
        centerPanel.add(panel3);

        // 脚本框
        JPanel panel6 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel6.add(lblScript);
        jsp.setPreferredSize(new Dimension(300, 90));
        panel6.add(jsp);
        centerPanel.add(panel6);

        JPanel panel5 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel5.add(btn7);
        panel5.add(btn8);
        panel5.add(btnStart);
        panel5.add(label4);
        panel5.add(btn4);
        southPanel.add(panel5);

        addMouseListener(this);
        addMouseMotionListener(this);
        btn1.addActionListener(this);
        btn2.addActionListener(this);
        btnStart.addActionListener(this);
        btn2.addKeyListener(new MyListener());
        btnStart.addKeyListener(new MyListener());
        btn4.addKeyListener(new MyListener());
        btn4.addActionListener(this);
        btn4.setEnabled(false);
        jcb.addActionListener(this);
        btnDelay.addActionListener(this);
        btnInputContent.addActionListener(this);
        jcb2.addActionListener(this);
        btn6.addActionListener(this);
        btn7.addActionListener(this);
        btn8.addActionListener(this);
    }

    public void run() {
        while (true) {
            synchronized (this) {
                if (xystate) {
                    btn1.setEnabled(true);
                    xystate = false;
                    break;
                }
            }

            jtf1.setText("" + MouseInfo.getPointerInfo().getLocation().getX());
            jtf2.setText("" + MouseInfo.getPointerInfo().getLocation().getY());
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent arg0) {
        if (btn1.equals(arg0.getSource())) {
            btn1.setEnabled(false);
            new Thread(this).start();
        }
        if (btn2.equals(arg0.getSource())) {
            String str = jta.getText();
            jta.setText(str.substring(0, str.lastIndexOf("</loop>")));
            jta.append("<move x=\"");
            String temp = jtf1.getText();
            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) != '.') {
                    jta.append("" + temp.charAt(i));
                } else {
                    break;
                }
            }

            jta.append("\" y=\"");
            temp = jtf2.getText();
            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) != '.') {
                    jta.append("" + temp.charAt(i));
                } else {
                    break;
                }
            }
            jta.append("\"/>\n");
            jta.append("</loop>");
        }
        if (btnStart.equals(arg0.getSource())) {
            btn4.setEnabled(true);
            btnStart.setEnabled(false);
            try {
                OutputStream fw = new FileOutputStream("info.xml");
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fw,"UTF-8")));
                out.write(jta.getText().toString());
                out.flush();
                out.close();
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                new thdStart();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (btn4.equals(arg0.getSource())) {
            thdStart.state = true;
            btnStart.setEnabled(true);
            btn4.setEnabled(false);
        }
        if (jcb.equals(arg0.getSource())) {
            String str = jta.getText();
            jta.setText(str.substring(0, str.lastIndexOf("</loop>")));
            jta.append("<mousePress id=\"");
            switch (jcb.getSelectedIndex()) {
                case 0: {
                    jta.append("left");
                    jta.append("\"/>\n");
                    jta.append("<mouseRelease id=\"");
                    jta.append("left");
                    break;
                }
                case 1: {
                    jta.append("right");
                    jta.append("\"/>\n");
                    jta.append("<mouseRelease id=\"");
                    jta.append("right");
                    break;
                }
                case 2: {
                    jta.append("left");
                    jta.append("\"/>\n");
                    jta.append("<mouseRelease id=\"");
                    jta.append("left");
                    jta.append("\"/>\n");
                    jta.append("<mousePress id=\"");
                    jta.append("left");
                    jta.append("\"/>\n");
                    jta.append("<mouseRelease id=\"");
                    jta.append("left");
                    break;
                }
                case 3: {
                    jta.append("center");
                    jta.append("\"/>\n");
                    jta.append("<mouseRelease id=\"");
                    jta.append("center");
                    break;
                }

            }

            jta.append("\"/>\n");
            jta.append("</loop>");
        }
        if (btnDelay.equals(arg0.getSource())) {

            String str = jtf3.getText();
            try {
                Long.parseLong(str);
                str = jta.getText();
                jta.setText(str.substring(0, str.lastIndexOf("</loop>")));
                jta.append("<delay time=\"");
                str = jtf3.getText();
                jta.append(str);
                jta.append("\"/>\n");
                jta.append("</loop>");
            } catch (Exception e) {
                jtf3.setText("延时时间设置错误!");
            }
        }
        if (btn6.equals(arg0.getSource())) {
            String str;
            char c = 0;
            str = jtf4.getText();

            if ((str.length() > 0) && (((c = str.charAt(0)) >= '0' && c <= '9')
                    || (c >= 'A' && c <= 'Z'))) {
                str = jta.getText();
                jta.setText(str.substring(0, str.lastIndexOf("</loop>")));

                switch (jcb2.getSelectedIndex()) {
                    case 0: {
                        jta.append("<keyPress value=\"");
                        jta.append(String.valueOf((int) c));
                        break;
                    }
                    case 1: {
                        jta.append("<keyRelease value=\"");
                        jta.append(String.valueOf((int) c));
                        break;
                    }
                    case 2: {
                        jta.append("<keyPress value=\"");
                        jta.append(String.valueOf((int) c));
                        jta.append("\"/>\n");
                        jta.append("<keyRelease value=\"");
                        jta.append(String.valueOf((int) c));
                        break;
                    }

                }

                jta.append("\"/>\n");
                jta.append("</loop>");
            }
        }
        if (btn7.equals(arg0.getSource())) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml5", "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showOpenDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();

                try {
                    FileReader fr = new FileReader(file);
                    char[] buf = new char[200];
                    int rs;
                    jta.setText("");
                    while ((rs = fr.read(buf)) > 0) {
                        jta.append(new String(buf, 0, rs));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (btn8.equals(arg0.getSource())) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(jta.getText().toString());
                    fw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (btnInputContent.equals(arg0.getSource())) {

            String content = txtInputContent.getText();
            if(!content.isEmpty() && content != "") {
                try {
                    String str = jta.getText();
                    jta.setText(str.substring(0, str.lastIndexOf("</loop>")));
                    jta.append("<input value=\"");
                    jta.append(content);
                    jta.append("\"/>\n");
                    jta.append("</loop>");
                } catch (Exception e) {
                    jtf3.setText("延时时间设置错误!");
                }
            }
        }
    }

    public void mouseClicked(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mouseEntered(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mouseExited(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mousePressed(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mouseReleased(MouseEvent arg0) {
        // TODO Auto-generated method stub

    }

    public void mouseDragged(MouseEvent arg0) {

    }

    public void mouseMoved(MouseEvent arg0) {

    }
}
