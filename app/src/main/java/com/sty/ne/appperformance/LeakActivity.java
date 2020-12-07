package com.sty.ne.appperformance;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import com.sty.ne.appperformance.watcher.LeakObservable;
import com.sty.ne.appperformance.watcher.LeakObserver;

import androidx.appcompat.app.AppCompatActivity;

public class LeakActivity extends AppCompatActivity implements LeakObserver {
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
    }


    private void setViewsListener() {
        btnLeak.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LeakObservable.getInstance().register(LeakActivity.this);
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void handle() {

    }
}