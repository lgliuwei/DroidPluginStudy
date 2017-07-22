package com.liuwei.proxy_hook.reflect;

import com.liuwei.proxy_hook.log.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * 反射调用示例
 * Created by liuwei on 17/3/1.
 */
public class ReflectTest {
    public static void main(String[] args){
        try {
            // 1、通过反射获取BeReflected所属的类
            Class<?> beReflectedClass = Class.forName("com.liuwei.proxy_hook.reflect.BeReflected");
            Logger.i(ReflectTest.class, beReflectedClass);

            // 2、通过反射创建实例化一个类
            Object beReflected = beReflectedClass.newInstance();
            Logger.i(ReflectTest.class, beReflected);

            // 3、通过反射调用一个私有方法和成员变量
            Method method = beReflectedClass.getDeclaredMethod("method1");
            method.setAccessible(true);// 将此值设为true即可访问私有的方法和成员变量
            method.invoke(beReflected);// 访问普通成员变量和方法是需要在调用invoke方法是传入该类的对象

            Field field1 = beReflectedClass.getDeclaredField("field1");
            field1.setAccessible(true);
            Logger.i(ReflectTest.class, "field 改变前的值:" + field1.get(beReflected));
            field1.set(beReflected, "我是 field1 被改变后的值");
            Logger.i(ReflectTest.class, "field 改变后的值:" + field1.get(beReflected));

            // 4、通过反射调用一个静态的方法和变量
            Method staticMethod = beReflectedClass.getDeclaredMethod("staticMethod");
            staticMethod.invoke(null);

            Field staticField = beReflectedClass.getDeclaredField("staticField");
            staticField.setAccessible(true);
            Logger.i(ReflectTest.class, staticField.get(null));

            // 5、通过反射访问一个带参数的方法
            Method method1 = beReflectedClass.getDeclaredMethod("method1", String.class);
            method1.setAccessible(true);
            method1.invoke(beReflected, "我是被传入的参数");

            // 6、遍历类中所有的方法和成员变量
            for (Method tempMethod : beReflectedClass.getDeclaredMethods()) {
                Logger.i(ReflectTest.class, tempMethod.getName());
            }
            for (Field tempField : beReflectedClass.getDeclaredFields()) {
                Logger.i(ReflectTest.class, tempField.getName());
            }

        } catch (Exception e) {
            e.printStackTrace();
        }


    }
}
