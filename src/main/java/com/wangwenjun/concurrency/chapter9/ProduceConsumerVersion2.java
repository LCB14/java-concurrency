package com.wangwenjun.concurrency.chapter9;

import java.util.stream.Stream;

/***************************************
 * 程序会一直处于假死的状态（但不是死锁）的情形举例如下：
 * 在t1时刻，p1进行了正常的生产，完成其第一次循环
 * 在t2时刻，p2由于仓库不为空，p2进行wait
 * 在t3时刻，p1进行第二次循环，由于仓库不为空，p1进行wait
 * 在t4时刻，c1进行消费，消费完了后，通知p1进行生产（p1进行生产，p1生成完了后，p1进行wait），c1进行wait
 * 在t5时刻，c2进行消费，仓库不为空，c2进行消费，完了notify了c1（这是出错的点，notify不能指定notify的对象），然后c2wait，虽然notify了c1，但是c1一看仓库没有东西，自己把自己wait掉
 * 在t6时刻，p1，p2，c1，c2均处于wait的状态，程序停住不执行了
 ***************************************/
public class ProduceConsumerVersion2 {

    private int i = 0;

    final private Object LOCK = new Object();

    private volatile boolean isProduced = false;

    public void produce() {
        synchronized (LOCK) {
            if (isProduced) {
                try {
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
        ProduceConsumerVersion2 pc = new ProduceConsumerVersion2();
        Stream.of("P1", "P2").forEach(n ->
                new Thread(n) {
                    @Override
                    public void run() {
                        while (true)
                            pc.produce();
                    }
                }.start()
        );
        Stream.of("C1", "C2").forEach(n ->
                new Thread(n) {
                    @Override
                    public void run() {
                        while (true)
                            pc.consume();
                    }
                }.start()
        );
    }
}