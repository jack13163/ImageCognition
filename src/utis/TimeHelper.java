package utis;

public class TimeHelper {
    public interface Job{
        void run();
    }

    /**
     * 启动任务，并开始计时
     * @param job
     */
    public static void startWatch(Job job){

        // 计时开始
        long start = System.currentTimeMillis();
        // 运行费时操作
        job.run();
        //表示定义当前系统时间，单位：毫秒
        long end = System.currentTimeMillis();
        // 计时结束
        long useTime = end - start;
        System.out.println("use time: " + useTime + "ms.");
    }
}
