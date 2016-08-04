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
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/18.
 */
@Repository
public interface AgentPurchaseOrderRepository extends JpaRepository<AgentPurchaseOrder, String>, JpaSpecificationExecutor<AgentPurchaseOrder> {

    AgentPurchaseOrder findByPOrderIdAndAgentAndShop(String pOrderId, Agent agent, Shop shop);


    List<AgentPurchaseOrder> findByAgentAndShop(Agent agent, Shop shop);

    /**
     * 按日期统计代理商/门店有效的采购单
     *
     * @param agent
     * @param shop
     * @param start
     * @param end
     * @return
     */
    int countByAgentAndShopAndCreateTimeBetweenAndDisabledFalse(Agent agent,Shop shop, Date start, Date end);

    /**
     * 按日期统计下级代理商/下级门店有效的采购单
     *
     * @param agentId
     * @param start
     * @param end
     * @return
     */
//    @Query("select count(a) from AgentPurchaseOrder a where (a.agent.parentAgent.id = ?1 or a.shop.agent.id = ?1) and a.createTime between ?2 and ?3 and a.disabled = false ")
//    int countByAuthor_ParentAuthor_IdAndCreateTimeBetweenAndDisabledFalse(int agentId, Date start, Date end);
    int countByAgent_ParentAgent_IdAndCreateTimeBetweenAndDisabledFalse(int agentId,Date start,Date end);
    int countByShop_Agent_IdAndCreateTimeBetweenAndDisabledFalse(int agentId,Date start,Date end);

//    @Query("select count(a) from AgentPurchaseOrder a where (a.agent.parentAgent.id = ?1 or a.shop.agent.id = ?1) and a.status = ?2 and a.shipStatus = ?3 and a.disabled = false ")
//    int countByAuthor_ParentAuthor_IdAndStatusAndShipStatusAndDisabledFalse(Integer authorId, PurchaseEnum.OrderStatus status,
//                                                                               PurchaseEnum.ShipStatus shipStatus);
    int countByAgent_ParentAgent_IdAndStatusAndShipStatusAndDisabledFalse(Integer authorId, PurchaseEnum.OrderStatus status,PurchaseEnum.ShipStatus shipStatus);
    int countByShop_Agent_IdAndStatusAndShipStatusAndDisabledFalse(Integer authorId, PurchaseEnum.OrderStatus status,PurchaseEnum.ShipStatus shipStatus);

    @Query("select count(a) from AgentPurchaseOrder a where (a.agent.id = ?1 or a.shop.id = ?1) and a.status = ?2 and a.shipStatus = ?3 and a.receivedTime is null and a.disabled = false ")
    int countByAuthor_IdAndStatusAndShipStatusAndReceivedTimeIsNullAndDisabledFalse(Integer authorId, PurchaseEnum.OrderStatus status,
                                                                                       PurchaseEnum.ShipStatus shipStatus);

//    @Query("select count(a) from AgentPurchaseOrder a where (a.agent.parentAgent.id = ?1 or a.shop.agent.id = ?1) and a.status = ?2 and a.disabled = false ")
//    int countByAuthor_ParentAuthor_IdAndStatusAndDisabledFalse(Integer authorId, PurchaseEnum.OrderStatus status);
    int countByAgent_ParentAgent_IdAndStatusAndDisabledFalse(Integer authorId,PurchaseEnum.OrderStatus status);
    int countByShop_Agent_IdAndStatusAndDisabledFalse(Integer authorId,PurchaseEnum.OrderStatus status);



}
