package autokey;//package autokey;

import java.awt.MouseInfo;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
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


@SuppressWarnings("serial")
public class GamePanel extends JPanel implements Runnable, ActionListener,
        MouseListener, MouseMotionListener {
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
    JButton btn5 = new JButton("添加");
    JLabel label7 = new JLabel("按键方式:");
    Vector<String> vector2 = new Vector<String>();
    JComboBox jcb2 = new JComboBox(vector2);
    JLabel label8 = new JLabel("0~9|A~Z:");
    JTextField jtf4 = new JTextField(10);
    JButton btn6 = new JButton("添加");
    JTextArea jta = new JTextArea("<loop>\n</loop>");
    JScrollPane jsp = new JScrollPane(jta);
    JButton btn7 = new JButton("导入");
    JButton btn8 = new JButton("导出");
    static JButton btn3 = new JButton("开始");
    JLabel label4 = new JLabel("（按ESC停止）");
    static JButton btn4 = new JButton("停止");
    public static boolean xystate = false;

    GamePanel() {
        super();
        setLayout(null);
        this.setBounds((kettest.tk.width - 400) / 2, (kettest.tk.height - 300) / 2, 400, 300);
        label1.setBounds(10, 10, 15, 25);
        jtf1.setBounds(30, 10, 40, 25);
        label2.setBounds(80, 10, 15, 25);
        jtf2.setBounds(100, 10, 40, 25);
        btn1.setBounds(150, 10, 60, 25);
        label3.setBounds(215, 10, 100, 25);
        btn2.setBounds(300, 10, 60, 25);

        label6.setBounds(10, 40, 60, 25);
        jcb.setBounds(70, 40, 60, 25);
        label5.setBounds(150, 40, 60, 25);
        jtf3.setBounds(210, 40, 80, 25);
        btn5.setBounds(300, 40, 60, 25);

        label7.setBounds(10, 70, 60, 25);
        jcb2.setBounds(70, 70, 60, 25);
        label8.setBounds(150, 70, 60, 25);
        jtf4.setBounds(210, 70, 80, 25);
        btn6.setBounds(300, 70, 60, 25);

        jsp.setBounds(10, 100, 350, 225);

        btn7.setBounds(10, 330, 60, 25);
        btn8.setBounds(80, 330, 60, 25);
        btn3.setBounds(150, 330, 60, 25);
        label4.setBounds(215, 330, 100, 25);
        btn4.setBounds(300, 330, 60, 25);

        vector.add("左击");
        vector.add("右击");
        vector.add("双击");

        vector2.add("键入");
        vector2.add("键出");
        vector2.add("键入键出");

        add(label1);
        add(jtf1);
        add(label2);
        add(jtf2);
        add(btn1);
        add(label3);
        add(btn2);

        add(label6);
        add(jcb);
        add(label5);
        add(jtf3);
        add(btn5);

        add(label7);
        add(jcb2);
        jcb2.setSelectedIndex(0);
        add(label8);
        add(jtf4);
        add(btn6);

        add(jsp);
        add(btn7);
        add(btn8);
        add(btn3);
        add(label4);
        add(btn4);

        btn2.addKeyListener(new MyListener());
        addMouseListener(this);
        addMouseMotionListener(this);
        btn1.addActionListener(this);
        btn2.addActionListener(this);
        btn3.addActionListener(this);
        btn3.addKeyListener(new MyListener());
        btn4.addKeyListener(new MyListener());
        btn4.addActionListener(this);
        btn4.setEnabled(false);
        jcb.addActionListener(this);
        btn5.addActionListener(this);
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
        if (btn3.equals(arg0.getSource())) {
            btn4.setEnabled(true);
            btn3.setEnabled(false);
            try {
                FileWriter fw = new FileWriter(new File("info.xml"));
                fw.write(jta.getText().toString());
                fw.close();
            } catch (IOException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            try {
                new thdStart();
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        if (btn4.equals(arg0.getSource())) {
            thdStart.state = true;
            btn3.setEnabled(true);
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
        if (btn5.equals(arg0.getSource())) {

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
                //    System.out.println(file.getPath());

                try {
                    FileReader fr = new FileReader(file);
                    char[] buf = new char[200];
                    int rs;
                    jta.setText("");
                    while ((rs = fr.read(buf)) > 0) {
                        //System.out.println(new String(buf,0,rs));
                        jta.append(new String(buf, 0, rs));
                    }
                } catch (Exception e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }
        }
        if (btn8.equals(arg0.getSource())) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter(
                    "xml", "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(jta.getText().toString());
                    fw.close();
                } catch (IOException e1) {
                    // TODO Auto-generated catch block
                    e1.printStackTrace();
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
