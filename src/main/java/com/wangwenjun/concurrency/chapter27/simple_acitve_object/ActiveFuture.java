package com.wangwenjun.concurrency.chapter27.simple_acitve_object;


import com.wangwenjun.concurrency.chapter19.FutureTask;

public class ActiveFuture<T> extends FutureTask<T> {

    //重写 finish(),权限修饰为public，可以使得线程执行完成后传递最终结果
    @Override
    public void finish(T result) {
        super.finish(result);
    }
}
