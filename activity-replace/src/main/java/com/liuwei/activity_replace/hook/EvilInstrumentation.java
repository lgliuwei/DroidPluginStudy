package com.liuwei.activity_replace.hook;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.IBinder;

import com.liuwei.activity_replace.StubActivity;

import java.lang.reflect.Method;

/**
 * 方法一
 * Created by liuwei on 17/3/2.
 */
public class EvilInstrumentation extends Instrumentation {

    private Instrumentation mInstrumentation;

    public EvilInstrumentation(Instrumentation mInstrumentation) {
        this.mInstrumentation = mInstrumentation;
    }

    public ActivityResult execStartActivity(
            Context who, IBinder contextThread, IBinder token, Activity target,
            Intent intent, int requestCode, Bundle options) {
        // 在此此处先将intent原本的Component保存起来,
        // 然后创建一个新的intent只想StubActivity并替换掉原本的Activity,以达通过ams验证的目的,然后等ams验证通过后再将其还原。
        // 方法一:在此替换虽然非常简单,但此处很明显不是一个好的hook点,因为mInstrumentation是Activity的成员变量,但是在程序中Activity并不是一个,而是有多个实例,所以需要在每个实例中hook掉才可以。
        Intent replaceIntent = new Intent(target, StubActivity.class);
        replaceIntent.putExtra(StubActivity.TARGET_COMPONENT, intent);
        intent = replaceIntent;

        try {
            Method execStartActivitie = mInstrumentation.getClass().getDeclaredMethod("execStartActivity", Context.class,
                    IBinder.class, IBinder.class, Activity.class,
                    Intent.class, int.class, Bundle.class);
            return (ActivityResult)execStartActivitie.invoke(mInstrumentation, who, contextThread, token,
                    target, intent, requestCode, options);
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
