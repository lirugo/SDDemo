package com.smartalliance.sd.demo;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.Path;

interface ApiConfig {
    @POST("/api/v1/client-request")
    @Headers({"Content-Type: application/octet-stream"})
    Call<com.smartalliance.sd.demo.ServerResponseRequestId> getRequestId(@Body RequestBody body);

    @GET("/api/v1/client-request/{id}")
    Call<ServerResponseRequestStatus> getRequestStatus(@Path("id") Integer id);
}