package com.wangwenjun.classLoader.chapter2;

/***************************************
 * @author:Alex Wang
 * @Date:2017/4/2 QQ:532500648
 * QQ交流群:286081824
 ***************************************/
public class BootClassLoader {

    public static void main(String[] args) throws ClassNotFoundException {
        // 根类加载器从系统属性"sun.boot.class.path"所指定的目录中加载类库
        System.out.println("根类加载器：" + System.getProperty("sun.boot.class.path"));

        // 拓展类加载器从系统属性"java.ext.dirs"所指定的目录中加载类库
        System.out.println("拓展类加载器：" + System.getProperty("java.ext.dirs"));

        // 应用类加载器从环境变量classpath或系统属性"java.class.path"所指定的目录中加载类
        System.out.println("应用程序类加载器：" + System.getProperty("java.class.path"));

        Class<?> klass = Class.forName("com.wangwenjun.classLoader.chapter2.SimpleObject");
        System.out.println(klass.getClassLoader());
        System.out.println(klass.getClassLoader().getParent());
        System.out.println(klass.getClassLoader().getParent().getParent());


        Class<?> clazz = Class.forName("java.lang.String");
        System.out.println(clazz);
        System.out.println(clazz.getClassLoader());


    }
}
