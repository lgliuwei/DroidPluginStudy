package com.liuwei.proxy_hook.proxy.staticproxy;

import com.liuwei.proxy_hook.log.Logger;

/**
 * 静态代理类
 * Created by liuwei on 17/3/1.
 */
public class ProxySubject1 implements Subject1 {
    private Subject1 subject1;
    public ProxySubject1(Subject1 subject1) {
        this.subject1 = subject1;
    }
    @Override
    public void method1() {
        Logger.i(ProxySubject1.class, "我是代理,我会在执行实体方法1之前先做一些预处理的工作");
        subject1.method1();
    }
    @Override
    public void method2() {
        Logger.i(ProxySubject1.class, "我是代理,我会在执行实体方法2之前先做一些预处理的工作");
        subject1.method2();
    }
}
