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
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 显示采购商品信息，加入购物车
 * Created by helloztt on 2016/5/23.
 */
public class PurchaseControllerTest extends CommonTestBase {

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


    @Before
    @SuppressWarnings("Duplicates")
    public void init() {
        //模拟数据
        //用户相关
        mockCustomer = mockMallCustomer();
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent);
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent);
        mockSecondLevelShop = mockShop(mockCustomer, mockSecondLevelAgent);

        //平台商品相关
        for (int i = 0; i < random.nextInt(10) + 1; i++) {
            MallGoods mockGoodsWith1Products = mockMallGoods(mockCustomer.getCustomerId(), null);
            List<MallProduct> mockGoodsWith1ProductsList = new ArrayList<>();
            mockGoodsWith1ProductsList.add(mockMallProduct(mockGoodsWith1Products));
            mockGoodsWith1Products.setProducts(mockGoodsWith1ProductsList);
            mockGoodsWith1Products.setStore(mockGoodsWith1ProductsList.stream().mapToInt(p -> p.getStore()).sum());
            mockGoodsWith1Products = mockMallGoods(mockGoodsWith1Products);
            mockGoodsWith1ProductList.add(mockGoodsWith1Products);
        }

        for (int i = 0; i < random.nextInt(10) + 1; i++) {
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
            for (int j = 0; j <= random.nextInt(mockGoodsWithNProductList.get(i).getProducts().size()); j++) {
                mockFirstLevelAgentProductList.add(mockAgentProduct(mockGoodsWithNProductList.get(i).getProducts().get(j), mockFirstLevelAgent));
            }
        }
    }

    /**
     * 一级代理商 显示商品采购列表
     */
    @Test
    public void testShowGoodsListByFirstLevelAgent() throws Exception {
        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        //1.显示平台方商品(不带搜索条件)
        MvcResult resultWithNoSearch = mockMvc.perform(get("/purchase/showGoodsList").session(session))
                .andExpect(status().isOk())
                .andReturn();
        //总记录数
        long totalRecords = (Long) resultWithNoSearch.getModelAndView().getModel().get("totalRecords");
        Assert.assertEquals(mockGoodsWith1ProductList.size() + mockGoodsWithNProductList.size(), totalRecords);
        List<MallGoods> realGoodsList = (List<MallGoods>) resultWithNoSearch.getModelAndView().getModel().get("goodsList");
        for (int i = 0; i < mockGoodsWith1ProductList.size(); i++) {
            Assert.assertEquals(mockGoodsWith1ProductList.get(i).getGoodsId(), realGoodsList.get(i).getGoodsId());
        }
        for (int i = 0; i < mockGoodsWithNProductList.size(); i++) {
            Assert.assertEquals(mockGoodsWithNProductList.get(i).getGoodsId(), realGoodsList.get(mockGoodsWith1ProductList.size() + i).getGoodsId());
        }
        //2.显示平台方商品（按商品名称搜索）
        MvcResult resultWithGoodsName = mockMvc.perform(get("/purchase/showGoodsList").session(session)
                .param("goodsName", mockGoodsWith1ProductList.get(0).getName()))
                .andExpect(status().isOk())
                .andReturn();
        long totalRecordsWithGoodsName = (Long) resultWithGoodsName.getModelAndView().getModel().get("totalRecords");
        Assert.assertEquals(1, totalRecordsWithGoodsName);
        List<MallGoods> realGoodsWithGoodsName = (List<MallGoods>) resultWithGoodsName.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(mockGoodsWith1ProductList.get(0).getGoodsId(), realGoodsWithGoodsName.get(0).getGoodsId());
    }

    /**
     * 一级代理商下级门店登录 显示商品采购列表
     *
     * @throws Exception
     */
    @Test
    public void testShowGoodsListByFirstLevelShop() throws Exception {
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));

        //1.显示上级代理商商品
        MvcResult resultWithNoSearch = mockMvc.perform(get("/purchase/showGoodsList").session(session))
                .andExpect(status().isOk())
                .andReturn();
        //总记录数
        long totalRecords = (Long) resultWithNoSearch.getModelAndView().getModel().get("totalRecords");
        List<MallGoods> realGoodsListWithNoSearch = (List<MallGoods>) resultWithNoSearch.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(mockFirstLevelAgentGoodsWith1ProductList.size() + mockFirstLevelAgentGoodsWithNProductList.size(), totalRecords);
        for (int i = 0; i < mockFirstLevelAgentGoodsWith1ProductList.size(); i++) {
            Assert.assertEquals(mockFirstLevelAgentGoodsWith1ProductList.get(i).getGoodsId(), realGoodsListWithNoSearch.get(i).getGoodsId());
        }
        for (int i = 0; i < mockFirstLevelAgentGoodsWithNProductList.size(); i++) {
            Assert.assertEquals(mockFirstLevelAgentGoodsWithNProductList.get(i).getGoodsId(), realGoodsListWithNoSearch.get(mockFirstLevelAgentGoodsWith1ProductList.size() + i).getGoodsId());
        }

        //2.显示上级代理商商品（按商品名称搜索）
        MvcResult resultWithGoodsName = mockMvc.perform(get("/purchase/showGoodsList").session(session)
                .param("goodsName", mockFirstLevelAgentGoodsWith1ProductList.get(0).getName()))
                .andExpect(status().isOk())
                .andReturn();
        long totalRecordsWithGoodsName = (Long) resultWithGoodsName.getModelAndView().getModel().get("totalRecords");
        Assert.assertEquals(1, totalRecordsWithGoodsName);
        List<MallGoods> realGoodsWithGoodsName = (List<MallGoods>) resultWithGoodsName.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(mockFirstLevelAgentGoodsWith1ProductList.get(0).getGoodsId(), realGoodsWithGoodsName.get(0).getGoodsId());
    }

    /**
     * 一级代理商下级代理商登录 显示商品采购列表
     *
     * @throws Exception
     */
    @Test
    public void testShowGoodsListBySecondLevelAgent() throws Exception {
        //一级代理商下级代理商登录
        MockHttpSession session = loginAs(mockSecondLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        //1.显示上级代理商商品
        MvcResult resultWithNoSearch = mockMvc.perform(get("/purchase/showGoodsList").session(session))
                .andExpect(status().isOk())
                .andReturn();
        //总记录数
        long totalRecords = (Long) resultWithNoSearch.getModelAndView().getModel().get("totalRecords");
        List<MallGoods> realGoodsListWithNoSearch = (List<MallGoods>) resultWithNoSearch.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(mockFirstLevelAgentGoodsWith1ProductList.size() + mockFirstLevelAgentGoodsWithNProductList.size(), totalRecords);
        for (int i = 0; i < mockFirstLevelAgentGoodsWith1ProductList.size(); i++) {
            Assert.assertEquals(mockFirstLevelAgentGoodsWith1ProductList.get(i).getGoodsId(), realGoodsListWithNoSearch.get(i).getGoodsId());
        }
        for (int i = 0; i < mockFirstLevelAgentGoodsWithNProductList.size(); i++) {
            Assert.assertEquals(mockFirstLevelAgentGoodsWithNProductList.get(i).getGoodsId(), realGoodsListWithNoSearch.get(mockFirstLevelAgentGoodsWith1ProductList.size() + i).getGoodsId());
        }

        //2.显示上级代理商商品（按商品名称搜索）
        MvcResult resultWithGoodsName = mockMvc.perform(get("/purchase/showGoodsList").session(session)
                .param("goodsName", mockFirstLevelAgentGoodsWith1ProductList.get(0).getName()))
                .andExpect(status().isOk())
                .andReturn();
        long totalRecordsWithGoodsName = (Long) resultWithGoodsName.getModelAndView().getModel().get("totalRecords");
        Assert.assertEquals(1, totalRecordsWithGoodsName);
        List<MallGoods> realGoodsWithGoodsName = (List<MallGoods>) resultWithGoodsName.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(mockFirstLevelAgentGoodsWith1ProductList.get(0).getGoodsId(), realGoodsWithGoodsName.get(0).getGoodsId());
    }

    /**
     * 一级代理商登录 加入购物车
     *
     * @throws Exception
     */
    @Test
    public void testAddShoppingByFirstLevelAgent() throws Exception {
        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        //1.数量为0 校验
        MvcResult resultWithNum0 = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNum0 = new String(resultWithNum0.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNum0 = JSONObject.parseObject(contentWithNum0);
        Assert.assertEquals("请输入要订购的数量", objWithNum0.getString("msg"));

        //2.商品为空且货品未空 校验
        MvcResult resultWithNoGoodsIdAndProductId = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoGoodsIdAndProductId = new String(resultWithNoGoodsIdAndProductId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoGoodsIdAndProductId = JSONObject.parseObject(contentWithNoGoodsIdAndProductId);
        Assert.assertEquals("请选择要订购的货品！", objWithNoGoodsIdAndProductId.getString("msg"));

        //3.商品不存在 校验
        MvcResult resultWithNotExistGoodsId = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("goodsId", String.valueOf(Integer.MAX_VALUE)))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNotExistGoodsId = new String(resultWithNotExistGoodsId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNotExistGoodsId = JSONObject.parseObject(contentWithNotExistGoodsId);
        Assert.assertEquals("请选择要订购的商品！", objWithNotExistGoodsId.getString("msg"));

        //4.商品货品数量大于1件 校验
        MvcResult resultWithTooMuchProduct = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("goodsId", String.valueOf(mockGoodsWithNProductList.get(0).getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithTooMuchProduct = new String(resultWithTooMuchProduct.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithTooMuchProduct = JSONObject.parseObject(contentWithTooMuchProduct);
        Assert.assertEquals("请选择要订购的货品！", objWithTooMuchProduct.getString("msg"));

        //5.货品不存在 校验
        MvcResult resultWithNotExistProductId = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("productId", String.valueOf(Integer.MAX_VALUE)))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNotExistProductId = new String(resultWithNotExistProductId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNotExistProductId = JSONObject.parseObject(contentWithNotExistProductId);
        Assert.assertEquals("请选择要订购的商品！", objWithNotExistProductId.getString("msg"));

        //6.数量大于可用库存 校验
        Integer randomNum = random.nextInt(100) + mockGoodsWith1ProductList.get(0).getProducts().get(0).getStore() - mockGoodsWith1ProductList.get(0).getProducts().get(0).getFreez() + 1;
        MvcResult resultWithTooMuchNum = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", String.valueOf(randomNum))
                        .param("goodsId", String.valueOf(mockGoodsWith1ProductList.get(0).getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithTooMuchNum = new String(resultWithTooMuchNum.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithTooMuchNum = JSONObject.parseObject(contentWithTooMuchNum);
        Assert.assertTrue(randomNum > (mockGoodsWith1ProductList.get(0).getProducts().get(0).getStore() - mockGoodsWith1ProductList.get(0).getProducts().get(0).getFreez()));
        Assert.assertEquals("库存不足！", objWithTooMuchNum.getString("msg"));


        //获取可用库存大于1的商品
        MallGoods mockGoodsWith1Product = null;
        for (MallGoods mockGoods : mockGoodsWith1ProductList) {
            if (mockGoods.getProducts().get(0).getStore() - mockGoods.getProducts().get(0).getFreez() > 1) {
                mockGoodsWith1Product = mockGoods;
                break;
            }
        }
        //7.get 校验

        MvcResult resultWithGet = mockMvc.perform(
                get("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("goodsId", String.valueOf(mockGoodsWith1Product.getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithGet = new String(resultWithGet.getResponse().getContentAsByteArray(), "UTF-8");
        System.out.println(contentWithGet);
        JSONObject objWithGet = JSONObject.parseObject(contentWithGet);
        Assert.assertEquals("Request method 'GET' not supported", objWithGet.getString("msg"));
        Assert.assertEquals("500", objWithGet.getString("code"));

        //8.无规格商品 校验
        MvcResult resultWith1Product = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("goodsId", String.valueOf(mockGoodsWith1Product.getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWith1Product = new String(resultWith1Product.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWith1Product = JSONObject.parseObject(contentWith1Product);
        Assert.assertEquals("200", objWith1Product.getString("code"));

        //获取多规格商品中货品可用数量大于1的货品
        MallProduct mockProductWithNProdct = null;
        for (MallGoods mockGoods : mockGoodsWithNProductList) {
            for (MallProduct mockProduct : mockGoods.getProducts()) {
                if (mockProduct.getStore() - mockProduct.getFreez() > 1) {
                    mockProductWithNProdct = mockProduct;
                    break;
                }
            }
            if (mockProductWithNProdct != null) {
                break;
            }
        }
        //9.单个货品 校验
        MvcResult resultWithNProduct = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("productId", String.valueOf(mockProductWithNProdct.getProductId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNProduct = new String(resultWithNProduct.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNProduct = JSONObject.parseObject(contentWithNProduct);
        Assert.assertEquals("200", objWithNProduct.getString("code"));

    }

    /**
     * 一级代理商下级门店登录 加入购物车
     *
     * @throws Exception
     */
    @Test
    public void testAddShoppingByFirstLevelShop() throws Exception {
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        testAddShopping(session);
    }

    /**
     * 一级代理商下级代理商登录 加入购物车
     *
     * @throws Exception
     */
    @Test
    public void testAddShoppingBySecondLevelAgent() throws Exception {
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockSecondLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        testAddShopping(session);
    }

    private void testAddShopping(MockHttpSession session) throws Exception {
        //1.数量为0 校验
        MvcResult resultWithNum0 = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNum0 = new String(resultWithNum0.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNum0 = JSONObject.parseObject(contentWithNum0);
        Assert.assertEquals("请输入要订购的数量", objWithNum0.getString("msg"));

        //2.商品为空且货品未空 校验
        MvcResult resultWithNoGoodsIdAndProductId = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoGoodsIdAndProductId = new String(resultWithNoGoodsIdAndProductId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoGoodsIdAndProductId = JSONObject.parseObject(contentWithNoGoodsIdAndProductId);
        Assert.assertEquals("请选择要订购的货品！", objWithNoGoodsIdAndProductId.getString("msg"));

        //3.商品不存在 校验
        MvcResult resultWithNotExistGoodsId = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("goodsId", String.valueOf(Integer.MAX_VALUE)))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNotExistGoodsId = new String(resultWithNotExistGoodsId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNotExistGoodsId = JSONObject.parseObject(contentWithNotExistGoodsId);
        Assert.assertEquals("请选择要订购的商品！", objWithNotExistGoodsId.getString("msg"));

        //4.商品货品数量大于1件 校验
        MvcResult resultWithTooMuchProduct = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("goodsId", String.valueOf(mockFirstLevelAgentGoodsWithNProductList.get(0).getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithTooMuchProduct = new String(resultWithTooMuchProduct.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithTooMuchProduct = JSONObject.parseObject(contentWithTooMuchProduct);
        Assert.assertEquals("请选择要订购的货品！", objWithTooMuchProduct.getString("msg"));

        //5.货品不存在 校验
        MvcResult resultWithNotExistProductId = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("productId", String.valueOf(Integer.MAX_VALUE)))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNotExistProductId = new String(resultWithNotExistProductId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNotExistProductId = JSONObject.parseObject(contentWithNotExistProductId);
        Assert.assertEquals("请选择要订购的商品！", objWithNotExistProductId.getString("msg"));

        //6.数量大于可用库存 校验
        Integer randomNum = random.nextInt(100) + mockFirstLevelAgentGoodsWith1ProductList.get(0).getProducts().get(0).getStore() - mockFirstLevelAgentGoodsWith1ProductList.get(0).getProducts().get(0).getFreez() + 1;
        MvcResult resultWithTooMuchNum = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", String.valueOf(randomNum))
                        .param("goodsId", String.valueOf(mockFirstLevelAgentGoodsWith1ProductList.get(0).getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithTooMuchNum = new String(resultWithTooMuchNum.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithTooMuchNum = JSONObject.parseObject(contentWithTooMuchNum);
        Assert.assertTrue(randomNum > (mockFirstLevelAgentGoodsWith1ProductList.get(0).getProducts().get(0).getStore() - mockFirstLevelAgentGoodsWith1ProductList.get(0).getProducts().get(0).getFreez()));
        Assert.assertEquals("库存不足！", objWithTooMuchNum.getString("msg"));


        //获取可用库存大于1的商品
        MallGoods mockGoodsWith1Product = null;
        for (MallGoods mockGoods : mockFirstLevelAgentGoodsWith1ProductList) {
            if (mockGoods.getProducts().get(0).getStore() - mockGoods.getProducts().get(0).getFreez() > 1) {
                mockGoodsWith1Product = mockGoods;
                break;
            }
        }
        //7.get 校验

        MvcResult resultWithGet = mockMvc.perform(
                get("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("goodsId", String.valueOf(mockGoodsWith1Product.getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithGet = new String(resultWithGet.getResponse().getContentAsByteArray(), "UTF-8");
        System.out.println(contentWithGet);
        JSONObject objWithGet = JSONObject.parseObject(contentWithGet);
        Assert.assertEquals("Request method 'GET' not supported", objWithGet.getString("msg"));
        Assert.assertEquals("500", objWithGet.getString("code"));

        //8.无规格商品 校验
        MvcResult resultWith1Product = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("goodsId", String.valueOf(mockGoodsWith1Product.getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWith1Product = new String(resultWith1Product.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWith1Product = JSONObject.parseObject(contentWith1Product);
        Assert.assertEquals("200", objWith1Product.getString("code"));

        //获取多规格商品中货品可用数量大于1的货品
        MallProduct mockProductWithNProdct = null;
        for (MallGoods mockGoods : mockFirstLevelAgentGoodsWithNProductList) {
            for (MallProduct mockProduct : mockGoods.getProducts()) {
                if (mockProduct.getStore() - mockProduct.getFreez() > 1) {
                    mockProductWithNProdct = mockProduct;
                    break;
                }
            }
            if (mockProductWithNProdct != null) {
                break;
            }
        }
        //9.单个货品 校验
        MvcResult resultWithNProduct = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("productId", String.valueOf(mockProductWithNProdct.getProductId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNProduct = new String(resultWithNProduct.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNProduct = JSONObject.parseObject(contentWithNProduct);
        Assert.assertEquals("200", objWithNProduct.getString("code"));
    }

    /**
     * 一级代理商登录 显示货品列表
     *
     * @throws Exception
     */
    @Test
    public void testShowProductListByFirstLevelAgent() throws Exception {
        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        //1.goodsId不传
        MvcResult resultWithNoGoodsId = mockMvc.perform(
                post("/purchase/showProductList")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoGoodsId = new String(resultWithNoGoodsId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoGoodsId = JSONObject.parseObject(contentWithNoGoodsId);
        Assert.assertEquals("500", objWithNoGoodsId.getString("code"));
        System.out.println(contentWithNoGoodsId);
        //2.goodsId =0
        MvcResult resultWithGoodsId0 = mockMvc.perform(
                post("/purchase/showProductList")
                        .session(session)
                        .param("goodsId", "0"))
                .andExpect(status().isOk())
                .andReturn();
        List<MallProduct> productListWithGoodsId0 = (List<MallProduct>) resultWithGoodsId0.getModelAndView().getModel().get("productList");
        Assert.assertNull(productListWithGoodsId0);

        //3.货品数量校验
        MvcResult result = mockMvc.perform(
                post("/purchase/showProductList")
                        .session(session)
                        .param("goodsId", String.valueOf(mockGoodsWithNProductList.get(0).getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        List<MallProduct> productList = (List<MallProduct>) result.getModelAndView().getModel().get("productList");
        Assert.assertEquals(mockGoodsWithNProductList.get(0).getProducts().size(), productList.size());

    }

    /**
     * 一级代理商下级门店登录 显示货品列表
     *
     * @throws Exception
     */
    @Test
    public void testShowProductListByFirstLevelShop() throws Exception {
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        testShowProductList(session);
    }

    /**
     * 一级代理商下级代理商登录 显示货品列表
     *
     * @throws Exception
     */
    @Test
    public void testShowProductListBySecondLevelShop() throws Exception {
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockSecondLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        testShowProductList(session);
    }

    private void testShowProductList(MockHttpSession session) throws Exception {
        //1.goodsId不传
        MvcResult resultWithNoGoodsId = mockMvc.perform(
                post("/purchase/showProductList")
                        .session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoGoodsId = new String(resultWithNoGoodsId.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoGoodsId = JSONObject.parseObject(contentWithNoGoodsId);
        Assert.assertEquals("500", objWithNoGoodsId.getString("code"));
        System.out.println(contentWithNoGoodsId);

        //2.goodsId =0
        MvcResult resultWithGoodsId0 = mockMvc.perform(
                post("/purchase/showProductList")
                        .session(session)
                        .param("goodsId", "0"))
                .andExpect(status().isOk())
                .andReturn();
        List<MallProduct> productListWithGoodsId0 = (List<MallProduct>) resultWithGoodsId0.getModelAndView().getModel().get("productList");
        Assert.assertNull(productListWithGoodsId0);

        //3.货品数量校验
        int expectSize = 0;
        for (AgentProduct agentProduct : mockFirstLevelAgentProductList) {
            if (agentProduct.getGoodsId().equals(mockFirstLevelAgentGoodsWithNProductList.get(0).getGoodsId())) {
                expectSize++;
            }
        }
        MvcResult result = mockMvc.perform(
                post("/purchase/showProductList")
                        .session(session)
                        .param("goodsId", String.valueOf(mockFirstLevelAgentGoodsWithNProductList.get(0).getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        List<MallProduct> productList = (List<MallProduct>) result.getModelAndView().getModel().get("productList");
        Assert.assertEquals(expectSize, productList.size());
    }

}