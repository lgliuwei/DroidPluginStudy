package com.liuwei.proxy_hook.hook.simplehook;

/**
 * 车引擎
 * Created by liuwei on 17/3/1.
 */
public class CarEngine implements CarEngineInterface {
    @Override
    public int maxSpeed() {
        return 60;
    }
}
