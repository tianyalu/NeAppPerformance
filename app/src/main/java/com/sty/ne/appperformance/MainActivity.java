package com.sty.ne.appperformance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.core.app.ActivityCompat;
import androidx.core.view.LayoutInflaterCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.usage.NetworkStats;
import android.app.usage.NetworkStatsManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.net.NetworkCapabilities;
import android.net.TrafficStats;
import android.os.Build;
import android.os.Bundle;
import android.os.RemoteException;
import android.telephony.TelephonyManager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.Button;

import com.sty.ne.appperformance.activity.ChurnActivity;
import com.sty.ne.appperformance.activity.LeakActivity;
import com.sty.ne.appperformance.activity.ProvinceActivity;
import com.sty.ne.appperformance.tool.LauncherTimer;
import com.sty.ne.appperformance.util.FpsUtil;
import com.sty.ne.appperformance.util.LogUtil;
import com.sty.ne.appperformance.util.PermissionUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;

public class MainActivity extends AppCompatActivity {
    private String[] needPermissions = { Manifest.permission.READ_PHONE_STATE};

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        installCustomFactory();
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //异步加载
        new AsyncLayoutInflater(this).inflate(R.layout.activity_main, null,
                new AsyncLayoutInflater.OnInflateFinishedListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onInflateFinished(@NonNull View view, int resid, @Nullable ViewGroup parent) {
                setContentView(view);
                afterSetView();
            }
        });
        //afterSetView();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void afterSetView() {
        findViews();
        otherFunction();
//        FpsUtil.getFps();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void otherFunction() {
        requestPermissions();
        statistics1();
        getStat(getUid());
    }

    private void requestPermissions() {
        if (!PermissionUtils.checkPermissions(this, needPermissions)) {
            PermissionUtils.requestPermissions(this, needPermissions);
        }
    }

    private void installCustomFactory() {
        LayoutInflaterCompat.setFactory2(getLayoutInflater(), new LayoutInflater.Factory2() {
            @Nullable
            @Override
            public View onCreateView(@Nullable View parent, @NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                //handle
                LogUtil.e("onCreateView", "name=" + name);
                return getDelegate().createView(parent, name, context, attrs);
                // E/onCreateView: 2/name=LinearLayout
                // E/onCreateView: 2/name=ViewStub
                // E/onCreateView: 2/name=FrameLayout
                // E/onCreateView: 2/name=androidx.appcompat.widget.ActionBarOverlayLayout
                // E/onCreateView: 2/name=androidx.appcompat.widget.ContentFrameLayout
                // E/onCreateView: 2/name=androidx.appcompat.widget.ActionBarContainer
                // E/onCreateView: 2/name=androidx.appcompat.widget.Toolbar
                // E/onCreateView: 2/name=androidx.appcompat.widget.ActionBarContextView
                // E/onCreateView: 2/name=androidx.constraintlayout.widget.ConstraintLayout
                // E/onCreateView: 2/name=TextView
            }

            @Nullable
            @Override
            public View onCreateView(@NonNull String name, @NonNull Context context, @NonNull AttributeSet attrs) {
                return null;
            }
        });
    }

    private void findViews() {
        final View viewRoot = findViewById(R.id.root);
        Button btnGotoChurn = findViewById(R.id.btn_goto_churn);
        Button btnGotoLeak = findViewById(R.id.btn_goto_leak);
        Button btnGotoProvince = findViewById(R.id.btn_goto_province);
        btnGotoChurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ChurnActivity.class));
            }
        });
        btnGotoLeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LeakActivity.class));
            }
        });
        btnGotoProvince.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, ProvinceActivity.class));
            }
        });
        viewRoot.getViewTreeObserver().addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {
            @Override
            public boolean onPreDraw() {
                viewRoot.getViewTreeObserver().removeOnPreDrawListener(this);
                LauncherTimer.logEnd("tag3");
                return false;
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        LauncherTimer.logEnd("tag1");
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        LauncherTimer.logEnd("tag2");
    }

     // D/Time: 2/tag1 launcher time=101
     // D/Time: 2/tag3 launcher time=139
     // D/Time: 2/tag2 launcher time=146


    @Override
    protected void onDestroy() {
        super.onDestroy();
//        FpsUtil.stopGetFps();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.REQUEST_PERMISSIONS_CODE) {
            if (!PermissionUtils.verifyPermissions(grantResults)) {
                PermissionUtils.showMissingPermissionDialog(this);
            } else {

            }
        }
    }

    //---------------------- 网络流量统计↓ ------------------------------------------
    private int getUid() {
        try {
            PackageManager pm = getPackageManager();
            ApplicationInfo ai = pm.getApplicationInfo(getPackageName(), 0);
            return ai.uid;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    //流量统计方式一（从开机到现在）
    private void statistics() {
        TrafficStats.getUidRxBytes(getUid());
        TrafficStats.getUidTxBytes(getUid());
        TrafficStats.getTotalRxBytes();
    }

    //流量统计方式二（从开机到现在）
    private long[] getStat(int uid) {
        String line, line2;
        long[] stats = new long[2];
        try {
            File fileSnd = new File("/proc/uid_stat/" + uid + "/tcp_snd");
            File fileRcv = new File("/proc/uid_stat/" + uid + "/tcp_rcv");
            BufferedReader br1 = new BufferedReader(new FileReader(fileSnd));
            BufferedReader br2 = new BufferedReader(new FileReader(fileRcv));
            while ((line = br1.readLine()) != null && (line2 = br2.readLine()) != null) {
                stats[0] = Long.parseLong(line);
                stats[1] = Long.parseLong(line2);
            }
            br1.close();
            br2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        LogUtil.i("statistics1: ", "stat0=" + stats[0] + " stat1=" + stats[1]);
        return stats;
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void statistics1() {
        long current = System.currentTimeMillis();
        long start = System.currentTimeMillis() - 30 * 60 * 1000;
        long rx = 0;
        long tx = 0;
        NetworkStatsManager statsManager = (NetworkStatsManager) getSystemService(Context.NETWORK_STATS_SERVICE);
        try {
            NetworkStats networkStats = statsManager.querySummary(NetworkCapabilities.TRANSPORT_WIFI, getSubscriberId(),
                    start, current);
            if (networkStats != null) {
                NetworkStats.Bucket bucketOut = new NetworkStats.Bucket();
                int uid = getUid();
                while (networkStats.hasNextBucket()) {
                    networkStats.getNextBucket(bucketOut);
                    if (uid == bucketOut.getUid()) {
                        rx += bucketOut.getRxBytes();
                        tx += bucketOut.getTxBytes();
                    }
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        LogUtil.d("statistics1: ", "rx=" + rx + " tx=" + tx);
    }

    private String getSubscriberId() {
        TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_PHONE_STATE) !=
                PackageManager.PERMISSION_GRANTED) {
            return "";
        }
        return manager.getSubscriberId();
    }
}