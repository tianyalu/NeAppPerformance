package com.sty.ne.appperformance.repo;

import com.sty.ne.appperformance.service.ApiService;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:23 PM
 */
public class Repos {

    private ApiService apiService;

    private MiscRepo miscRepo;

    public Repos(ApiService apiService) {
        this.apiService = apiService;
        initRepo();
    }
    private void initRepo() {
        miscRepo = new MiscRepo(apiService);
    }

    public MiscRepo getMiscRepo() {
        return miscRepo;
    }

    private void unInit() {
    }
}
