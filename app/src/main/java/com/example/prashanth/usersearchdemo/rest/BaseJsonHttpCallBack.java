package com.example.prashanth.usersearchdemo.rest;

import android.util.Log;


import com.example.prashanth.usersearchdemo.threading.BusinessExecutor;
import com.example.prashanth.usersearchdemo.threading.IBusinessExecutor;

import org.json.JSONException;
import org.json.JSONObject;

import retrofit2.Response;

public abstract class BaseJsonHttpCallBack extends HttpCallBack<JSONObject> {

    private static final String LOG_TAG = "BaseJsonHttpCallBack";

    private IBusinessExecutor mBusinessExecutor = BusinessExecutor.getInstance();

    @Override
    void onFailure(final String errorMessage) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                onError(errorMessage, null);
            }
        });
    }

    @Override
    void success(final JSONObject response, final long currentServerTime) {
        mBusinessExecutor.executeInBusinessThread(new Runnable() {
            @Override
            public void run() {
                if (response == null) {
                    onError("Wrong response", null);
                    return;
                }

                if (response.has("es") && !response.isNull("es")) {
                    try {
                        final String errorMessage = response.getString("es");
                        if (errorMessage != null && !errorMessage.isEmpty()) {
                            if (Integer.parseInt(errorMessage) == 1) {
                                String message = getErrorMessage(response);
                                if(message != null) {
                                    onError(message, response);
                                } else {
                                    onError("message", response);
                                }
                                return;
                            } else if(Integer.parseInt(errorMessage) == 2){
                                onEmptyData(response);
                                return;
                            }
                        }
                    } catch (JSONException e) {
                        Log.d(LOG_TAG, e.getMessage());
                    }
                }
                onSuccess(response, currentServerTime);
            }
        });
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
    void unauthenticated(Response<JSONObject> response) {
    }

    protected abstract void onSuccess(JSONObject result, long currentServerTime);

    protected abstract void onError(String message, JSONObject jsonResponse);

    protected abstract void onEmptyData(JSONObject jsonResponse);
}
