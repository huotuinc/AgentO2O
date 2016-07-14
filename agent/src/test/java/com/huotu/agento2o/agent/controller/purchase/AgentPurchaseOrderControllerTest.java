/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.controller.purchase;

import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrderItem;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 新增采购单，查询，删除
 * Created by helloztt on 2016/5/25.
 */
public class AgentPurchaseOrderControllerTest extends CommonTestBase {

    private static String BASE_URL = "/purchaseOrder";

    //平台方
    private MallCustomer mockCustomer;
    //一级代理商
    private MallCustomer mockFirstLevelAgent;
    //二级代理商
    private MallCustomer mockSecondLevelAgent;
    //一级代理商下级门店
    private Shop mockFirstLevelShop;
    //二级代理商下级门店
    private Shop mockSecondLevelShop;

    //无规格商品(只有一个货品)
    private List<MallGoods> mockGoodsWith1ProductList = new ArrayList<>();
    //多规格商品(有多个货品)
    private List<MallGoods> mockGoodsWithNProductList = new ArrayList<>();
    //一级代理商货品
    private List<AgentProduct> mockFirstLevelAgentProductList = new ArrayList<>();
    private List<MallGoods> mockFirstLevelAgentGoodsWith1ProductList = new ArrayList<>();
    private List<MallGoods> mockFirstLevelAgentGoodsWithNProductList = new ArrayList<>();
    //一级代理商采购单
    private List<AgentPurchaseOrder> mockFirstLevelAgentPurchaseOrderList = new ArrayList<>();
    //一级代理商购物车
    private List<ShoppingCart> mockFirstLevelAgentShoppingCartList = new ArrayList<>();

    //一级代理商下级门店 货品
    private List<AgentProduct> mockFirstLevelShopProductList = new ArrayList<>();
    //一级代理商下级门店 购物车
    private List<ShoppingCart> mockFirstLevelShopShoppingCartList = new ArrayList<>();
    //一级代理商下级门店采购单
    private List<AgentPurchaseOrder> mockFirstLevelShopPurchaseOrderList = new ArrayList<>();

    //一级代理商下级代理商 购物车
    private List<ShoppingCart> mockSecondLevelAgentShoppingCartList = new ArrayList<>();

