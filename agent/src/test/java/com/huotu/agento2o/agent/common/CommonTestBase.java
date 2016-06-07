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
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.service.common.*;
import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.config.Address;
import com.huotu.agento2o.service.entity.config.InvoiceConfig;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallGoodsType;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.entity.order.MallOrderItem;
import com.huotu.agento2o.service.entity.purchase.*;
import com.huotu.agento2o.service.repository.author.ShopRepository;
import com.huotu.agento2o.service.repository.config.AddressRepository;
import com.huotu.agento2o.service.repository.config.InvoiceConfigRepository;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.goods.MallGoodsTypeRepository;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.order.MallAfterSalesRepository;
import com.huotu.agento2o.service.repository.order.MallOrderItemRepository;
import com.huotu.agento2o.service.repository.order.MallOrderRepository;
import com.huotu.agento2o.service.repository.purchase.*;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.config.AddressService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.Date;
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
public abstract class CommonTestBase extends SpringWebTest {

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
    protected AgentPurchaseOrderItemRepository agentPurchaseOrderItemRepository;
    @Autowired
    protected InvoiceConfigRepository invoiceConfigRepository;
    @Autowired
    protected MallGoodsTypeRepository goodsTypeRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private MallAfterSalesRepository mallAfterSalesRepository;
    @Autowired
    private AgentLevelService agentLevelService;
    //标准类目 羽绒服
    protected MallGoodsType standardGoodsType;

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AgentReturnOrderRepository agentReturnedOrderRepository;

    @Autowired
    private MallOrderItemRepository mallOrderItemRepository;

    @Autowired
    private MallProductRepository mallProductRepository;
    @Before
    public void initBase() {
        standardGoodsType = goodsTypeRepository.findByStandardTypeIdAndDisabledFalseAndCustomerId("50011167", -1);
    }

    @Autowired
    protected MallOrderRepository orderRepository;

    protected MockHttpSession loginAs(String userName, String password, String roleType) throws Exception {
        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType", roleType))
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        saveAuthedSession(session);
        return session;
    }

    @SuppressWarnings("Duplicates")
    protected MallCustomer mockMallCustomer() {
        MallCustomer customer = new MallCustomer();
        customer.setNickName(UUID.randomUUID().toString());
        customer.setUsername(UUID.randomUUID().toString());
        customer.setPassword(passWord);
        return customerService.newCustomer(customer);
    }

    @SuppressWarnings("Duplicates")
    protected Agent mockAgent(MallCustomer mockCustomer, Agent parentAgent) {
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
        if (parentAgent != null) {
            agent.setParentAuthor(parentAgent);
        }
        agent.setAgentLevel(mockAgentLevel(mockCustomer));
        agent.setStatus(AgentStatusEnum.CHECKED);
        agent = agentService.addAgent(agent);
        agentService.flush();
        return agent;
    }

    protected Shop mockShop(MallCustomer mockCustomer, Agent parentAgent) {
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
        if (parentAgent != null) {
            shop.setParentAuthor(parentAgent);
        }
        //密码进行加密保存
        shop.setPassword(passwordEncoder.encode(shop.getPassword()));
        shopRepository.save(shop);
        shopRepository.flush();
        return shop;
    }

    /**
     * 商品自定义分类
     *
     * @return
     */
    protected MallGoodsType mockMallGoodsType(Integer customerId) {
        MallGoodsType goodsType = new MallGoodsType();
        goodsType.setName(UUID.randomUUID().toString());
        goodsType.setDisabled(false);
        goodsType.setCustomerId(customerId);
        return goodsTypeRepository.saveAndFlush(goodsType);
    }

    /**
     * 平台方商品
     *
     * @param customerId
     * @param agentId
     * @return
     */
    protected MallGoods mockMallGoods(Integer customerId, Integer agentId) {
        MallGoods mockMallGoods = new MallGoods();
        mockMallGoods.setCost(random.nextDouble());
        mockMallGoods.setPrice(random.nextDouble());
        mockMallGoods.setCustomerId(customerId);
        //平台方商品
        if (agentId == null) {
            mockMallGoods.setAgentId(0);
        } else {
            mockMallGoods.setAgentId(agentId);
        }
        mockMallGoods.setDisabled(false);
        mockMallGoods.setStore(random.nextInt());
        mockMallGoods.setName(UUID.randomUUID().toString());
        mockMallGoods.setThumbnailPic(UUID.randomUUID().toString());
        return mockMallGoods;
    }

