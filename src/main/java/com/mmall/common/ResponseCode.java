package com.mmall.common;

/**
 * Created by flnnf on 2017/6/27.
 * 返回消息的常量类
 */
public enum ResponseCode {

    SUCCESS(0,"SUCCESS"),

    ERROR(1,"ERROR"),

    NEED_LOGIN(2,"NEED_LOGIN"),

    ILLEGAL_ARGUMENT(3,"ILLEGAL_ARGUMENT");

    private final int code;

    private final String desc;

    ResponseCode(int code, String desc){
        this.code = code;
        this.desc = desc;
    }

    public int getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
