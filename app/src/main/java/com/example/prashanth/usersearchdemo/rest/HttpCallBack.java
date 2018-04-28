package com.example.prashanth.usersearchdemo.rest;
import org.json.JSONObject;

import retrofit2.Response;

public abstract class HttpCallBack<T> {

    abstract void onFailure(String errorMessage);

    abstract void onReceiveEmptyData(JSONObject errorData);

    abstract void success(T response, long serverTime);

    abstract void unauthenticated(Response<T> response);
}
