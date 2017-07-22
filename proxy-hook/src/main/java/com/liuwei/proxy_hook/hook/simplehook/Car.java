package com.liuwei.proxy_hook.hook.simplehook;

import com.liuwei.proxy_hook.log.Logger;

/**
 * Created by liuwei on 17/3/1.
 */
public class Car {
    private CarEngineInterface carEngine;
    public Car() {
        this.carEngine = new CarEngine();
    }
    public void showMaxSpeed(){
        Logger.i(Car.class, "我卯足劲,玩命跑的最大速度可以达到:" + carEngine.maxSpeed());
    }
}
