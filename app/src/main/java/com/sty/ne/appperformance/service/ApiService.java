package com.sty.ne.appperformance.service;

import com.sty.ne.appperformance.model.Province;
import com.sty.ne.appperformance.net.common.Result;
import com.sty.ne.appperformance.net.okhttp.OkHttp;
import com.sty.ne.appperformance.net.proto.base.Proto;
import com.sty.ne.appperformance.net.proto.get.ProvinceProto;

import java.util.List;

import androidx.lifecycle.MediatorLiveData;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:04 PM
 */
public class ApiService {
    private OkHttp okHttp;

    public ApiService() {
        okHttp = OkHttp.getInstance();
    }

    private void execute(Proto proto) {
        proto.execute(okHttp);
    }

    public void province(MediatorLiveData<Result<List<Province>>> liveData) {
        execute(new ProvinceProto(liveData));
    }
}

