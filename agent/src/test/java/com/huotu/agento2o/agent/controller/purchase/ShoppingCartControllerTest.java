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
import com.huotu.agento2o.service.common.InvoiceEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.config.InvoiceConfig;
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

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;


/**
 * 购物车列表，删除，清空，编辑，显示采购单
 * Created by helloztt on 2016/5/24.
 */
public class ShoppingCartControllerTest extends CommonTestBase {

    private static String BASE_URL = "/shoppingCart";

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

    //一级代理商购物车
    private List<ShoppingCart> mockFirstLevelAgentShoppingCartList = new ArrayList<>();

    //一级代理商下级门店 购物车
    private List<ShoppingCart> mockFirstLevelShopShoppingCartList = new ArrayList<>();

    //一级代理商下级代理商 购物车
    private List<ShoppingCart> mockSecondLevelAgentShoppingCartList = new ArrayList<>();

    @Before
    @SuppressWarnings("Duplicates")
    public void init() {
        //模拟数据
        //用户相关
        mockCustomer = mockMallCustomer();
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelShop = mockShop(mockCustomer, mockSecondLevelAgent.getAgent());

        //平台商品相关
        for (int i = 0; i <= random.nextInt(10); i++) {
            MallGoods mockGoodsWith1Products = mockMallGoods(mockCustomer.getCustomerId(), false);
            List<MallProduct> mockGoodsWith1ProductsList = new ArrayList<>();
            mockGoodsWith1ProductsList.add(mockMallProduct(mockGoodsWith1Products));
            mockGoodsWith1Products.setProducts(mockGoodsWith1ProductsList);
            mockGoodsWith1Products.setStore(mockGoodsWith1ProductsList.stream().mapToInt(p -> p.getStore()).sum());
            mockGoodsWith1Products = mockMallGoods(mockGoodsWith1Products);
            mockGoodsWith1ProductList.add(mockGoodsWith1Products);
        }

        for (int i = 0; i <= random.nextInt(10); i++) {
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
            ShoppingCart mockShoppingCart = mockShoppingCart(mockFirstLevelAgentProductList.get(i).getProduct(), mockSecondLevelAgent);
            mockSecondLevelAgentShoppingCartList.add(mockShoppingCart);
        }

    }

