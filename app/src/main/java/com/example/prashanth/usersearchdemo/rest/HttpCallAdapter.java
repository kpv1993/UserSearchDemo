package com.example.prashanth.usersearchdemo.rest;

import android.util.Log;

import com.example.prashanth.usersearchdemo.rest.httpconverters.JsonResponseBodyConverter;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Date;
import java.util.concurrent.Executor;

import okhttp3.Headers;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HttpCallAdapter<T> implements HttpCall<T> {

    private static final String TAG = "HttpCallAdapter";
    private final Call<T> call;
    private final Executor callbackExecutor;
    private final String accessToken;

    public HttpCallAdapter(Call<T> call, Executor callbackExecutor, String accessToken) {
        this.call = call;
        this.callbackExecutor = callbackExecutor;
        this.accessToken = accessToken;
    }

    @Override
    public void cancel() {
        call.cancel();
    }

    @Override
    public void enqueue(final HttpCallBack<T> callback) {
        call.enqueue(new Callback<T>() {
            @Override
            public void onResponse(Call<T> call, final Response<T> response) {
                if(callbackExecutor != null){
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            processResponse(response, callback);
                        }
                    });
                } else {
                    processResponse(response, callback);
                }
            }

            @Override
            public void onFailure(Call<T> call, Throwable t) {
                final String message;
                if (t instanceof IOException) {
                    message = "IO Error";
                } else {
                    message = "Network Error";
                }
                if(callbackExecutor != null){
                    callbackExecutor.execute(new Runnable() {
                        @Override
                        public void run() {
                            processFailure(message, callback);
                        }
                    });
                } else {
                    processFailure(message, callback);
                }
            }
        });
    }

    @Override
    public Response<T> execute() throws IOException {
        return call.execute();
    }

    @Override
    public HttpCall<T> clone() {
        return new HttpCallAdapter<>(call.clone(), callbackExecutor, accessToken);
    }

    private void processResponse(Response<T> response, HttpCallBack<T> callback) {
        int code = response.code();
        if (code >= 200 && code < 300) {
            Headers headers = response.headers();
            Date date = headers.getDate("Date");
            long serverTime = -1;
            if(date!= null){
                serverTime = date.getTime();
            }
            callback.success(response.body(), serverTime);
        } else if (code == 401) {
            callback.unauthenticated(response);
            JSONObject errorData = new JSONObject();
            try {
                errorData = new JsonResponseBodyConverter().convert(response.errorBody());
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
            String errorMessage = getErrorMessage(errorData);
            if(errorMessage != null){
                callback.onFailure(errorMessage);
            } else {
                callback.onFailure("Network Error");
            }
        } else if(code >=500) {
            Log.e(TAG, response.raw().request().url()+" code: "+code +" "+response.message());
            callback.onFailure("Server Error");
        } else {
            JSONObject errorData = new JSONObject();
            try {
                errorData = new JsonResponseBodyConverter().convert(response.errorBody());
            } catch (IOException e) {
                Log.d(TAG, e.getMessage());
            }
            String errorMessage = getErrorKey(errorData);
            if(errorMessage != null){
                if(Integer.parseInt(errorMessage) == 1){
                    String message = getErrorMessage(errorData);
                    if(message != null) {
                        callback.onFailure(message);
                    } else {
                        callback.onFailure("Network Error");
                    }
                } else if(Integer.parseInt(errorMessage) == 2) {
                    callback.onReceiveEmptyData(errorData);
                }
            } else {
                callback.onFailure("Network Error");
            }
        }
    }

    private String getErrorKey(JSONObject errorData){
        if(errorData.keys().hasNext()){
            if (errorData.has("es") && !errorData.
                    isNull("es")) {
                try {
                    final String errorMessage = errorData.getString("es");
                    if (errorMessage != null && !errorMessage.isEmpty()) {
                        if (Integer.parseInt(errorMessage) != 0) {
                            return errorMessage;
                        }
                    }
                } catch (JSONException e) {
                    return null;
                }
            }
        } else {
            return null;
        }
        return null;
    }

    private String getErrorMessage(JSONObject errorData){
        if(errorData.keys().hasNext()){
            if (errorData.has("message") && !errorData.
                    isNull("message")) {
                try {
                    final String errorMessage = errorData.getString("message");
                    if (errorMessage != null && !errorMessage.isEmpty()) {
                        return errorMessage;
                    }
                } catch (JSONException e) {
                    return null;
                }
            }
        } else {
            return null;
        }
        return null;
    }

    private void processFailure(String message, HttpCallBack<T> callback) {
        callback.onFailure(message);
    }
}
