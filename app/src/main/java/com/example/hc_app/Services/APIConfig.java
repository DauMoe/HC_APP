package com.example.hc_app.Services;
import com.example.hc_app.Models.RespObj;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

public interface APIConfig {
    //Login
    @POST("api/login")
    Call<RespObj> login(@Body RequestBody body);

    //GetSteps
    @POST("api/list_steps")
    Call<RespObj> GetSteps(@Body RequestBody body);

    //Get Last Record
    @POST("api/last_record")
    Call<RespObj> GetRecords(@Body RequestBody body);
}
