/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.common;


import com.huotu.agento2o.common.ienum.ICommonEnum;

/**
 * Created by admin on 2015/11/30.
 */
public interface InvoiceEnum {

    enum InvoiceStatus implements ICommonEnum {
        NOT_APPLY(0, "未申请"),
        APPLIED(1, "申请中"),
        INVOICED(2, "已开票"),
        RETURNED(3, "已拒绝");

        private Integer code;
        private String value;

        InvoiceStatus(Integer code, String value) {
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

    enum InvoiceTypeStatus implements ICommonEnum {
        NORMALINVOICE(0, "普通发票"),
        TAXINVOICE(1, "增值税发票");
        private Integer code;
        private String value;

        InvoiceTypeStatus(Integer code, String value) {
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
