package com.wangwenjun.concurrency.chapter4;

/***************************************
 * @author:Alex Wang
 * @Date:2017/2/17 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class DaemonThread {

    public static void main(String[] args) throws InterruptedException {

        Thread t = new Thread() {
            @Override
            public void run() {
                try {
                    System.out.println(Thread.currentThread().getName() + " running");
                    Thread.sleep(100000);
                    System.out.println(Thread.currentThread().getName() + " done.");
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };
        // 必须在 start  方法之前进行设置
        t.setDaemon(true);
        t.start();
        //runnable  ->running| ->dead| ->blocked

        // JDK1.7
        Thread.sleep(5_000);
        System.out.println(Thread.currentThread().getName());
    }
}

/**
 * A<---------------------------------->B
 * ->daemonThread(health check)
 */