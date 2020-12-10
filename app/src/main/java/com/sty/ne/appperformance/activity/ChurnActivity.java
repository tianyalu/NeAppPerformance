package com.sty.ne.appperformance.activity;

import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.Button;

import com.sty.ne.appperformance.R;

import androidx.appcompat.app.AppCompatActivity;

public class ChurnActivity extends AppCompatActivity {
    private Handler mHandler;
    private Button btnChurn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_churn);

        initView();
        setViewsListener();
    }

    private void initView() {
        btnChurn = findViewById(R.id.btn_churn);
        mHandler = new Handler();
    }


    private void setViewsListener() {
        btnChurn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                churn(0);
            }
        });
    }

    Runnable r = new Runnable() {
        @Override
        public void run() {
            allocate();
        }
    };

    private void allocate() {
        for (int i = 0; i < 1000; i++) {
            String ob[] = new String[10000];
        }
        churn(50);
    }

    private void churn(int delay) {
        mHandler.postDelayed(r, delay);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mHandler.removeCallbacks(r);
    }
}