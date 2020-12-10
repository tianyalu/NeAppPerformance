package com.sty.ne.appperformance.net.common;

/**
 * @Author: tian
 * @UpdateDate: 2020/12/9 9:11 PM
 */
public enum ResCode {
    CANCEL(-5, "主动取消"),

    TIMEOUT(-4, "Time out"),

    NET_UNAVAILABLE(-3, "网络罢工，请检查您的网络设置"),

    CLIENT_EXCEPTION(-2, "Client Exception"),

    CLIENT_ERROR(-1, "网络罢工，请检查您的网络设置"),

    NO_RESPONSE(0, "你已被禁言"),

    SUCCESS(200, "SUCCESS"),

    BAD_PACKET(400, "Bad Packet"),

    NO_AUTH(401, "No Auth"),

    TARGET_NOT_EXIST(404, "对方不存在"),

    REQUIRED_PARAMETER_MISSING(414, "Required param missing"),

    UNKNOWN(500, "网络罢工，请检查您的网络设置"),

    BAD_GATEWAY(502, "服务器罢工"),

    UPSTREAM_UNAVAILABLE(503, "网络罢工，请检查您的网络设置"),

    NO_SUCH_DESTINATION(504, "服务器处理错误"),
    ;

    ResCode(int value, String message) {
        this.value = value;
        this.message = message;
    }

    private int value;

    private String message;

    public int getValue() {
        return value;
    }

    public String getMessage() {
        return message;
    }

    public static ResCode typeOfValue(int value, ResCode def) {
        for (ResCode code : ResCode.values()) {
            if (code.getValue() == value) {
                return code;
            }
        }
        return def;
    }

    public static ResCode typeOfValue(int value) {
        return typeOfValue(value, CLIENT_ERROR);
    }

    public static String msgOfValue(int value, String def) {
        ResCode resCode = typeOfValue(value, NO_RESPONSE);
        return resCode == NO_RESPONSE ? def : resCode.message;
    }
}
