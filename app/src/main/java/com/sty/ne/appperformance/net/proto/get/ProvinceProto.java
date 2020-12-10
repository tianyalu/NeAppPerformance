package com.sty.ne.appperformance.net.proto.get;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.sty.ne.appperformance.model.Province;
import com.sty.ne.appperformance.net.proto.base.GetProto;
import com.sty.ne.appperformance.util.JsonUtil;

import java.util.Collections;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.MediatorLiveData;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:13 PM
 */
public class ProvinceProto extends GetProto<List<Province>> {
    public ProvinceProto(MediatorLiveData liveData) {
        super(liveData);
    }

    @Override
    protected String getApi() {
        return "type=province";
    }
    @Override
    protected List<Province> getRespData(JsonElement data) {
        if (data instanceof JsonArray) {
            return JsonUtil.fromJson((JsonArray) data, Province.LIST_TYPE_TOKEN);
        }
        return Collections.emptyList();
    }

    @NonNull
    @Override
    public String dataKey() {
        return "provincelist";
    }
}
