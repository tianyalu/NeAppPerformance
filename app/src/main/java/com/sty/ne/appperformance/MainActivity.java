package com.sty.ne.appperformance;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.asynclayoutinflater.view.AsyncLayoutInflater;
import androidx.core.view.LayoutInflaterCompat;

import android.content.Context;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;

import com.sty.ne.appperformance.tool.LauncherTimer;
import com.sty.ne.appperformance.util.FpsUtil;
import com.sty.ne.appperformance.util.LogUtil;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        installCustomFactory();
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_main);

        //异步加载
        new AsyncLayoutInflater(this).inflate(R.layout.activity_main, null,
                new AsyncLayoutInflater.OnInflateFinishedListener() {
            @Override
            public void onInflateFinished(@NonNull View view, int resid, @Nullable ViewGroup parent) {
                setContentView(view);
                afterSetView();
            }
        });
        //afterSetView();
    }

    private void afterSetView() {
        findViews();
//        FpsUtil.getFps();
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
}