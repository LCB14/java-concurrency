package com.wangwenjun.concurrency.chapter11;

/***************************************
 * @author:Alex Wang
 * @Date:2017/2/24 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class ThreadException {
    private final static int A = 10;
    private final static int B = 0;


    public static void main(String[] args) {

        //new Test1().test();

        Thread t = new Thread(() -> {
            try {
                Thread.sleep(5_000L);
                int result = A / B;
                System.out.println(result);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
        t.start();

        // 在通常的情况例如上面的线程当抛出异常的时候，JVM只能将异常输出到控制台或者日志文件中，而如果我们没有看着这些异常信息的话，
        // 其实我们此时还不知道其实线程已经发生异常了。
        // 使用setUncaughtExceptionHandler可以捕获到当线程出现问题时的异常信息，捕获到异常信息后就可以采取一些措施来通知相关的人员了，例如
        // 发送短息等手段。下面为了演示，还是照样输出了
        t.setUncaughtExceptionHandler((thread, e) -> {
            System.out.println(e);
            System.out.println(thread);
        });
        t.start();
    }
}