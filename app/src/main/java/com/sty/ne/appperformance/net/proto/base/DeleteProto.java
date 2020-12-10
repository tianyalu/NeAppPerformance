package com.sty.ne.appperformance.net.proto.base;


import com.sty.ne.appperformance.net.okhttp.OkHttp;

import androidx.lifecycle.MediatorLiveData;

/**
 */
public abstract class DeleteProto<T> extends Proto<T> {

    public DeleteProto(MediatorLiveData liveData) {
        super(liveData);
    }

    @Override
    public void execute(OkHttp okHttp) {
        super.execute(okHttp);
        okHttp.httpDelete(getUrl(), getBody(), getHeader(), getCallback());
    }
}
