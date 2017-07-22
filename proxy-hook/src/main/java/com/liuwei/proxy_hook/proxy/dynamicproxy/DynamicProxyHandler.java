package com.liuwei.proxy_hook.proxy.dynamicproxy;

import com.liuwei.proxy_hook.log.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * 动态代理
 * Created by liuwei on 17/3/1.
 * 注:动态代理的步骤:
 *  1、写一个InvocationHandler的实现类,并实现invoke方法,return method.invoke(...);
 *  2、使用Proxy类的newProxyInstance方法生成一个代理对象。例如:生成Subject1的代理对象,注意第三个参数中要将一个实体对象传入
 *          Proxy.newProxyInstance(
                         Subject1.class.getClassLoader(),
                         new Class[] {Subject1.class},
                         new DynamicProxyHandler(new RealSubject1()));

 */
public class DynamicProxyHandler implements InvocationHandler {
    private Object object;

    public DynamicProxyHandler(Object object) {
        this.object = object;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        Logger.i(DynamicProxyHandler.class, "我正在动态代理[" + object.getClass().getSimpleName() + "]的[" + method.getName() + "]方法");
        return method.invoke(object, args);
    }

    /**
     * 调用Proxy.newProxyInstance即可生成一个代理对象
     * @param object
     * @return
     */
    public static Object newProxyInstance(Object object) {
        // 传入被代理对象的classloader,实现的接口,还有DynamicProxyHandler的对象即可。
        return Proxy.newProxyInstance(object.getClass().getClassLoader(),
                object.getClass().getInterfaces(),
                new DynamicProxyHandler(object));
    }
}
