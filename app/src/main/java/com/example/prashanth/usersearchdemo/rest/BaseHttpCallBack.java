package com.example.prashanth.usersearchdemo.rest;

import com.example.prashanth.usersearchdemo.threading.BusinessExecutor;
import com.example.prashanth.usersearchdemo.threading.IBusinessExecutor;

import org.json.JSONObject;

import retrofit2.Response;


public abstract class BaseHttpCallBack<T>  extends HttpCallBack<T>  {
    private static final String LOG_TAG = "BaseHttpCallBack";
    private IBusinessExecutor mBusinessExecutor = BusinessExecutor.getInstance();

    @Override
    void onFailure(final String errorMessage) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                onError(errorMessage);
            }
        });
    }

    @Override
    void onReceiveEmptyData(final JSONObject errorData) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                onEmptyData(errorData);
            }
        });
    }

    @Override
    void success(final T response, final long currentServerTime) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                if(response == null){
                    onError("Wrong response");
                    return;
                }
                onSuccess(response, currentServerTime);
            }
        });
    }

    @Override
    void unauthenticated(Response<T> response) {

    }

    protected abstract void onSuccess(T result, long currentServerTime);
    protected abstract void onError(String message);
    protected abstract void onEmptyData(JSONObject errorData);
}
