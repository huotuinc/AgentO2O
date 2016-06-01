package com.huotu.agento2o.service.common;

import com.huotu.agento2o.common.ienum.ICommonEnum;

/**
 * Created by helloztt on 2016/5/9.
 */
public enum AuthorityEnum implements ICommonEnum {
    /**
     * 以下为角色
     */
    ROLE_MANAGER("ROLE_ROOT", "超级管理员"),
    ROLE_AGENT("ROLE_AGENT", "代理商"),
    ROLE_SHOP("ROLE_SHOP", "门店"),
    /**
     * 以下为权限
     */
    ORDER("ORDER", "订单管理"),
    PURCHASE("PURCHASE", "我的采购管理"),
    AGENT_PURCHASE("AGENT_PURCHASE", "下级采购管理"),
    SHOP_MANAGE("SHOP_MANAGER","门店管理"),
    BASE_DATA("BASE_DATA", "基本设置");
    private String code;
    private String value;

    AuthorityEnum(String code, String value) {
        this.code = code;
        this.value = value;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
