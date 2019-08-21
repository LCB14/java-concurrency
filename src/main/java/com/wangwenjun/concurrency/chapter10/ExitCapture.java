package com.wangwenjun.concurrency.chapter10;

/*
*  当线程在运行的过程中如果在run方法中抛出了异常或者人为杀死（如果是在Linux上运行的话，指的是kill PID，而不是kill -9 PID！强制杀死线程的话，addShutdownHook方法中的代码是不会执行的）该线程的话，
*  在线程退出之前会执行addShutdownHook方法中的代码（在还没有执行完addShutdownHook方法中的代码之前，线程此还是可以执行的）。因此当例如人为kill掉这个线程的话，此时线程的输出很可能为下面的输出：

I am working...
I am working...
The application will be exit
notify to the admin
I am working... // 此时线程还在正常的输出
will release resource(socket, file, connection)
I am working... // 此时线程还在正常的输出
*/
public class ExitCapture {

    public static void main(String[] args) {

        // 当程序问题时，会调用这个hook方法
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("The application will be exit");
            notifyAndRelease();
        }));

        int i = 0;
        while(true) {
            try {
                Thread.sleep(1_000);
                System.out.println("I am working...");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            i++;
            // 模拟程序在运行过程中出现问题
            if (i > 20) {
                throw new RuntimeException("error");
            }
        }
    }

    private static void notifyAndRelease() {
        System.out.println("notify to the admin");

        // 模拟释放
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
        }

        System.out.println("will release resource(socket, file, connection)");

        // 模拟释放
        try {
            Thread.sleep(1_000);
        } catch (InterruptedException e) {
        }
    }
}
