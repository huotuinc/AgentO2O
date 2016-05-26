/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.common;

import com.huotu.agento2o.agent.config.MVCConfig;
import com.huotu.agento2o.agent.config.SecurityConfig;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.InvoiceEnum;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.config.InvoiceConfig;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.repository.config.InvoiceConfigRepository;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.order.MallOrderRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderRepository;
import com.huotu.agento2o.service.repository.purchase.ShoppingCartRepository;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.purchase.ShoppingCartService;
import org.junit.Assert;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by helloztt on 2016/5/9.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MVCConfig.class, ServiceConfig.class})
@WebAppConfiguration
@Transactional
public abstract class CommonTestBase extends SpringWebTest{

    protected String passWord = UUID.randomUUID().toString();

    protected Random random = new Random();
    @Autowired
    protected MallCustomerService customerService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private ShopService shopService;
    @Autowired
    protected ShoppingCartRepository shoppingCartRepository;
    @Autowired
    protected MallGoodsRepository goodsRepository;
    @Autowired
    protected MallProductRepository productRepository;
    @Autowired
    protected AgentProductRepository agentProductRepository;
    @Autowired
    protected AgentPurchaseOrderRepository agentPurchaseOrderRepository;
    @Autowired
    protected InvoiceConfigRepository invoiceConfigRepository;
    @Autowired
    protected MallOrderRepository orderRepository;

    protected MockHttpSession loginAs(String userName, String password,String roleType) throws Exception {
        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",roleType))
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        saveAuthedSession(session);
        return session;
    }

    @SuppressWarnings("Duplicates")
    protected MallCustomer mockMallCustomer(){
        MallCustomer customer = new MallCustomer();
        customer.setNickName(UUID.randomUUID().toString());
        customer.setUsername(UUID.randomUUID().toString());
        customer.setPassword(passWord);
        return customerService.newCustomer(customer);
    }

    @SuppressWarnings("Duplicates")
    protected Agent mockAgent(MallCustomer mockCustomer, Agent parentAgent){
        Agent agent = new Agent();
        agent.setCustomer(mockCustomer);
        agent.setUsername(UUID.randomUUID().toString());
        agent.setPassword(passWord);
        agent.setName(UUID.randomUUID().toString());
        agent.setContact(UUID.randomUUID().toString());
        agent.setMobile(UUID.randomUUID().toString());
        agent.setTelephone(UUID.randomUUID().toString());
        agent.setAddress(UUID.randomUUID().toString());
        agent.setDisabled(false);
        agent.setDeleted(false);
        if(parentAgent != null){
            agent.setParentAuthor(parentAgent);
        }
        agent.setStatus(AgentStatusEnum.CHECKED);
        agent = agentService.addAgent(agent);
        agentService.flush();
        return agent;
    }

    protected Shop mockShop(MallCustomer mockCustomer,Agent parentAgent){
        Shop shop = new Shop();
        shop.setCustomer(mockCustomer);
        shop.setUsername(UUID.randomUUID().toString());
        shop.setPassword(passWord);
        shop.setName(UUID.randomUUID().toString());
        shop.setContact(UUID.randomUUID().toString());
        shop.setMobile(UUID.randomUUID().toString());
        shop.setTelephone(UUID.randomUUID().toString());
        shop.setAddress(UUID.randomUUID().toString());
        shop.setDeleted(false);
        shop.setDisabled(false);
        shop.setStatus(AgentStatusEnum.CHECKED);
        if(parentAgent != null){
            shop.setParentAuthor(parentAgent);
        }
        shop = shopService.addShop(shop);
        shopService.flush();
        return shop;
    }

    /**
     * 平台方商品
     * @param customerId
     * @param agentId
     * @return
     */
    protected MallGoods mockMallGoods(Integer customerId, Integer agentId){
        MallGoods mockMallGoods = new MallGoods();
        mockMallGoods.setCost(random.nextDouble());
        mockMallGoods.setPrice(random.nextDouble());
        mockMallGoods.setCustomerId(customerId);
        //平台方商品
        if(agentId == null){
            mockMallGoods.setAgentId(0);
        }else {
            mockMallGoods.setAgentId(agentId);
        }
        mockMallGoods.setDisabled(false);
        mockMallGoods.setStore(random.nextInt());
        mockMallGoods.setName(UUID.randomUUID().toString());
        return mockMallGoods;
    }

    protected MallGoods mockMallGoods(MallGoods mockMallGoods){
        return goodsRepository.saveAndFlush(mockMallGoods);
    }

