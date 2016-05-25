package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.ArrayList;
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
    public void testFindOne(){

        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        String rOrderId = SerialNo.create();
        agentReturnedOrder.setROrderId(rOrderId);

        agentReturnOrderRepository.save(agentReturnedOrder);

        AgentReturnedOrder agentReturnedOrder1 = agentReturnedOrderService.findOne(rOrderId);
        Assert.assertNotNull(agentReturnedOrder1);

        agentReturnedOrder1 = agentReturnedOrderService.findOne("");
        Assert.assertNull(agentReturnedOrder1);

        agentReturnedOrder1 = agentReturnedOrderService.findOne(null);
        Assert.assertNull(agentReturnedOrder1);
    }

    @Test
    public void testAddReturnOrder(){
        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        String rOrderId = SerialNo.create();
        agentReturnedOrder.setROrderId(rOrderId);

        agentReturnedOrderService.addReturnOrder(agentReturnedOrder);

        AgentReturnedOrder agentReturnedOrder1 = agentReturnOrderRepository.findOne(rOrderId);
        Assert.assertNotNull(agentReturnedOrder1);

        agentReturnedOrder1 = agentReturnedOrderService.addReturnOrder(null);
        Assert.assertNull(agentReturnedOrder1);

    }

    /**
     *  构造退货单列表用于分页查询接口测试
     *  构造一个一级代理商
     *  构造一个二级代理商
     *  平台查询所有一级代理商退货列表
     *  一级代理商查询下级退货单列表
     *  代理商查看自己的退货单列表
     *
     */
    private void createAgentReturnOrderList(){
        MallCustomer mallCustomer = mockMallCustomer();
        Agent parentAgent1 = mockAgent(mallCustomer,null);
        Agent parentAgent2 = mockAgent(mallCustomer,null);
        Agent agent1 = mockAgent(mallCustomer,parentAgent1);
        Agent agent2 = mockAgent(mallCustomer,parentAgent1);

        List<AgentReturnedOrder> parentAgent1ROrders = new ArrayList<>();



    }

    @Test
    public void testFindAll(){
        ReturnedOrderSearch returnedOrderSearch = new ReturnedOrderSearch();
        agentReturnedOrderService.findAll(returnedOrderSearch);
    }




}
