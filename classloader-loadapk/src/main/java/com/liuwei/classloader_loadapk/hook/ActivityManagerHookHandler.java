package com.liuwei.classloader_loadapk.hook;

import android.content.ComponentName;
import android.content.Intent;

import com.liuwei.classloader_loadapk.StubActivity;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;

/**
 * Created by liuwei on 17/3/3.
 */
public class ActivityManagerHookHandler implements InvocationHandler {

    private final static String START_ACTVIITY = "startActivity";

    private Object object;

    public ActivityManagerHookHandler(Object object) {
        this.object = object;
    }

    /**
     *
     * @param proxy
     * @param method
     * @param args
     * @return
     * @throws Throwable
     *
     * 源码路径:/frameworks/base/core/java/android/app/ActivityManagerNative.ActivityManagerProxy
     *
     * public int startActivity(IApplicationThread caller, String callingPackage, Intent intent,
    2404            String resolvedType, IBinder resultTo, String resultWho, int requestCode,
    2405            int startFlags, ProfilerInfo profilerInfo, Bundle options) throws RemoteException {
     */
    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        if (START_ACTVIITY.equals(method.getName())) {
            int index = -1;
            // 遍历出第一个Intent参数
            for (int i = 0; i < args.length; i++) {
                if (args[i] instanceof Intent) {
                    index = i;
                }
            }
            if (index == -1) {
                return null;
            }
            Intent intent = (Intent)args[index];
            Intent replaceIntent = new Intent();
            replaceIntent.setComponent(new ComponentName(StubActivity.TARGET_PACKAGE, StubActivity.class.getName()));
            replaceIntent.putExtra(StubActivity.TARGET_COMPONENT, intent);

            args[index] = replaceIntent;
        }
        return method.invoke(object, args);
    }
}
