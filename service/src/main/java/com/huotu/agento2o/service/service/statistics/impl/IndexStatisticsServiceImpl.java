/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.statistics.impl;

import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.common.SettlementEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.model.statistics.IndexStatistics;
import com.huotu.agento2o.service.repository.order.MallOrderRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.repository.settlement.SettlementRepository;
import com.huotu.agento2o.service.service.statistics.IndexStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/10.
 */
@Service
public class IndexStatisticsServiceImpl implements IndexStatisticsService {
    @Autowired
    private MallOrderRepository orderRepository;
    @Autowired
    private AgentPurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private AgentReturnOrderRepository returnOrderRepository;
    @Autowired
    private AgentProductRepository agentProductRepository;
    @Autowired
    private SettlementRepository settlementRepository;

    @Override
    public int orderCountByDate(Author author, Date start, Date end) {
        List<OrderEnum.ShipMode> agentShopTypeList = new ArrayList<>();
        agentShopTypeList.add(OrderEnum.ShipMode.SHOP_DELIVERY);
        agentShopTypeList.add(OrderEnum.ShipMode.PLATFORM_DELIVERY);
        if (author instanceof Shop) {
            return orderRepository.countByShop_IdAndAgentShopTypeInAndShop_IdAndCreateTimeBetween(author.getId(), agentShopTypeList, start, end);
        } else {
            return orderRepository.countByShop_ParentAuthor_IdAndAgentShopTypeInAndCreateTimeBetween(author.getId(), agentShopTypeList, start, end);
        }
    }

    @Override
    public int purchaseOrderCountByDate(int authorId, Date start, Date end) {
        return purchaseOrderRepository.countByAuthor_IdAndCreateTimeBetweenAndDisabledFalse(authorId, start, end);
    }

    @Override
    public int subPurchaseOrderCountByDate(int agentId, Date start, Date end) {
        return purchaseOrderRepository.countByAuthor_ParentAuthor_IdAndCreateTimeBetweenAndDisabledFalse(agentId, start, end);
    }

    @Override
    public int returnedOrderCountByDate(int authorId, Date start, Date end) {
        return returnOrderRepository.countByAuthor_IdAndCreateTimeBetweenAndDisabledFalse(authorId, start, end);
    }

    @Override
    public int subReturnedOrderCountByDate(int agentId, Date start, Date end) {
        return returnOrderRepository.countByAuthor_ParentAuthor_IdAndCreateTimeBetweenAndDisabledFalse(agentId, start, end);
    }

    @Override
    public int unDeliveryOrderCount(int shopId) {
        return orderRepository.countByShop_IdAndPayStatusAndShipStatus(shopId, OrderEnum.PayStatus.PAYED, OrderEnum.ShipStatus.NOT_DELIVER);
    }

    @Override
    public int unDeliveryPurchaseOrderCount(int agentId) {
        return purchaseOrderRepository.countByAuthor_ParentAuthor_IdAndPayStatusAndShipStatusAndDisabledFalse(agentId, PurchaseEnum.PayStatus.PAYED, PurchaseEnum.ShipStatus.NOT_DELIVER);
    }

    @Override
    public int unDeliveryReturnedOrderCount(int agentId) {
        return returnOrderRepository.countByAuthor_IdAndStatusAndShipStatusAndDisabledFalse(agentId, PurchaseEnum.OrderStatus.CHECKED, PurchaseEnum.ShipStatus.NOT_DELIVER);
    }

    @Override
    public int checkingPurchaseOrderCount(int agentId) {
        return purchaseOrderRepository.countByAuthor_ParentAuthor_IdAndStatusAndDisabledFalse(agentId, PurchaseEnum.OrderStatus.CHECKING);
    }

    @Override
    public int checkingReturnedOrderCount(int agentId) {
        return returnOrderRepository.countByAuthor_ParentAuthor_IdAndStatusAndDisabledFalse(agentId, PurchaseEnum.OrderStatus.CHECKING);
    }

    @Override
    public int toReceivePurchaseOrderCount(int authorId) {
        return purchaseOrderRepository.countByAuthor_IdAndPayStatusAndShipStatusAndReceivedTimeIsNullAndDisabledFalse(authorId, PurchaseEnum.PayStatus.PAYED, PurchaseEnum.ShipStatus.DELIVERED);
    }

