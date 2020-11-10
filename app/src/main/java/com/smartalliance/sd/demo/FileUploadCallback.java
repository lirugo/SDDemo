package com.smartalliance.sd.demo;

public interface FileUploadCallback {
    void onProgressUpdate(int percentage);
    void onError(String message);
    void onSuccess(String message);
}
