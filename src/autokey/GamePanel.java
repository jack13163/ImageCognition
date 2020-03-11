package autokey;


import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.io.*;
import java.util.Vector;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;


public class GamePanel extends JPanel implements Runnable, ActionListener, MouseListener, MouseMotionListener {
    JLabel lblX = new JLabel("X:");
    JTextField txtX = new JTextField(5);
    JLabel lblY = new JLabel("Y:");
    JTextField txtY = new JTextField(5);
    JButton btnLocation = new JButton("定位");
    JButton btnAddLocation = new JButton("添加");

    JLabel lblFindImage= new JLabel("找图:");
    JTextField txtImagePath = new JTextField(20);
    JButton btnScreencut = new JButton("截图");
    JButton btnAddImage = new JButton("添加");

    Vector<String> vector = new Vector<String>();
    JLabel lblClickType = new JLabel("点击方式:");
    JComboBox btnClickType = new JComboBox(vector);

    JLabel lblDelay = new JLabel("延时(ms):");
    JTextField txtDelay = new JTextField("2000", 5);
    JButton btnDelay = new JButton("添加");

    JLabel lblKeyType = new JLabel("按键方式:");
    Vector<String> vector2 = new Vector<String>();
    JComboBox cbKeyType = new JComboBox(vector2);
    JLabel lblKey = new JLabel("0~9|A~Z:");
    JTextField txtKey = new JTextField(10);
    JButton btnAddKey = new JButton("添加");

    JLabel lblInputContent = new JLabel("输入内容:");
    JTextArea txtInputContent = new JTextArea();
    JScrollPane jspContent = new JScrollPane(txtInputContent);
    JButton btnInputContent = new JButton("添加");

    JLabel lblScript = new JLabel("脚本内容:");
    JTextArea txtScript = new JTextArea("<loop id=\"1\">\n</loop>");
    JScrollPane jsp = new JScrollPane(txtScript);

    JButton btnImport = new JButton("导入");
    JButton btnExport = new JButton("导出");
    static JButton btnStart = new JButton("开始");
    static JButton btnStop = new JButton("停止");

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
        northPanel.setPreferredSize(new Dimension(400, 280));
        centerPanel.setPreferredSize(new Dimension(400, 200));
        southPanel.setPreferredSize(new Dimension(400, 100));
        contentPanel.add(northPanel, BorderLayout.NORTH);
        contentPanel.add(centerPanel, BorderLayout.CENTER);
        contentPanel.add(southPanel, BorderLayout.SOUTH);

        northPanel.setLayout(new GridLayout(5, 1));
        JPanel panel1 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel1.add(lblX);
        panel1.add(txtX);
        panel1.add(lblY);
        panel1.add(txtY);
        panel1.add(btnLocation);
        panel1.add(btnAddLocation);
        northPanel.add(panel1);

        JPanel panel8 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel8.add(lblFindImage);
        panel8.add(txtImagePath);
        panel8.add(btnScreencut);
        panel8.add(btnAddImage);
        northPanel.add(panel8);

        JPanel panel2 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel2.add(lblClickType);
        panel2.add(btnClickType);
        northPanel.add(panel2);

        JPanel panel7 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel7.add(lblDelay);
        panel7.add(txtDelay);
        panel7.add(btnDelay);
        northPanel.add(panel7);

        JPanel panel4 = new JPanel(new FlowLayout(FlowLayout.LEFT));
        panel4.add(lblKeyType);
        panel4.add(cbKeyType);
        panel4.add(txtKey);
        panel4.add(lblKey);
        panel4.add(btnAddKey);
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
        panel5.add(btnImport);
        panel5.add(btnExport);
        panel5.add(btnStart);
        panel5.add(btnStop);
        southPanel.add(panel5);

        addMouseListener(this);
        addMouseMotionListener(this);
        btnLocation.addActionListener(this);
        btnAddLocation.addActionListener(this);
        btnScreencut.addActionListener(this);
        btnAddImage.addActionListener(this);
        btnStart.addActionListener(this);
        btnStart.addKeyListener(new MyListener());
        btnStop.addKeyListener(new MyListener());
        btnStop.addActionListener(this);
        btnStop.setEnabled(false);
        btnClickType.addActionListener(this);
        btnDelay.addActionListener(this);
        btnInputContent.addActionListener(this);
        cbKeyType.addActionListener(this);
        btnAddKey.addActionListener(this);
        btnImport.addActionListener(this);
        btnExport.addActionListener(this);

