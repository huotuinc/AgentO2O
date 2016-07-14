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

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallGoodsType;
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
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 显示采购商品信息，加入购物车
 * Created by helloztt on 2016/5/23.
 */
public class PurchaseControllerTest extends CommonTestBase {

    private static String BASE_URL = "/purchase";

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

    private List<MallGoodsType> mockCustomerGoodsTypeList = new ArrayList<>();
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
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelShop = mockShop(mockCustomer, mockSecondLevelAgent.getAgent());

        //平台自定义商品
        for (int i = 0; i < random.nextInt(10) + 1; i++) {
            MallGoodsType customerGoodsType = mockMallGoodsType(mockCustomer.getCustomerId());
            mockCustomerGoodsTypeList.add(customerGoodsType);
        }
        //平台商品相关(保证至少有10个商品)
        for (int i = 0; i < random.nextInt(10) + 10; i++) {
            MallGoods mockGoodsWith1Products = mockMallGoods(mockCustomer.getCustomerId(), false);
            int randomGoodsType = random.nextInt(2);
            if (randomGoodsType == 0) {
                //标准分类
                mockGoodsWith1Products.setTypeId(standardGoodsType.getTypeId());
            } else {
                //自定义分类
                int randomCustomerGoodsType = random.nextInt(mockCustomerGoodsTypeList.size());
                mockGoodsWith1Products.setTypeId(mockCustomerGoodsTypeList.get(randomCustomerGoodsType).getTypeId());
            }
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
            for (int j = 0; j <= random.nextInt(mockGoodsWithNProductList.get(i).getProducts().size()); j++) {
                mockFirstLevelAgentProductList.add(mockAgentProduct(mockGoodsWithNProductList.get(i).getProducts().get(j), mockFirstLevelAgent));
            }
        }

    }

    /**
     * 一级代理商 显示商品采购列表
     */
    @Test
    @SuppressWarnings("Duplicates")
    public void testShowGoodsListByFirstLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/showGoodsList";
        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        //1.显示平台方商品(不带搜索条件)
        MvcResult resultWithNoSearch = mockMvc.perform(get(controllerUrl).session(session))
                .andExpect(status().isOk())
                .andReturn();
        //总记录数
        long totalRecords = (Long) resultWithNoSearch.getModelAndView().getModel().get("totalRecords");
        Assert.assertEquals(mockGoodsWith1ProductList.size() + mockGoodsWithNProductList.size(), totalRecords);
        List<MallGoods> realGoodsList = (List<MallGoods>) resultWithNoSearch.getModelAndView().getModel().get("goodsList");
        for (int i = 0; i < Math.min(Constant.PAGESIZE, mockGoodsWith1ProductList.size()); i++) {
            Assert.assertEquals(mockGoodsWith1ProductList.get(i).getGoodsId(), realGoodsList.get(i).getGoodsId());
        }
        if (mockGoodsWith1ProductList.size() < Constant.PAGESIZE) {
            for (int i = 0; i < Math.min(Constant.PAGESIZE - mockGoodsWith1ProductList.size(), mockGoodsWithNProductList.size()); i++) {
                Assert.assertEquals(mockGoodsWithNProductList.get(i).getGoodsId(), realGoodsList.get(mockGoodsWith1ProductList.size() + i).getGoodsId());
            }
        }
        //2.显示平台方商品（按商品名称搜索）
        MvcResult resultWithGoodsName = mockMvc.perform(get(controllerUrl).session(session)
                .param("goodsName", mockGoodsWith1ProductList.get(0).getName()))
                .andExpect(status().isOk())
                .andReturn();
        long totalRecordsWithGoodsName = (Long) resultWithGoodsName.getModelAndView().getModel().get("totalRecords");
        Assert.assertEquals(1, totalRecordsWithGoodsName);
        List<MallGoods> realGoodsWithGoodsName = (List<MallGoods>) resultWithGoodsName.getModelAndView().getModel().get("goodsList");
        Assert.assertEquals(mockGoodsWith1ProductList.get(0).getGoodsId(), realGoodsWithGoodsName.get(0).getGoodsId());

        //标准分类商品
        List<MallGoods> standardTypeGoods = new ArrayList<>();
        //自定义分类商品
        int randomCustomerTypeId = random.nextInt(mockCustomerGoodsTypeList.size());
        MallGoodsType mockCustomerGoodsType = mockCustomerGoodsTypeList.get(randomCustomerTypeId);
        List<MallGoods> customerTypeGoods = new ArrayList<>();
        mockGoodsWith1ProductList.forEach(goods -> {
            if (standardGoodsType.getTypeId().equals(goods.getTypeId())) {
                standardTypeGoods.add(goods);
            } else if (mockCustomerGoodsType.getTypeId().equals(goods.getTypeId())) {
                customerTypeGoods.add(goods);
            }
        });
        mockGoodsWithNProductList.forEach(goods -> {
            if (standardGoodsType.getTypeId().equals(goods.getTypeId())) {
                standardTypeGoods.add(goods);
            } else if (mockCustomerGoodsType.getTypeId().equals(goods.getTypeId())) {
                customerTypeGoods.add(goods);
            }
        });
        // 3.按标准类型搜索
        MvcResult resultWithStandardTypeId = mockMvc.perform(get(controllerUrl).session(session)
                .param("standardTypeId", standardGoodsType.getStandardTypeId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customerTypeList"))
                .andExpect(model().attributeExists("goodsList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andReturn();
        List<MallGoods> realGoodsListWithStandardTypeId = (List<MallGoods>) resultWithStandardTypeId.getModelAndView().getModel().get("goodsList");
        Assert.assertNotNull(realGoodsListWithStandardTypeId);
        Assert.assertEquals(standardTypeGoods.size(), realGoodsListWithStandardTypeId.size());
        for (int i = 0; i < Math.min(Constant.PAGESIZE, realGoodsListWithStandardTypeId.size()); i++) {
            Assert.assertEquals(standardTypeGoods.get(i).getGoodsId(), realGoodsListWithStandardTypeId.get(i).getGoodsId());
        }

        // 4.按自定义类型搜索
        MvcResult resultWithCustomerTypeId = mockMvc.perform(get(controllerUrl).session(session)
                .param("customerTypeId", String.valueOf(mockCustomerGoodsType.getTypeId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customerTypeList"))
                .andExpect(model().attributeExists("goodsList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andReturn();
        List<MallGoods> realGoodsListWithCustomerTypeId = (List<MallGoods>) resultWithCustomerTypeId.getModelAndView().getModel().get("goodsList");
        Assert.assertNotNull(realGoodsListWithCustomerTypeId);
        Assert.assertEquals(customerTypeGoods.size(), realGoodsListWithCustomerTypeId.size());
        for (int i = 0; i < Math.min(20, realGoodsListWithCustomerTypeId.size()); i++) {
            Assert.assertEquals(customerTypeGoods.get(i).getGoodsId(), realGoodsListWithCustomerTypeId.get(i).getGoodsId());
        }

        // 5.按商品名称和标准类型搜索
        MvcResult resultWithGoodsNameAndStandardTypeId = mockMvc.perform(get(controllerUrl).session(session)
                .param("standardTypeId", standardGoodsType.getStandardTypeId())
                .param("goodsName", standardTypeGoods.get(0).getName()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customerTypeList"))
                .andExpect(model().attributeExists("goodsList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andReturn();
        List<MallGoods> realGoodsListWithGoodsNameAndStandardTypeId = (List<MallGoods>) resultWithGoodsNameAndStandardTypeId.getModelAndView().getModel().get("goodsList");
        Assert.assertNotNull(realGoodsListWithGoodsNameAndStandardTypeId);
        Assert.assertEquals(1, realGoodsListWithGoodsNameAndStandardTypeId.size());
        Assert.assertEquals(standardTypeGoods.get(0).getGoodsId(), realGoodsListWithGoodsNameAndStandardTypeId.get(0).getGoodsId());

        // 6.按商品名称和自定义类型搜索
        MvcResult resultWithGoodsNameAndCustomerTypeId = mockMvc.perform(get(controllerUrl).session(session)
                .param("customerTypeId", String.valueOf(mockCustomerGoodsType.getTypeId()))
                .param("goodsName", customerTypeGoods.get(0).getName()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customerTypeList"))
                .andExpect(model().attributeExists("goodsList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andReturn();
        List<MallGoods> realGoodsListWithGoodsNameAndCustomerTypeId = (List<MallGoods>) resultWithGoodsNameAndCustomerTypeId.getModelAndView().getModel().get("goodsList");
        Assert.assertNotNull(realGoodsListWithGoodsNameAndCustomerTypeId);
        Assert.assertEquals(1, realGoodsListWithGoodsNameAndCustomerTypeId.size());
        Assert.assertEquals(customerTypeGoods.get(0).getGoodsId(), realGoodsListWithGoodsNameAndCustomerTypeId.get(0).getGoodsId());

        // 7.按标准类型和自定义类型搜索
        MvcResult resultWithStandardAndCustomerTypeId = mockMvc.perform(get(controllerUrl).session(session)
                .param("customerTypeId", String.valueOf(mockCustomerGoodsType.getTypeId()))
                .param("standardTypeId", standardGoodsType.getStandardTypeId()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("customerTypeList"))
                .andExpect(model().attributeExists("goodsList"))
                .andExpect(model().attributeExists("pageSize"))
                .andExpect(model().attributeExists("pageNo"))
                .andExpect(model().attributeExists("totalPages"))
                .andExpect(model().attributeExists("totalRecords"))
                .andReturn();
        List<MallGoods> realGoodsListWithStandardANdCustomerTypeId = (List<MallGoods>) resultWithStandardAndCustomerTypeId.getModelAndView().getModel().get("goodsList");
        Assert.assertNotNull(realGoodsListWithStandardANdCustomerTypeId);
        Assert.assertEquals(0, realGoodsListWithStandardANdCustomerTypeId.size());
    }

    /**
     * 一级代理商下级门店登录 显示商品采购列表
     *
     * @throws Exception
     */
    @Test
    @SuppressWarnings("Duplicates")
    public void testShowGoodsListByFirstLevelShop() throws Exception {
        String controllerUrl = BASE_URL + "/showGoodsList";
        //一级代理商下级门店登录
        MockHttpSession session = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));

        //1.显示上级代理商商品
        MvcResult resultWithNoSearch = mockMvc.perform(get(controllerUrl).session(session))
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
        MvcResult resultWithGoodsName = mockMvc.perform(get(controllerUrl).session(session)
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
    @SuppressWarnings("Duplicates")
    public void testShowGoodsListBySecondLevelAgent() throws Exception {
        String controllerUrl = BASE_URL + "/showGoodsList";
        //一级代理商下级代理商登录
        MockHttpSession session = loginAs(mockSecondLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        //1.显示上级代理商商品
        MvcResult resultWithNoSearch = mockMvc.perform(get(controllerUrl).session(session))
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
        MvcResult resultWithGoodsName = mockMvc.perform(get(controllerUrl).session(session)
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
        Assert.assertEquals("订购数量必须大于0！", objWithNum0.getString("msg"));

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
        Assert.assertEquals("订购数量必须大于0！", objWithNum0.getString("msg"));

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
        Integer randomNum = random.nextInt(100) + mockFirstLevelAgentProductList.get(0).getStore() - mockFirstLevelAgentProductList.get(0).getFreez() + 1;
        MvcResult resultWithTooMuchNum = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", String.valueOf(randomNum))
                        .param("goodsId", String.valueOf(mockFirstLevelAgentProductList.get(0).getGoodsId())))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithTooMuchNum = new String(resultWithTooMuchNum.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithTooMuchNum = JSONObject.parseObject(contentWithTooMuchNum);
        Assert.assertTrue(randomNum > (mockFirstLevelAgentProductList.get(0).getStore() - mockFirstLevelAgentProductList.get(0).getFreez()));
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
        MallProduct mockProductWithNProduct = null;
        for (MallGoods mockGoods : mockFirstLevelAgentGoodsWithNProductList) {
            for (MallProduct mockProduct : mockGoods.getProducts()) {
                if (mockProduct.getStore() - mockProduct.getFreez() > 1) {
                    mockProductWithNProduct = mockProduct;
                    break;
                }
            }
            if (mockProductWithNProduct != null) {
                break;
            }
        }
        //9.单个货品 校验
        MvcResult resultWithNProduct = mockMvc.perform(
                post("/purchase/addShopping")
                        .session(session)
                        .param("num", "1")
                        .param("productId", String.valueOf(mockProductWithNProduct.getProductId())))
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
    public void testShowProductListBySecondLevelAgent() throws Exception {
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

    @Test
    public void testGetStandardType() throws Exception {
        //一级代理商登录
        MockHttpSession session = loginAs(mockFirstLevelAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        String controllerUrl = "/purchase/getType";
        //1.不传standardTypeId
        MvcResult resultWithNoParam = mockMvc.perform(post(controllerUrl).session(session))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoParam = new String(resultWithNoParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(contentWithNoParam);
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultCode(), obj.getIntValue("code"));
        List<MallGoodsType> expectTypeList = goodsTypeRepository.findByParentStandardTypeIdAndDisabledFalseAndCustomerIdOrderByTOrderAsc("0", -1);
        List<MallGoodsType> realTypeListWithNoParam = JSONArray.parseArray(obj.getString("data"), MallGoodsType.class);
        Assert.assertEquals(expectTypeList.size(), realTypeListWithNoParam.size());
        for (int i = 0; i < expectTypeList.size(); i++) {
            Assert.assertEquals(expectTypeList.get(i).getName(), realTypeListWithNoParam.get(i).getName());
        }
        //2.standardTypeId为0
        MvcResult resultWith0 = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("standardTypeId", "0"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWith0 = new String(resultWith0.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWith0 = JSONObject.parseObject(contentWith0);
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultCode(), objWith0.getIntValue("code"));
        List<MallGoodsType> realTypeWith0 = JSONArray.parseArray(objWith0.getString("data"), MallGoodsType.class);
        Assert.assertEquals(expectTypeList.size(), realTypeWith0.size());
        for (int i = 0; i < expectTypeList.size(); i++) {
            Assert.assertEquals(expectTypeList.get(i).getName(), realTypeWith0.get(i).getName());
        }
        //3.standardTypeId 不存在
        MvcResult resultWithErrorParam = mockMvc.perform(post(controllerUrl)
                .session(session)
                .param("standardTypeId", "-1"))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithErrorParam = new String(resultWithErrorParam.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithErrorParam = JSONObject.parseObject(contentWithErrorParam);
        Assert.assertEquals(ResultCodeEnum.DATA_NULL.getResultCode(), objWithErrorParam.getIntValue("code"));
    }

}