package com.liuwei.proxy_hook.proxy.staticproxy;

import com.liuwei.proxy_hook.log.Logger;

/**
 * 实体类
 * Created by liuwei on 17/3/1.
 */
public class RealSubject2 implements Subject2 {
    @Override
    public void method1() {
        Logger.i(RealSubject2.class, "我是RealSubject2的方法1");
    }

    @Override
    public void method2() {
        Logger.i(RealSubject2.class, "我是RealSubject2的方法2");
    }
}
