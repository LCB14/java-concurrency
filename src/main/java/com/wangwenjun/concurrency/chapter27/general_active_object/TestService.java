package com.wangwenjun.concurrency.chapter27.general_active_object;


import com.wangwenjun.concurrency.chapter19.Future;

public interface TestService {

    @ActiveMethod
    Future<String> testReturn(long orderId);

    //提交订单，但是没有返回值
    @ActiveMethod
    void test(String account, long orderId);

}
