package com.sty.ne.appperformance.repo;

import com.sty.ne.appperformance.model.Province;
import com.sty.ne.appperformance.net.common.Result;
import com.sty.ne.appperformance.service.ApiService;

import java.util.List;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:22 PM
 */
public class MiscRepo extends IRepo {


    public MiscRepo(ApiService apiService) {
        super(apiService);
    }

    public LiveData<Result<List<Province>>> province() {
        MediatorLiveData<Result<List<Province>>> liveData = new MediatorLiveData<>();
        apiService.province(liveData);
        return liveData;
    }

    @Override
    public void unInit() {
    }
}