        addKeyListener(new MyListener());
    }

    public void run() {
        while (true) {
            synchronized (this) {
                if (xystate) {
                    btnLocation.setEnabled(true);
                    xystate = false;
                    break;
                }
            }

            txtX.setText("" + MouseInfo.getPointerInfo().getLocation().getX());
            txtY.setText("" + MouseInfo.getPointerInfo().getLocation().getY());
            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public void actionPerformed(ActionEvent arg0) {
        if (btnLocation.equals(arg0.getSource())) {
            btnLocation.setEnabled(false);
            new Thread(this).start();
        }
        if (btnAddLocation.equals(arg0.getSource())) {
            String str = txtScript.getText();
            txtScript.setText(str.substring(0, str.lastIndexOf("</loop>")));
            txtScript.append("<move x=\"");
            String temp = txtX.getText();
            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) != '.') {
                    txtScript.append("" + temp.charAt(i));
                } else {
                    break;
                }
            }

            txtScript.append("\" y=\"");
            temp = txtY.getText();
            for (int i = 0; i < temp.length(); i++) {
                if (temp.charAt(i) != '.') {
                    txtScript.append("" + temp.charAt(i));
                } else {
                    break;
                }
            }
            txtScript.append("\"/>\n");
            txtScript.append("</loop>");
        }
        if (btnScreencut.equals(arg0.getSource())) {

            try {
                // 截图
                new ScreenShot((filepath -> {
                    txtImagePath.setText(filepath);
                })).setVisible(true);
            } catch (Exception e) {
                txtDelay.setText(e.getMessage());
            }
        }
        if (btnAddImage.equals(arg0.getSource())) {

            try {
                String str = txtScript.getText();
                txtScript.setText(str.substring(0, str.lastIndexOf("</loop>")));
                txtScript.append("<image src=\"");
                txtScript.append(txtImagePath.getText());
                txtScript.append("\"/>\n");
                txtScript.append("</loop>");
            } catch (Exception e) {
                txtDelay.setText("延时时间设置错误!");
            }
        }
        if (btnStart.equals(arg0.getSource())) {
            btnStop.setEnabled(true);
            btnStart.setEnabled(false);
            try {
                OutputStream fw = new FileOutputStream("info.xml");
                PrintWriter out = new PrintWriter(new BufferedWriter(new OutputStreamWriter(fw, "UTF-8")));
                out.write(txtScript.getText().toString());
                out.flush();
                out.close();
                fw.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }

            try {
                new ScriptRunner();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        if (btnStop.equals(arg0.getSource())) {
            ScriptRunner.state = true;
            btnStart.setEnabled(true);
            btnStop.setEnabled(false);
        }
        if (btnClickType.equals(arg0.getSource())) {
            String str = txtScript.getText();
            txtScript.setText(str.substring(0, str.lastIndexOf("</loop>")));
            txtScript.append("<mousePress id=\"");
            switch (btnClickType.getSelectedIndex()) {
                case 0: {
                    txtScript.append("left");
                    txtScript.append("\"/>\n");
                    txtScript.append("<mouseRelease id=\"");
                    txtScript.append("left");
                    break;
                }
                case 1: {
                    txtScript.append("right");
                    txtScript.append("\"/>\n");
                    txtScript.append("<mouseRelease id=\"");
                    txtScript.append("right");
                    break;
                }
                case 2: {
                    txtScript.append("left");
                    txtScript.append("\"/>\n");
                    txtScript.append("<mouseRelease id=\"");
                    txtScript.append("left");
                    txtScript.append("\"/>\n");
                    txtScript.append("<mousePress id=\"");
                    txtScript.append("left");
                    txtScript.append("\"/>\n");
                    txtScript.append("<mouseRelease id=\"");
                    txtScript.append("left");
                    break;
                }
                case 3: {
                    txtScript.append("center");
                    txtScript.append("\"/>\n");
                    txtScript.append("<mouseRelease id=\"");
                    txtScript.append("center");
                    break;
                }

            }

            txtScript.append("\"/>\n");
            txtScript.append("</loop>");
        }
        if (btnDelay.equals(arg0.getSource())) {

            String str = txtDelay.getText();
            try {
                Long.parseLong(str);
                str = txtScript.getText();
                txtScript.setText(str.substring(0, str.lastIndexOf("</loop>")));
                txtScript.append("<delay time=\"");
                str = txtDelay.getText();
                txtScript.append(str);
                txtScript.append("\"/>\n");
                txtScript.append("</loop>");
            } catch (Exception e) {
                txtDelay.setText("延时时间设置错误!");
            }
        }
        if (btnAddKey.equals(arg0.getSource())) {
            String str;
            char c = 0;
            str = txtKey.getText();

            if ((str.length() > 0) && (((c = str.charAt(0)) >= '0' && c <= '9')
                    || (c >= 'A' && c <= 'Z'))) {
                str = txtScript.getText();
                txtScript.setText(str.substring(0, str.lastIndexOf("</loop>")));

                switch (cbKeyType.getSelectedIndex()) {
                    case 0: {
                        txtScript.append("<keyPress value=\"");
                        txtScript.append(String.valueOf((int) c));
                        break;
                    }
                    case 1: {
                        txtScript.append("<keyRelease value=\"");
                        txtScript.append(String.valueOf((int) c));
                        break;
                    }
                    case 2: {
                        txtScript.append("<keyPress value=\"");
                        txtScript.append(String.valueOf((int) c));
                        txtScript.append("\"/>\n");
                        txtScript.append("<keyRelease value=\"");
                        txtScript.append(String.valueOf((int) c));
                        break;
                    }

                }

                txtScript.append("\"/>\n");
                txtScript.append("</loop>");
            }
        }
        if (btnImport.equals(arg0.getSource())) {
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
                    txtScript.setText("");
                    while ((rs = fr.read(buf)) > 0) {
                        txtScript.append(new String(buf, 0, rs));
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
        if (btnExport.equals(arg0.getSource())) {
            JFileChooser chooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("xml", "xml");
            chooser.setFileFilter(filter);
            int returnVal = chooser.showSaveDialog(this);
            if (returnVal == JFileChooser.APPROVE_OPTION) {
                File file = chooser.getSelectedFile();
                try {
                    FileWriter fw = new FileWriter(file);
                    fw.write(txtScript.getText().toString());
                    fw.close();
                } catch (IOException e1) {
                    e1.printStackTrace();
                }
            }
        }
        if (btnInputContent.equals(arg0.getSource())) {

            String content = txtInputContent.getText();
            if (!content.isEmpty() && content != "") {
                try {
                    String str = txtScript.getText();
                    txtScript.setText(str.substring(0, str.lastIndexOf("</loop>")));
                    txtScript.append("<input value=\"");
                    txtScript.append(content);
                    txtScript.append("\"/>\n");
                    txtScript.append("</loop>");
                } catch (Exception e) {
                    txtDelay.setText("延时时间设置错误!");
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
