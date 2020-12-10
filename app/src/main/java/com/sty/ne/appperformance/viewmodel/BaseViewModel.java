package com.sty.ne.appperformance.viewmodel;

import android.app.Application;

import com.sty.ne.appperformance.app.AppService;
import com.sty.ne.appperformance.repo.Repos;
import com.sty.ne.appperformance.service.ApiService;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:02 PM
 */
public class BaseViewModel extends AndroidViewModel {
    protected AppService appService;
    protected Repos repos;

    public BaseViewModel(@NonNull Application application) {
        super(application);
        appService = AppService.getInstance();
        repos = appService.getRepos();
    }
}
