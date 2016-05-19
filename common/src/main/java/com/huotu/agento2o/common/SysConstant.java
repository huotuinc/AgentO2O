package com.huotu.agento2o.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by helloztt on 2016/5/9.
 */
@Component
public class SysConstant {
    public static String COOKIE_DOMAIN;
    public static final String ORDER_BATCH_DELIVER_SHEET_NAME = "OrderToDelivery";
    public static String[] SHOP_EXPORT_HEADER = {
            "门店名称",
            "门店所在区域",
            "用户名",
            "联系人",
            "手机号码",
            "门店审核状态",
            "账号状态"
    };

    @Autowired
    private void initConstant(Environment environment) {
        COOKIE_DOMAIN = environment.getProperty("cookie.domain", ".huobantest.com");
    }
}
