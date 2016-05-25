/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.model.order;

import lombok.Data;

/**
 * Created by WenbinChen on 2015/10/23 14:36.
 */
@Data
public class OrderExportModel {

    private int agentId;
    private String txtOrderId;
    private String txtShipName;
    private String txtShipMobile;
    private String ddlShipStatus;
    private String ddlPayStatus;
    private String ddlOrderByField;
    private String raSortType_0;
    private String txtBeginTime;
    private String txtEndTime;
    private String txtEndPaytime;
    private String txtBeginPaytime;
    private String txtBeginPage;
    private String txtEndPage;
    private String ddlShipMode;


}
