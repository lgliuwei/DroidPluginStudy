package com.liuwei.proxy_hook.hook.activityhook;

import android.app.Activity;
import android.app.Instrumentation;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Created by liuwei on 17/3/1.
 */
public class ActivityThreadHookHelper {

    /**
     * 这里hook了以Context.startActivity方式启动的方法
     */
    public static void doContextStartHook(){
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");

            /**
             * 查看源码ActivityThread的源码可以发现,此类是一个单例,并却由currentActivityThread()获得对象
             * public static ActivityThread currentActivityThread() {
                     return sCurrentActivityThread;
                }
             */
            Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object activityThread = currentActivityThreadMethod.invoke(null);

            Field mInstrumentationField = activityThreadClass.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation originalInstrumentation = (Instrumentation)mInstrumentationField.get(activityThread);
            mInstrumentationField.set(activityThread, new EvilInstrumentation(originalInstrumentation));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 这里hook了以Activity.StartActivity方式启动的方法
     * @param activity
     */
    public static void doActivityStartHook(Activity activity){
        try {
            Field mInstrumentationField = Activity.class.getDeclaredField("mInstrumentation");
            mInstrumentationField.setAccessible(true);
            Instrumentation originalInstrumentation = (Instrumentation)mInstrumentationField.get(activity);
            mInstrumentationField.set(activity, new EvilInstrumentation(originalInstrumentation));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
