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
    public static String AGENT_KEY = "1232228433";

    @Autowired
    private void initConstant(Environment environment) {
        COOKIE_DOMAIN = environment.getProperty("cookie.domain", ".huobantest.com");
        HUOBANMALL_PUSH_URL = environment.getProperty("huobanmall.pushUrl", "http://mallapi.pdmall.com");
        HUOBANMALL_RESOURCE_HOST = environment.getProperty("huobanmall.resourceUrl", "http://manager.pdmall.com");
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
            "门店备注",
            "自定义字段"
    };

    public static String[] AGENT_ORDER_EXPORT_HEADER = {
            "采购单号",
            "货品名称",
            "采购方（代理商/门店）",
            "发货方",
            "下单时间",
            "支付时间",
            "总额",
            "运费",
            "审核状态",
            "付款状态",
            "发货状态",
            "收货人",
            "联系方式",
            "收货地址",
            "配送方式",
            "发票类型",
            "发票抬头",
            "发票内容",
            "纳税人识别码",
            "开户银行名称",
            "开户银行账号",
            "审核备注",
            "采购单备注",
            "发货方备注",
            "状态"
    };
    public static String[] RETURNED_ORDER_EXPORT_HEADER = {
            "采购单号",
            "货品名称",
            "退货方（代理商/门店）",
            "上级进货方",
            "创建时间",
            "支付时间",
            "总额",
            "运费",
            "审核状态",
            "付款状态",
            "发货状态",
            "配送方式",
            "审核备注",
            "退货单备注",
            "上级备注",
            "状态"
    };
    public static String[] SHOP_EXPORT_HEADER = {
            "登录名",
            "门店名称",
            "门店所在区域",
            "地址",
            "经度",
            "纬度",
            "联系人",
            "手机号码",
            "电话号码",
            "E-mail",
            "上级代理商",
//            "客服电话",
//            "售后电话",
//            "售后QQ",
            "备注",
            "审核备注",
            "门店审核状态",
            "账号状态"
    };

    public static String[] AGENT_EXPORT_HEADER = {
            "代理商名称",
            "用户名",
            "联系人",
            "手机号码",
            "电话号码",
            "E-mail",
            "代理商所在区域",
            "地址",
            "代理商等級",
            "账号状态",
            "备注",
            "创建时间"
    };
}
