package com.sty.ne.appperformance.viewmodel;

import android.app.Application;

import com.sty.ne.appperformance.model.Province;
import com.sty.ne.appperformance.net.common.Result;
import com.sty.ne.appperformance.repo.MiscRepo;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.LiveData;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:24 PM
 */
public class MiscViewModel extends BaseViewModel {

    private MiscRepo repo;

    public MiscViewModel(@NonNull Application application) {
        super(application);
        repo = repos.getMiscRepo();
    }

    public LiveData<Result<List<Province>>> province() {
        return repo.province();
    }
}
