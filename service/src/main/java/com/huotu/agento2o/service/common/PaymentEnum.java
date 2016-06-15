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
 * Created by helloztt on 2016/5/13.
 */
public interface PaymentEnum {
    /**
     * 支付状态
     */
    enum PayStatus implements ICommonEnum {
        SUCCESS(1, "支付成功"),
        FAILED(2, "支付失败"),
        CANCEL(3, "未支付"),
        ERROR(4, "处理异常"),
        INVALID(5, "非法参数"),
        PROGRESS(6, "处理中"),
        TIMEOUT(7,"超时"),
        READY(8,"准备中");

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
