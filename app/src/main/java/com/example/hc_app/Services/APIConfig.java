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

    //Update user info
    @POST("api/update_info")
    Call<RespObj> UpdateUserInfo(@Body RequestBody body);

    //Create user
    @POST("api/new_user")
    Call<RespObj> CreateUser(@Body RequestBody body);

    //GetSteps
    @POST("api/list_steps")
    Call<RespObj> GetSteps(@Body RequestBody body);

    //Get Last Record
    @POST("api/last_record")
    Call<RespObj> GetRecords(@Body RequestBody body);

    //Update steps
    @POST("api/new_steps")
    Call<RespObj> UpdateStep(@Body RequestBody body);

    //Get recommend exercise
    @POST("api/list_recom_exer")
    Call<RespObj> GetRecommend(@Body RequestBody body);

    //Get list exercise
    @POST("api/list_exer")
    Call<RespObj> GetListExercise();

    //Get list group exercise
    @POST("api/get_group_exercise")
    Call<RespObj> GetGroupExercise();

    //Get detail exercise
    @POST("api/get_detail_exercise")
    Call<RespObj> GetDetailExercise(@Body RequestBody body);

    //Get chart data
    @POST("api/get_chart_data")
    Call<RespObj> GetChartData(@Body RequestBody body);

    //Get list history
    @POST("api/list_history")
    Call<RespObj> GetListHistory(@Body RequestBody body);
}
