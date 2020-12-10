package com.sty.ne.appperformance.net.proto.base;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sty.ne.appperformance.net.common.ProtoCode;
import com.sty.ne.appperformance.net.common.ProtoResult;
import com.sty.ne.appperformance.net.common.ResCode;
import com.sty.ne.appperformance.net.common.Result;
import com.sty.ne.appperformance.net.config.Servers;
import com.sty.ne.appperformance.net.okhttp.OkHttp;
import com.sty.ne.appperformance.util.LogUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import androidx.annotation.NonNull;
import androidx.collection.ArrayMap;
import androidx.lifecycle.MediatorLiveData;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 *
 */
public abstract class Proto<T> {

    private static final String TAG = "Proto";

    private MediatorLiveData<Result<T>> liveData;

    public Proto(MediatorLiveData<Result<T>> liveData) {
        this.liveData = liveData;
    }

    protected HashMap getForm() {
        return new HashMap();
    }

    protected String getUrl() {
        String url = getBaseUrl() + getApi();
        return url;
    }

    protected String getBaseUrl() {
        return Servers.getBaseUrl();
    }

    protected abstract String getApi();

    protected abstract Map getBody();

    protected abstract T getRespData(JsonElement data);

    public void execute(OkHttp okHttp) {
        LogUtil.d(TAG, "request=" + getUrl());
    }

    protected RequestBody getRequestBody() {
        return null;
    }

    final protected Map<String, String> getHeader() {
        Map<String, String> header = new HashMap<>();
        onHeaderExtra(header);
        return header;
    }

    protected void onHeaderExtra(Map<String, String> header) {
        // nop
    }

    protected Callback getCallback() {
        return new Callback() {

            @Override
            public void onFailure(Call call, IOException e) {
                Result result = onFail(ProtoCode.NET_ERROR, "网络罢工，请检查您的网络设置");
                setResult(result);
            }

            @Override
            public void onResponse(Call call, Response response) {
                try {
                    Result result = onOK(response.code(), response.body().string());
                    setResult(result);
                } catch (Exception e) {
                    Result result = onFail(ProtoCode.EXCEPTION, "json parse");
                    setResult(result);
                    e.printStackTrace();
                }
            }
        };
    }

    protected int parseCode(JsonObject object) {
        if (object.has("code")) {
            return object.get("code").getAsInt();
        }
        return -1;
    }

    protected JsonElement parseData(JsonObject object) {
        return object.get(dataKey());
    }

    private Result onOK(int code, String string) {
        LogUtil.d(TAG, "response=" + getUrl());
        if (code == 200) {
            JsonElement jsonElement = new JsonParser().parse(string);
            LogUtil.d(TAG, jsonElement != null ? jsonElement.toString() : string);
            if (jsonElement != null && jsonElement.isJsonObject()) {
                JsonObject object = jsonElement.getAsJsonObject();
                int status = parseCode(object);
                if (status == ResCode.SUCCESS.getValue()) {
                    JsonElement data = parseData(object);
                    return transformOk(status, data);
                } else {
                    return transformErr(status, object);
                }
            } else {
                return new Result(ProtoResult.result(ResCode.CLIENT_EXCEPTION.getValue()));
            }
        } else {
            return new Result(ProtoResult.result(code));
        }
    }

    protected Result transformOk(int status, JsonElement data) {
        return new Result(ProtoResult.result(status), getRespData(data));
    }

    protected Result transformErr(int status, JsonObject data) {
        JsonElement element = data.get("errmsg");
        return new Result(new ProtoResult(status, element != null ? element.getAsString() : ""));
    }

    private Result onFail(int code, String reason) {
        LogUtil.d(TAG, "response=" + getUrl() + " onFail=" + code + " reason=" + reason);
        return new Result(ProtoResult.result(code, reason));
    }

    @NonNull
    public String dataKey() {
        return "result";
    }

    protected void setResult(Result<T> result) {
        liveData.postValue(result);
    }

    public static MapBuilder map(int size) {
        return new MapBuilder(size);
    }

    public static class MapBuilder {

        private Map<String, String> map;

        public MapBuilder(int size) {
            map = new ArrayMap<>(size);
        }

        public MapBuilder() {
            map = new ArrayMap<>();
        }

        public MapBuilder put(String key, String value) {
            map.put(key, value);
            return this;
        }

        public Map<String, String> build() {
            return map;
        }
    }


}
