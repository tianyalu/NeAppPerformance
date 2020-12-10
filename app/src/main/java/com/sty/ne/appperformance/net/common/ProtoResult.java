package com.sty.ne.appperformance.net.common;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:10 PM
 */
public class ProtoResult {

    private int resCode;

    private String error;

    public ProtoResult(int resCode) {
        this.resCode = resCode;
    }

    public ProtoResult(int resCode, String error) {
        this.resCode = resCode;
        this.error = error;
    }


    public int getResCode() {
        return resCode;
    }

    public void setResCode(int resCode) {
        this.resCode = resCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }

    public static ProtoResult result(int resCode) {
        return new ProtoResult(resCode);
    }

    public static ProtoResult result(int resCode, String error) {
        return new ProtoResult(resCode, error);
    }
}
