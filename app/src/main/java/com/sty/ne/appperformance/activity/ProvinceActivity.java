package com.sty.ne.appperformance.activity;

import android.os.Bundle;

import com.sty.ne.appperformance.R;
import com.sty.ne.appperformance.fragment.ProvinceFragment;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 8:38 PM
 */
public class ProvinceActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_province);
        if(savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction().replace(R.id.fl_container, ProvinceFragment.newInstance()).commitNow();
        }
    }
}
