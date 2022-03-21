package com.wangwenjun.concurrency.chapter1;

/***************************************
 * @author:Alex Wang
 * @Date:2017/2/14 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class TemplateMethod {
    protected void wrapPrint(String message) {
    }

    public static void main(String[] args) {
        TemplateMethod t1 = new TemplateMethod() {
            @Override
            protected void wrapPrint(String message) {
                System.out.println("*" + message + "*");
            }
        };
        t1.print("Hello Thread");

        TemplateMethod t2 = new TemplateMethod() {
            @Override
            protected void wrapPrint(String message) {
                System.out.println("+" + message + "+");
            }
        };
        t2.print("Hello Thread");

    }

    /**
     * print 方法中的wrapPrint支持自定义，但print不行
     * <p>
     * 模拟 Thread 的 run 方法
     */
    public final void print(String message) {
        System.out.println("################");
        wrapPrint(message);
        System.out.println("################");
    }
}
