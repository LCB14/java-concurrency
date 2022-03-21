package com.wangwenjun.concurrency.chapter2;

/***************************************
 * @author:Alex Wang
 * @Date:2017/2/15 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class TicketWindow extends Thread {

    private final String name;

    /**
     * 因为该属性被 static 修饰，所以该属性会被该类的所有实例所共享
     */
    private static final int MAX = 5000;
    private static int index = 1;

    public TicketWindow(String name) {
        this.name = name;
    }

    @Override
    public void run() {
        while (index <= MAX) {
            //静态变量不存在并发的问题？？存在！往后的视频中作者也说了
            System.out.println("柜台：" + name + "当前的号码是:" + (index++));
        }
    }
}
