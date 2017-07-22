package com.liuwei.activity_replace.hook;


import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.liuwei.activity_replace.StubActivity;
import com.liuwei.activity_replace.log.Logger;

import java.lang.reflect.Field;


/**
 * Created by liuwei on 17/3/2.
 */
//public class ActivityThreadHandler extends Handler {
// 一开始我们打算替换ActivityTread中的mH成员变量,所以在这直接继承了Handler,在替换时抛出了异常,发现是行不通的,
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
            } catch (NoSuchFieldException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }
        }
        mBaseHandler.handleMessage(msg);
        return true;
    }
}
