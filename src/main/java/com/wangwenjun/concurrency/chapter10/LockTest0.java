package com.wangwenjun.concurrency.chapter10;

import java.util.Optional;
import java.util.stream.Stream;

/***************************************
 * 自定义锁
 ***************************************/
public class LockTest0 {
    public static void main(String[] args) throws InterruptedException {

        final BooleanLock0 booleanLock = new BooleanLock0();
        Stream.of("T1", "T2", "T3", "T4")
                .forEach(name ->
                        new Thread(() -> {
                            try {
                                booleanLock.lock();
                                //booleanLock.lock(100L);
                                Optional.of(Thread.currentThread().getName() + " have the lock Monitor")
                                        .ifPresent(System.out::println);
                                work();
                            } catch (InterruptedException e) {
                                e.printStackTrace();
                            } catch (Exception e) {
                                Optional.of(Thread.currentThread().getName() + " time out")
                                        .ifPresent(System.out::println);
                            } finally {
                                booleanLock.unlock();
                            }
                        }, name).start()
                );
    }

    private static void work() throws InterruptedException {
        Optional.of(Thread.currentThread().getName() + " is Working...")
                .ifPresent(System.out::println);
        Thread.sleep(5_000);
    }
}
