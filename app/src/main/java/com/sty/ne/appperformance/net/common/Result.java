package com.sty.ne.appperformance.net.common;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:09 PM
 */
/**
 * 一层协议错误，一层http错误
 */
public class Result<T> {

    private final ProtoResult protoResult;

    private final T data;

    public Result(ProtoResult result) {
        this(result, null);
    }

    public Result(ProtoResult result, T data) {
        this.protoResult = result;
        this.data = data;
    }

    public static <T> Result<T> asSuccess(T value) {
        return new Result<>(new ProtoResult(ResCode.SUCCESS.getValue()), value);
    }

    public static <T> Result<T> asFailed(int code, String err) {
        return new Result<>(new ProtoResult(code, err));
    }

    public static <T> Result<T> asFailed(Result source) {
        return asFailed(source.getProtoCode(), source.getError());
    }

    public boolean isSuccess() {
        return protoResult.getResCode() == ResCode.SUCCESS.getValue();
    }

    public int getProtoCode() {
        return protoResult != null ? protoResult.getResCode() : ResCode.SUCCESS.getValue();
    }

    public String getError() {
        return protoResult != null ? protoResult.getError() : "";
    }

    public ProtoResult getProtoResult() {
        return protoResult;
    }

    public T getData() {
        return data;
    }
}
