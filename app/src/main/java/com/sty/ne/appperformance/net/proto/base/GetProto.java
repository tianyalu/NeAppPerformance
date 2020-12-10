package com.sty.ne.appperformance.net.proto.base;

import android.net.Uri;


import com.sty.ne.appperformance.net.okhttp.OkHttp;

import java.util.Map;

import androidx.lifecycle.MediatorLiveData;

/**
 */
public abstract class GetProto<T> extends Proto<T> {

    public GetProto(MediatorLiveData liveData) {
        super(liveData);
    }

    @Override
    public void execute(OkHttp okHttp) {
        super.execute(okHttp);
        String url = getUrl();
        Map<String, String> body = getBody();
        if (body != null) {
            Uri.Builder builder = Uri.parse(url).buildUpon();
            for (Map.Entry<String, String> entry : body.entrySet()) {
                builder.appendQueryParameter(entry.getKey(), entry.getValue());
            }

            url = builder.build().toString();
        }

        okHttp.httpGet(url, getHeader(), getCallback());
    }

    @Override
    protected Map getBody() {
        return null;
    }
}
