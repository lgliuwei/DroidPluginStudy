package com.liuwei.proxy_hook.proxy.staticproxy;

import com.liuwei.proxy_hook.log.Logger;

/**
 * 静态代理类
 * Created by liuwei on 17/3/1.
 * 注:为了保持行为的一致性，代理类和委托类通常会实现相同的接口
 */
public class ProxySubject2 implements Subject2 {
    private Subject2 subject2;

    public ProxySubject2(Subject2 subject2) {
        this.subject2 = subject2;
    }

    @Override
    public void method1() {
        Logger.i(ProxySubject2.class, "我是代理,我会在执行实体方法1之前先做一些预处理的工作");
        subject2.method1();
    }

    @Override
    public void method2() {
        Logger.i(ProxySubject2.class, "我是代理,我会在执行实体方法2之前先做一些预处理的工作");
        subject2.method2();
    }
}
