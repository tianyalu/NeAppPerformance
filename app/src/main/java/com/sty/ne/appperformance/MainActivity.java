package com.sty.ne.appperformance;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;

import com.sty.ne.appperformance.tool.LauncherTimer;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        findViews();
    }

    private void findViews() {
        final View viewRoot = findViewById(R.id.root);
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
}