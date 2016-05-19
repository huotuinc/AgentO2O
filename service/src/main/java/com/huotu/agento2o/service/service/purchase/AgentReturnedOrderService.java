package com.huotu.agento2o.service.service.purchase;

import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
public interface AgentReturnedOrderService {

    List<AgentProduct> findAgentProductsByAgentId(Integer agentId);

    AgentReturnedOrder addReturnOrder(AgentReturnedOrder agentReturnedOrder);
}
