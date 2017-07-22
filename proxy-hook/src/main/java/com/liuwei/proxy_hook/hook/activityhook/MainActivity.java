package com.liuwei.proxy_hook.hook.activityhook;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.liuwei.proxy_hook.R;

public class MainActivity extends Activity {
    private Button btn_start_by_activity;
    private Button btn_start_by_context;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        // hook Activity.startActivity()的方法时不知道这行代码为什么放在attachBaseContext里面无效,
        // 调试发现,被hook的Instrumentation后来又会被替换掉原来的,
        // 猜想可能是attachBaseContext应该是在Activity的attach方法之前执行了。
//        ActivityThreadHookHelper.doActivityStartHook(this);
        btn_start_by_activity = (Button) findViewById(R.id.btn_start_by_activity);
        btn_start_by_context = (Button) findViewById(R.id.btn_start_by_context);
        btn_start_by_activity.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OtherActivity.class);
                startActivity(intent);
            }
        });

        btn_start_by_context.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OtherActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                getApplicationContext().startActivity(intent);
            }
        });
    }
    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
    }
}