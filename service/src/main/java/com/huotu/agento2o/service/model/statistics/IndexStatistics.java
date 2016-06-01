/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.model.statistics;

import lombok.Data;

/**
 * index界面显示的业务数据
 * Created by helloztt on 2016/5/10.
 */
@Data
public class IndexStatistics {
    //今日
    //采购单相关
    private Integer todayPurchaseOrderCount;
    private Integer todayUnDeliveryPurchaseOrderCount;

    //昨日
    //采购单相关
    private Integer yesterdayPurchaseOrderCount;
    private Integer yesterdayUnDeliveryPurchaseOrderCount;

    //待处理
    //待审核采购单
    private Integer toCheckPurchaseOrderCount;
}
