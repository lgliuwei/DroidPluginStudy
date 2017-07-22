package com.liuwei.proxy_hook.hook.activityhook;

import android.app.Application;
import android.content.Context;

/**
 * Created by liuwei on 17/4/16.
 */
public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
    }
    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        ActivityThreadHookHelper.doContextStartHook();
    }
}
