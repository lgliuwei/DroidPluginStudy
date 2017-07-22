package com.liuwei.classloader_loadapk;

import android.app.Activity;
import android.os.Bundle;
/**
 * Created by liuwei on 17/3/8.
 */
public class InnerActivity extends Activity {
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inner);
    }
}
