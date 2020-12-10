package com.sty.ne.appperformance.repo;

import com.sty.ne.appperformance.service.ApiService;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:21 PM
 */
public abstract class IRepo {

    protected ApiService apiService;

    public IRepo(ApiService apiService) {
        this.apiService = apiService;
    }

    public abstract void unInit();
}
