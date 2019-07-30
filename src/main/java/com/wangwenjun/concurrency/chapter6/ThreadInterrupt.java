package com.wangwenjun.concurrency.chapter6;

/***************************************
 * @author:Alex Wang
 * @Date:2017/2/19 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class ThreadInterrupt {

    private static final Object MONITOR = new Object();

    public static void main(String[] args) throws InterruptedException {

        //线程正常运行情况被打断
        /*Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    //这里的输出也是true
                    System.out.println("1: " + this.isInterrupted());
                }
            }
        };
        t.start();
        Thread.sleep(100);
        System.out.println(t.isInterrupted());
        t.interrupt();
        *//*
           这里的输出为true。若线程是在正常的情况下运行的话
          （即，没有调用“ wait(), wait(long), or wait(long, int) methods of the Object class,
           or of the join(), join(long), join(long, int), sleep(long), or sleep(long, int), methods of this class”）
            若此时打断了线程（即，调用了线程的interrupt方法），此时线程的interrupt status就会被赋值（即，此时线程的isInterrupted方法的返回值为true）
         //*
        System.out.println("3:" + t.isInterrupted());*/

        //线程阻塞情况下调用其interrupt方法
        /*Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                        System.out.println("1: " + this.isInterrupted());
                    } catch (InterruptedException e) {
                        //由于线程是通过调用“ wait(), wait(long), or wait(long, int) methods of the Object class,
                        // *   or of the join(), join(long), join(long, int), sleep(long), or sleep(long, int), methods of this class”这些方法来阻塞的，
                        // 因此当调用线程的interrupt方法时，根据java doc中对此种情况的描述，此时线程的interrupt status会被清除，因此下面的输出为false
                        System.out.println("2: " + this.isInterrupted());
                        e.printStackTrace();
                    }

                }
            }
        };

        t.start();
        Thread.sleep(100);
        System.out.println(t.isInterrupted());
        t.interrupt();
        //下面的输出看似是true，其实为false！因为此时线程是通过调用“ wait(), wait(long), or wait(long, int) methods of the Object class,
        //         *   or of the join(), join(long), join(long, int), sleep(long), or sleep(long, int), methods of this class”这些方法进行阻塞的，
        // 根据Thread类的interrupt方法的文档，若线程在执行了这些方法的情况被interrupt的话，线程的interrupt status就会被清除！当执行了t.interrupt方法后，其实线程的
        // interrupt status已经被清除！但是由于延迟的问题（这个自己现在也说不清除，但是通过下一个例子的演示情况确实存在这种情况），下面的这个输出有可能为true
        System.out.println("3: " + t.isInterrupted());*/

        /*Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    try {
                        Thread.sleep(10);
                        System.out.println("1: " + this.isInterrupted());
                    } catch (InterruptedException e) {
                        System.out.println("2: " + this.isInterrupted());
                        e.printStackTrace();
                    }

                }
            }
        };

        t.start();
        Thread.sleep(100);
        System.out.println(t.isInterrupted());
        t.interrupt();
        //当这里休眠了一会儿后，下面的输出肯定为false
        Thread.sleep(100);
        System.out.println("3:" + t.isInterrupted());*/

        /*Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {
                    synchronized (MONITOR) {
                        try {
                            MONITOR.wait(10);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                            System.out.println(isInterrupted());
                        }
                    }
                }
            }
        };

        t.start();
        Thread.sleep(100);
        System.out.println(t.isInterrupted());
        t.interrupt();
        System.out.println(t.isInterrupted());

        t.stop();*/

      /*  Thread t = new Thread(() -> {
            while (true) {
                synchronized (MONITOR) {
                    try {
                        MONITOR.wait(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        System.out.println(Thread.interrupted());
                    }
                }
            }
        });*/

        Thread t = new Thread() {
            @Override
            public void run() {
                while (true) {

                }
            }
        };

        t.start();
        Thread main = Thread.currentThread();
        Thread t2 = new Thread() {
            @Override
            public void run() {
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }

                //执行t的interrupt方法，并不会打断t！因为t.join方法是main线程的join，此时想要实现下面捕获异常的效果的话，必须执行main线程的interrupt方法来打断main线程！
                // 这里容易混淆，要十分注意！
                //t.interrupt();
                main.interrupt();
                System.out.println("interrupt");
            }
        };

        t2.start();
        try {
            t.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //---------------------------


    }
}
