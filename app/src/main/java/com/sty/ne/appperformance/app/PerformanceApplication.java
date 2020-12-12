package com.sty.ne.appperformance.app;

import android.app.Application;
import android.content.Context;
import android.os.Build;
import android.os.Debug;
import android.os.Environment;
import android.os.Trace;
import android.text.TextUtils;

import com.facebook.stetho.Stetho;
import com.sty.ne.appperformance.BuildConfig;
import com.sty.ne.appperformance.tool.LauncherTimer;
import com.sty.ne.appperformance.util.LogUtil;
import com.sty.ne.appperformance.util.ScreenUtil;
import com.sty.ne.appperformance.watcher.AppForegroundWatcher;
import com.tencent.bugly.crashreport.CrashReport;

import java.io.File;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * @Author: tian
 * @UpdateDate: 2020/11/30 8:08 PM
 */
public class PerformanceApplication extends Application {
    protected Context context;
    public static Application application;
    ExecutorService executorService;
    private int coreSize;

    @Override
    protected void attachBaseContext(Context base) {
        LauncherTimer.logStart();
        super.attachBaseContext(base);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        //traceview方式
        //File file = new File(Environment.getExternalStorageDirectory(), "app.trace");
        //Debug.startMethodTracing(file.getAbsolutePath()); //该方法可以指定trace文件生成路径： /storage/emulated/0/app.trace
        //Debug.startMethodTracing("Launcher");

        //systemtrace方式
        //Trace.beginSection("Launcher");

        coreSize = Runtime.getRuntime().availableProcessors();
        executorService = Executors.newFixedThreadPool(Math.max(2, Math.min(coreSize - 1, 4)));
        application = this;
        context = this.getApplicationContext();
        AppProfile.context = context;
        Stetho.initializeWithDefaults(context);
        ScreenUtil.init(context);
        //1. 异步线程中使用的api不能创建Handler
        //2. 不能有UI操作
        //3. 对异步要求不高
        async(new Runnable() {
            @Override
            public void run() {
                initLog();
            }
        });
        async(new Runnable() {
            @Override
            public void run() {
                AppForegroundWatcher.init(context);
            }
        });
        async(new Runnable() {
            @Override
            public void run() {
                CrashReport.initCrashReport(getApplicationContext(), "e9bf59bd43", false);
            }
        });

        //Trace.endSection();

        //Debug.stopMethodTracing();
        //sdcard/Android/data/com.sty.ne.appperformance/files/Launcher.trace  --> save as 导出来，用Profiler打开
    }

    private void async(Runnable runnable) {
        executorService.submit(runnable);
    }

    private void initLog() {
        String logPath = context.getExternalFilesDir("log").getAbsolutePath();
        if(TextUtils.isEmpty(logPath)) {
            logPath = context.getFilesDir().getAbsolutePath();
        }
        LogUtil.init(logPath, BuildConfig.DEBUG, buildTag());
    }

    private String buildTag() {
        StringBuilder sb = new StringBuilder();
        sb.append("init ").append(BuildConfig.VERSION_NAME);
        sb.append("_").append(BuildConfig.VERSION_CODE);
        sb.append("_").append(Build.VERSION.SDK_INT);
        sb.append("_").append(Build.BRAND);
        sb.append("_").append(Build.MANUFACTURER);
        sb.append("_").append(Build.PRODUCT);
        sb.append("_").append(Build.MODEL);
        return sb.toString();
    }


}
