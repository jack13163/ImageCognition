package autokey;

import java.awt.*;

public class CycleThread extends Thread {

    public interface UIUpdate{
        boolean updateUI();
    }

    UIUpdate uiUpdate;

    public CycleThread(UIUpdate uiUpdate) {
        this.uiUpdate = uiUpdate;
    }

    /**
     * 循环检测鼠标位置
     */
    public void run() {
        while (true) {
            // 返回true时，退出
            if(uiUpdate.updateUI()){
                break;
            }

            try {
                Thread.sleep(20);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
}
