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
 * Created by ztt on 2015/12/2.
 */
public interface WithdrawEnum {

    /**
     * 提现申请状态
     */
    enum WithdrawEnumStatus implements ICommonEnum {
        APPLYING(0, "申请中"),
        PAYED(1, "已打款"),
        RETURNED(2, "已拒绝");
        private Integer code;
        private String value;

        WithdrawEnumStatus(Integer code, String value) {
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
