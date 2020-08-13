package com.powernode.utils;

public enum  ExceptionEnum {

    OK(200,"OK"),
    NOT_FOUND(404,"未找到资源"),
    LOGIN_ERROR(1200,"登陆失败"),
    LOGIN_ACCOUNT_FAILURE(200,"账号失效")
    ;

    private int code;
    private String msg;

    ExceptionEnum(int code, String msg) {
        this.code = code;
        this.msg = msg;
    }

    public int getCode() {
        return code;
    }


    public String getMsg() {
        return msg;
    }

}
