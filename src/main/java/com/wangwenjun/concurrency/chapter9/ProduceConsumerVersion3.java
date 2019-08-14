package com.wangwenjun.concurrency.chapter9;

import java.util.stream.Stream;

/***************************************
 * 使用notifyAll与while循环避免假死的问题发生
 ***************************************/
public class ProduceConsumerVersion3 {

    private int i = 0;

    final private Object LOCK = new Object();

    private volatile boolean isProduced = false;

    public void produce() {
        synchronized (LOCK) {
            // 如果为if的话，则有可能产生数据丢失生（上一个生产者生产的别下一个生产者生产的给覆盖了）
            // t1时刻，p1进行了生产i1，正常结束
            // t2时刻，p2和p3判断if条件成立，进而进行wait
            // t3时刻，c1对i1进行消费，并调用了notifyAll方法，p2和p3同时被唤醒，p2进行生产i2，紧接着p3进行生产i3，从而将i2的值进行覆盖
            while (isProduced) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            i++;
            System.out.println("P->" + i);
            LOCK.notifyAll();
            isProduced = true;

        }
    }

    public void consume() {
        synchronized (LOCK) {
            while (!isProduced) {
                try {
                    LOCK.wait();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("C->" + i);
            LOCK.notifyAll();
            isProduced = false;
        }
    }

    public static void main(String[] args) {
        ProduceConsumerVersion3 pc = new ProduceConsumerVersion3();
        Stream.of("P1", "P2", "P3").forEach(n ->
                new Thread(n) {
                    @Override
                    public void run() {
                        while (true) {
                            pc.produce();
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start()
        );
        Stream.of("C1", "C2", "C3", "C4").forEach(n ->
                new Thread(n) {
                    @Override
                    public void run() {
                        while (true) {
                            pc.consume();
                            try {
                                Thread.sleep(10);
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            }
                        }
                    }
                }.start()
        );
    }
}