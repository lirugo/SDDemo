package com.smartalliance.sd.demo;

import android.app.Application;
import android.content.BroadcastReceiver;
import android.content.IntentFilter;

import com.smartalliance.sd_lib.activity.DetectCrannyActivity;

public class AppDemo extends Application
{
    private static com.smartalliance.sd.demo.AppDemo instance;

    public static com.smartalliance.sd.demo.AppDemo getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;


        //Register broadcast receiver
        IntentFilter filter = new IntentFilter();
        filter.addAction(DetectCrannyActivity.BROADCAST_ACTION);
        filter.addAction(DetectCrannyActivity.BROADCAST_ERROR_ACTION);
        this.registerReceiver(myBroadcastReceiver, filter);

    }

    @Override
    public void onTerminate() {
        unregisterReceiver(myBroadcastReceiver);

        super.onTerminate();
    }

    private BroadcastReceiver myBroadcastReceiver = new com.smartalliance.sd.demo.MyBroadcastReceiver();
}
