package com.wangwenjun.concurrency.chapter26;

import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.IntStream;

import static java.util.concurrent.ThreadLocalRandom.current;

public class Test {

    public static void main(String[] args) {
        //流水线上准备5个工人
        ProductionChannel channel = new ProductionChannel(5);
        AtomicInteger productionNo = new AtomicInteger();
        //流水线上游有8个工人不断得往传送带上放置待加工的产品
        IntStream.range(1, 8).forEach(i ->
                new Thread(() -> {
                    while (true) {
                        channel.offerProduction(new Production(productionNo.getAndIncrement()));
                        try {
                            TimeUnit.SECONDS.sleep(current().nextInt(10));
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                }).start());
    }

}