    @Before
    @SuppressWarnings("Duplicates")
    public void init() throws Exception {
        //模拟数据
        //用户相关
        mockCustomer = mockMallCustomer();
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelShop = mockShop(mockCustomer, mockSecondLevelAgent.getAgent());

        //平台商品相关
        for (int i = 0; i < random.nextInt(10) + 10; i++) {
            MallGoods mockGoodsWith1Products = mockMallGoods(mockCustomer.getCustomerId(), false);
            List<MallProduct> mockGoodsWith1ProductsList = new ArrayList<>();
            mockGoodsWith1ProductsList.add(mockMallProduct(mockGoodsWith1Products));
            mockGoodsWith1Products.setProducts(mockGoodsWith1ProductsList);
            mockGoodsWith1Products.setStore(mockGoodsWith1ProductsList.stream().mapToInt(p -> p.getStore()).sum());
            mockGoodsWith1Products = mockMallGoods(mockGoodsWith1Products);
            mockGoodsWith1ProductList.add(mockGoodsWith1Products);
        }

        for (int i = 0; i < random.nextInt(10) + 10; i++) {
            MallGoods mockGoodsWithNProducts = mockMallGoods(mockCustomer.getCustomerId(), false);
            List<MallProduct> productList = new ArrayList<>();
            for (int j = 0; j < random.nextInt(10) + 2; j++) {
                productList.add(mockMallProduct(mockGoodsWithNProducts));
            }
            mockGoodsWithNProducts.setProducts(productList);
            mockGoodsWithNProducts.setStore(mockGoodsWithNProductList.stream().mapToInt(p -> p.getStore()).sum());
            mockGoodsWithNProducts = mockMallGoods(mockGoodsWithNProducts);
            mockGoodsWithNProductList.add(mockGoodsWithNProducts);
        }

        //一级代理商货品
        for (int i = 0; i <= random.nextInt(mockGoodsWith1ProductList.size() - 5) + 5; i++) {
            mockFirstLevelAgentGoodsWith1ProductList.add(mockGoodsWith1ProductList.get(i));
            MallGoods mockGoodsWith1Products = mockGoodsWith1ProductList.get(i);
            mockGoodsWith1Products.getProducts().forEach(product -> {
                mockFirstLevelAgentProductList.add(mockAgentProduct(product, mockFirstLevelAgent));
            });
        }
        for (int i = 0; i <= random.nextInt(mockGoodsWithNProductList.size() - 5) + 5; i++) {
            mockFirstLevelAgentGoodsWithNProductList.add(mockGoodsWithNProductList.get(i));
            for (int j = 0; j < random.nextInt(mockGoodsWithNProductList.get(i).getProducts().size()) + 1; j++) {
                mockFirstLevelAgentProductList.add(mockAgentProduct(mockGoodsWithNProductList.get(i).getProducts().get(j), mockFirstLevelAgent));
            }
        }

        //一级代理商购物车(保证购物车中至少有2条记录)
        for (int i = 0; i <= random.nextInt(mockGoodsWith1ProductList.size() - 5) + 5; i++) {
            ShoppingCart mockShoppingCart = mockShoppingCart(mockGoodsWith1ProductList.get(i).getProducts().get(0), mockFirstLevelAgent);
            mockFirstLevelAgentShoppingCartList.add(mockShoppingCart);
        }
        for (int i = 0; i <= random.nextInt(mockGoodsWithNProductList.size() - 5) + 5; i++) {
            List<MallProduct> mockProductList = mockGoodsWithNProductList.get(i).getProducts();
            for (int j = 0; j <= random.nextInt(mockProductList.size()); j++) {
                ShoppingCart mockShoppingCart = mockShoppingCart(mockProductList.get(j), mockFirstLevelAgent);
                mockFirstLevelAgentShoppingCartList.add(mockShoppingCart);
            }
        }
        //一级代理商采购单（数据仅用于查询，可能与货品的预占库存不匹配）,保证至少有10个
        AgentPurchaseOrderItem mockPurchaseOrderItem = null;
        AgentPurchaseOrder mockPurchaseOrder = null;
        List<AgentPurchaseOrderItem> itemList = new ArrayList<>();
        int randomProduct = 0;
        //可删除
        mockPurchaseOrder = mockDisablePurchaseOrder(mockFirstLevelAgent);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelAgentPurchaseOrderList.add(mockPurchaseOrder);
        //可审核
        mockPurchaseOrder = mockCheckablePurchaseOrder(mockFirstLevelAgent);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelAgentPurchaseOrderList.add(mockPurchaseOrder);
        //可支付
        mockPurchaseOrder = mockPayablePurchaseOrder(mockFirstLevelAgent);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelAgentPurchaseOrderList.add(mockPurchaseOrder);
        //可发货
        mockPurchaseOrder = mockDeliverablePurchaseOrder(mockFirstLevelAgent);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelAgentPurchaseOrderList.add(mockPurchaseOrder);
        //可收货
        mockPurchaseOrder = mockReceivablePurchaseOrder(mockFirstLevelAgent);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelAgentPurchaseOrderList.add(mockPurchaseOrder);

        for (int i = 0; i <= random.nextInt(mockFirstLevelAgentProductList.size() - 3) + 3; i++) {
            mockPurchaseOrder = mockAgentPurchaseOrder(mockFirstLevelAgent);
            randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
            mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
            mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
            itemList.clear();
            itemList.add(mockPurchaseOrderItem);
            mockPurchaseOrder.setOrderItemList(itemList);
            mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
            mockFirstLevelAgentPurchaseOrderList.add(mockPurchaseOrder);
        }

        //一级代理商下级门店购物车
        for (int i = 0; i <= random.nextInt(mockFirstLevelAgentProductList.size() - 3) + 3; i++) {
            ShoppingCart mockShoppingCart = mockShoppingCart(mockFirstLevelAgentProductList.get(i), mockFirstLevelShop);
            mockFirstLevelShopShoppingCartList.add(mockShoppingCart);
        }
        //一级代理商下级门店采购单
        //可删除
        mockPurchaseOrder = mockDisablePurchaseOrder(mockFirstLevelShop);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelShopPurchaseOrderList.add(mockPurchaseOrder);
        //可审核
        mockPurchaseOrder = mockCheckablePurchaseOrder(mockFirstLevelShop);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelShopPurchaseOrderList.add(mockPurchaseOrder);
        //可支付
        mockPurchaseOrder = mockPayablePurchaseOrder(mockFirstLevelShop);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelShopPurchaseOrderList.add(mockPurchaseOrder);
        //可发货
        mockPurchaseOrder = mockDeliverablePurchaseOrder(mockFirstLevelShop);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelShopPurchaseOrderList.add(mockPurchaseOrder);
        //可收货
        mockPurchaseOrder = mockReceivablePurchaseOrder(mockFirstLevelShop);
        randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
        mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
        mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
        itemList.clear();
        itemList.add(mockPurchaseOrderItem);
        mockPurchaseOrder.setOrderItemList(itemList);
        mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
        mockFirstLevelShopPurchaseOrderList.add(mockPurchaseOrder);

        for (int i = 0; i <= random.nextInt(mockFirstLevelAgentProductList.size() - 3) + 3; i++) {
            mockPurchaseOrder = mockAgentPurchaseOrder(mockFirstLevelShop);
            randomProduct = random.nextInt(mockFirstLevelAgentProductList.size());
            mockPurchaseOrderItem = mockAgentPurchaseOrderItem(mockPurchaseOrder, mockFirstLevelAgentProductList.get(randomProduct).getProduct());
            mockPurchaseOrder.setFinalAmount(mockPurchaseOrderItem.getPrice() * mockPurchaseOrderItem.getNum());
            itemList.clear();
            itemList.add(mockPurchaseOrderItem);
            mockPurchaseOrder.setOrderItemList(itemList);
            mockPurchaseOrder = mockAgentPurchaseOrder(mockPurchaseOrder);
            mockFirstLevelShopPurchaseOrderList.add(mockPurchaseOrder);
        }


        //一级代理商下级代理商购物车
        for (int i = 0; i <= random.nextInt(mockFirstLevelAgentProductList.size() - 3) + 3; i++) {
            ShoppingCart mockShoppingCart = mockShoppingCart(mockFirstLevelAgentProductList.get(i), mockSecondLevelAgent);
            mockSecondLevelAgentShoppingCartList.add(mockShoppingCart);
        }
    }

