package com.huotu.agento2o.service.common;

/**
 * Created by helloztt on 2016/5/7.
 */
public enum RoleTypeEnum {
    AGENT(2,"代理登录"),
    SHOP(3,"门店登录");
//    OPERATOR(2,"操作员登录");
    private int code;
    private String name;

    RoleTypeEnum(int code, String name) {
        this.code = code;
        this.name = name;
    }

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
