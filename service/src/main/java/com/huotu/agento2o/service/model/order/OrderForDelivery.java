/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.model.order;

import com.alibaba.fastjson.annotation.JSONField;
import lombok.Getter;
import lombok.Setter;

/**
 * Created by allan on 3/25/16.
 */
@Setter
@Getter
public class OrderForDelivery {
    @JSONField(name = "OrderNo")
    private String orderNo;
    @JSONField(name = "LogiName")
    private String logiName;
    @JSONField(name = "LogiNo")
    private String logiNo;
    @JSONField(name = "LogiMoney")
    private double logiMoney;
    @JSONField(name = "LogiCode")
    private String logiCode;
    @JSONField(name = "Remark")
    private String remark;
    @JSONField(name = "AgentShopId")
    private Integer agentShopId;
}
