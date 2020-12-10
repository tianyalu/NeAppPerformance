package com.sty.ne.appperformance.net.common;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:11 PM
 */
public class ProtoCode {

    /**
     * 请求失败，本地错误码
     */
    public static final int NET_ERROR = -1;

    /**
     * 成功各种异常，本地错误码（可以分小类）
     */
    public static final int EXCEPTION = -2;

    /**
     * 成功
     */
    public static final int SUCCESS = 200;

    /**
     * 参数错误
     */
    public static final int PARAMS_ERROR = 400;

    /**
     * 未授权
     */
    public static final int UNAUTHORIZATION = 401;

    /**
     * 用户被禁用
     */
    public static final int FORBIDDEN = 402;

    /**
     * 已授权，被禁止
     */
    public static final int BLOCKED = 403;

    /**
     * 资源不存在
     */
    public static final int NOT_FOUND = 404;

    /**
     * 资源已存在
     */
    public static final int EXIST = 409;

    /**
     * 服务器错误
     */
    public static final int SERVER_ERROR = 500;

    /**
     * 设备已被封禁
     */
    public static final int DEVICE_FORBIDEN = 412;
}
