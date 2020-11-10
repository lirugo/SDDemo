package com.smartalliance.sd.demo;

import com.google.gson.annotations.SerializedName;

class ServerResponseRequestStatus {
    @SerializedName("status")
    private int status;
    public int getStatus() {
        return status;
    }
}