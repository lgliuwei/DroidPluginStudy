package com.liuwei.proxy_hook.hook.simplehook;

import com.liuwei.proxy_hook.log.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Proxy;

/**
 * Hook的简单示例
 * Created by liuwei on 17/3/1.
 */
public class Test {
    public static void main(String[] args) {
        Car car = new Car();
        Logger.i(Test.class, "------------------替换前----------------");
        car.showMaxSpeed();
        // 怎样在不手动修改CarEngine类和Car类的情况下将大速度提高?
        try {
            Field carEngineField = Car.class.getDeclaredField("carEngine");
            carEngineField.setAccessible(true);
            CarEngine carEngine = (CarEngine)carEngineField.get(car);
            // 方法1
//            carEngineField.set(car, new EvilCarEngine(carEngine));
            // 方法2
            CarEngineInterface carEngineProxy = (CarEngineInterface) Proxy.newProxyInstance(
                    CarEngine.class.getClassLoader(),
                    new Class[]{CarEngineInterface.class},
                    new CarEngineProxyHandler(carEngine));
            carEngineField.set(car, carEngineProxy);

        } catch (Exception e) {
            e.printStackTrace();
        }

        Logger.i(Test.class, "------------------替换后----------------");
        car.showMaxSpeed();
    }
}
