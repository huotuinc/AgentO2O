/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.common;

import com.huotu.agento2o.common.ienum.ICommonEnum;

/**
 * Created by liual on 2015-11-03.
 */
public interface OrderEnum {
    /**
     * 支付状态
     */
    enum PayStatus implements ICommonEnum {
        NOT_PAYED(0, "未支付"),
        PAYED(1, "已支付"),
        PARTY_PAYED(3, "部分付款"),
        PARTY_REFUND(4, "部分退款"),
        ALL_REFUND(5, "全额退款"),
        REFUNDING(6, "售后退款中");

        private Integer code;
        private String value;

        PayStatus(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 支付方式
     */
    enum PaymentOptions implements ICommonEnum {
        ALIPAY_MOBILE(1, "支付宝手机网页即时到账"),
        WEIXINPAY(2, "微信支付"),
        ALIPAY_PC(7, "支付宝网页即时到账"),
        REDEMPTION(8, "提货券"),
        WEIXINPAY_V3(9, "微信支付V3"),
        UNIONPAY(100, "银联在线支付"),
        BAIDUPAY(200, "百度钱包"),
        WEIXINPAY_APP(300, "微信APP支付"),
        WEIFUTONG(500, "威富通"),
        HUIJINTONG(600, "汇金宝");

        private Integer code;
        private String value;

        PaymentOptions(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 发货状态
     */
    enum ShipStatus implements ICommonEnum {
        NOT_DELIVER(0, "未发货"),
        DELIVERED(1, "已发货"),
        PARTY_DELIVER(2, "部分发货"),
        PARTY_RETURN(3, "部分退货"),
        RETURNED(4, "已退货");
        private Integer code;
        private String value;

        ShipStatus(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    /**
     * 发货退货单类型
     */
    enum DeliveryType implements ICommonEnum{
        DEVERY("delivery","发货单"),
        RETURN("return","退货单");
        private String code;
        private String value;

        DeliveryType(String code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    enum OrderStatus implements ICommonEnum {
        CLOSE(-1, "已关闭"),
        ACTIVE(0, "活动"),
        FINISH(1, "已完成");
        private Integer code;
        private String value;

        OrderStatus(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }

    enum OrderSourceType implements ICommonEnum {
        NORMAL(0, "普通"),
        CROWD_FUNDING(1, "重酬"),
        BARGAIN(2, "砍价"),
        GROUP_PURCHASE(3, "限时团购"),
        PIN_TUAN(4, "拼团"),
        SNATCH(5, "夺宝");
        private Integer code;
        private String value;

        OrderSourceType(Integer code, String value) {
            this.code = code;
            this.value = value;
        }

        @Override
        public Integer getCode() {
            return code;
        }

        public void setCode(Integer code) {
            this.code = code;
        }

        @Override
        public String getValue() {
            return value;
        }

        public void setValue(String value) {
            this.value = value;
        }
    }
}
