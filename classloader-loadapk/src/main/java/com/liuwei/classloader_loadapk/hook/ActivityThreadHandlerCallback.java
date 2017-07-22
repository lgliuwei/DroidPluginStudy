package com.liuwei.classloader_loadapk.hook;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Handler;
import android.os.Message;


import com.liuwei.classloader_loadapk.StubActivity;
import com.liuwei.classloader_loadapk.log.Logger;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;


/**
 * Created by liuwei on 17/3/2.
 */
//public class ActivityThreadHandler extends Handler {
// 一开始我打算替换ActivityTread中的mH成员变量,所以在这直接继承了Handler,在替换时抛出了异常,发现是行不通的,
// 我们只能另寻出路,当然这些替换的方法在weishu.me的博客中都已经给我们写好了,不过我们要想知道为什么就得自己动手先把坑趟一边才能知其所以然。
public class ActivityThreadHandlerCallback implements Handler.Callback {

    private Handler mBaseHandler;

    public ActivityThreadHandlerCallback(Handler mBaseHandler) {
        this.mBaseHandler = mBaseHandler;
    }


    /**
     *
     * @param msg
     * 源码路径:/frameworks/base/core/java/android/app/ActivityThread.java
     * public void handleMessage(Message msg) {
    1295            if (DEBUG_MESSAGES) Slog.v(TAG, ">>> handling: " + codeToString(msg.what));
    1296            switch (msg.what) {
    1297                case LAUNCH_ACTIVITY: {
    1298                    Trace.traceBegin(Trace.TRACE_TAG_ACTIVITY_MANAGER, "activityStart");
    1299                    final ActivityClientRecord r = (ActivityClientRecord) msg.obj;
    1300
    1301                    r.packageInfo = getPackageInfoNoCheck(
    1302                            r.activityInfo.applicationInfo, r.compatInfo);
    1303                    handleLaunchActivity(r, null);
    1304                    Trace.traceEnd(Trace.TRACE_TAG_ACTIVITY_MANAGER);
    1305                } break;
                    ...
        }
     */
    @Override
    public boolean handleMessage(Message msg) {
        Logger.i(ActivityThreadHandlerCallback.class, "接受到消息了msg:" + msg);

        if (msg.what == 100) {
            try {
                Object obj = msg.obj;
                Field intentField = obj.getClass().getDeclaredField("intent");
                intentField.setAccessible(true);
                Intent intent = (Intent)intentField.get(obj);

                Intent targetIntent = intent.getParcelableExtra(StubActivity.TARGET_COMPONENT);
                intent.setComponent(targetIntent.getComponent());



                Field activityInfoField = obj.getClass().getDeclaredField("activityInfo");
                activityInfoField.setAccessible(true);
                // 根据 getPackageInfo 根据这个 包名获取 LoadedApk的信息; 因此这里我们需要手动填上, 从而能够命中缓存
                ActivityInfo activityInfo = (ActivityInfo) activityInfoField.get(obj);

                activityInfo.applicationInfo.packageName = targetIntent.getPackage() == null ?
                        targetIntent.getComponent().getPackageName() : targetIntent.getPackage();

                hookPackageManager();
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        mBaseHandler.handleMessage(msg);
        return true;
    }

    private static void hookPackageManager() throws Exception {

        // 这一步是因为 initializeJavaContextClassLoader 这个方法内部无意中检查了这个包是否在系统安装
        // 如果没有安装, 直接抛出异常, 这里需要临时Hook掉 PMS, 绕过这个检查.

        Class<?> activityThreadClass = Class.forName("android.app.ActivityThread");
        Method currentActivityThreadMethod = activityThreadClass.getDeclaredMethod("currentActivityThread");
        currentActivityThreadMethod.setAccessible(true);
        Object currentActivityThread = currentActivityThreadMethod.invoke(null);

        // 获取ActivityThread里面原始的 sPackageManager
        Field sPackageManagerField = activityThreadClass.getDeclaredField("sPackageManager");
        sPackageManagerField.setAccessible(true);
        Object sPackageManager = sPackageManagerField.get(currentActivityThread);

        // 准备好代理对象, 用来替换原始的对象
        Class<?> iPackageManagerInterface = Class.forName("android.content.pm.IPackageManager");
        Object proxy = Proxy.newProxyInstance(iPackageManagerInterface.getClassLoader(),
                new Class<?>[]{iPackageManagerInterface},
                new IPackageManagerHookHandler(sPackageManager));

        // 1. 替换掉ActivityThread里面的 sPackageManager 字段
        sPackageManagerField.set(currentActivityThread, proxy);
    }
}
