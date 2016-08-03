package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.service.common.CustomerTypeEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import com.huotu.agento2o.service.model.purchase.ReturnOrderDeliveryInfo;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
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
import java.util.Date;
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

    @Autowired
    protected MallProductRepository productRepository;


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
        MallCustomer mallCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);

        MallCustomer parentAgent1 = mockAgent(mallCustomer,null);
        MallCustomer parentAgent2 = mockAgent(mallCustomer,null);
        MallCustomer agent1 = mockAgent(mallCustomer,parentAgent1.getAgent());
        MallCustomer agent2 = mockAgent(mallCustomer,parentAgent1.getAgent());

        List<AgentReturnedOrder> parentAgentROrders1 = new ArrayList<>();
        List<AgentReturnedOrder> parentAgentROrders2 = new ArrayList<>();

        List<AgentReturnedOrder> agentROrders1 = new ArrayList<>();
        List<AgentReturnedOrder> agentROrders2 = new ArrayList<>();
        for(int i=0;i<recordNum;i++){
            AgentReturnedOrder parentReturnOrder1 = new AgentReturnedOrder();

            parentReturnOrder1.setROrderId(SerialNo.create());
            parentReturnOrder1.setAuthor(parentAgent1);
            parentReturnOrder1.setParentAgent(null);
            parentAgentROrders1.add(parentReturnOrder1);

            AgentReturnedOrder parentReturnOrder2 = new AgentReturnedOrder();
            parentReturnOrder2.setROrderId(SerialNo.create());
            parentReturnOrder2.setAuthor(parentAgent2);
            parentReturnOrder1.setParentAgent(null);
            parentAgentROrders2.add(parentReturnOrder2);

            AgentReturnedOrder agentReturnedOrder1 = new AgentReturnedOrder();
            agentReturnedOrder1.setROrderId(SerialNo.create());
            agentReturnedOrder1.setAuthor(agent1);
            agentReturnedOrder1.setParentAgent(agent1.getParentAgent());
            agentROrders1.add(agentReturnedOrder1);

            AgentReturnedOrder agentReturnedOrder2 = new AgentReturnedOrder();
            agentReturnedOrder2.setROrderId(SerialNo.create());
            agentReturnedOrder2.setAuthor(agent2);
            agentReturnedOrder2.setParentAgent(agent2.getParentAgent());
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

        MallCustomer mallCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        MallCustomer agent = mockAgent(mallCustomer,null);
        MallGoods mallGoods = mockMallGoods(mallCustomer.getCustomerId(),agent.getId());
        MallProduct mallProduct = mockMallProduct(mallGoods);
        AgentProduct agentProduct = mockAgentProduct(mallProduct,agent.getAgent());
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

        agentReturnedOrderService.cancelReturnOrder(agent,rOrderId);

        // 设置disabled为true
        AgentReturnedOrder testAgentRturnOrder = agentReturnOrderRepository.findByAgentAndShopAndROrderIdAndDisabledFalse(agent.getAuthorAgent(),agent.getAuthorShop(),rOrderId);
        Assert.assertNull(testAgentRturnOrder);

        // 判断预占库存被释放
        AgentProduct testAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(agent.getAgent(),mallProduct);
        Assert.assertEquals(rawFreez,testAgentProduct.getFreez());

        rOrderId = "";
        agentReturnedOrderService.cancelReturnOrder(agent,rOrderId);// TODO: 2016/5/26

    }

    @Test
    public void testCheckReturnOrder(){
        MallCustomer mallCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        MallCustomer parentAgent = mockAgent(mallCustomer,null);
        MallCustomer subAgent = mockAgent(mallCustomer,parentAgent.getAgent());
        MallGoods mallGoods = mockMallGoods(mallCustomer.getCustomerId(), parentAgent.getId());
        MallProduct mallProduct = mockMallProduct(mallGoods);
        AgentProduct parentAgentProduct = mockAgentProduct(mallProduct,parentAgent.getAgent());
        AgentProduct subAgentProduct = mockAgentProduct(mallProduct,subAgent.getAgent());
        Integer parentRawFreez = parentAgentProduct.getFreez();
        Integer subRawFreez = subAgentProduct.getFreez();

        String parentROrderId = SerialNo.create();
        String subROrderId = SerialNo.create();
        Integer returnNum = 5;
        Integer customerId = mallCustomer.getCustomerId();

        // 增加一级代理商货品的预占库存
        parentAgentProduct.setFreez(parentAgentProduct.getFreez()+returnNum);
        agentProductRepository.saveAndFlush(parentAgentProduct);

        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        agentReturnedOrder.setROrderId(parentROrderId);
        agentReturnedOrder.setAuthor(parentAgent);
        agentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKING);
        agentReturnedOrder.setDisabled(false);
        agentReturnOrderRepository.save(agentReturnedOrder);

        AgentReturnedOrderItem agentReturnedOrderItem = new AgentReturnedOrderItem();
        agentReturnedOrderItem.setProduct(mallProduct);
        agentReturnedOrderItem.setNum(returnNum);
        agentReturnedOrderItem.setReturnedOrder(agentReturnedOrder);
        agentReturnOrderItemRepository.save(agentReturnedOrderItem);

        // 平台审核一级代理商退货单 不通过
        agentReturnedOrderService.checkReturnOrder(customerId,null,parentROrderId,EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(2)), "test");
        // 设置disabled为true
        AgentReturnedOrder testAgentRturnOrder = agentReturnOrderRepository.findByAgentAndShopAndROrderIdAndDisabledFalse(parentAgent.getAuthorAgent(),parentAgent.getAuthorShop(),parentROrderId);
        Assert.assertNull(testAgentRturnOrder);
        // 判断预占库存被释放
        AgentProduct testAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(parentAgent.getAgent(),mallProduct);
        Assert.assertEquals(parentRawFreez,testAgentProduct.getFreez());


        // 平台审核一级代理商退货单 通过,退单状态为审核通过
        agentReturnedOrder.setDisabled(false);
        agentReturnOrderRepository.save(agentReturnedOrder);
        agentReturnedOrderService.checkReturnOrder(customerId,null,parentROrderId,EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(1)), "test");
        AgentReturnedOrder passedAgentReturnedOrder =  agentReturnOrderRepository.findOne(parentROrderId);
        Assert.assertTrue(passedAgentReturnedOrder.getStatus().getCode()==1);


        // 一级代理商审核下级代理商退货单

        // 增加一级代理商货品的预占库存
        subAgentProduct.setFreez(subAgentProduct.getFreez()+returnNum);
        agentProductRepository.saveAndFlush(subAgentProduct);

        AgentReturnedOrder subAgentReturnedOrder = new AgentReturnedOrder();
        subAgentReturnedOrder.setROrderId(subROrderId);
        subAgentReturnedOrder.setAuthor(subAgent);
        subAgentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKING);
        subAgentReturnedOrder.setDisabled(false);
        agentReturnOrderRepository.save(subAgentReturnedOrder);

        AgentReturnedOrderItem subAgentReturnedOrderItem = new AgentReturnedOrderItem();
        agentReturnedOrderItem.setProduct(mallProduct);
        agentReturnedOrderItem.setNum(returnNum);
        agentReturnedOrderItem.setReturnedOrder(subAgentReturnedOrder);
        agentReturnOrderItemRepository.save(subAgentReturnedOrderItem);
        // 不通过
        agentReturnedOrderService.checkReturnOrder(null,parentAgent.getId(),subROrderId,EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(2)),"test");
        // 设置disabled为true
        AgentReturnedOrder testSubAgentRturnOrder = agentReturnOrderRepository.findOne(subROrderId);
        Assert.assertTrue(testSubAgentRturnOrder.isDisabled()==true);
        // 判断预占库存被释放
        AgentProduct testSubAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(subAgent.getAgent(),mallProduct);
        Assert.assertEquals(subRawFreez,testSubAgentProduct.getFreez());

        //通过
        subAgentReturnedOrder.setDisabled(false);
        agentReturnOrderRepository.save(subAgentReturnedOrder);
        agentReturnedOrderService.checkReturnOrder(null,parentAgent.getId(),subROrderId,EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(1)), "test");
        AgentReturnedOrder passedSubAgentReturnedOrder =  agentReturnOrderRepository.findOne(subROrderId);
        Assert.assertTrue(passedSubAgentReturnedOrder.getStatus().getCode()==1);

    }

    @Test
    public void testPushReturnOrderDelivery(){
        MallCustomer mallCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);

        MallCustomer agent = mockAgent(mallCustomer,null);
        MallGoods mallGoods = mockMallGoods(mallCustomer.getCustomerId(), agent.getId());
        MallProduct mallProduct = mockMallProduct(mallGoods);
        AgentProduct agentProduct = mockAgentProduct(mallProduct,agent.getAgent());

        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        String rOrderId = SerialNo.create();
        agentReturnedOrder.setROrderId(rOrderId);
        agentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKED);
        agentReturnOrderRepository.saveAndFlush(agentReturnedOrder);

