/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.searchable;

import lombok.Data;

/**
 * Created by WenbinChen on 2015/10/30 16:34.
 */
@Data
public class DeliverySearcher {
    private String username;
    private String deliveryNo;
    private String orderId;
    private String beginTime;
    private String endTime;
    private Integer agentId;
    private Integer shipMode = -1;
}
