package com.smartalliance.sd.demo;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import androidx.appcompat.app.AppCompatActivity;

import com.smartalliance.sd_lib.activity.DetectCrannyActivity;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private static String SDK_KEY = "NLooTdoGJfrDYzuwIKYuoGQGSewnXyqS";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity(R.layout.activity_main);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    public void callActivity(View view) {
        Intent intent = new Intent(this, DetectCrannyActivity.class);
        intent.putExtra(DetectCrannyActivity.SDK_KEY_INTENT, SDK_KEY);
        startActivity(intent);
    }

    private void initActivity(int layout) {
        setContentView(layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }
}