//        AgentReturnedOrderItem agentReturnedOrderItem = new AgentReturnedOrderItem();
//        AgentReturnedOrderItem.setProduct(mallProduct);
//        AgentReturnedOrderItem.setNum(10);
//        AgentReturnedOrderItem.setReturnedOrder(agentReturnedOrder);
//        agentReturnOrderItemRepository.save(agentReturnedOrderItem);


        ReturnOrderDeliveryInfo returnOrderDeliveryInfo = new ReturnOrderDeliveryInfo();


        agentReturnedOrderService.pushReturnOrderDelivery(returnOrderDeliveryInfo,agent.getId());





    }

    @Test
    public void testReceiveRetrnOrder(){

        Integer mallProductNum = 100;
        Integer parentAgentProductNum = 50;
        Integer parentAgentFreez = 15;
        Integer subAgentFreez = 15;
        Integer subAgentProductNum = 25;
        Integer parentReturnNum = 10;
        Integer subReturnNum = 10;

        MallCustomer mallCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        MallCustomer parentAgent = mockAgent(mallCustomer,null);
        MallCustomer subAgent = mockAgent(mallCustomer,parentAgent.getAgent());
        MallGoods mallGoods = mockMallGoods(mallCustomer.getCustomerId(),null);
        MallProduct mallProduct = mockMallProduct(mallGoods);
        mallProduct.setStore(mallProductNum);
        productRepository.saveAndFlush(mallProduct);


        AgentProduct parentAgentProduct = mockAgentProduct(mallProduct,parentAgent.getAgent());
        parentAgentProduct.setStore(parentAgentProductNum);
        parentAgentProduct.setFreez(parentAgentFreez);
        parentAgentProduct = agentProductRepository.saveAndFlush(parentAgentProduct);

        AgentProduct subAgentProduct = mockAgentProduct(mallProduct,subAgent.getAgent());
        subAgentProduct.setStore(subAgentProductNum);
        subAgentProduct.setFreez(subAgentFreez);
        subAgentProduct = agentProductRepository.saveAndFlush(subAgentProduct);

        // 平台确认收货,平台方库存增加，一级代理商库存减少，一级代理商预占库存减少
        AgentReturnedOrder parentAgentReturnedOrder = new AgentReturnedOrder();
        String parentROrderId = SerialNo.create();
        parentAgentReturnedOrder.setROrderId(parentROrderId);
        parentAgentReturnedOrder.setAuthor(parentAgent);
        parentAgentReturnedOrder.setDisabled(false);
        parentAgentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKED);
        parentAgentReturnedOrder.setShipStatus(PurchaseEnum.ReturnedShipStatus.DELIVERED);
        agentReturnOrderRepository.saveAndFlush(parentAgentReturnedOrder);

        AgentReturnedOrderItem parentAgentReturnedOrderItem = new AgentReturnedOrderItem();
        parentAgentReturnedOrderItem.setNum(parentReturnNum);
        parentAgentReturnedOrderItem.setProduct(mallProduct);
        parentAgentReturnedOrderItem.setReturnedOrder(parentAgentReturnedOrder);
        agentReturnOrderItemRepository.saveAndFlush(parentAgentReturnedOrderItem);

        agentReturnedOrderService.receiveReturnOrder(mallCustomer.getCustomerId(),null,parentROrderId);
        Assert.assertEquals(mallProductNum+parentReturnNum,mallProduct.getStore());
        Assert.assertTrue(parentAgentProduct.getStore() == parentAgentProductNum-parentReturnNum);
        Assert.assertTrue(parentAgentProduct.getFreez() == parentAgentFreez-parentReturnNum);

        // 上级代理商确认收货
        // 恢复上级代理商的库存
        parentAgentProduct.setStore(parentAgentProductNum);
        parentAgentProduct = agentProductRepository.saveAndFlush(parentAgentProduct);

        AgentReturnedOrder subAgentReturnedOrder = new AgentReturnedOrder();
        String subROrderId = SerialNo.create();
        subAgentReturnedOrder.setROrderId(subROrderId);
        subAgentReturnedOrder.setAuthor(subAgent);
        subAgentReturnedOrder.setDisabled(false);
        subAgentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKED);
        subAgentReturnedOrder.setShipStatus(PurchaseEnum.ReturnedShipStatus.DELIVERED);
        agentReturnOrderRepository.saveAndFlush(subAgentReturnedOrder);

        AgentReturnedOrderItem subAgentReturnedOrderItem = new AgentReturnedOrderItem();
        subAgentReturnedOrderItem.setNum(subReturnNum);
        subAgentReturnedOrderItem.setProduct(mallProduct);
        subAgentReturnedOrderItem.setReturnedOrder(subAgentReturnedOrder);
        agentReturnOrderItemRepository.saveAndFlush(subAgentReturnedOrderItem);

        agentReturnedOrderService.receiveReturnOrder(null,parentAgent.getId(),subROrderId);
        Assert.assertTrue(parentAgentProductNum+subReturnNum == parentAgentProduct.getStore());
        Assert.assertTrue(subAgentProduct.getStore()==subAgentProductNum-subReturnNum);
        Assert.assertTrue(subAgentProduct.getFreez() == subAgentFreez-subReturnNum);

    }

    @Test
    public void testPayReturnOrder(){

        String parentROrderId = SerialNo.create();
        String subROrderId = SerialNo.create();

        MallCustomer mallCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        MallCustomer parentAgent = mockAgent(mallCustomer,null);
        MallCustomer subAgent = mockAgent(mallCustomer,parentAgent.getAgent());

        //平台方支付退款给关联的一级代理商
        AgentReturnedOrder parentAgentReturnedOrder = new AgentReturnedOrder();
        parentAgentReturnedOrder.setDisabled(false);
        parentAgentReturnedOrder.setAuthor(parentAgent);
        parentAgentReturnedOrder.setROrderId(parentROrderId);
        parentAgentReturnedOrder.setShipStatus(PurchaseEnum.ReturnedShipStatus.DELIVERED);
        parentAgentReturnedOrder.setReceivedTime(new Date());
        parentAgentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKED);
        agentReturnOrderRepository.save(parentAgentReturnedOrder);

        agentReturnedOrderService.payReturnOrder(mallCustomer.getCustomerId(),null,parentROrderId);
        AgentReturnedOrder parentTestAgentReturnedOrder = agentReturnOrderRepository.findOne(parentROrderId);
        Assert.assertEquals(PurchaseEnum.PayStatus.PAYED,parentTestAgentReturnedOrder.getPayStatus());

        // 上级代理商支付退款给关联的下级代理商
        AgentReturnedOrder subAgentReturnedOrder = new AgentReturnedOrder();
        subAgentReturnedOrder.setROrderId(subROrderId);
        subAgentReturnedOrder.setDisabled(false);
        subAgentReturnedOrder.setAuthor(subAgent);
        subAgentReturnedOrder.setShipStatus(PurchaseEnum.ReturnedShipStatus.DELIVERED);
        subAgentReturnedOrder.setReceivedTime(new Date());
        subAgentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKED);
        subAgentReturnedOrder.setPayStatus(PurchaseEnum.PayStatus.NOT_PAYED);
        agentReturnOrderRepository.save(subAgentReturnedOrder);

        agentReturnedOrderService.payReturnOrder(null,parentAgent.getId(),subROrderId);
        AgentReturnedOrder subTestAgentReturnedOrder = agentReturnOrderRepository.findOne(subROrderId);
        Assert.assertEquals(PurchaseEnum.PayStatus.PAYED,subTestAgentReturnedOrder.getPayStatus());
    }

    @Test
    public void testEditReturnNum(){

        Integer agentProductStore = 1000;
        Integer agentProductFreez = 900;

        MallCustomer mallCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        MallCustomer agent = mockAgent(mallCustomer,null);
        MallGoods mallGoods = mockMallGoods(mallCustomer.getCustomerId(),agent.getId());
        MallProduct mallProduct = mockMallProduct(mallGoods);
        AgentProduct agentProduct = mockAgentProduct(mallProduct,agent.getAgent());
        agentProduct.setStore(agentProductStore);
        agentProduct.setFreez(agentProductFreez);
        agentProductRepository.save(agentProduct);
        int editNum = 10;
        // editNum <= agentProductStore - agentProductFreez
        ApiResult apiResult = agentReturnedOrderService.editReturnNum(agent,mallProduct.getProductId(),editNum);
        Assert.assertTrue(apiResult.getCode() == ResultCodeEnum.SUCCESS.getResultCode());

        editNum = 110;
        // editNum > agentProductStore - agentProductFreez
        apiResult = agentReturnedOrderService.editReturnNum(agent,mallProduct.getProductId(),editNum);
        Assert.assertFalse(apiResult.getCode() == ResultCodeEnum.SUCCESS.getResultCode());
    }
}
