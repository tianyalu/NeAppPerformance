package com.sty.ne.appperformance.app;

import com.sty.ne.appperformance.repo.Repos;
import com.sty.ne.appperformance.service.ApiService;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:20 PM
 */
public class AppService {
    private static AppService appService;

    public static AppService getInstance() {
        if (appService == null) {
            synchronized (AppService.class) {
                if (appService == null) {
                    appService = new AppService();
                }
            }
        }
        return appService;
    }

    private ApiService apiService;

    private Repos repos;

    private AppService() {
        apiService = new ApiService();
        repos = new Repos(apiService);
    }

    public Repos getRepos() {
        return repos;
    }
}
