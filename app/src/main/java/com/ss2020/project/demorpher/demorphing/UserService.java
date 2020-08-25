package com.ss2020.project.demorpher.demorphing;

import com.google.gson.JsonObject;

import org.json.JSONObject;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.Headers;
import retrofit2.http.POST;

public interface UserService {
    @Headers("Content-type: application/json")
    @POST("/api/anomalydetector/demorphing/analyse")
    Call<UserResponse> postData(@Body JsonObject body);
}