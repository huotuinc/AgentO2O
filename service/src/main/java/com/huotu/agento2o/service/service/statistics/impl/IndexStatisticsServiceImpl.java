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

import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.model.statistics.IndexStatistics;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderRepository;
import com.huotu.agento2o.service.service.statistics.IndexStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.convert.Jsr310Converters;
import org.springframework.data.jpa.convert.threeten.Jsr310JpaConverters;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.Date;

/**
 * Created by helloztt on 2016/5/10.
 */
@Service
public class IndexStatisticsServiceImpl implements IndexStatisticsService {
    @Autowired
    private AgentPurchaseOrderRepository purchaseOrderRepository;

    @Override
    public int purchaseOrderCountByDate(int agentId, Date start, Date end) {
        return purchaseOrderRepository.countByAuthor_IdAndCreateTimeBetween(agentId,start,end);
    }

    @Override
    public int unDeliveryPurchaseOrderCountByDate(int agentId, PurchaseEnum.PayStatus payStatus, PurchaseEnum.ShipStatus shipStatus, Date start, Date end) {
        return purchaseOrderRepository.countByAuthor_IdAndPayStatusAndShipStatusAndCreateTimeBetween(agentId,payStatus,shipStatus,start,end);
    }

    @Override
    public int checkingPurchaseOrderCount(int agentId) {
        return purchaseOrderRepository.countByAuthor_IdAndStatus(agentId, PurchaseEnum.OrderStatus.CHECKING);
    }

    public IndexStatistics orderStatistics(Author author) {
        IndexStatistics indexStatistics = new IndexStatistics();
        //今日
        LocalDate nowDate = LocalDate.now();
        Date todayStart = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.atStartOfDay());
        Date todayEnd = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.plusDays(1).atStartOfDay());
        //代理商显示采购单信息
        if(author instanceof Agent){
            indexStatistics.setTodayPurchaseOrderCount(purchaseOrderCountByDate(author.getId(),todayStart,todayEnd));
            indexStatistics.setTodayUnDeliveryPurchaseOrderCount(unDeliveryPurchaseOrderCountByDate(
                    author.getId(), PurchaseEnum.PayStatus.PAYED,PurchaseEnum.ShipStatus.NOT_DELIVER,todayStart,todayEnd
            ));
        }

        //昨日
        Date yesterdayStart = Jsr310Converters.LocalDateTimeToDateConverter.INSTANCE.convert(nowDate.minusDays(1).atStartOfDay());
        Date yesterdayEnd = todayStart;
        //代理商显示采购单信息
        if(author instanceof Agent){
            indexStatistics.setYesterdayPurchaseOrderCount(purchaseOrderCountByDate(author.getId(),yesterdayStart,yesterdayEnd));
            indexStatistics.setYesterdayUnDeliveryPurchaseOrderCount(unDeliveryPurchaseOrderCountByDate(
                    author.getId(), PurchaseEnum.PayStatus.PAYED, PurchaseEnum.ShipStatus.NOT_DELIVER,yesterdayStart,yesterdayEnd
            ));
        }

        //待处理
        if(author instanceof Agent){
            indexStatistics.setToCheckPurchaseOrderCount(checkingPurchaseOrderCount(author.getId()));
        }

        return indexStatistics;
    }
}
