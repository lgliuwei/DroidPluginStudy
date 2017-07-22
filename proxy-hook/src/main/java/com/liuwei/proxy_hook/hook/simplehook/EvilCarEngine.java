package com.liuwei.proxy_hook.hook.simplehook;


/**
 * Created by liuwei on 17/3/1.
 */
public class EvilCarEngine extends CarEngine {
    private CarEngineInterface base;
    public EvilCarEngine(CarEngineInterface base) {
        this.base = base;
    }
    public int maxSpeed() {
        return 3 * base.maxSpeed();
    }
}
