package autokey;//package autokey;

import java.awt.Dimension;
import java.awt.Toolkit;

import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;

@SuppressWarnings("serial")
public class kettest extends JFrame{
	public static Dimension tk = Toolkit.getDefaultToolkit().getScreenSize();
	kettest() {
		super("AutoKey");
		JPanel jp = new GamePanel();
        add(jp);

		this.setBounds((tk.width - 400) / 2, (tk.height - 400) / 2, 400, 400);
		this.setVisible(true);
        setAlwaysOnTop(true);
	}
	/**
	 * @param args
	 */
	public static void main(String[] args){
        SwingUtilities.invokeLater(new Runnable() {
            public void run()
            {
             new kettest().setDefaultCloseOperation(EXIT_ON_CLOSE);
            }

        });
		
	}

}
