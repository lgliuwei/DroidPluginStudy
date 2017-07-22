package com.liuwei.proxy_hook.proxy;

import com.liuwei.proxy_hook.log.Logger;
import com.liuwei.proxy_hook.proxy.dynamicproxy.DynamicProxyHandler;
import com.liuwei.proxy_hook.proxy.staticproxy.ProxySubject1;
import com.liuwei.proxy_hook.proxy.staticproxy.ProxySubject2;
import com.liuwei.proxy_hook.proxy.staticproxy.RealSubject1;
import com.liuwei.proxy_hook.proxy.staticproxy.RealSubject2;
import com.liuwei.proxy_hook.proxy.staticproxy.Subject1;
import com.liuwei.proxy_hook.proxy.staticproxy.Subject2;

import java.lang.reflect.Proxy;

/**
 * Created by liuwei on 17/3/1.
 */
public class ProxyTest {
    public static void main(String[] args){
        // static proxy
        ProxySubject1 proxySubject1 = new ProxySubject1(new RealSubject1());
        proxySubject1.method1();
        proxySubject1.method2();

        // 如果想对RealSubject2代理显然不得不重新再写一个代理类。
        ProxySubject2 proxySubject2 = new ProxySubject2(new RealSubject2());
        proxySubject2.method1();
        proxySubject2.method2();

        Logger.i(ProxyTest.class, "----------分割线----------\n");

        // 如果写一个代理类就能对上面两个都能代理就好了,动态代理就解决了这个问题
        Subject1 dynamicProxyHandler1 = (Subject1) DynamicProxyHandler.newProxyInstance(new RealSubject1());
        dynamicProxyHandler1.method1();
        dynamicProxyHandler1.method2();

        Subject2 dynamicProxyHandler2 = (Subject2)DynamicProxyHandler.newProxyInstance(new RealSubject2());
        dynamicProxyHandler2.method1();
        dynamicProxyHandler2.method2();
    }
}