    /**
     * 一级代理商新增采购单
     *
     * @throws Exception
     */
    @Test
    public void testAddPurchaseByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        // 1.不传参数 校验
        // 1-1.不传 shoppingCartIds
        MvcResult resultWithNoId = mockMvc.perform(
                post(controllerUrl)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoId = new String(resultWithNoId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoId = JSONObject.parseObject(contentWithNoId);
        Assert.assertEquals("500", objWithNoId.getString("code"));
        Assert.assertEquals("没有传输数据", objWithNoId.getString("msg"));

        // 1-2.不传 收货人信息
        MvcResult resultWithNoShipInfo = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoShipInfo = new String(resultWithNoShipInfo.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoShipInfo = JSONObject.parseObject(contentWithNoShipInfo);
        Assert.assertEquals("请输入收货信息！", objWithNoShipInfo.getString("msg"));

        // 1-3.不传 配送方式
        MvcResult resultWithNoSendMode = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoSendMode = new String(resultWithNoSendMode.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoSendMode = JSONObject.parseObject(contentWithNoSendMode);
        Assert.assertEquals("请选择配送方式！", objWithNoSendMode.getString("msg"));

        // 1-4.不传 发票类型
        MvcResult resultWithNoTaxType = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoTaxType = new String(resultWithNoTaxType.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoTaxType = JSONObject.parseObject(contentWithNoTaxType);
        Assert.assertEquals("请选择发票类型！", objWithNoTaxType.getString("msg"));

        // 1-5.配送方式 错误
        MvcResult resultWithErrorSendMode = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "-1")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithErrorSendMode = new String(resultWithErrorSendMode.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithErrorSendMode = JSONObject.parseObject(contentWithErrorSendMode);
        Assert.assertEquals("请选择配送方式！", objWithErrorSendMode.getString("msg"));

        // 1-6.发票类型 错误
        MvcResult resultWithErrorTaxType = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "-1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithErrorTaxType = new String(resultWithErrorTaxType.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithErrorTaxType = JSONObject.parseObject(contentWithErrorTaxType);
        Assert.assertEquals("请选择发票类型！", objWithErrorTaxType.getString("msg"));

        // 1-7.不传 普通发票信息
        MvcResult resultWithNoNormalInvoiceInfo = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoNormalInvoiceInfo = new String(resultWithNoNormalInvoiceInfo.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoNormalInvoiceInfo = JSONObject.parseObject(contentWithNoNormalInvoiceInfo);
        Assert.assertEquals("请输入普通发票信息！", objWithNoNormalInvoiceInfo.getString("msg"));

        // 1-8.不传 增值税发票信息
        MvcResult resultWithNoTaxInvoiceInfo = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "2"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoTaxInvoiceInfo = new String(resultWithNoTaxInvoiceInfo.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoTaxInvoiceInfo = JSONObject.parseObject(contentWithNoTaxInvoiceInfo);
        Assert.assertEquals("请输入增值税发票信息！", objWithNoTaxInvoiceInfo.getString("msg"));

        // 2.shoppingCartIds 不存在
        MvcResult resultWithErrorId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", "0")
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithErrorId = new String(resultWithErrorId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithErrorId = JSONObject.parseObject(contentWithErrorId);
        Assert.assertEquals("500", objWithErrorId.getString("code"));
        Assert.assertEquals("没有传输数据", objWithErrorId.getString("msg"));

        // 3.库存不足
//        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockFirstLevelAgent);
        ShoppingCart mockShoppingCart = mockFirstLevelAgentShoppingCartList.get(0);
        mockShoppingCart.setNum(mockShoppingCart.getProduct().getStore() - mockShoppingCart.getProduct().getFreez() + 1);
        mockShoppingCart = shoppingCartRepository.saveAndFlush(mockShoppingCart);
        MvcResult resultWithNumTooMuchId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockShoppingCart.getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNumTooMuchId = new String(resultWithNumTooMuchId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNumTooMuchId = JSONObject.parseObject(contentWithNumTooMuchId);
        Assert.assertEquals("库存不足，下单失败！", objWithNumTooMuchId.getString("msg"));
        /*agentPurchaseOrderRepository.flush();
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockFirstLevelAgent);
        assertEquals(0,afterOrderList.size() - beforeOrderList.size());*/

    }

    /**
     * 一级代理商登录 新增采购单成功（单个货品）
     *
     * @throws Exception
     */
    @Test
    @SuppressWarnings("Duplicates")
    public void testAddPurchaseOneIdByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        // 4.新增采购单成功
        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockFirstLevelAgent.getAuthorAgent(), mockFirstLevelAgent.getAuthorShop());
        MvcResult result = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockFirstLevelAgent.getAuthorAgent(), mockFirstLevelAgent.getAuthorShop());
        Assert.assertEquals(1, afterOrderList.size() - beforeOrderList.size());
        ShoppingCart deleteShoppingCart = shoppingCartRepository.findOne(mockFirstLevelAgentShoppingCartList.get(0).getId());
        Assert.assertNull(deleteShoppingCart);
    }

    /**
     * 一级代理商下级门店登录 新增采购单成功（单个货品）
     *
     * @throws Exception
     */
    @Test
    public void testAddPurchaseOneIdByFirstLevelShop() throws Exception {
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        // 4.新增采购单成功
        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockFirstLevelShop.getAuthorAgent(), mockFirstLevelShop.getAuthorShop());
        MvcResult result = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelShopShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockFirstLevelShop.getAuthorAgent(), mockFirstLevelShop.getAuthorShop());
        Assert.assertEquals(1, afterOrderList.size() - beforeOrderList.size());
        ShoppingCart deleteShoppingCart = shoppingCartRepository.findOne(mockFirstLevelShopShoppingCartList.get(0).getId());
        Assert.assertNull(deleteShoppingCart);
    }

    /**
     * 一级代理商下级代理商登录 新增采购单成功（单个货品）
     *
     * @throws Exception
     */
    @Test
    @SuppressWarnings("Duplicates")
    public void testAddPurchaseOneIdBySecondLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockSecondLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        // 4.新增采购单成功
        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockSecondLevelAgent.getAuthorAgent(), mockFirstLevelShop.getAuthorShop());
        MvcResult result = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockSecondLevelAgentShoppingCartList.get(0).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockSecondLevelAgent.getAuthorAgent(), mockSecondLevelAgent.getAuthorShop());
        Assert.assertEquals(1, afterOrderList.size() - beforeOrderList.size());
        ShoppingCart deleteShoppingCart = shoppingCartRepository.findOne(mockSecondLevelAgentShoppingCartList.get(0).getId());
        Assert.assertNull(deleteShoppingCart);
    }

