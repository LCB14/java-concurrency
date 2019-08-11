package com.wangwenjun.concurrency.chapter9;

import java.util.stream.Stream;

/***************************************
 * 下面程序单个生产者，单个消费者的情况下是正常的
 * 若是
 * 1. 多个生产者
 * 2. 多个消费者
 * 3. 多个生产者多个消费者
 * 都会出现问题。造成问题的根本原因是当消费线程（生产线程）notify时，notify的不一定是生产线程（消费线程），从而导致程序一直处于假死的状态（这种状态还是死锁，而是所有的线程都wait了）
 ***************************************/
public class ProduceConsumerVersion21 {

    private int i = 0;

    final private Object LOCK = new Object();

    private volatile boolean isProduced = false;

    public void produce() {
        synchronized (LOCK) {
            if (isProduced) {
                try {
                    //多个生成的情况下，多个生产者会在此进行wait
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            } else {
                i++;
                System.out.println("P->" + i);
                LOCK.notify();
                isProduced = true;
            }
        }
    }

    public void consume() {
        synchronized (LOCK) {
            if (isProduced) {
                System.out.println("C->" + i);
                LOCK.notify();
                isProduced = false;
            } else {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void main(String[] args) {
        ProduceConsumerVersion21 pc = new ProduceConsumerVersion21();
        new Thread("t1"){
            @Override
            public void run() {
                while (true) {
                    pc.produce();
                }
            }
        }.start();

        new Thread("t2"){
            @Override
            public void run() {
                while (true) {
                    pc.consume();
                }
            }
        }.start();
    }
}