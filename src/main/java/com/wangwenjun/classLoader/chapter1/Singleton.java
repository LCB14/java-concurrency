package com.wangwenjun.classLoader.chapter1;

/***************************************
 * @author:Alex Wang
 * @Date:2017/4/1 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class Singleton {
    // 0 1
//    private static Singleton instance = new Singleton();

    public static int x = 0;

    public static int y;

    // 1 1
    private static Singleton instance = new Singleton();

    /**
     * <p>
     *  类 -- 链接阶段
     *    int x = 0;
     *    int y = 0;
     *    instance = null;
     * <p/>
     * <p>
     *     getInstance 导致 Singleton 进入初始化阶段
     *     x = 0;
     *     y = 0;
     *     instance = new Singleton();
     *     x = 1;
     *     y = 1;
     * <p/>
     */

    /**
     * <p>
     *      类 -- 链接阶段
     *      1.instance = null;
     *      2.x = 0;
     *      3.y = 0;
     * <p>
     * <p>
     *     类 -- 初始化阶段
     *     instance = new Singleton();
     *     x++=>x=1
     *     y++=>y=1
     *
     *     x = 0;
     *     y = 1;
     * </p>
     */

    private Singleton() {
        x++;
        y++;
    }

    public static Singleton getInstance() {
        return instance;
    }

    public static void main(String[] args) {
        Singleton singleton = getInstance();
        System.out.println(singleton.x);
        System.out.println(singleton.y);
    }
}