    /**
     * 平台方货品
     * @param mockGoods
     * @return
     */
    @SuppressWarnings("Duplicates")
    protected MallProduct mockMallProduct(MallGoods mockGoods){
        MallProduct mockMallProduct = new MallProduct();
        mockMallProduct.setGoods(mockGoods);
        mockMallProduct.setStandard(UUID.randomUUID().toString());
        mockMallProduct.setCost(random.nextDouble());
        mockMallProduct.setPrice(random.nextDouble());
        mockMallProduct.setName(mockGoods.getName());
        mockMallProduct.setMarketable(true);
        mockMallProduct.setLocalStock(false);
        int store = random.nextInt(100) + 1;
        mockMallProduct.setStore(store);
        mockMallProduct.setFreez(random.nextInt(store));
        return mockMallProduct;
    }

    /**
     * 代理商货品
     * @param mockMallProduct
     * @param mockAuthor
     * @return
     */
    @SuppressWarnings("Duplicates")
    protected AgentProduct mockAgentProduct(MallProduct mockMallProduct, Author mockAuthor){
        AgentProduct agentProduct = new AgentProduct();
        agentProduct.setAuthor(mockAuthor);
        agentProduct.setProduct(mockMallProduct);
        agentProduct.setGoodsId(mockMallProduct.getGoods().getGoodsId());
        agentProduct.setStore(random.nextInt(100));
        agentProduct.setFreez(0);
        agentProduct.setWarning(0);
        agentProduct.setDisabled(false);
        return agentProductRepository.saveAndFlush(agentProduct);
    }

    protected ShoppingCart mockShoppingCart(MallProduct mockMallProduct, Author author){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setAuthor(author);
        shoppingCart.setProduct(mockMallProduct);
        shoppingCart.setNum(random.nextInt(mockMallProduct.getStore() - mockMallProduct.getFreez()) + 1);
        shoppingCart.setCreateTime(new Date());
        return shoppingCartRepository.saveAndFlush(shoppingCart);
    }

    protected ShoppingCart mockShoppingCart(AgentProduct agentProduct,Author author){
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setAuthor(author);
        shoppingCart.setProduct(agentProduct.getProduct());
        shoppingCart.setNum(random.nextInt(agentProduct.getStore() - agentProduct.getFreez()) + 1);
        shoppingCart.setCreateTime(new Date());
        return shoppingCartRepository.saveAndFlush(shoppingCart);
    }

    /**
     *
     * @param author
     * @param type 发票类型：0普通发票；1增值税发票
     * @return
     */
    protected InvoiceConfig mockInvoiceConfig(Author author , int type){
        InvoiceConfig invoiceConfig = new InvoiceConfig();
        invoiceConfig.setAuthor(author);
        if(type == 0){
            invoiceConfig.setType(InvoiceEnum.InvoiceTypeStatus.NORMALINVOICE);
            invoiceConfig.setTaxTitle(UUID.randomUUID().toString());
            invoiceConfig.setTaxContent(UUID.randomUUID().toString());
        }else if (type == 1){
            invoiceConfig.setType(InvoiceEnum.InvoiceTypeStatus.TAXINVOICE);
            invoiceConfig.setTaxTitle(UUID.randomUUID().toString());
            invoiceConfig.setTaxContent(UUID.randomUUID().toString());
            invoiceConfig.setTaxpayerCode(UUID.randomUUID().toString());
            invoiceConfig.setAccountNo(UUID.randomUUID().toString());
            invoiceConfig.setBankName(UUID.randomUUID().toString());
        }
        invoiceConfig.setDefaultType(1);
        InvoiceConfig otherInvoiceConfig = invoiceConfigRepository.findByAuthorAndDefaultType(author,1);
        if(otherInvoiceConfig != null){
            otherInvoiceConfig.setDefaultType(0);
            invoiceConfigRepository.save(otherInvoiceConfig);
        }
        return invoiceConfigRepository.saveAndFlush(invoiceConfig);
    }

    /**
     * 模拟一个订单
     *
     */
    @SuppressWarnings("Duplicates")
    protected MallOrder mockMallOrder(Shop shop ){

        MallOrder mallOrder = new MallOrder();
        mallOrder.setOrderId(random.nextInt()+"1");
        mallOrder.setAgentMarkType("p2");
        mallOrder.setAgentMarkText("XXXX"+random.nextInt());
        mallOrder.setOrderStatus(OrderEnum.OrderStatus.ACTIVE);
        mallOrder.setPaymentType(OrderEnum.PaymentOptions.ALIPAY_PC);
        mallOrder.setOrderSourceType(OrderEnum.OrderSourceType.NORMAL);
        mallOrder.setPayStatus(OrderEnum.PayStatus.PAYED);
        mallOrder.setShipStatus(OrderEnum.ShipStatus.NOT_DELIVER);
        mallOrder.setIsTax(0);
        mallOrder.setIsProtect(0);
        mallOrder.setCreateTime(new Date());
        if (random.nextInt()%2 == 0)
            mallOrder.setShop(shop);
        else
            mallOrder.setBeneficiaryShop(shop);
        return orderRepository.saveAndFlush(mallOrder);

    }

}
