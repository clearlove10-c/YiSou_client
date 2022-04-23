package com.smlz.yisounews.entity;

public class HttpResult {
    private int code;
    private String msg;
    private Object data;

    public HttpResult(int code, String msg, Object data) {
        this.code = code;
        this.msg = msg;
        this.data = data;
    }

    public HttpResult(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public HttpResult() {
    }

    public int getCode() {
        return code;
    }

    public String getMsg() {
        return msg;
    }

    public Object getData() {
        return data;
    }

    @Override
    public String toString() {
        return "HttpResult{" +
                "code=" + code +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
