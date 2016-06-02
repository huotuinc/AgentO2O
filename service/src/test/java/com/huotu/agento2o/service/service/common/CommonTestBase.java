/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.common;

import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.config.Address;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.entity.order.MallOrderItem;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.repository.config.AddressRepository;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.order.MallAfterSalesRepository;
import com.huotu.agento2o.service.repository.order.MallOrderRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.Random;
import java.util.UUID;

/**
 * Created by helloztt on 2016/5/14.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = ServiceConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class CommonTestBase {
    protected Random random = new Random();
    @Autowired
    protected MallCustomerService customerService;
    @Autowired
    protected AgentService agentService;
    @Autowired
    protected MallGoodsRepository goodsRepository;
    @Autowired
    protected MallProductRepository productRepository;
    @Autowired
    protected AgentProductRepository agentProductRepository;
    @Autowired
    private AgentLevelService agentLevelService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private MallOrderRepository orderRepository;
    @Autowired
    private MallAfterSalesRepository afterSalesRepository;
    @Autowired
    private AddressRepository addressRepository;

    @SuppressWarnings("Duplicates")
    protected MallCustomer mockMallCustomer() {
        MallCustomer customer = new MallCustomer();
        customer.setNickName(UUID.randomUUID().toString());
        customer.setUsername(UUID.randomUUID().toString());
        customer.setPassword(UUID.randomUUID().toString());
        return customerService.newCustomer(customer);
    }

    @SuppressWarnings("Duplicates")
    protected Agent mockAgent(MallCustomer mockCustomer, Agent parentAgent) {
        Agent agent = new Agent();
        agent.setCustomer(mockCustomer);
        agent.setUsername(UUID.randomUUID().toString());
        agent.setPassword(UUID.randomUUID().toString());
        agent.setName(UUID.randomUUID().toString());
        agent.setContact(UUID.randomUUID().toString());
        agent.setMobile(UUID.randomUUID().toString());
        agent.setTelephone(UUID.randomUUID().toString());
        agent.setAddress(UUID.randomUUID().toString());
        agent.setStatus(AgentStatusEnum.CHECKED);
        agent.setDisabled(false);
        agent.setDeleted(false);
        if (parentAgent != null) {
            agent.setParentAuthor(parentAgent);
        }
        agent = agentService.addAgent(agent);
        agentService.flush();
        return agent;
    }

    @SuppressWarnings("Duplicates")
    protected Shop mockShop(Agent parentAgent) {
        Shop shop = new Shop();
        shop.setUsername(UUID.randomUUID().toString());
        shop.setPassword(UUID.randomUUID().toString());
        shop.setName(UUID.randomUUID().toString());
        shop.setContact(UUID.randomUUID().toString());
        shop.setMobile(UUID.randomUUID().toString());
        shop.setTelephone(UUID.randomUUID().toString());
        shop.setAddress(UUID.randomUUID().toString());
        shop.setDisabled(false);
        shop.setDeleted(false);
        if (parentAgent != null) {
            shop.setParentAuthor(parentAgent);
        }
        shop = shopService.addShop(shop);
        agentService.flush();
        return shop;
    }

    @SuppressWarnings("Duplicates")
    protected MallGoods mockMallGoods(Integer customerId, Integer agentId) {
        MallGoods mockMallGoods = new MallGoods();
        mockMallGoods.setCost(random.nextDouble());
        mockMallGoods.setPrice(random.nextDouble());
        mockMallGoods.setCustomerId(customerId);
        //平台方商品
//        if(agentId == null){
//            mockMallGoods.setAgentId(0);
//        }else {
//            mockMallGoods.setAgentId(agentId);
//        }
        mockMallGoods.setDisabled(false);
        mockMallGoods.setStore(random.nextInt());
        mockMallGoods.setName(UUID.randomUUID().toString());
        return goodsRepository.saveAndFlush(mockMallGoods);
    }

    @SuppressWarnings("Duplicates")
    protected MallProduct mockMallProduct(MallGoods mockGoods) {
        MallProduct mockMallProduct = new MallProduct();
        mockMallProduct.setGoods(mockGoods);
        mockMallProduct.setStandard(UUID.randomUUID().toString());
        mockMallProduct.setCost(random.nextDouble());
        mockMallProduct.setPrice(random.nextDouble());
        mockMallProduct.setName(mockGoods.getName());
        mockMallProduct.setMarketable(true);
        mockMallProduct.setLocalStock(false);
        return productRepository.saveAndFlush(mockMallProduct);
    }

    /**
     * 代理商货品
     *
     * @param mockMallProduct
     * @param mockAgent
     * @return
     */
    @SuppressWarnings("Duplicates")
    protected AgentProduct mockAgentProduct(MallProduct mockMallProduct, Agent mockAgent) {
        AgentProduct agentProduct = new AgentProduct();
        agentProduct.setAuthor(mockAgent);
        agentProduct.setProduct(mockMallProduct);
        agentProduct.setGoodsId(mockMallProduct.getGoods().getGoodsId());
        agentProduct.setStore(random.nextInt());
        agentProduct.setFreez(0);
        agentProduct.setWarning(0);
        agentProduct.setDisabled(false);
        return agentProductRepository.saveAndFlush(agentProduct);
    }

    /**
     * 代理商等级
     *
     * @param mockCustomer
     * @return
     */
    protected AgentLevel mockAgentLevel(MallCustomer mockCustomer) {
        AgentLevel agentLevel = new AgentLevel();
        agentLevel.setLevelName(UUID.randomUUID().toString());
        agentLevel.setComment(UUID.randomUUID().toString());
        agentLevel.setLevel(Math.abs(random.nextInt()));
        agentLevel.setCustomer(mockCustomer);
        agentLevel = agentLevelService.addAgentLevel(agentLevel);
        agentLevelService.flush();
        return agentLevel;
    }

    /**
     * 模拟一个订单
     */
    @SuppressWarnings("Duplicates")
    protected MallOrder mockMallOrder(Shop shop) {

        MallOrder mallOrder = new MallOrder();
        mallOrder.setOrderId(random.nextInt() + "1");
        mallOrder.setAgentMarkType("p2");
        mallOrder.setAgentMarkText("XXXX" + random.nextInt());
        mallOrder.setOrderStatus(OrderEnum.OrderStatus.ACTIVE);
        mallOrder.setPaymentType(OrderEnum.PaymentOptions.ALIPAY_PC);
        mallOrder.setOrderSourceType(OrderEnum.OrderSourceType.NORMAL);
        mallOrder.setPayStatus(OrderEnum.PayStatus.PAYED);
        mallOrder.setShipStatus(OrderEnum.ShipStatus.NOT_DELIVER);
        mallOrder.setIsTax(0);
        mallOrder.setIsProtect(0);
        mallOrder.setCreateTime(new Date());
        if (random.nextInt() % 2 == 0)
            mallOrder.setShop(shop);
        else
            mallOrder.setBeneficiaryShop(shop);
        return orderRepository.saveAndFlush(mallOrder);

    }

    /**
     * 模拟一个售后单
     */
    protected MallAfterSales mockMallAfterSales(Shop shop) {
        MallAfterSales mallAfterSales = new MallAfterSales();
        mallAfterSales.setAfterId(random.nextInt() + "1");
        mallAfterSales.setCreateTime(new Date());
        if (random.nextInt() % 2 == 0)
            mallAfterSales.setShop(shop);
        else
            mallAfterSales.setBeneficiaryShop(shop);
        mallAfterSales.setAfterSaleStatus(AfterSaleEnum.AfterSaleStatus.APPLYING);
        mallAfterSales.setPayStatus(OrderEnum.PayStatus.ALL_REFUND);
        mallAfterSales.setAfterSaleType(AfterSaleEnum.AfterSaleType.REFUND);
        mallAfterSales.setAfterSalesReason(AfterSaleEnum.AfterSalesReason.ONTER_REASON);
        mallAfterSales.setOrderItem(new MallOrderItem());
        return afterSalesRepository.saveAndFlush(mallAfterSales);
    }

    /**
     * 收货地址
     * @param author
     * @return
     */
    protected Address mockAddress(Author author) {
        Address address = new Address();
        address.setAddress(UUID.randomUUID().toString());
        address.setDefault(false);
        address.setDistrict(UUID.randomUUID().toString());
        address.setAuthor(author);
        address.setCity(UUID.randomUUID().toString());
        address.setTelephone(UUID.randomUUID().toString());
        address.setProvince(UUID.randomUUID().toString());
        address.setReceiver(UUID.randomUUID().toString());
        return addressRepository.saveAndFlush(address);
    }
}
