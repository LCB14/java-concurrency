package com.wangwenjun.concurrency.chapter10;

/***************************************
 * interrupt一个处于block状态的线程时，这个线程不会继续执行未完成的任务！
 ***************************************/
public class SynchronizedProblem {

    public static void main(String[] args) throws InterruptedException {

        new Thread() {
            @Override
            public void run() {
                SynchronizedProblem.run();
            }
        }.start();

        Thread.sleep(1000);

        Thread t2 = new Thread() {
            @Override
            public void run() {
                System.out.println("11111");
                SynchronizedProblem.run();
                //虽然t2被打断了，但是22222是不会输出的！
                System.out.println("22222");
            }
        };
        t2.start();

        Thread.sleep(2000);

        t2.interrupt();
        System.out.println(t2.isInterrupted());
    }

    private synchronized static void run() {
        System.out.println(Thread.currentThread());
        while (true) {

        }
    }
}
