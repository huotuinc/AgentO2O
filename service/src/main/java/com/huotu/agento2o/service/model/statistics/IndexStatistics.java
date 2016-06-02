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
    //true:代理商;false:门店
    private boolean isAgent;
    //订单数
    private Integer todayOrderCount;
    private Integer yesterdayOrderCount;

    //采购单相关
    //我的采购单数
    private Integer todayPurchaseOrderCount;
    private Integer yesterdayPurchaseOrderCount;
    //下级采购单数
    private Integer todaySubPurchaseOrderCount;
    private Integer yesterdaySubPurchaseOrderCount;
    //退货单相关
    //我的退货单数
    private Integer todayReturnedOrderCount;
    private Integer yesterdayReturnedOrderCount;
    //下级退货单数
    private Integer todaySubReturnedOrderCount;
    private Integer yesterdaySubReturnedOrderCount;

    //待处理
    //待发货订单
    private Integer toDeliveryOrderCount;
    //待审核采购单(代理商)
    private Integer toCheckPurchaseOrderCount;
    //待发货采购单（代理商）
    private Integer toDeliveryPurchaseOrderCount;
    //待确认收货采购单（代理商/门店）
    private Integer toReceivePurchaseOrderCount;
    //待审核退货单(代理商)
    private Integer toCheckReturnedOrderCount;
    //待发货退货单(代理商/门店)
    private Integer toDeliveryReturnedOrderCount;
    //待确认收货退货单(代理商)
    private Integer toReceiveReturnedOrderCount;

    //库存预警
    private Integer productNotifyCount;
}
