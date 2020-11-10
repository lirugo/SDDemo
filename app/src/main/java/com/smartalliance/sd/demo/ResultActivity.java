package com.smartalliance.sd.demo;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.smartalliance.sd_lib.activity.DetectCrannyActivity;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ResultActivity extends AppCompatActivity implements FileUploadCallback{
    private static final String TAG = "ResultActivity";
    private ImageView popupPortraitImageView;
    private ImageView popupLandscapeImageView;
    private ProgressBar progressBar;
    private TextView progressBarTextView;
    private FrameLayout progressBarFrameLayout;
    private String fileNamePortraitImage;
    private String fileNameLandscapeImage;
    private String fileNamePortraitData;
    private String fileNameLandscapeData;
    private Button popupConfirmButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        initActivity(R.layout.activity_result);

        Intent intent = getIntent();
        popupPortraitImageView = findViewById(R.id.popupPortraitImageView);
        popupLandscapeImageView = findViewById(R.id.popupLandscapeImageView);
        progressBarFrameLayout = findViewById(R.id.progressBarFrameLayout);
        popupConfirmButton = findViewById(R.id.popupConfirmButton);
        progressBar = findViewById(R.id.progressBar);
        progressBarTextView = findViewById(R.id.progressBarTextView);

        fileNamePortraitImage = intent.getStringExtra("fileNamePortraitImage");
        fileNamePortraitData = intent.getStringExtra("fileNamePortraitData");
        fileNameLandscapeImage = intent.getStringExtra("fileNameLandscapeImage");
        fileNameLandscapeData = intent.getStringExtra("fileNameLandscapeData");

        if (fileNamePortraitImage != null)
            popupPortraitImageView.setImageBitmap(BitmapFactory.decodeFile(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileNamePortraitImage));
        if (fileNamePortraitImage != null)
            popupLandscapeImageView.setImageBitmap(BitmapFactory.decodeFile(getApplicationContext().getFilesDir().getAbsolutePath() + "/" + fileNameLandscapeImage));
    }

    private void initActivity(int layout) {
        setContentView(layout);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    public void confirmAndSend(View view) {
        showProgressBar(true);
        getRequestId(this);
    }

    public void restart(View view) {
        startActivity(new Intent(this, DetectCrannyActivity.class));
    }

    private byte[] file2Bytes(File file) {
        long length = file.length();

        // Create the byte array to hold the data
        byte[] bytes = new byte[(int) length];

        // Read in the bytes
        int offset = 0;
        int numRead = 0;

        InputStream is = null;
        try {
            is = new FileInputStream(file);
            try {
                while (offset < bytes.length
                        && (numRead = is.read(bytes, offset, bytes.length - offset)) >= 0) {
                    offset += numRead;
                }
            } finally {
                is.close();

            }

            // Ensure all the bytes have been read in
            if (offset < bytes.length) {
                throw new IOException("Could not completely read file " + file.getName());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return bytes;
    }

    private Integer requestId = 0;

    private void getRequestId(Context context) {
        String filePortraitName = context.getFilesDir().getAbsolutePath() + "/" + fileNamePortraitData;
        String fileLandscapeName = context.getFilesDir().getAbsolutePath() + "/" + fileNameLandscapeData;
        File filePortrait = new File(filePortraitName);
        File fileLandscape = new File(fileLandscapeName);

        byte[] portrait = file2Bytes(filePortrait);
        byte[] landscape = file2Bytes(fileLandscape);
        byte[] bodyBytes = new byte[portrait.length + landscape.length];
        System.arraycopy(portrait, 0, bodyBytes, 0, portrait.length);
        System.arraycopy(landscape, 0, bodyBytes, portrait.length, landscape.length);


        if (portrait.length == 0 || landscape.length == 0) {
            Toast.makeText(context, "First create request", Toast.LENGTH_SHORT).show();
            return;
        }

        // Parsing any Media type file
        String fileCommonBodyTemp = context.getFilesDir().getAbsolutePath() + "/" + "common-body-file.tmp";
        File bodyFile = new File(fileCommonBodyTemp);
        try {
            OutputStream os = new FileOutputStream(bodyFile);
            os.write(bodyBytes);
            os.close();
        }
        catch (Exception e) {
            e.printStackTrace();
        }

        com.smartalliance.sd.demo.ProgressRequestBody progressRequestBody = new com.smartalliance.sd.demo.ProgressRequestBody(bodyFile, "MediaType.parse(\"application/octet-stream\")",this);

        com.smartalliance.sd.demo.ApiConfig getResponse = AppConfig.getRetrofit().create(com.smartalliance.sd.demo.ApiConfig.class);
        Call<com.smartalliance.sd.demo.ServerResponseRequestId> call = getResponse.getRequestId(progressRequestBody);
        call.enqueue(new Callback() {
            @Override
            public void onResponse(Call call, Response response) {
                com.smartalliance.sd.demo.ServerResponseRequestId serverResponse = (com.smartalliance.sd.demo.ServerResponseRequestId) response.body();
                if (serverResponse != null) {
                    requestId = serverResponse.getId();
                    getRequestStatus(context);
                }
                showProgressBar(false);
            }

            @Override
            public void onFailure(Call call, Throwable t) {
                showProgressBar(false);
                Log.d(TAG, "onFailure: ");
            }
        });
    }

    private void getRequestStatus(Context context) {
        if (requestId > 0) {
            com.smartalliance.sd.demo.ApiConfig getResponse = AppConfig.getRetrofit().create(com.smartalliance.sd.demo.ApiConfig.class);
            Call<ServerResponseRequestStatus> call = getResponse.getRequestStatus(requestId);
            call.enqueue(new Callback() {
                @Override
                public void onResponse(Call call, Response response) {
                    ServerResponseRequestStatus serverResponse = (ServerResponseRequestStatus) response.body();
                    if (serverResponse != null) {
                        //Code to text
                        int status = serverResponse.getStatus();
                        String statusStr = "REPEAT";
                        if (status == 1)
                            statusStr = "SOLID";
                        else if (status == 2)
                            statusStr = "BROKEN";
                        else if (status == -1)
                            statusStr = "REPEAT";

                        Toast.makeText(context, "STATUS: " + statusStr, Toast.LENGTH_LONG).show();
                    }
                    showProgressBar(false);
                }

                @Override
                public void onFailure(Call call, Throwable t) {
                    showProgressBar(false);
                    Log.d(TAG, "onFailure: ");
                }
            });
        } else {
            Toast.makeText(context, "First send request", Toast.LENGTH_SHORT).show();
        }
    }

    private void showProgressBar(boolean isVisible) {
        if (isVisible) {
            popupConfirmButton.setEnabled(false);
            if (progressBarFrameLayout != null)
                progressBarFrameLayout.setVisibility(View.VISIBLE);
        } else {
            popupConfirmButton.setEnabled(true);
            if (progressBarFrameLayout != null)
                progressBarFrameLayout.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onProgressUpdate(int percentage) {
        progressBar.setProgress(percentage);
        progressBarTextView.setText(percentage + "%");

        if(percentage == 100){
            progressBarTextView.setText("Analysing....");
        }
    }

    @Override
    public void onError(String message) {

        progressBarTextView.setText(message);
    }

    @Override
    public void onSuccess(String message) {
        progressBarTextView.setText(message);
    }
}