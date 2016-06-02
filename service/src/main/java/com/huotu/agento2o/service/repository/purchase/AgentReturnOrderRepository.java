package com.huotu.agento2o.service.repository.purchase;

import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
@Repository
public interface AgentReturnOrderRepository extends JpaRepository<AgentReturnedOrder,String>, JpaSpecificationExecutor<AgentReturnedOrder> {

    List<AgentReturnedOrder> findByAuthor_IdAndDisabledFalse(Integer agentId);

    AgentReturnedOrder findByAuthorAndROrderIdAndDisabledFalse(Author author,String rOrderId);

    int countByAuthor_IdAndCreateTimeBetweenAndDisabledFalse(Integer authorId, Date start,Date end);

    int countByAuthor_IdAndStatusAndShipStatusAndDisabledFalse(Integer authorId,PurchaseEnum.OrderStatus status,PurchaseEnum.ShipStatus shipStatus);

    int countByAuthor_ParentAuthor_IdAndCreateTimeBetweenAndDisabledFalse(Integer agentId, Date start,Date end);

    int countByAuthor_ParentAuthor_IdAndStatusAndDisabledFalse(Integer agentId, PurchaseEnum.OrderStatus status);

    int countByAuthor_ParentAuthor_IdAndStatusAndShipStatusAndReceivedTimeIsNullAndDisabledFalse(Integer agentId,PurchaseEnum.OrderStatus status,PurchaseEnum.ShipStatus shipStatus);
}
