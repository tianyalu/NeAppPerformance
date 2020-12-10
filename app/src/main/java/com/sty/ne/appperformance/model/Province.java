package com.sty.ne.appperformance.model;

import com.google.gson.annotations.SerializedName;
import com.google.gson.reflect.TypeToken;

import java.util.List;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 8:43 PM
 */
public class Province {
    public static final TypeToken<List<Province>> LIST_TYPE_TOKEN = new TypeToken<List<Province>>(){};

    @SerializedName("pid")
    private int pid;
    @SerializedName("id")
    private int id;
    @SerializedName("city")
    private String city;
    @SerializedName("en")
    private String en;
    @SerializedName("province")
    private String province;

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public String getEn() {
        return en;
    }

    public void setEn(String en) {
        this.en = en;
    }

    public String getProvince() {
        return province;
    }

    public void setProvince(String province) {
        this.province = province;
    }
}