    /**
     * 一级代理商登录 新增采购单成功（多个货品）
     *
     * @throws Exception
     */
    @Test
    public void testAddPurchaseTwoIdByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        //多个id
        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockFirstLevelAgent.getAuthorAgent(), mockFirstLevelAgent.getAuthorShop());
        int beforeFreez0 = mockFirstLevelAgentShoppingCartList.get(0).getProduct().getFreez();
        int beforeFreez1 = mockFirstLevelAgentShoppingCartList.get(1).getProduct().getFreez();
        MvcResult resultWithIds = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId()))
                        .param("shoppingCartIds", String.valueOf(mockFirstLevelAgentShoppingCartList.get(1).getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithIds = new String(resultWithIds.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithIds = JSONObject.parseObject(contentWithIds);
        Assert.assertEquals("200", objWithIds.getString("code"));
        ShoppingCart deleteShoppingCart = shoppingCartRepository.findOne(mockFirstLevelAgentShoppingCartList.get(0).getId());
        Assert.assertNull(deleteShoppingCart);
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAgentAndShop(mockFirstLevelAgent.getAuthorAgent(), mockFirstLevelAgent.getAuthorShop());
        Assert.assertEquals(1, afterOrderList.size() - beforeOrderList.size());
        MallProduct afterProduct0 = productRepository.findOne(mockFirstLevelAgentShoppingCartList.get(0).getProduct().getProductId());
        MallProduct afterProduct1 = productRepository.findOne(mockFirstLevelAgentShoppingCartList.get(1).getProduct().getProductId());
        Assert.assertEquals(mockFirstLevelAgentShoppingCartList.get(0).getNum(),
                Integer.valueOf(afterProduct0.getFreez() - beforeFreez0));
        Assert.assertEquals(mockFirstLevelAgentShoppingCartList.get(1).getNum(),
                Integer.valueOf(afterProduct1.getFreez() - beforeFreez1));
    }

    /**
     * 一级代理商下级门店 新增采购单 库存不足
     *
     * @throws Exception
     */
    @Test
    public void testAddPurchaseByFirstLevelShop() throws Exception {
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));

        // 库存不足
        ShoppingCart mockShoppingCart = mockFirstLevelShopShoppingCartList.get(0);
        AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(mockFirstLevelShop.getAgent(), mockShoppingCart.getProduct());
        mockShoppingCart.setNum(parentAgentProduct.getStore() - parentAgentProduct.getFreez() + 1);
        mockShoppingCart = shoppingCartRepository.saveAndFlush(mockShoppingCart);
        MvcResult resultWithNumTooMuchId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockShoppingCart.getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNumTooMuchId = new String(resultWithNumTooMuchId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNumTooMuchId = JSONObject.parseObject(contentWithNumTooMuchId);
        Assert.assertEquals("库存不足，下单失败！", objWithNumTooMuchId.getString("msg"));
    }

    /**
     * 一级代理商下级代理商 新增采购单 库存不足测试
     *
     * @throws Exception
     */
    @Test
    public void testAddPurchaseBySecondLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockSecondLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        // 库存不足
        ShoppingCart mockShoppingCart = mockSecondLevelAgentShoppingCartList.get(0);
        AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(mockFirstLevelAgent.getAgent(), mockShoppingCart.getProduct());
        mockShoppingCart.setNum(parentAgentProduct.getStore() - parentAgentProduct.getFreez() + 1);
        mockShoppingCart = shoppingCartRepository.saveAndFlush(mockShoppingCart);
        MvcResult resultWithNumTooMuchId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartIds", String.valueOf(mockShoppingCart.getId()))
                        .param("shipName", "shipName")
                        .param("shipMobile", "shipMobile")
                        .param("shipAddr", "shipAddr")
                        .param("sendModeCode", "0")
                        .param("taxTypeCode", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNumTooMuchId = new String(resultWithNumTooMuchId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNumTooMuchId = JSONObject.parseObject(contentWithNumTooMuchId);
        Assert.assertEquals("库存不足，下单失败！", objWithNumTooMuchId.getString("msg"));
    }

    /**
     * 一级代理商登录 采购单列表
     *
     * @throws Exception
     */
    @Test
    public void testShowPurchaseOrderListByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/showPurchaseOrderList";
        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        // 1.没有搜索条件
        MvcResult resultWithNoSearch = mockMvc.perform(post(controllerUrl)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("purchaseOrderList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andReturn();

        List<AgentPurchaseOrder> purchaseOrderListWithNoSearch = (List<AgentPurchaseOrder>) resultWithNoSearch.getModelAndView().getModel().get("purchaseOrderList");
        Integer pageSizeWithNoSearch = (Integer) resultWithNoSearch.getModelAndView().getModel().get("pageSize");
        Assert.assertNotNull(purchaseOrderListWithNoSearch);
        Assert.assertEquals(Math.min(pageSizeWithNoSearch, mockFirstLevelAgentPurchaseOrderList.size()), purchaseOrderListWithNoSearch.size());
        for (int i = 0; i < purchaseOrderListWithNoSearch.size(); i++) {
            mockSecondLevelAgentShoppingCartList.contains(purchaseOrderListWithNoSearch.get(i));
        }

        // 2.按采购单号搜索
        MvcResult resultWithPOrderId = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("pOrderId", mockFirstLevelAgentPurchaseOrderList.get(0).getPOrderId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("purchaseOrderList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andReturn();

        List<AgentPurchaseOrder> purchaseOrderListWithPOrderId = (List<AgentPurchaseOrder>) resultWithPOrderId.getModelAndView().getModel().get("purchaseOrderList");
        Assert.assertNotNull(purchaseOrderListWithPOrderId);
        Assert.assertEquals(1, purchaseOrderListWithPOrderId.size());
        Assert.assertEquals(mockFirstLevelAgentPurchaseOrderList.get(0).getPOrderId(), purchaseOrderListWithPOrderId.get(0).getPOrderId());

        // 3.按商品名称搜索
        String searchGoodsName = mockFirstLevelAgentPurchaseOrderList.get(0).getOrderItemList().get(0).getName();
        long goodsNameCount = mockFirstLevelAgentPurchaseOrderList.stream().filter(order ->
                order.getOrderItemList().stream().filter(
                        item -> item.getProduct().getName().equals(searchGoodsName)).count() > 0
        ).count();
        MvcResult resultWithGoodsName = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("orderItemName", searchGoodsName))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("purchaseOrderList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andReturn();
        List<AgentPurchaseOrder> purchaseOrderListWithGoodsName = (List<AgentPurchaseOrder>) resultWithGoodsName.getModelAndView().getModel().get("purchaseOrderList");
        Integer pageSizeWithGoodsName = (Integer) resultWithNoSearch.getModelAndView().getModel().get("pageSize");
        Assert.assertNotNull(purchaseOrderListWithGoodsName);
        Assert.assertEquals(Math.min(pageSizeWithGoodsName, goodsNameCount), purchaseOrderListWithGoodsName.size());
        if (purchaseOrderListWithGoodsName.size() > 0) {
            purchaseOrderListWithGoodsName.forEach(order -> {
                long filterCount = order.getOrderItemList().stream().filter(item -> item.getProduct().getName().equals(searchGoodsName)).count();
                Assert.assertTrue(filterCount > 0);
            });
        }

        // 4.按付款状态搜索(已支付)
        long payedCount = mockFirstLevelAgentPurchaseOrderList.stream().filter(p ->
                p.getPayStatus().equals(PurchaseEnum.PayStatus.PAYED)
        ).count();
        MvcResult resultWithPayStatus = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("payStatusCode", String.valueOf(PurchaseEnum.PayStatus.PAYED.getCode())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("purchaseOrderList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andReturn();
        List<AgentPurchaseOrder> purchaseOrderListWithPayStatus = (List<AgentPurchaseOrder>) resultWithPayStatus.getModelAndView().getModel().get("purchaseOrderList");
        Integer pageSizeWithPayStatus = (Integer) resultWithPayStatus.getModelAndView().getModel().get("pageSize");
        Assert.assertNotNull(purchaseOrderListWithPayStatus);
        Assert.assertEquals(Math.min(pageSizeWithPayStatus, payedCount), purchaseOrderListWithPayStatus.size());
        if (purchaseOrderListWithPayStatus.size() > 0) {
            purchaseOrderListWithPayStatus.forEach(p -> {
                Assert.assertEquals(PurchaseEnum.PayStatus.PAYED.getCode(), p.getPayStatus().getCode());
            });
        }

        // 5.按发货状态搜索(已发货)
        long deliveryCount = mockFirstLevelAgentPurchaseOrderList.stream().filter(p ->
                p.getShipStatus().equals(PurchaseEnum.ShipStatus.DELIVERED)
        ).count();
        MvcResult resultWithDeliveryStatus = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("shipStatusCode", String.valueOf(PurchaseEnum.ShipStatus.DELIVERED.getCode())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("purchaseOrderList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andReturn();
        List<AgentPurchaseOrder> purchaseOrderListWithDeliveryStatus = (List<AgentPurchaseOrder>) resultWithDeliveryStatus.getModelAndView().getModel().get("purchaseOrderList");
        Integer pageSizeWithDeliveryCount = (Integer) resultWithDeliveryStatus.getModelAndView().getModel().get("pageSize");
        Assert.assertNotNull(purchaseOrderListWithDeliveryStatus);
        Assert.assertEquals(Math.min(pageSizeWithDeliveryCount, deliveryCount), purchaseOrderListWithDeliveryStatus.size());
        if (purchaseOrderListWithDeliveryStatus.size() > 0) {
            purchaseOrderListWithDeliveryStatus.forEach(p -> {
                Assert.assertEquals(PurchaseEnum.ShipStatus.DELIVERED.getCode(), p.getShipStatus().getCode());
            });
        }

        // 6.按审核状态搜索(已审核)
        long statusCount = mockFirstLevelAgentPurchaseOrderList.stream().filter(p ->
                p.getStatus().equals(PurchaseEnum.OrderStatus.CHECKED)
        ).count();
        MvcResult resultWithStatus = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("statusCode", String.valueOf(PurchaseEnum.OrderStatus.CHECKED.getCode())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("purchaseOrderList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andReturn();
        List<AgentPurchaseOrder> purchaseOrderListWithStatus = (List<AgentPurchaseOrder>) resultWithStatus.getModelAndView().getModel().get("purchaseOrderList");
        Integer pageSizeWithStatus = (Integer) resultWithStatus.getModelAndView().getModel().get("pageSize");
        Assert.assertNotNull(purchaseOrderListWithStatus);
        Assert.assertEquals(Math.min(pageSizeWithStatus, statusCount), purchaseOrderListWithStatus.size());
        if (purchaseOrderListWithStatus.size() > 0) {
            purchaseOrderListWithStatus.forEach(p -> {
                Assert.assertEquals(PurchaseEnum.OrderStatus.CHECKED.getCode(), p.getStatus().getCode());
            });
        }

        // 7.按下单时间搜索
        // TODO: 2016/5/30
        // 8.按支付时间搜索
        // TODO: 2016/5/30
    }

    /**
     * 一级代理商登录 采购单详细
     *
     * @throws Exception
     */
    @Test
    public void testShowPurchaseOrderDetailByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/showPurchaseOrderDetail";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        //1.不传参数
        MvcResult resultWithNoParam = mockMvc.perform(get(controllerUrl).session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoParam = new String(resultWithNoParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(contentWithNoParam);
        Assert.assertEquals("Required String parameter 'pOrderId' is not present", obj.getString("msg"));

        //2.参数为空
        MvcResult resultWithEmptyParam = mockMvc.perform(get(controllerUrl).session(session).param("pOrderId", ""))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithEmptyParam = new String(resultWithEmptyParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithEmptyParam = JSONObject.parseObject(contentWithEmptyParam);
        Assert.assertEquals("采购单不存在！", objWithEmptyParam.getString("msg"));

        //3.传正确参数
        MvcResult result = mockMvc.perform(get(controllerUrl)
                .session(session)
                .param("pOrderId", mockFirstLevelAgentPurchaseOrderList.get(0).getPOrderId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("deliveryList"))
                .andExpect(model().attributeExists("purchaseOrder"))
                .andExpect(model().attributeExists("sendmentEnum"))
                .andExpect(model().attributeExists("taxTypeEnum"))
                .andReturn();
        AgentPurchaseOrder realAgentPurchaseOrder = (AgentPurchaseOrder) result.getModelAndView().getModel().get("purchaseOrder");
        Assert.assertEquals(mockFirstLevelAgentPurchaseOrderList.get(0).getPOrderId(), realAgentPurchaseOrder.getPOrderId());

    }

    /**
     * 一级代理商登录 取消采购单
     *
     * @throws Exception
     */
    @Test
    public void testDeletePurchaseOrderByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/deletePurchaseOrder";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        //获取一个不可取消的采购单
        AgentPurchaseOrder cannotDeletePurchaseOrder = mockFirstLevelAgentPurchaseOrderList.stream().filter(p -> !p.deletable()).findAny().get();
        //获取一个可取消的采购单
        AgentPurchaseOrder deletablePurchaseOrder = mockFirstLevelAgentPurchaseOrderList.stream().filter(p -> p.deletable()).findAny().get();

        //1.不传参数
        MvcResult resultWithNoParam = mockMvc.perform(post(controllerUrl)
                .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoParam = new String(resultWithNoParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoParam = JSONObject.parseObject(contentWithNoParam);
        Assert.assertEquals("Required String parameter 'pOrderId' is not present", objWithNoParam.getString("msg"));

        //2.参数为空
        MvcResult resultWithEmptyParam = mockMvc.perform(post(controllerUrl)
                .session(session).param("pOrderId", ""))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithEmptyParam = new String(resultWithEmptyParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithEmptyParam = JSONObject.parseObject(contentWithEmptyParam);
        Assert.assertEquals(ResultCodeEnum.DATA_NULL.getResultCode(), objWithEmptyParam.getIntValue("code"));

        //3.传不可取消的采购单单号
        MvcResult resultWithCanNotDelete = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("pOrderId", cannotDeletePurchaseOrder.getPOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithCanNotDeleteParam = new String(resultWithCanNotDelete.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithCanNotDeleteParam = JSONObject.parseObject(contentWithCanNotDeleteParam);
        Assert.assertEquals("采购单已审核或已支付，无法删除！", objWithCanNotDeleteParam.getString("msg"));

        //4.传可取消的采购单单号
        MvcResult result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("pOrderId", deletablePurchaseOrder.getPOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultCode(), obj.getIntValue("code"));
    }

    /**
     * 一级代理商登录 支付采购单
     *
     * @throws Exception
     */
    @Test
    public void testPayPurchaseOrderByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/payPurchaseOrder";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        //获取一个可支付的采购单
        AgentPurchaseOrder payablePurchaseOrder = mockFirstLevelAgentPurchaseOrderList.stream().filter(p -> p.payabled()).findAny().get();
        //获取一个不可支付的采购单
        AgentPurchaseOrder cannotPayPurchaseOrder = mockFirstLevelAgentPurchaseOrderList.stream().filter(p -> !p.payabled()).findAny().get();
        //1.不传参数
        MvcResult resultWithNoParam = mockMvc.perform(post(controllerUrl)
                .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoParam = new String(resultWithNoParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoParam = JSONObject.parseObject(contentWithNoParam);
        Assert.assertEquals("Required String parameter 'pOrderId' is not present", objWithNoParam.getString("msg"));

        //2.参数为空
        MvcResult resultWithEmptyParam = mockMvc.perform(post(controllerUrl)
                .session(session).param("pOrderId", ""))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithEmptyParam = new String(resultWithEmptyParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithEmptyParam = JSONObject.parseObject(contentWithEmptyParam);
        Assert.assertEquals(ResultCodeEnum.DATA_NULL.getResultCode(), objWithEmptyParam.getIntValue("code"));

        //3.传不可取消的采购单单号
        MvcResult resultWithCanNotDelete = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("pOrderId", cannotPayPurchaseOrder.getPOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithCanNotDeleteParam = new String(resultWithCanNotDelete.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithCanNotDeleteParam = JSONObject.parseObject(contentWithCanNotDeleteParam);
        Assert.assertEquals("采购单未审核或已已支付，无法支付！", objWithCanNotDeleteParam.getString("msg"));

        //4.传可取消的采购单单号
        MvcResult result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("pOrderId", payablePurchaseOrder.getPOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultCode(), obj.getIntValue("code"));

    }

    @Test
    public void testReceivePurchaseOrderByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/receive";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        //获取一个可支付的采购单
        AgentPurchaseOrder receivablePurchaseOrder = mockFirstLevelAgentPurchaseOrderList.stream().filter(p -> p.receivable()).findAny().get();
        //获取一个不可支付的采购单
        AgentPurchaseOrder cannotReceivePurchaseOrder = mockFirstLevelAgentPurchaseOrderList.stream().filter(p -> !p.receivable()).findAny().get();
        //1.不传参数
        MvcResult resultWithNoParam = mockMvc.perform(post(controllerUrl)
                .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoParam = new String(resultWithNoParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoParam = JSONObject.parseObject(contentWithNoParam);
        Assert.assertEquals("Required String parameter 'pOrderId' is not present", objWithNoParam.getString("msg"));

        //2.参数为空
        MvcResult resultWithEmptyParam = mockMvc.perform(post(controllerUrl)
                .session(session).param("pOrderId", ""))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithEmptyParam = new String(resultWithEmptyParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithEmptyParam = JSONObject.parseObject(contentWithEmptyParam);
        Assert.assertEquals(ResultCodeEnum.DATA_NULL.getResultCode(), objWithEmptyParam.getIntValue("code"));

        //3.传不可取消的采购单单号
        MvcResult resultWithCanNotDelete = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("pOrderId", cannotReceivePurchaseOrder.getPOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithCanNotParam = new String(resultWithCanNotDelete.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithCanNotParam = JSONObject.parseObject(contentWithCanNotParam);
        Assert.assertEquals("采购单无法确认收货！", objWithCanNotParam.getString("msg"));

        //4.传可取消的采购单单号
        MvcResult result = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("pOrderId", receivablePurchaseOrder.getPOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultCode(), obj.getIntValue("code"));
    }

    /**
     * 显示下级采购单列表
     *
     * @throws Exception
     */
    @Test
    public void testShowAgentPurchaseOrderList() throws Exception {
        String controllerUrl = BASE_URL + "/showAgentPurchaseOrderList";

        //1.门店登录（预期没有权限）
        MockHttpSession shopSession = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult resultWithErrorRole = mockMvc.perform(get(controllerUrl)
                .session(shopSession))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithErrorRole = new String(resultWithErrorRole.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithErrorRole = JSONObject.parseObject(contentWithErrorRole);
        Assert.assertEquals("没有权限", objWithErrorRole.getString("msg"));

        //2.无搜索条件
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult resultWithNoSearch = mockMvc.perform(get(controllerUrl)
                .session(session))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("purchaseOrderList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andExpect(model().attributeExists("payStatusEnums"))
                .andExpect(model().attributeExists("shipStatusEnums"))
                .andExpect(model().attributeExists("orderStatusEnums"))
                .andReturn();
        List<AgentPurchaseOrder> realPurchaseOrderList = (List<AgentPurchaseOrder>) resultWithNoSearch.getModelAndView().getModel().get("purchaseOrderList");
        Assert.assertEquals(mockFirstLevelShopPurchaseOrderList.size(), realPurchaseOrderList.size());
    }

}