package com.liuwei.classloader_loadapk.hook;
import android.os.Handler;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * Created by liuwei on 17/3/2.
 */
public class HookHelper {

    /**
     * 通过源码可以可以发现,IActivityManager的实现类是一个单例,所以是一个非常好的hook点
     *
     * 源码路径:/frameworks/base/core/java/android/app/ActivityManagerNative.java
     * private static final Singleton<IActivityManager> gDefault = new Singleton<IActivityManager>() {
     2377        protected IActivityManager More ...create() {
     2378            IBinder b = ServiceManager.getService("activity");
     2379            if (false) {
     2380                Log.v("ActivityManager", "default service binder = " + b);
     2381            }
     2382            IActivityManager am = asInterface(b);
     2383            if (false) {
     2384                Log.v("ActivityManager", "default service = " + am);
     2385            }
     2386            return am;
     2387        }
     2388    };
     */
    public static void doIActivityManagerHook(){
        try {
            Class<?> activityManagerNativeClass = Class.forName("android.app.ActivityManagerNative");

            Method getDefaultMethod = activityManagerNativeClass.getDeclaredMethod("getDefault");
            getDefaultMethod.setAccessible(true);
            // 获取IActivityManager的对象
            Object iActivityManager = getDefaultMethod.invoke(null);
            Class<?> iActivityManagerInterface = Class.forName("android.app.IActivityManager");

            // 构建iActivityManager的动态代理
            Object activityManagerHookHandler = Proxy.newProxyInstance(iActivityManager.getClass().getClassLoader(), new Class[]{iActivityManagerInterface},
                    new ActivityManagerHookHandler(iActivityManager));

            // 获取ActivityManagerNative.gDefault,目的是把原来的iActivityManager替换成我们自己构建的activityManagerHookHandler
            Field gDefaultField = activityManagerNativeClass.getDeclaredField("gDefault");
            gDefaultField.setAccessible(true);
            Object gDefault = gDefaultField.get(null);

            Class<?> singletonClass = Class.forName("android.util.Singleton");
            Field mInstanceField = singletonClass.getDeclaredField("mInstance");
            mInstanceField.setAccessible(true);
            // 替换
            mInstanceField.set(gDefault, activityManagerHookHandler);
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
    }


    /**
     * 将替换的activity在此时还原回来
     */
    public static void doHandlerHook(){
        try {
            Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThread = activityThreadClass.getDeclaredMethod("currentActivityThread");
            Object activityThread = currentActivityThread.invoke(null);

            Field mHField = activityThreadClass.getDeclaredField("mH");
            mHField.setAccessible(true);
            Handler mH = (Handler)mHField.get(activityThread);

            // 1、ActivityThreadHandler是Handler的子类,在这替换时我们发现会抛出异常:
            // java.lang.IllegalArgumentException: field android.app.ActivityThread.mH has type android.app.ActivityThread$H,
            // got com.liuwei.activity_replace.hook.ActivityThreadHandler
            // 这显然是不行的,因为ActvitiyTreadHandler是不能转换成ActivityThread$H类型的。
//            mHField.set(activityThread, new ActivityThreadHandler(mH));// 这条路行不通,注释掉

            Field mCallbackField = Handler.class.getDeclaredField("mCallback");
            mCallbackField.setAccessible(true);
            mCallbackField.set(mH, new ActivityThreadHandlerCallback(mH));

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
