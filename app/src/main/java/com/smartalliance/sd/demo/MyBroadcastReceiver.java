package com.smartalliance.sd.demo;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.smartalliance.sd_lib.activity.DetectCrannyActivity;

import java.io.File;

public class MyBroadcastReceiver extends BroadcastReceiver {
    private static final String TAG = "MyBroadcastReceiver";
    @Override
    public void onReceive(Context context, Intent intent) {
        if (DetectCrannyActivity.BROADCAST_ACTION.equals(intent.getAction())) {
            String fn1 = intent.getStringExtra("fileNamePortraitImage");
            String fn2 = intent.getStringExtra("fileNamePortraitData");
            String fn3 = intent.getStringExtra("fileNameLandscapeImage");
            String fn4 = intent.getStringExtra("fileNameLandscapeData");

            File f1 = new File(context.getFilesDir(), fn1);
            File f2 = new File(context.getFilesDir(), fn2);
            File f3 = new File(context.getFilesDir(), fn3);
            File f4 = new File(context.getFilesDir(), fn4);

            Log.d(TAG, "F 1 " + f1 + " bytes: " + f1.length());
            Log.d(TAG, "F 2 " + f2 + " bytes: " + f2.length());
            Log.d(TAG, "F 3 " + f3 + " bytes: " + f3.length());
            Log.d(TAG, "F 4 " + f4 + " bytes: " + f4.length());

            intent = new Intent(AppDemo.getInstance(), ResultActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            intent.putExtra("fileNamePortraitImage", fn1);
            intent.putExtra("fileNamePortraitData", fn2);
            intent.putExtra("fileNameLandscapeImage", fn3);
            intent.putExtra("fileNameLandscapeData", fn4);
            AppDemo.getInstance().getApplicationContext().startActivity(intent);

        }else if (intent.getAction().equals(DetectCrannyActivity.BROADCAST_ERROR_ACTION)) {
            int errorCode = intent.getIntExtra("errorCode", 100);
            Log.d(TAG, "error code: " + errorCode);
            Toast.makeText(AppDemo.getInstance().getApplicationContext(), "error code " + errorCode, Toast.LENGTH_LONG).show();
        }
    }
}