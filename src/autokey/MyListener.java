package autokey;//package autokey;

import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class MyListener extends KeyAdapter {

	public void keyPressed(KeyEvent arg0) {
		if(arg0.getKeyCode()==KeyEvent.VK_ESCAPE){
			// 结束定位
			GamePanel.xystate=true;

			// 结束运行
			ScriptRunner.state=true;
			GamePanel.btnStart.setEnabled(true);
			GamePanel.btnStop.setEnabled(false);
		}
	}
}