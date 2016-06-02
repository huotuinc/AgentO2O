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

import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.model.statistics.IndexStatistics;

import java.util.Date;

/**
 * Created by helloztt on 2016/5/10.
 */
public interface IndexStatisticsService {

    /**
     * 按日期统计订单数量（代理商/门店）
     *
     * @param author
     * @param start
     * @param end
     * @return
     */
    int orderCountByDate(Author author, Date start, Date end);

    /**
     * 按日期统计采购单数量（代理商/门店）
     *
     * @param authorId
     * @param start
     * @param end
     * @return
     */
    int purchaseOrderCountByDate(int authorId, Date start, Date end);

    /**
     * 按日期统计下级采购单数量(代理商)
     * @param agentId
     * @param start
     * @param end
     * @return
     */
    int subPurchaseOrderCountByDate(int agentId,Date start,Date end);

    /**
     * 按日期统计退货单数（代理商/门店）
     * @param authorId
     * @param start
     * @param end
     * @return
     */
    int returnedOrderCountByDate(int authorId,Date start,Date end);

    /**
     * 按日期统计下级退货单数（代理商）
     * @param agentId
     * @param start
     * @param end
     * @return
     */
    int subReturnedOrderCountByDate(int agentId,Date start,Date end);


    /**
     * 按日期统计已付款未发货订单数量（门店）
     *
     * @param shopId
     * @return
     */
    int unDeliveryOrderCount(int shopId);

    /**
     * 统计下级已付款未发货采购单数量（代理商）
     *
     * @param agentId
     * @return
     */
    int unDeliveryPurchaseOrderCount(int agentId);

    /**
     * 统计待退货的退货单（代理商/门店）
     * @param agentId
     * @return
     */
    int unDeliveryReturnedOrderCount(int agentId);

    /**
     * 待审核采购单数（代理商）
     *
     * @param agentId
     * @return
     */
    int checkingPurchaseOrderCount(int agentId);

    /**
     * 待审核采购退货单数（代理商）
     *
     * @param agentId
     * @return
     */
    int checkingReturnedOrderCount(int agentId);

    /**
     * 待确认收货采购单数（代理商/门店）
     * @param authorId
     * @return
     */
    int toReceivePurchaseOrderCount(int authorId);

    /**
     * 待确认收货退货单（代理商）
     * @param authorId
     * @return
     */
    int toReceiveReturnedOrderCount(int authorId);

    IndexStatistics orderStatistics(Author author);
}
