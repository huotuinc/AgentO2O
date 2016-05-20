package com.huotu.agento2o.service.service.purchase;

import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
public interface AgentReturnOrderItemService  {

    List<AgentReturnedOrderItem> addReturnOrderItemList(List<AgentReturnedOrderItem> agentReturnedOrderItems);
    List<AgentReturnedOrderItem> addReturnOrderItemList(AgentReturnedOrder agentReturnedOrder, Integer[]productIds,Integer[] productNums);
}
