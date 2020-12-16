package com.sty.ne.appperformance.activity;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.sty.ne.appperformance.R;
import com.sty.ne.appperformance.watcher.LeakObservable;
import com.sty.ne.appperformance.watcher.LeakObserver;

import java.lang.ref.WeakReference;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

public class LeakActivity extends AppCompatActivity implements LeakObserver {
    //Handler 内存泄漏问题
    private static int HANDLER_FLAG = 987;
    private Button btnLeak;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_leak);

        initView();
        setViewsListener();
    }

    private void initView() {
        btnLeak = findViewById(R.id.btn_leak);
        Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.splash_guide);

        mHandler.sendEmptyMessageDelayed(HANDLER_FLAG, 1000);
    }


    private void setViewsListener() {
        btnLeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeakObservable.getInstance().register(LeakActivity.this);
            }
        });
    }

    /**
     * Handler 内存泄漏原因：
     *  在JAVA中，非静态内部类和匿名类都会潜在持有他们所属外部类的引用，即MessageQueue-->Message-->Handler-->LeakActivity
     * Handler 内存泄漏解决方案:
     * 1. static：优点在于不会内存泄漏；缺点是如果频繁使用static，会导致jvm加载类的时候消耗过多
     * 2. WeakReference: 优点在于gc回收时，会自动被清除掉；缺点是不知道什么时候回被回收，若被回收，
     *      且有自定义调用Activity方法的话会报异常
     * 3. SoftReference: 优点在于gc回收时，内存不足的情况下才会被清除；缺点是不知道什么时候回被回收
     */
    private static Handler mHandler = new Handler() {
        private WeakReference<LeakActivity> activity;

        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
            activity.get(); //调用自定义的Activity方法

            mHandler.sendEmptyMessageDelayed(HANDLER_FLAG, 1000);
        }
    };

    private static Handler mHandler2 = new Handler(new Handler.Callback() {
        @Override
        public boolean handleMessage(@NonNull Message msg) {
            return false;
        }
    });

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeMessages(HANDLER_FLAG);
    }

    @Override
    public void handle() {

    }
}