    @Override
    public int toReceiveReturnedOrderCount(int authorId) {
        return returnOrderRepository.countByAuthor_ParentAuthor_IdAndStatusAndShipStatusAndReceivedTimeIsNullAndDisabledFalse(authorId, PurchaseEnum.OrderStatus.CHECKED, PurchaseEnum.ShipStatus.DELIVERED);
    }

    @Override
    public int toCheckSettlementCount(int authorId) {
        return settlementRepository.countByShop_IdAndAuthorStatusAndCustomerStatusNot(authorId, SettlementEnum.SettlementCheckStatus.NOT_CHECK, SettlementEnum.SettlementCheckStatus.RETURNED);
    }

    public IndexStatistics orderStatistics(Author author) {
        IndexStatistics indexStatistics = new IndexStatistics();
        //今日
        LocalDate nowDate = LocalDate.now();
        Date todayStart = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.atStartOfDay());
        Date todayEnd = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.plusDays(1).atStartOfDay());
        //昨日
        Date yesterdayStart = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.minusDays(1).atStartOfDay());
        Date yesterdayEnd = todayStart;
        //代理商显示采购单信息
        if (author != null && author.getType() == Agent.class) {
            indexStatistics.setAgent(true);
            //今日下级采购单数，昨日下级采购单数
            indexStatistics.setTodaySubPurchaseOrderCount(subPurchaseOrderCountByDate(author.getId(), todayStart, todayEnd));
            indexStatistics.setYesterdaySubPurchaseOrderCount(subPurchaseOrderCountByDate(author.getId(), yesterdayStart, yesterdayEnd));

            //今日退货单数，昨日退货单数
            indexStatistics.setTodayReturnedOrderCount(returnedOrderCountByDate(author.getId(), todayStart, todayEnd));
            indexStatistics.setYesterdayReturnedOrderCount(returnedOrderCountByDate(author.getId(), yesterdayStart, yesterdayEnd));

            //待审核采购单数
            indexStatistics.setToCheckPurchaseOrderCount(checkingPurchaseOrderCount(author.getId()));
            //待发货采购单数
            indexStatistics.setToDeliveryPurchaseOrderCount(unDeliveryPurchaseOrderCount(author.getId()));

            //待审核退货单
            indexStatistics.setToCheckReturnedOrderCount(checkingReturnedOrderCount(author.getId()));
            indexStatistics.setToReceiveReturnedOrderCount(toReceiveReturnedOrderCount(author.getId()));
            indexStatistics.setProductNotifyCount(agentProductRepository.countByWarningAgentInfo(author.getId()));
        } else if (author != null && author.getType() ==  Shop.class) {
            indexStatistics.setAgent(false);
            indexStatistics.setToCheckSettlementCount(toCheckSettlementCount(author.getId()));
            indexStatistics.setProductNotifyCount(agentProductRepository.countByWarningShopInfo(author.getId()));
        }
        //订单相关
        //今日订单数，昨日订单数
        indexStatistics.setTodayOrderCount(orderCountByDate(author, todayStart, todayEnd));
        indexStatistics.setYesterdayOrderCount(orderCountByDate(author, yesterdayStart, yesterdayEnd));

        //今日我的采购单数，昨日我的采购单数
        indexStatistics.setTodayPurchaseOrderCount(purchaseOrderCountByDate(author.getId(), todayStart, todayEnd));
        indexStatistics.setYesterdayPurchaseOrderCount(purchaseOrderCountByDate(author.getId(), yesterdayStart, yesterdayEnd));

        //今日我的退货单数，昨日我的退货单数
        indexStatistics.setTodayReturnedOrderCount(returnedOrderCountByDate(author.getId(), todayStart, todayEnd));
        indexStatistics.setYesterdayReturnedOrderCount(returnedOrderCountByDate(author.getId(), yesterdayStart, yesterdayEnd));

        //待发货订单数
        indexStatistics.setToDeliveryOrderCount(unDeliveryOrderCount(author.getId()));
        //待确认收货采购单数
        indexStatistics.setToReceivePurchaseOrderCount(toReceivePurchaseOrderCount(author.getId()));
        indexStatistics.setToDeliveryReturnedOrderCount(unDeliveryReturnedOrderCount(author.getId()));
        return indexStatistics;
    }
}
