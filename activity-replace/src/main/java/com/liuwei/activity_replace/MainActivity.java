package com.liuwei.activity_replace;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.liuwei.activity_replace.hook.HookHelper;


public class MainActivity extends Activity {

    Button btn_start;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        HookHelper.doInstrumentationHook(this);// 方法一,简单,但需要在每个activity的实例中调用才能生效
        btn_start = (Button) findViewById(R.id.btn_start);
        btn_start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, OtherActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(newBase);
//        HookHelper.doIActivityManagerHook();// 方法二,相对方法一复杂一些,但加载一次即可
        HookHelper.doHandlerHook();
    }
}
