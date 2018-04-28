package com.example.prashanth.usersearchdemo;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.example.prashanth.usersearchdemo.threading.MainExecutor;

public class MyApp extends Application implements Application.ActivityLifecycleCallbacks{

    private static Context sContext;

    @Override
    public void onCreate() {
        super.onCreate();
        sContext = this;
        registerActivityLifecycleCallbacks(this);
        MainExecutor.getInstance().setMainThreadHandler(new Handler());
        Log.d("MyApp", "onCreate");

    }

    public static Context getContext(){
        return sContext;
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(Activity activity) {

    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {

    }


}
