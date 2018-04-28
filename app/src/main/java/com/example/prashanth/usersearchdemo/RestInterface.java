package com.example.prashanth.usersearchdemo;

import com.example.prashanth.usersearchdemo.rest.HttpCall;

import org.json.JSONObject;

import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Query;
import retrofit2.http.Url;

public interface RestInterface {

    @GET("search/users")
    HttpCall<JSONObject> hitApi(@Query("q") String q,
                                @Query("sort") String sort,
                                @Query("per_page") String perPage);
}
