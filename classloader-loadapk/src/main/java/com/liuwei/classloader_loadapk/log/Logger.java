package com.liuwei.classloader_loadapk.log;

/**
 * Created by liuwei on 17/2/8.
 */
public class Logger {
    public static void i(String TAG, String info) {
        System.out.println("[" + TAG + "] : " + info);
    }

    public static void i(String TAG, Object obj){
        System.out.println("[" + TAG + "] : " + obj);
    }

    public static void i(Class className, String info) {
        System.out.println("[" + className.getSimpleName() + "] : " + info);
    }
}
