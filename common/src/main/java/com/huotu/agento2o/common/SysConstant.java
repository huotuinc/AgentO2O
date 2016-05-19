package com.huotu.agento2o.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by helloztt on 2016/5/9.
 */
@Component
public class SysConstant {
    public static final String ORDER_BATCH_DELIVER_SHEET_NAME = "OrderToDelivery";
    public static String COOKIE_DOMAIN;
    public static String HUOBANMALL_PUSH_URL;
    public static String HUOBANMALL_RESOURCE_HOST;
    public static String AGENT_KEY;

    @Autowired
    private void initConstant(Environment environment) {
        COOKIE_DOMAIN = environment.getProperty("cookie.domain", ".huobantest.com");
        HUOBANMALL_PUSH_URL = environment.getProperty("huobanmall.pushUrl", "http://192.168.1.70:31105");
        HUOBANMALL_RESOURCE_HOST = environment.getProperty("huobanmall.resourceUrl", "http://192.168.1.70:3152");
    }

    public static String[] ORDER_EXPORT_HEADER = {
            "订单号",
            "订单名称",
            "状态",
            "货品数量",
            "下单时间",
            "支付时间",
            "支付状态",
            "发货状态",
            "订单金额",
            "运费",
            "优惠金额",
            "收货人",
            "收货人手机",
            "收货人地址",
            "货品列表",
            "买家备注",
            "客服备注",
            "代理商备注",
            "自定义字段"
    };
    public static String[] SHOP_EXPORT_HEADER = {
            "门店名称",
            "门店所在区域",
            "用户名",
            "联系人",
            "手机号码",
            "门店审核状态",
            "账号状态"
    };
}
