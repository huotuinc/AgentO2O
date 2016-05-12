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
 * 采购单相关状态枚举
 * Created by helloztt on 2016/5/12.
 */
public interface PurchaseEnum {
    /**
     * 采购单审核状态
     */
    enum OrderStatus implements ICommonEnum{
        CHECKING(0,"待审核"),
        CHECKED(1,"审核通过"),
        RETURNED(2,"审核不通过");
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
    /**
     * 配送方式
     */
    enum SendmentStatus implements ICommonEnum{
        HOME_DELIVERY(0,"送货上门"),
        EXPRESS(1,"物流快递");
        private Integer code;
        private String value;

        SendmentStatus(Integer code, String value) {
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
     * 发票类型
     */
    enum TaxType implements ICommonEnum{
        NONE(0,"不开发票"),
        NORMAL(1,"普通发票"),
        TAX(2,"增值税发票");
        private Integer code;
        private String value;

        TaxType(Integer code, String value) {
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
    enum ShipStatus implements ICommonEnum{
        NOT_DELIVER(0, "未发货"),
        DELIVERED(1, "已发货");
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
     * 付款状态
     */
    enum PayStatus implements ICommonEnum{
        NOT_PAYED(0, "未支付"),
        PAYED(1, "已支付");
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


}
