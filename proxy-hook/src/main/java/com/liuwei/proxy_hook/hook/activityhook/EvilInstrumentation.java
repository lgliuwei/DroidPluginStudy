package com.liuwei.proxy_hook.hook.activityhook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.liuwei.proxy_hook.log.Logger;

import java.lang.reflect.Method;

/**
 * Created by liuwei on 17/3/1.
 */
public class EvilInstrumentation extends Instrumentation {
    private Instrumentation instrumentation;
    public EvilInstrumentation(Instrumentation instrumentation) {
        this.instrumentation = instrumentation;
    }
    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        Logger.i(EvilInstrumentation.class, "请注意! startActivity已经被hook了!");
        try {
            Method execStartActivity = Instrumentation.class.getDeclaredMethod("execStartActivity", Context.class,
                    IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            return (ActivityResult)execStartActivity.invoke(instrumentation, who, contextThread, token, target,
                    intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
