package com.liuwei.proxy_hook.hook.simplehook;

import com.liuwei.proxy_hook.log.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * Created by liuwei on 17/3/1.
 */
public class CarEngineProxyHandler implements InvocationHandler {
    private Object object;
    public CarEngineProxyHandler(Object object) {
        this.object = object;
    }
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if ("maxSpeed".equals(method.getName())) {
            Logger.i(CarEngineProxyHandler.class, "我是动态代理,我已拦截到 maxSpeed 方法,并偷偷返回了另一个值!");
            return 180;
        }
        return method.invoke(object, args);
    }
}
