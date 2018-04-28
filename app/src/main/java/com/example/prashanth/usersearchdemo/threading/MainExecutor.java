package com.example.prashanth.usersearchdemo.threading;

import android.os.Handler;

public class MainExecutor implements IMainExecutor {
    private static MainExecutor sMainExecutor = new MainExecutor();
    private static Handler sMainThreadHandler;

    public static IMainExecutor getInstance() {
        return sMainExecutor;
    }

    private MainExecutor() {
    }

    @Override
    public void executeInMainThread(Runnable command) {
        sMainThreadHandler.post(command);
    }

    @Override
    public void setMainThreadHandler(Handler handler) {
        sMainThreadHandler = handler;
    }

    @Override
    public void scheduleInMainThread(Runnable command, long timeInMillis) {
        sMainThreadHandler.postDelayed(command, timeInMillis);
    }

    @Override
    public void removeRunnable(Runnable command) {
        sMainThreadHandler.removeCallbacks(command);
    }
}
