/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.repository.purchase;

import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/18.
 */
@Repository
public interface AgentPurchaseOrderRepository extends JpaRepository<AgentPurchaseOrder, String>, JpaSpecificationExecutor<AgentPurchaseOrder> {

    AgentPurchaseOrder findByPOrderIdAndAuthor(String pOrderId, Author author);

    List<AgentPurchaseOrder> findByAuthor(Author author);

    /**
     * 按日期统计代理商/门店有效的采购单
     * @param authorId
     * @param start
     * @param end
     * @return
     */
    int countByAuthor_IdAndCreateTimeBetweenAndDisabledFalse(Integer authorId,Date start,Date end);

    /**
     * 按日期统计下级代理商/下级门店有效的采购单
     * @param agentId
     * @param start
     * @param end
     * @return
     */
    int countByAuthor_ParentAuthor_IdAndCreateTimeBetweenAndDisabledFalse(int agentId,Date start,Date end);

    int countByAuthor_ParentAuthor_IdAndPayStatusAndShipStatusAndDisabledFalse(Integer authorId, PurchaseEnum.PayStatus payStatus,
                                                                      PurchaseEnum.ShipStatus shipStatus);

    int countByAuthor_IdAndPayStatusAndShipStatusAndDisabledFalse(Integer authorId, PurchaseEnum.PayStatus payStatus,
                                                                  PurchaseEnum.ShipStatus shipStatus);

    int countByAuthor_ParentAuthor_IdAndStatusAndDisabledFalse(Integer authorId,PurchaseEnum.OrderStatus status);
}
