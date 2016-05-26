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
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.junit.Assert.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 新增采购单，审核，付款，发货，确认收货
 * Created by helloztt on 2016/5/25.
 */
public class AgentPurchaseOrderControllerTest extends CommonTestBase {

    private static String BASE_URL = "/purchaseOrder";

    //平台方
    private MallCustomer mockCustomer;
    //一级代理商
    private Agent mockFirstLevelAgent;
    //二级代理商
    private Agent mockSecondLevelAgent;
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

    //一级代理商下级门店 购物车
    private List<ShoppingCart> mockFirstLevelShopShoppingCartList = new ArrayList<>();

    //一级代理商下级代理商 购物车
    private List<ShoppingCart> mockSecondLevelAgentShoppingCartList = new ArrayList<>();

    @Before
    @SuppressWarnings("Duplicates")
    public void init() throws Exception {
        //模拟数据
        //用户相关
        mockCustomer = mockMallCustomer();
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent);
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent);
        mockSecondLevelShop = mockShop(mockCustomer, mockSecondLevelAgent);

        //平台商品相关
        for (int i = 0; i < random.nextInt(10)+10; i++) {
            MallGoods mockGoodsWith1Products = mockMallGoods(mockCustomer.getCustomerId(), null);
            List<MallProduct> mockGoodsWith1ProductsList = new ArrayList<>();
            mockGoodsWith1ProductsList.add(mockMallProduct(mockGoodsWith1Products));
            mockGoodsWith1Products.setProducts(mockGoodsWith1ProductsList);
            mockGoodsWith1Products.setStore(mockGoodsWith1ProductsList.stream().mapToInt(p -> p.getStore()).sum());
            mockGoodsWith1Products = mockMallGoods(mockGoodsWith1Products);
            mockGoodsWith1ProductList.add(mockGoodsWith1Products);
        }

        for (int i = 0; i < random.nextInt(10)+10; i++) {
            MallGoods mockGoodsWithNProducts = mockMallGoods(mockCustomer.getCustomerId(), null);
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
        for (int i = 0; i <= random.nextInt(mockGoodsWith1ProductList.size()); i++) {
            mockFirstLevelAgentGoodsWith1ProductList.add(mockGoodsWith1ProductList.get(i));
            MallGoods mockGoodsWith1Products = mockGoodsWith1ProductList.get(i);
            mockGoodsWith1Products.getProducts().forEach(product -> {
                mockFirstLevelAgentProductList.add(mockAgentProduct(product, mockFirstLevelAgent));
            });
        }
        for (int i = 0; i <= random.nextInt(mockGoodsWithNProductList.size()); i++) {
            mockFirstLevelAgentGoodsWithNProductList.add(mockGoodsWithNProductList.get(i));
            for (int j = 0; j < random.nextInt(mockGoodsWithNProductList.get(i).getProducts().size()) + 1; j++) {
                mockFirstLevelAgentProductList.add(mockAgentProduct(mockGoodsWithNProductList.get(i).getProducts().get(j), mockFirstLevelAgent));
            }
        }

        //一级代理商购物车(保证购物车中至少有2条记录)
        for (int i = 0; i <= random.nextInt(mockGoodsWith1ProductList.size()); i++) {
            ShoppingCart mockShoppingCart = mockShoppingCart(mockGoodsWith1ProductList.get(i).getProducts().get(0), mockFirstLevelAgent);
            mockFirstLevelAgentShoppingCartList.add(mockShoppingCart);
        }
        for (int i = 0; i <= random.nextInt(mockGoodsWithNProductList.size()); i++) {
            List<MallProduct> mockProductList = mockGoodsWithNProductList.get(i).getProducts();
            for (int j = 0; j <= random.nextInt(mockProductList.size()); j++) {
                ShoppingCart mockShoppingCart = mockShoppingCart(mockProductList.get(j), mockFirstLevelAgent);
                mockFirstLevelAgentShoppingCartList.add(mockShoppingCart);
            }
        }

        //一级代理商下级门店购物车
        for (int i = 0; i <= random.nextInt(mockFirstLevelAgentProductList.size()); i++) {
            ShoppingCart mockShoppingCart = mockShoppingCart(mockFirstLevelAgentProductList.get(i), mockFirstLevelShop);
            mockFirstLevelShopShoppingCartList.add(mockShoppingCart);
        }
        //一级代理商下级代理商购物车
        for (int i = 0; i <= random.nextInt(mockFirstLevelAgentProductList.size()); i++) {
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
        // TODO: 2016/5/25
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
        String contentWithNoTaxInvoiceInfo = new String(resultWithNoTaxInvoiceInfo.getResponse().getContentAsByteArray(),"UTF-8");
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
        String contentWithErrorId = new String(resultWithErrorId.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject objWithErrorId = JSONObject.parseObject(contentWithErrorId);
        Assert.assertEquals("500", objWithErrorId.getString("code"));
        Assert.assertEquals("没有传输数据", objWithErrorId.getString("msg"));

        // 3.库存不足
//        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAuthor(mockFirstLevelAgent);
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
        String contentWithNumTooMuchId = new String(resultWithNumTooMuchId.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject objWithNumTooMuchId = JSONObject.parseObject(contentWithNumTooMuchId);
        Assert.assertEquals("库存不足，下单失败！",objWithNumTooMuchId.getString("msg"));
        /*agentPurchaseOrderRepository.flush();
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAuthor(mockFirstLevelAgent);
        assertEquals(0,afterOrderList.size() - beforeOrderList.size());*/

    }

    /**
     * 一级代理商登录 新增采购单成功（单个货品）
     * @throws Exception
     */
    @Test
    @SuppressWarnings("Duplicates")
    public void testAddPurchaseOneIdByFirstLevelAgent() throws Exception{
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        // 4.新增采购单成功
        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAuthor(mockFirstLevelAgent);
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
        String content = new String(result.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAuthor(mockFirstLevelAgent);
        Assert.assertEquals(1,afterOrderList.size() - beforeOrderList.size());
        ShoppingCart deleteShoppingCart = shoppingCartRepository.findOne(mockFirstLevelAgentShoppingCartList.get(0).getId());
        Assert.assertNull(deleteShoppingCart);
    }

    /**
     * 一级代理商下级门店登录 新增采购单成功（单个货品）
     * @throws Exception
     */
    @Test
    public void testAddPurchaseOneIdByFirstLevelShop() throws Exception{
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        // 4.新增采购单成功
        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAuthor(mockFirstLevelShop);
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
        String content = new String(result.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAuthor(mockFirstLevelShop);
        Assert.assertEquals(1,afterOrderList.size() - beforeOrderList.size());
        ShoppingCart deleteShoppingCart = shoppingCartRepository.findOne(mockFirstLevelShopShoppingCartList.get(0).getId());
        Assert.assertNull(deleteShoppingCart);
    }

    /**
     * 一级代理商下级代理商登录 新增采购单成功（单个货品）
     * @throws Exception
     */
    @Test
    @SuppressWarnings("Duplicates")
    public void testAddPurchaseOneIdBySecondLevelAgent() throws Exception{
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockSecondLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        // 4.新增采购单成功
        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAuthor(mockSecondLevelAgent);
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
        String content = new String(result.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAuthor(mockSecondLevelAgent);
        Assert.assertEquals(1,afterOrderList.size() - beforeOrderList.size());
        ShoppingCart deleteShoppingCart = shoppingCartRepository.findOne(mockSecondLevelAgentShoppingCartList.get(0).getId());
        Assert.assertNull(deleteShoppingCart);
    }

    /**
     * 一级代理商登录 新增采购单成功（多个货品）
     * @throws Exception
     */
    @Test
    public void testAddPurchaseTwoIdByFirstLevelAgent() throws Exception{
        String controllerUrl = BASE_URL + "/addPurchase";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        //多个id
        List<AgentPurchaseOrder> beforeOrderList = agentPurchaseOrderRepository.findByAuthor(mockFirstLevelAgent);
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
        String contentWithIds = new String(resultWithIds.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject objWithIds = JSONObject.parseObject(contentWithIds);
        Assert.assertEquals("200", objWithIds.getString("code"));
        ShoppingCart deleteShoppingCart = shoppingCartRepository.findOne(mockFirstLevelAgentShoppingCartList.get(0).getId());
        Assert.assertNull(deleteShoppingCart);
        List<AgentPurchaseOrder> afterOrderList = agentPurchaseOrderRepository.findByAuthor(mockFirstLevelAgent);
        Assert.assertEquals(1,afterOrderList.size() - beforeOrderList.size());
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
        AgentProduct parentAgentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(mockFirstLevelShop.getParentAuthor(),mockShoppingCart.getProduct());
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
        String contentWithNumTooMuchId = new String(resultWithNumTooMuchId.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject objWithNumTooMuchId = JSONObject.parseObject(contentWithNumTooMuchId);
        Assert.assertEquals("库存不足，下单失败！",objWithNumTooMuchId.getString("msg"));
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
        AgentProduct parentAgentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(mockSecondLevelAgent.getParentAuthor(),mockShoppingCart.getProduct());
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
        String contentWithNumTooMuchId = new String(resultWithNumTooMuchId.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject objWithNumTooMuchId = JSONObject.parseObject(contentWithNumTooMuchId);
        Assert.assertEquals("库存不足，下单失败！",objWithNumTooMuchId.getString("msg"));
    }

    /**
     * 一级代理商登录 采购单列表
     * @throws Exception
     */
    @Test
    public void testShowPurchaseOrderListByFirstLevelAgent() throws Exception{
        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(),passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

    }


}