package com.sty.ne.appperformance.net.proto.base;


import com.sty.ne.appperformance.net.okhttp.OkHttp;

import androidx.lifecycle.MediatorLiveData;

/**
 */
public abstract class PostProto<T> extends Proto<T> {

    public interface ProtoType {

        int FORM = 0;

        int MULTI = 1;
    }

    protected int getProtoType() {
        return ProtoType.FORM;
    }

    public PostProto(MediatorLiveData liveData) {
        super(liveData);
    }

    @Override
    public void execute(OkHttp okHttp) {
        super.execute(okHttp);
        if (getProtoType() == ProtoType.FORM) {
            okHttp.httpPost(getUrl(), getBody(), getHeader(), getCallback());
        } else if (getProtoType() == ProtoType.MULTI){
            okHttp.httpMulti(getUrl(), getRequestBody(), getHeader(), getCallback());
        }
    }
}
