package com.huotu.agento2o.service.repository.purchase;

import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */

@Repository
public interface AgentReturnOrderItemRepository extends JpaRepository<AgentReturnedOrderItem,Integer> {

    List<AgentReturnedOrderItem> findByReturnedOrder_rOrderId(String rOrderId);
}
