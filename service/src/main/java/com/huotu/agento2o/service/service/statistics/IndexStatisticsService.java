/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.statistics;

import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.model.statistics.IndexStatistics;

import java.util.Date;

/**
 * Created by helloztt on 2016/5/10.
 */
public interface IndexStatisticsService {

    /**
     * 按日期统计采购单数量（代理商）
     * @param agentId
     * @param start
     * @param end
     * @return
     */
    int purchaseOrderCountByDate(int agentId, Date start,Date end);

    /**
     * 按日期统计已付款未发货采购单数量
     * @param agentId
     * @param payStatus
     * @param shipStatus
     * @param start
     * @param end
     * @return
     */
    int unDeliveryPurchaseOrderCountByDate(int agentId, PurchaseEnum.PayStatus payStatus,PurchaseEnum.ShipStatus shipStatus,Date start,Date end);

    /**
     * 待审核采购单数
     * @param agentId
     * @return
     */
    int checkingPurchaseOrderCount(int agentId);

    IndexStatistics orderStatistics(Author author);
}
