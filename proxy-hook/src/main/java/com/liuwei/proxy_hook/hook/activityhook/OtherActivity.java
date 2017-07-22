package com.liuwei.proxy_hook.hook.activityhook;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.liuwei.proxy_hook.R;

public class OtherActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_other);
    }
}
