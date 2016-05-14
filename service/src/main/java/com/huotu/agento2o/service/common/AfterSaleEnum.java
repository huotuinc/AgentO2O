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
 * Created by allan on 12/31/15.
 */
public interface AfterSaleEnum {
    /**
     * 售后状态
     */
    enum AfterSaleStatus implements ICommonEnum {
        APPLYING(0, "售后申请中"),
        REFUNDING(1, "退款中"),
        AFTER_SALE_REFUSED(2, "拒绝退款"),
        REFUND_SUCCESS(3, "退款成功"),
        CANCELED(4, "售后已取消"),
        WAITING_BUYER_RETURN(5, "等待买家退货"),
        WAITING_FOR_CONFIRM(6, "买家退货等待确认");
        private Integer code;
        private String value;

        AfterSaleStatus(Integer code, String value) {
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
     * 协商状态
     */
    enum AfterItemsStatus implements ICommonEnum {
        APPLYING(0, "申请中"),
        WAITING_BUYER_RETURN(1, "等待买家退货"),
        WAITING_FOR_CONFIRM(2, "买家退货等待确认"),
        REFUNDING_FINISH(3, "退款完成"),
        AFTER_SALE_REFUSED(4, "拒绝售后"),
        CANCELED(5, "已取消"),
        REFUNDING(6, "退款中"),
        BUYER_MSG(7, "买家留言");

        private Integer code;
        private String value;

        AfterItemsStatus(Integer code, String value) {
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
     * 申请方式
     */
    enum AfterSaleType implements ICommonEnum {
        UN_SELECT(-1, "未选择"),
        REFUND(0, "退款"),
        RETURN_AND_REFUND(1, "退款并退货");

        private Integer code;
        private String value;

        AfterSaleType(Integer code, String value) {
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

    enum AfterSalesReason implements ICommonEnum {
        UN_SELECT(-1, "未选择"),
        ALL_AGREE(0, "跟卖家协商双方同意"),
        WRONG_BUYING(1, "买错不想要了"),
        GOOD_PROBLEM(2, "商品质量有问题"),
        NEVER_RECEIVED(3, "没有收到货"),
        ONTER_REASON(4, "其他原因");

        private Integer code;
        private String value;

        AfterSalesReason(Integer code, String value) {
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

    enum AfterSalesIsLogis implements ICommonEnum {
        OTHERS(0,"其他"),
        LOGISTICS(1,"物流"),
        MESSAGE(2,"留言"),
        REFUND(3,"退款");

        private Integer code;
        private String value;

        AfterSalesIsLogis(Integer code, String value) {
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
