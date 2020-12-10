package com.sty.ne.appperformance.net.proto.base;


import com.sty.ne.appperformance.net.okhttp.OkHttp;

import androidx.lifecycle.MediatorLiveData;

/**
 */
public abstract class PutProto<T> extends Proto<T> {

    public PutProto(MediatorLiveData liveData) {
        super(liveData);
    }


    @Override
    public void execute(OkHttp okHttp) {
        okHttp.httpPut(getUrl(), getBody(), getHeader(), getCallback());
    }
}
