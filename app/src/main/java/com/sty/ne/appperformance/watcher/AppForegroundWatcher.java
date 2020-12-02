package com.sty.ne.appperformance.watcher;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * @Author: tian
 * @UpdateDate: 2020/11/30 8:50 PM
 */
public class AppForegroundWatcher implements Application.ActivityLifecycleCallbacks {
    private static AppForegroundWatcher instance;
    private boolean paused = false;
    private Handler handler = new Handler();
    private Runnable delayTask;
    public boolean isForeground = false;

    public static void init(Context context) {
        if(instance == null) {
            instance = new AppForegroundWatcher();
            ((Application)context).registerActivityLifecycleCallbacks(instance);
        }
    }

    public static void stop(Context context) {
        if(context instanceof Application && instance != null) {
            ((Application) context).unregisterActivityLifecycleCallbacks(instance);
            instance = null;
        }
    }

    public static boolean isForeground() {
        return instance.isForeground;
    }

    @Override
    public void onActivityCreated(@NonNull Activity activity, @Nullable Bundle savedInstanceState) {

    }

    @Override
    public void onActivityStarted(@NonNull Activity activity) {

    }

    @Override
    public void onActivityResumed(@NonNull Activity activity) {
        this.paused = false;
        isForeground = true;
        if(delayTask != null) {
            handler.removeCallbacks(delayTask);
        }
    }

    @Override
    public void onActivityPaused(@NonNull Activity activity) {
        this.paused = true;
        if(delayTask == null) {
            delayTask = new Runnable() {
                @Override
                public void run() {
                    isForeground = false;
                }
            };
        }
        handler.postDelayed(delayTask, 500);
    }

    @Override
    public void onActivityStopped(@NonNull Activity activity) {

    }

    @Override
    public void onActivitySaveInstanceState(@NonNull Activity activity, @NonNull Bundle outState) {

    }

    @Override
    public void onActivityDestroyed(@NonNull Activity activity) {

    }
}
