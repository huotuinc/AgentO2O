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
 * Created by helloztt on 2016/3/24.
 */
public interface SettlementEnum {
    //结算单状态
    enum SettlementStatusEnum implements ICommonEnum {
        CHECKING(0, "核实中"),
        READY_SETTLE(1, "待结算"),
        SETTLED(2, "已结算");

        private Integer code;
        private String value;

        SettlementStatusEnum(Integer code, String value) {
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

    //结算单审核状态
    enum SettlementCheckStatus implements ICommonEnum {
        NOT_CHECK(0, "待审核"),
        CHECKED(1, "审核通过"),
        RETURNED(2, "审核不通过");

        private Integer code;
        private String value;

        SettlementCheckStatus(Integer code, String value) {
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
