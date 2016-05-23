package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.purchase.AgentReturnOrderItemService;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
public class AgentReturnOrderItemServiceImplTest extends CommonTestBase {

    @Autowired
    private AgentReturnOrderItemService agentReturnOrderItemService;

    @Test
    public void testAddReturnOrderItems(){
        List<AgentReturnedOrderItem> agentReturnedOrderItems = new ArrayList<>();
        for(int i=0;i<5;i++){
//            AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
//            agentReturnedOrder.setROrderId("20160518151420230901");
            AgentReturnedOrderItem agentReturnedOrderItem = new AgentReturnedOrderItem();
//            agentReturnedOrderItem.setReturnedOrder(agentReturnedOrder);
            agentReturnedOrderItems.add(agentReturnedOrderItem);
        }
        agentReturnOrderItemService.addReturnOrderItemList(agentReturnedOrderItems);

    }
}
