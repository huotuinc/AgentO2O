package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
public class AgentReturnedOrderServiceTest extends CommonTestBase {

    @Autowired
    private AgentReturnedOrderService agentReturnedOrderService;

    @Autowired
    private AgentReturnOrderRepository agentReturnOrderRepository;

    @Autowired
    private AgentReturnOrderItemRepository agentReturnOrderItemRepository;


    @Test
    public void testFindAgentProductsByAgentId(){
        Integer agentId = -1;
        List<AgentProduct> agentProducts =  agentReturnedOrderService.findAgentProductsByAgentId(agentId);
        Assert.assertTrue(agentProducts.size()==0);

        agentId = null;
        agentProducts =  agentReturnedOrderService.findAgentProductsByAgentId(agentId);
        Assert.assertTrue(agentProducts.size()==0);

        agentId = 1;
        agentProducts =  agentReturnedOrderService.findAgentProductsByAgentId(agentId);
        Assert.assertTrue(agentProducts.size()>=0);
    }

    @Rollback(value = false)
    @Test
    public void testAgentReturnOrder(){
        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        agentReturnedOrder.setROrderId("20160518204924549393");
        agentReturnedOrder.setDisabled(true);
        agentReturnOrderRepository.save(agentReturnedOrder);
    }

}