    protected MallGoods mockMallGoods(MallGoods mockMallGoods) {
        return goodsRepository.saveAndFlush(mockMallGoods);
    }

    /**
     * 平台方货品
     *
     * @param mockGoods
     * @return
     */
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
        int store = random.nextInt(100) + 1;
        mockMallProduct.setStore(store);
        mockMallProduct.setFreez(random.nextInt(store));
        return mockMallProduct;
    }

    /**
     * 代理商货品
     *
     * @param mockMallProduct
     * @param mockAuthor
     * @return
     */
    @SuppressWarnings("Duplicates")
    protected AgentProduct mockAgentProduct(MallProduct mockMallProduct, Author mockAuthor) {
        AgentProduct agentProduct = new AgentProduct();
        agentProduct.setAuthor(mockAuthor);
        agentProduct.setProduct(mockMallProduct);
        agentProduct.setGoodsId(mockMallProduct.getGoods().getGoodsId());
        agentProduct.setStore(20);
        agentProduct.setFreez(10);
        agentProduct.setWarning(0);
        agentProduct.setDisabled(false);
        return agentProductRepository.saveAndFlush(agentProduct);
    }

    protected ShoppingCart mockShoppingCart(MallProduct mockMallProduct, Author author) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setAuthor(author);
        shoppingCart.setProduct(mockMallProduct);
        shoppingCart.setNum(random.nextInt(mockMallProduct.getStore() - mockMallProduct.getFreez()) + 1);
        shoppingCart.setCreateTime(new Date());
        return shoppingCartRepository.saveAndFlush(shoppingCart);
    }

    protected ShoppingCart mockShoppingCart(AgentProduct agentProduct, Author author) {
        ShoppingCart shoppingCart = new ShoppingCart();
        shoppingCart.setAuthor(author);
        shoppingCart.setProduct(agentProduct.getProduct());
        if (agentProduct.getStore() - agentProduct.getFreez() == 0) {
            shoppingCart.setNum(0);
        } else {
            shoppingCart.setNum(random.nextInt(agentProduct.getStore() - agentProduct.getFreez()) + 1);
        }
        shoppingCart.setCreateTime(new Date());
        return shoppingCartRepository.saveAndFlush(shoppingCart);
    }

    /**
     * @param author
     * @param type   发票类型：0普通发票；1增值税发票
     * @return
     */
    protected InvoiceConfig mockInvoiceConfig(Author author, int type) {
        InvoiceConfig invoiceConfig = new InvoiceConfig();
        invoiceConfig.setAuthor(author);
        if (type == 0) {
            invoiceConfig.setType(InvoiceEnum.InvoiceTypeStatus.NORMALINVOICE);
            invoiceConfig.setTaxTitle(UUID.randomUUID().toString());
            invoiceConfig.setTaxContent(UUID.randomUUID().toString());
        } else if (type == 1) {
            invoiceConfig.setType(InvoiceEnum.InvoiceTypeStatus.TAXINVOICE);
            invoiceConfig.setTaxTitle(UUID.randomUUID().toString());
            invoiceConfig.setTaxContent(UUID.randomUUID().toString());
            invoiceConfig.setTaxpayerCode(UUID.randomUUID().toString());
            invoiceConfig.setAccountNo(UUID.randomUUID().toString());
            invoiceConfig.setBankName(UUID.randomUUID().toString());
        }
        invoiceConfig.setDefaultType(1);
        InvoiceConfig otherInvoiceConfig = invoiceConfigRepository.findByAuthorAndDefaultType(author, 1);
        if (otherInvoiceConfig != null) {
            otherInvoiceConfig.setDefaultType(0);
            invoiceConfigRepository.save(otherInvoiceConfig);
        }
        return invoiceConfigRepository.saveAndFlush(invoiceConfig);
    }

    protected AgentPurchaseOrder mockAgentPurchaseOrder(Author author) {
        AgentPurchaseOrder purchaseOrder = new AgentPurchaseOrder();
        purchaseOrder.setPOrderId(SerialNo.create());
        purchaseOrder.setAuthor(author);
        purchaseOrder.setCostFreight(0);
        purchaseOrder.setShipName(UUID.randomUUID().toString());
        purchaseOrder.setShipMobile(UUID.randomUUID().toString());
        purchaseOrder.setShipAddr(UUID.randomUUID().toString());
        int randomSendMode = random.nextInt(2);
        purchaseOrder.setSendMode(EnumHelper.getEnumType(PurchaseEnum.SendmentStatus.class, randomSendMode));
        int randomTaxType = random.nextInt(3);
        purchaseOrder.setTaxType(EnumHelper.getEnumType(PurchaseEnum.TaxType.class, randomTaxType));
        purchaseOrder.setCreateTime(new Date());
        if (randomTaxType == PurchaseEnum.TaxType.NORMAL.getCode()) {
            purchaseOrder.setTaxTitle(UUID.randomUUID().toString());
            purchaseOrder.setTaxContent(UUID.randomUUID().toString());
        } else if (randomTaxType == PurchaseEnum.TaxType.TAX.getCode()) {
            purchaseOrder.setTaxTitle(UUID.randomUUID().toString());
            purchaseOrder.setTaxContent(UUID.randomUUID().toString());
            purchaseOrder.setTaxTitle(UUID.randomUUID().toString());
            purchaseOrder.setBankName(UUID.randomUUID().toString());
            purchaseOrder.setAccountNo(UUID.randomUUID().toString());
        }
        int randomStatus = random.nextInt(3);
        purchaseOrder.setStatus(EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, randomStatus));
        if (randomStatus == PurchaseEnum.OrderStatus.CHECKED.getCode()) {
            //审核通过
            int randomPayStatus = random.nextInt(2);
            purchaseOrder.setPayStatus(EnumHelper.getEnumType(PurchaseEnum.PayStatus.class, randomPayStatus));
            if (randomPayStatus == PurchaseEnum.PayStatus.PAYED.getCode()) {
                //已支付
                purchaseOrder.setPayTime(new Date());
                int randomShipStatus = random.nextInt(2);
                purchaseOrder.setShipStatus(EnumHelper.getEnumType(PurchaseEnum.ShipStatus.class, randomShipStatus));
                if (randomShipStatus == PurchaseEnum.ShipStatus.DELIVERED.getCode()) {
                }
            }
            purchaseOrder.setDisabled(false);
        } else if (randomStatus == PurchaseEnum.OrderStatus.RETURNED.getCode()) {
            //审核不通过
            int randomDisabled = random.nextInt(2);
            if (randomDisabled == 0) {
                purchaseOrder.setDisabled(false);
            } else {
                purchaseOrder.setDisabled(true);
            }
        }
        return purchaseOrder;
    }

    protected AgentPurchaseOrder mockAgentPurchaseOrder(AgentPurchaseOrder mockPurchaseOrder) {
        return agentPurchaseOrderRepository.saveAndFlush(mockPurchaseOrder);
    }

    protected AgentPurchaseOrderItem mockAgentPurchaseOrderItem(AgentPurchaseOrder agentPurchaseOrder, MallProduct mallProduct) {
        AgentPurchaseOrderItem orderItem = new AgentPurchaseOrderItem();
        orderItem.setPurchaseOrder(agentPurchaseOrder);
        orderItem.setNum(random.nextInt(100));
        orderItem.setProduct(mallProduct);
        orderItem.setBn(mallProduct.getBn());
        orderItem.setName(mallProduct.getName());
        orderItem.setPdtDesc(mallProduct.getStandard());
        orderItem.setUnit(mallProduct.getUnit());
        orderItem.setPrice(mallProduct.getPrice());
        if (PurchaseEnum.ShipStatus.DELIVERED.equals(agentPurchaseOrder.getShipStatus())) {
            orderItem.setSendNum(orderItem.getNum());
        } else {
            orderItem.setSendNum(0);
        }
        return agentPurchaseOrderItemRepository.saveAndFlush(orderItem);
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
     * 模拟一个订单中的货单
     * @param order
     * @param product
     * @param mallAfterSales
     * @param nums
     * @return
     */
    protected MallOrderItem mockMallOrderItem(MallOrder order, MallProduct product , MallAfterSales mallAfterSales,int nums){
        MallOrderItem mallOrderItem = new MallOrderItem();
        mallOrderItem.setItemId(random.nextLong()+1);
        mallOrderItem.setOrder(order);
        mallOrderItem.setProduct(product);
        mallOrderItem.setNums(nums);
        mallOrderItem.setShipStatus(OrderEnum.ShipStatus.NOT_DELIVER);
        mallOrderItem.setAfterSales(mallAfterSales);
        mallOrderItemRepository.saveAndFlush(mallOrderItem);
        return  mallOrderItem;
    }

    protected MallProduct mockMallProduct(){
        MallProduct mallProduct = new MallProduct();
        mallProduct.setProductId(random.nextInt()+1);
        mallProduct.setGoods(new MallGoods());
        mallProduct.setName("xxx");
        return mallProductRepository.saveAndFlush(mallProduct);
    }

    protected MallAfterSales mockMallAfterSales(Shop shop ,String orderId) {
        MallAfterSales mallAfterSales = new MallAfterSales();
        mallAfterSales.setAfterId(random.nextInt() + "1");
        mallAfterSales.setOrderItem(new MallOrderItem());
        mallAfterSales.setAfterSaleStatus(AfterSaleEnum.AfterSaleStatus.APPLYING);
        mallAfterSales.setOrderId(orderId);
        mallAfterSales.setPayStatus(OrderEnum.PayStatus.ALL_REFUND);
        if (random.nextInt() % 2 == 0)
            mallAfterSales.setShop(shop);
        else
            mallAfterSales.setBeneficiaryShop(shop);
        mallAfterSales.setAfterSaleType(AfterSaleEnum.AfterSaleType.REFUND);
        mallAfterSales.setAfterSalesReason(AfterSaleEnum.AfterSalesReason.GOOD_PROBLEM);
        mallAfterSales.setCreateTime(new Date());
        return mallAfterSalesRepository.saveAndFlush(mallAfterSales);
    }

    protected MallAfterSales mockMallAfterSales(Shop shop) {
        MallAfterSales mallAfterSales = new MallAfterSales();
        mallAfterSales.setAfterId(random.nextInt() + "1");
        mallAfterSales.setOrderItem(new MallOrderItem());
        mallAfterSales.setAfterSaleStatus(AfterSaleEnum.AfterSaleStatus.APPLYING);
        mallAfterSales.setOrderId(random.nextInt() + "1");
        mallAfterSales.setPayStatus(OrderEnum.PayStatus.ALL_REFUND);
        if (random.nextInt() % 2 == 0)
            mallAfterSales.setShop(shop);
        else
            mallAfterSales.setBeneficiaryShop(shop);
        mallAfterSales.setAfterSaleType(AfterSaleEnum.AfterSaleType.REFUND);
        mallAfterSales.setAfterSalesReason(AfterSaleEnum.AfterSalesReason.GOOD_PROBLEM);
        mallAfterSales.setCreateTime(new Date());
        return mallAfterSalesRepository.saveAndFlush(mallAfterSales);
    }

    @SuppressWarnings("Duplicates")
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

    @SuppressWarnings("Duplicates")
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

    protected AgentReturnedOrder mockAgentReturnOrder(Author author){
        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        agentReturnedOrder.setROrderId(SerialNo.create());
        agentReturnedOrder.setAuthor(author);
        agentReturnedOrder.setDisabled(false);
        agentReturnedOrder.setShipStatus(PurchaseEnum.ShipStatus.NOT_DELIVER);
        agentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKING);
        agentReturnedOrder.setPayStatus(PurchaseEnum.PayStatus.NOT_PAYED);
        agentReturnedOrder.setSendmentStatus(PurchaseEnum.SendmentStatus.HOME_DELIVERY);
        agentReturnedOrder.setCreateTime(new Date());
        return agentReturnedOrderRepository.saveAndFlush(agentReturnedOrder);
    }
}
