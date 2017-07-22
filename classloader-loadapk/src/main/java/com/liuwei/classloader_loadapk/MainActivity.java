package com.liuwei.classloader_loadapk;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.liuwei.classloader_loadapk.classloader_hook.ActivityThread$LoadedApkHook;
import com.liuwei.classloader_loadapk.hook.HookHelper;
import com.liuwei.classloader_loadapk.utils.Utils;

public class MainActivity extends Activity {

    Button btn_other_apk_activity;
    Button btn_start_inner_activity;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn_other_apk_activity = (Button) findViewById(R.id.btn_other_apk_activity);
        btn_start_inner_activity = (Button) findViewById(R.id.btn_start_inner_activity);
        btn_other_apk_activity.setOnClickListener(listener);
        btn_start_inner_activity.setOnClickListener(listener);
    }

    private View.OnClickListener listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch(v.getId()){
                case R.id.btn_other_apk_activity:
                    Intent intent = new Intent();
                    intent.setComponent(new ComponentName("com.liuwei.activity_replace",
                            "com.liuwei.activity_replace.MainActivity"));
                    startActivity(intent);
                    break;
                case R.id.btn_start_inner_activity:
                    Intent intent1 = new Intent(MainActivity.this, InnerActivity.class);
                    startActivity(intent1);
                    break;

            }

        }
    };

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
        Utils.extractAssets(newBase, "activity-replace.apk");
        ActivityThread$LoadedApkHook.doHookActivityThreadLoadedApk(newBase, getFileStreamPath("activity-replace.apk"));
        HookHelper.doIActivityManagerHook();
        HookHelper.doHandlerHook();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == android.view.KeyEvent.KEYCODE_BACK) {
            finish();
            System.exit(0);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
