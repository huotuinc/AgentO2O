/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.model.purchase;

import lombok.Data;

/**
 * Created by allan on 3/25/16.
 */
@Data
public class ReturnOrderDeliveryInfo {
    private String orderId;
    private String logiName;
    private String logiNo;
    private String logiCode;
    private String remark;
    private double freight;
    private String sendItems;
    private String shipName;
    private String shipMobile;
    private String shipTel;
    private String shipAddr;
}
