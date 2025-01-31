package com.wangwenjun.concurrency.chapter27.general_active_object;


import com.wangwenjun.concurrency.chapter19.Future;
import com.wangwenjun.concurrency.chapter19.FutureService;
import com.wangwenjun.concurrency.chapter19.Task;

import java.util.concurrent.TimeUnit;


public class TestServiceImpl implements TestService {

    @Override
    public Future<String> testReturn(long orderId) {
        try {
            TimeUnit.SECONDS.sleep(3);
            System.out.println(orderId);

        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        return FutureService.<Long, String>newService().submit(new Task<Long, String>() {
            @Override
            public String get(Long input) {
                return String.valueOf(input + 1);
            }
        }, orderId, null);
    }

    @Override
    public void test(String account, long orderId) {
        try {
            TimeUnit.SECONDS.sleep(3);
            System.out.println(account + " " + orderId);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
