package com.huotu.agento2o.service.repository.purchase;

import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
@Repository
public interface AgentReturnOrderRepository extends JpaRepository<AgentReturnedOrder, String>, JpaSpecificationExecutor<AgentReturnedOrder> {

    AgentReturnedOrder findByAgentAndShopAndROrderIdAndDisabledFalse(Agent agent, Shop shop, String rOrderId);

    @Query("select count(a) from AgentReturnedOrder a where (a.agent.id = ?1 or a.shop.id = ?1) and a.createTime between ?2 and ?3 and a.disabled = false ")
    int countByAuthor_IdAndCreateTimeBetweenAndDisabledFalse(Integer authorId, Date start, Date end);

    @Query("select count(a) from AgentReturnedOrder a where (a.agent.id = ?1 or a.shop.id = ?1) and a.status = ?2 and a.shipStatus = ?3 and a.disabled = false ")
    int countByAuthor_IdAndStatusAndShipStatusAndDisabledFalse(Integer authorId, PurchaseEnum.OrderStatus status, PurchaseEnum.ShipStatus shipStatus);

    @Query("select count(a) from AgentReturnedOrder a where (a.agent.parentAgent.id = ?1 or a.shop.agent.id = ?1) and a.createTime between ?2 and ?3 and a.disabled = false ")
    int countByAuthor_ParentAuthor_IdAndCreateTimeBetweenAndDisabledFalse(Integer agentId, Date start, Date end);

    @Query("select count(a) from AgentReturnedOrder a where (a.agent.parentAgent.id = ?1 or a.shop.agent.id = ?1) and a.status = ?2 and a.disabled = false ")
    int countByAuthor_ParentAuthor_IdAndStatusAndDisabledFalse(Integer agentId, PurchaseEnum.OrderStatus status);

    @Query("select count(a) from AgentReturnedOrder a where (a.agent.parentAgent.id = ?1 or a.shop.agent.id = ?1) and a.status = ?2 and a.shipStatus = ?3 and a.receivedTime is not null and a.disabled = false ")
    int countByAuthor_ParentAuthor_IdAndStatusAndShipStatusAndReceivedTimeIsNullAndDisabledFalse(Integer agentId, PurchaseEnum.OrderStatus status, PurchaseEnum.ShipStatus shipStatus);
}