    /**
     * 一级代理商登录 显示购物车列表
     *
     * @throws Exception
     */
    @Test
    public void testShowShoppingCartByFirstLevelAgent() throws Exception {
        //一级代理商登录
        //1.购物车列表
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        MvcResult result = mockMvc.perform(
                post(BASE_URL + "/showShoppingCart")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        List<ShoppingCart> realShoppingCartList = (List<ShoppingCart>) result.getModelAndView().getModel().get("shoppingCartList");
        Assert.assertEquals(mockFirstLevelAgentShoppingCartList.size(), realShoppingCartList.size());
    }

    /**
     * 一级代理商下级门店登录 显示购物车列表
     *
     * @throws Exception
     */
    @Test
    public void testShowShoppingCartByFirstLevelShop() throws Exception {
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult result = mockMvc.perform(
                post("/shoppingCart/showShoppingCart")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        List<ShoppingCart> realShoppingCartList = (List<ShoppingCart>) result.getModelAndView().getModel().get("shoppingCartList");
        Assert.assertEquals(mockFirstLevelShopShoppingCartList.size(), realShoppingCartList.size());

    }

    /**
     * 一级代理商下级门店登录 显示购物车列表
     *
     * @throws Exception
     */
    @Test
    public void testShowShoppingCartBySecondLevelAgent() throws Exception {
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockSecondLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(
                post("/shoppingCart/showShoppingCart")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        List<ShoppingCart> realShoppingCartList = (List<ShoppingCart>) result.getModelAndView().getModel().get("shoppingCartList");
        Assert.assertEquals(mockSecondLevelAgentShoppingCartList.size(), realShoppingCartList.size());
    }

    /**
     * 一级代理商登录 删除购物车记录
     *
     * @throws Exception
     */
    @Test
    public void testDeleteByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/delete";

        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        // 1.不传参数
        MvcResult resultWithNoData = mockMvc.perform(
                post(controllerUrl)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoData = new String(resultWithNoData.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoData = JSONObject.parseObject(contentWithNoData);
        Assert.assertEquals("500", objWithNoData.getString("code"));

        // 2.购物车记录不存在
        MvcResult resultWithData0 = mockMvc.perform(
                post(controllerUrl)
                        .session(session).param("shoppingCartId", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithData0 = new String(resultWithData0.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithData0 = JSONObject.parseObject(contentWithData0);
        Assert.assertEquals("没有传输数据", objWithData0.get("msg"));

        //3.非当前用户购物车
        MvcResult resultWithErrorId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockSecondLevelAgentShoppingCartList.get(0).getId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithErrorId = new String(resultWithErrorId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithErrorId = JSONObject.parseObject(contentWithErrorId);
        Assert.assertEquals("没有传输数据", objWithErrorId.get("msg"));

        // 3.GET
        MvcResult resultWithGet = mockMvc.perform(
                get(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithGet = new String(resultWithGet.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithGet = JSONObject.parseObject(contentWithGet);
        Assert.assertEquals("500", objWithGet.getString("code"));

        // 4.传参数
        MvcResult result = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockFirstLevelAgentShoppingCartList.get(0).getId())))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
    }

    @Test
    public void testDeleteByFirstLevelShop() throws Exception {
        // TODO: 2016/5/24
    }

    @Test
    public void testDeleteBySecondLevelAgent() throws Exception {
        // TODO: 2016/5/24
    }

    /**
     * 一级代理商登录 修改购物车数量
     *
     * @throws Exception
     */
    @Test
    public void testEditNumByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/editNum";
        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        // 1.不传参数
        MvcResult resultWithNoData = mockMvc.perform(
                post(controllerUrl)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoData = new String(resultWithNoData.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoData = JSONObject.parseObject(contentWithNoData);
        Assert.assertEquals("500", objWithNoData.getString("code"));

        // 2.id为0
        MvcResult resultWithId0 = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", "0")
                        .param("num", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithId0 = new String(resultWithId0.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithId0 = JSONObject.parseObject(contentWithId0);
        Assert.assertEquals("500", objWithId0.getString("code"));
        Assert.assertEquals("没有传输数据", objWithId0.getString("msg"));

        // 3.num为0

        ShoppingCart mockShoppingCart = mockFirstLevelAgentShoppingCartList.get(0);
        MvcResult resultWithNum0 = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("num", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNum0 = new String(resultWithNum0.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNum0 = JSONObject.parseObject(contentWithNum0);
        Assert.assertEquals("订购数量必须大于0！", objWithNum0.getString("msg"));

        // 4.id不存在
        MvcResult resultWithErrorId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockSecondLevelAgentShoppingCartList.get(0).getId()))
                        .param("num", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithErrorId = new String(resultWithErrorId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithErrorId = JSONObject.parseObject(contentWithErrorId);
        Assert.assertEquals("500", objWithErrorId.getString("code"));

        // 5.库存不足
        MvcResult resultWithTooMuchNum = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("num", String.valueOf(mockShoppingCart.getProduct().getStore() - mockShoppingCart.getProduct().getFreez() + 1)))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithTooMuchNum = new String(resultWithTooMuchNum.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithTooMuchNum = JSONObject.parseObject(contentWithTooMuchNum);
        Assert.assertEquals("库存不足", objWithTooMuchNum.getString("msg"));

        // 6.操作成功
        MvcResult result = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("num", String.valueOf(mockShoppingCart.getProduct().getStore() - mockShoppingCart.getProduct().getFreez())))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
    }

    /**
     * 一级代理商下级门店登录 修改购物车数量
     *
     * @throws Exception
     */
    @Test
    public void testEditNumByFirstLevelShop() throws Exception {
        String controllerUrl = BASE_URL + "/editNum";
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));

        // 1.不传参数
        MvcResult resultWithNoData = mockMvc.perform(
                post(controllerUrl)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoData = new String(resultWithNoData.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoData = JSONObject.parseObject(contentWithNoData);
        Assert.assertEquals("500", objWithNoData.getString("code"));

        // 2.id为0
        MvcResult resultWithId0 = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithId0 = new String(resultWithId0.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithId0 = JSONObject.parseObject(contentWithId0);
        Assert.assertEquals("500", objWithId0.getString("code"));

        // 3.num为0

        ShoppingCart mockShoppingCart = mockFirstLevelShopShoppingCartList.get(0);
        AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(mockFirstLevelAgent.getAgent(), mockShoppingCart.getProduct());
        MvcResult resultWithNum0 = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("num", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNum0 = new String(resultWithNum0.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNum0 = JSONObject.parseObject(contentWithNum0);
        Assert.assertEquals("订购数量必须大于0！", objWithNum0.getString("msg"));

        // 4.id不存在
        MvcResult resultWithErrorId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockSecondLevelAgentShoppingCartList.get(0).getId()))
                        .param("num", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithErrorId = new String(resultWithErrorId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithErrorId = JSONObject.parseObject(contentWithErrorId);
        Assert.assertEquals("500", objWithErrorId.getString("code"));

        // 5.库存不足
        MvcResult resultWithTooMuchNum = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("num", String.valueOf(parentAgentProduct.getStore() - parentAgentProduct.getFreez() + 1)))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithTooMuchNum = new String(resultWithTooMuchNum.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithTooMuchNum = JSONObject.parseObject(contentWithTooMuchNum);
        Assert.assertEquals("库存不足", objWithTooMuchNum.getString("msg"));

        // 6.操作成功
        MvcResult result = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("num", String.valueOf(parentAgentProduct.getStore() - parentAgentProduct.getFreez())))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
    }

    /**
     * 一级代理商下级代理商登录 修改购物车数量
     *
     * @throws Exception
     */
    @Test
    public void testDeleteAllByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/deleteAll";
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        MvcResult result = mockMvc.perform(
                post(controllerUrl)
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));

        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findByAgent_IdOrderByCreateTimeDesc(mockFirstLevelAgent.getId());
        Assert.assertEquals(0, shoppingCartList.size());
    }

    @Test
    public void testDeleteAllByFirstLevelShop() throws Exception {
        // TODO: 2016/5/24
    }

    /**
     * 一级代理商登录 填写采购单页面
     *
     * @throws Exception
     */
    @Test
    public void testAddPurchaseByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/addPurchase";
        // 一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        // 1.不传参数
        MvcResult resultWithNoData = mockMvc.perform(
                post(controllerUrl)
                        .session(session))
                //302 跳转
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/shoppingCart/showShoppingCart"))
                .andReturn();

        // 2.shoppingCartId=0
        MvcResult resultWithId0 = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", "0"))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/shoppingCart/showShoppingCart"))
                .andReturn();

        ShoppingCart mockShoppingCart = mockFirstLevelAgentShoppingCartList.get(0);
        ShoppingCart mockShoppingCart1 = mockFirstLevelAgentShoppingCartList.get(1);
        // 3.传2个id,其中一个为0
        MvcResult resultWithIdAnd0 = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("shoppingCartId", "0"))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentPurchaseOrder"))
                .andExpect(model().attributeExists("shoppingCartList"))
                .andExpect(model().attributeExists("sendmentEnum"))
                .andExpect(model().attributeExists("taxTypeEnum"))
                .andReturn();
        List<ShoppingCart> shoppingCartListWithIdAnd0 = (List<ShoppingCart>) resultWithIdAnd0.getModelAndView().getModel().get("shoppingCartList");
        Assert.assertEquals(1, shoppingCartListWithIdAnd0.size());
        Assert.assertEquals(mockShoppingCart.getId(), shoppingCartListWithIdAnd0.get(0).getId());

        // 4-1.传1个正确的id 无发票
        MvcResult resultWithIdNoInvoice = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentPurchaseOrder"))
                .andExpect(model().attributeExists("shoppingCartList"))
                .andExpect(model().attributeExists("sendmentEnum"))
                .andExpect(model().attributeExists("taxTypeEnum"))
                .andReturn();
        List<ShoppingCart> shoppingCartListWithIdNoInvoice = (List<ShoppingCart>) resultWithIdNoInvoice.getModelAndView().getModel().get("shoppingCartList");
        AgentPurchaseOrder purchaseOrderWithIdNoInvoice = (AgentPurchaseOrder) resultWithIdNoInvoice.getModelAndView().getModel().get("agentPurchaseOrder");
        Assert.assertEquals(1, shoppingCartListWithIdNoInvoice.size());
        Assert.assertEquals(mockShoppingCart.getId(), shoppingCartListWithIdNoInvoice.get(0).getId());
        Assert.assertEquals(PurchaseEnum.TaxType.NONE, purchaseOrderWithIdNoInvoice.getTaxType());

        //4-2.传1个正确的id 普通发票
        InvoiceConfig normalInvoiceConfig = mockInvoiceConfig(mockFirstLevelAgent, InvoiceEnum.InvoiceTypeStatus.NORMALINVOICE.getCode());
        MvcResult resultWithIdNormalInvoice = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentPurchaseOrder"))
                .andExpect(model().attributeExists("shoppingCartList"))
                .andExpect(model().attributeExists("sendmentEnum"))
                .andExpect(model().attributeExists("taxTypeEnum"))
                .andReturn();
        List<ShoppingCart> shoppingCartListWithIdNormalInvoice = (List<ShoppingCart>) resultWithIdNormalInvoice.getModelAndView().getModel().get("shoppingCartList");
        AgentPurchaseOrder purchaseOrderWithIdNormalInvoice = (AgentPurchaseOrder) resultWithIdNormalInvoice.getModelAndView().getModel().get("agentPurchaseOrder");
        Assert.assertEquals(1, shoppingCartListWithIdNormalInvoice.size());
        Assert.assertEquals(mockShoppingCart.getId(), shoppingCartListWithIdNormalInvoice.get(0).getId());
        Assert.assertEquals(PurchaseEnum.TaxType.NORMAL, purchaseOrderWithIdNormalInvoice.getTaxType());
        Assert.assertEquals(normalInvoiceConfig.getTaxTitle(), purchaseOrderWithIdNormalInvoice.getTaxTitle());
        Assert.assertEquals(normalInvoiceConfig.getTaxContent(), purchaseOrderWithIdNormalInvoice.getTaxContent());

        //4-3.传1个正确的id 增值税发票
        InvoiceConfig taxInvoiceConfig = mockInvoiceConfig(mockFirstLevelAgent, InvoiceEnum.InvoiceTypeStatus.TAXINVOICE.getCode());
        MvcResult resultWithIdTaxInvoice = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentPurchaseOrder"))
                .andExpect(model().attributeExists("shoppingCartList"))
                .andExpect(model().attributeExists("sendmentEnum"))
                .andExpect(model().attributeExists("taxTypeEnum"))
                .andReturn();
        List<ShoppingCart> shoppingCartListWithIdTaxInvoice = (List<ShoppingCart>) resultWithIdTaxInvoice.getModelAndView().getModel().get("shoppingCartList");
        AgentPurchaseOrder purchaseOrderWithIdTaxInvoice = (AgentPurchaseOrder) resultWithIdTaxInvoice.getModelAndView().getModel().get("agentPurchaseOrder");
        Assert.assertEquals(1, shoppingCartListWithIdTaxInvoice.size());
        Assert.assertEquals(mockShoppingCart.getId(), shoppingCartListWithIdTaxInvoice.get(0).getId());
        Assert.assertEquals(PurchaseEnum.TaxType.TAX, purchaseOrderWithIdTaxInvoice.getTaxType());
        Assert.assertEquals(taxInvoiceConfig.getTaxTitle(), purchaseOrderWithIdTaxInvoice.getTaxTitle());
        Assert.assertEquals(taxInvoiceConfig.getTaxContent(), purchaseOrderWithIdTaxInvoice.getTaxContent());
        Assert.assertEquals(taxInvoiceConfig.getAccountNo(), purchaseOrderWithIdTaxInvoice.getAccountNo());
        Assert.assertEquals(taxInvoiceConfig.getTaxpayerCode(), purchaseOrderWithIdTaxInvoice.getTaxpayerCode());
        Assert.assertEquals(taxInvoiceConfig.getBankName(), purchaseOrderWithIdTaxInvoice.getBankName());


        // 5.传2个正确的id
        MvcResult resultWithTwoId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("shoppingCartId", String.valueOf(mockShoppingCart1.getId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentPurchaseOrder"))
                .andExpect(model().attributeExists("shoppingCartList"))
                .andExpect(model().attributeExists("sendmentEnum"))
                .andExpect(model().attributeExists("taxTypeEnum"))
                .andReturn();
        List<ShoppingCart> shoppingCartListWithTwoId = (List<ShoppingCart>) resultWithTwoId.getModelAndView().getModel().get("shoppingCartList");
        Assert.assertEquals(2, shoppingCartListWithTwoId.size());
        Assert.assertEquals(mockShoppingCart.getId(), shoppingCartListWithTwoId.get(0).getId());
        Assert.assertEquals(mockShoppingCart1.getId(), shoppingCartListWithTwoId.get(1).getId());

        // 6.可用库存不足
        mockShoppingCart.setNum(mockShoppingCart.getProduct().getStore() - mockShoppingCart.getProduct().getFreez() + 1);
        mockShoppingCart = shoppingCartRepository.save(mockShoppingCart);

        MvcResult resultWithNumTooMuchId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId())))
                .andExpect(status().isFound())
                .andExpect(view().name("redirect:/shoppingCart/showShoppingCart"))
                .andReturn();
        // 7.传2个id，其中一个库存不足
        MvcResult resultWithNumTooMuchIdAndId = mockMvc.perform(
                post(controllerUrl)
                        .session(session)
                        .param("shoppingCartId", String.valueOf(mockShoppingCart.getId()))
                        .param("shoppingCartId", String.valueOf(mockShoppingCart1.getId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentPurchaseOrder"))
                .andExpect(model().attributeExists("shoppingCartList"))
                .andExpect(model().attributeExists("sendmentEnum"))
                .andExpect(model().attributeExists("taxTypeEnum"))
                .andReturn();
        List<ShoppingCart> shoppingCartListWithNumTooMuchIdAndId = (List<ShoppingCart>) resultWithNumTooMuchIdAndId.getModelAndView().getModel().get("shoppingCartList");
        Assert.assertEquals(1, shoppingCartListWithNumTooMuchIdAndId.size());
        Assert.assertEquals(mockShoppingCart1.getId(), shoppingCartListWithNumTooMuchIdAndId.get(0).getId());


    }

    @Test
    public void testAddPurchaseByFirstLevelShop() throws Exception {
        // TODO: 2016/5/25
    }

    @Test
    public void testAddPurchaseBySecondLevelAgent() throws Exception {
        // TODO: 2016/5/25
    }


}