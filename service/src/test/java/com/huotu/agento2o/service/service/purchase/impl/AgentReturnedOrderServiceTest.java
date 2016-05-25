package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

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
    @Test
    public void testFindAll(){

        int recordNum = 3;
        MallCustomer mallCustomer = mockMallCustomer();

        Agent parentAgent1 = mockAgent(mallCustomer,null);
        Agent parentAgent2 = mockAgent(mallCustomer,null);
        Agent agent1 = mockAgent(mallCustomer,parentAgent1);
        Agent agent2 = mockAgent(mallCustomer,parentAgent1);

        List<AgentReturnedOrder> parentAgentROrders1 = new ArrayList<>();
        List<AgentReturnedOrder> parentAgentROrders2 = new ArrayList<>();

        List<AgentReturnedOrder> agentROrders1 = new ArrayList<>();
        List<AgentReturnedOrder> agentROrders2 = new ArrayList<>();
        for(int i=0;i<recordNum;i++){
            AgentReturnedOrder parentReturnOrder1 = new AgentReturnedOrder();

            parentReturnOrder1.setROrderId(SerialNo.create());
            parentReturnOrder1.setAuthor(parentAgent1);
            parentAgentROrders1.add(parentReturnOrder1);

            AgentReturnedOrder parentReturnOrder2 = new AgentReturnedOrder();
            parentReturnOrder2.setROrderId(SerialNo.create());
            parentReturnOrder2.setAuthor(parentAgent2);
            parentAgentROrders2.add(parentReturnOrder2);

            AgentReturnedOrder agentReturnedOrder1 = new AgentReturnedOrder();
            agentReturnedOrder1.setROrderId(SerialNo.create());
            agentReturnedOrder1.setAuthor(agent1);
            agentROrders1.add(agentReturnedOrder1);

            AgentReturnedOrder agentReturnedOrder2 = new AgentReturnedOrder();
            agentReturnedOrder2.setROrderId(SerialNo.create());
            agentReturnedOrder2.setAuthor(agent2);
            agentROrders2.add(agentReturnedOrder2);
        }

        agentReturnOrderRepository.save(parentAgentROrders1);
        agentReturnOrderRepository.save(parentAgentROrders2);
        agentReturnOrderRepository.save(agentROrders1);
        agentReturnOrderRepository.save(agentROrders2);


        ReturnedOrderSearch returnedOrderSearch = new ReturnedOrderSearch();

        // 平台方查询该平台下所有的一级代理商的退货单
        returnedOrderSearch.setCustomerId(mallCustomer.getCustomerId());
        returnedOrderSearch.setParentAgentId(0);
        Page page = agentReturnedOrderService.findAll(returnedOrderSearch);
        Assert.assertEquals(recordNum*2,page.getContent().size());

        // 上级代理商查询所有的下级代理商,parentAgent1 下有agent1，agent2 两个代理商
        returnedOrderSearch = new ReturnedOrderSearch();
        returnedOrderSearch.setParentAgentId(parentAgent1.getId());
        page = agentReturnedOrderService.findAll(returnedOrderSearch);
        Assert.assertEquals(recordNum*2,page.getContent().size());

        // 代理商查询自己的退货单
        returnedOrderSearch = new ReturnedOrderSearch();
        returnedOrderSearch.setAgentId(agent1.getId());
        page = agentReturnedOrderService.findAll(returnedOrderSearch);
        Assert.assertEquals(recordNum,page.getContent().size());

    }


    /**
     *  只有审核中的退货单才可以取消退货
     *  取消退货设置该退单disabled 为true
     *  取消退货需要释放预占库存
     */
    @Test
    public void testCancelReturnOrder(){
        String rOrderId = SerialNo.create();
        Integer returnNum = 5;

        MallCustomer mallCustomer = mockMallCustomer();
        Agent agent = mockAgent(mallCustomer,null);
        MallGoods mallGoods = mockMallGoods(mallCustomer.getCustomerId(),agent.getId());
        MallProduct mallProduct = mockMallProduct(mallGoods);
        AgentProduct agentProduct = mockAgentProduct(mallProduct,agent);
        Integer rawFreez = agentProduct.getFreez();

        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        agentReturnedOrder.setROrderId(rOrderId);
        agentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKING);
        agentReturnedOrder.setAuthor(agent);
        agentReturnedOrder.setDisabled(false);
        agentReturnOrderRepository.save(agentReturnedOrder);

        AgentReturnedOrderItem agentReturnedOrderItem = new AgentReturnedOrderItem();
        agentReturnedOrderItem.setProduct(mallProduct);
        agentReturnedOrderItem.setNum(returnNum);
        agentReturnedOrderItem.setReturnedOrder(agentReturnedOrder);
        agentReturnOrderItemRepository.save(agentReturnedOrderItem);

        // 增加预占库存
        agentProduct.setFreez(agentProduct.getFreez()+returnNum);
        agentProductRepository.save(agentProduct);

        agentReturnedOrderService.cancelReturnOrder(rOrderId);
        // 设置disabled为true
        AgentReturnedOrder testAgentRturnOrder = agentReturnOrderRepository.findByROrderIdAndDisabledFalse(rOrderId);
        Assert.assertNull(testAgentRturnOrder);

        // 判断预占库存被释放
        AgentProduct testAgentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(agent,mallProduct);
        Assert.assertEquals(rawFreez,testAgentProduct.getFreez());

    }

    @Test
    public void testCheckReturnOrder(){
        // TODO: 2016/5/25
    }

    @Test
    public void testPushReturrnOrderDelivery(){
        // TODO: 2016/5/25
    }

    @Test
    public void testReceiveRetrnOrder(){
        // TODO: 2016/5/25
    }

    @Test
    public void testPayReturnOrder(){
        // TODO: 2016/5/25
    }

    @Test
    public void testEditReturnNum(){
        // TODO: 2016/5/25
    }




}
