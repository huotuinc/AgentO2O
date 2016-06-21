/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.controller.order;

import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
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
 * Created by AiWelv on 2016/5/25.
 */
public class OrderControllerTest extends CommonTestBase {

    private static String BASE_URL = "/order";

    //平台方
    private MallCustomer mockCustomer;
    //一级代理商
    private Agent mockFirstLevelAgent;
    //二级代理商
    private Agent mockSecondLevelAgent;
    //一级代理商下级门店
    private Shop mockFirstLevelShop;
    //二级代理商下级门店1,门店2
    private Shop mockSecondLevelShopOne;
    private Shop mockSecondLevelShopTwo;

    //二级代理商下级门店的订单
    private List<MallOrder> mockSecondLevelShopOneList = new ArrayList();
    private List<MallOrder> mockSecondLevelShopTwoList = new ArrayList();
    //二级代理商所能看见的订单
    private List<MallOrder> mockSecondLevelAgentList = new ArrayList();


    @Before
    @SuppressWarnings("Duplicates")
    public void init() {
        //模拟数据
        //用户相关
        System.out.println(passWord+"____");
        mockCustomer = mockMallCustomer();
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent);
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent);
        mockSecondLevelShopOne = mockShop(mockCustomer, mockSecondLevelAgent);
        mockSecondLevelShopTwo = mockShop(mockCustomer, mockSecondLevelAgent);

        //二级代理商下级门店1的订单
        for (int i = 0; i <= random.nextInt(10) + 1; i++) {
            mockSecondLevelShopOneList.add(mockMallOrder(mockSecondLevelShopOne));
        }

        //二级代理商下级门店2的订单
        for (int i = 0; i < random.nextInt(5) + 1; i++) {
            mockSecondLevelShopTwoList.add(mockMallOrder(mockSecondLevelShopTwo));
        }

    }

    /**
     * 代理商/门店订单列表
     *
     * @throws Exception
     */
    @Test
    public void testGetOrdersAll() throws Exception {
        //二级代理商下级门店1订单列表
        MockHttpSession sessionShop1 = loginAs(mockSecondLevelShopOne.getUsername(), passWord);
        MvcResult resultShop1 = mockMvc.perform(
                get(BASE_URL + "/getOrdersPage")
                        .session(sessionShop1))
                .andExpect(status().isOk())
                .andReturn();
        List<MallOrder> mallOrderList1 = (List<MallOrder>) resultShop1.getModelAndView().getModel().get("ordersList");
        Assert.assertEquals(mockSecondLevelShopOneList.size(), mallOrderList1.size());

        //二级代理商下级门店2订单列表
        MockHttpSession sessionShop2 = loginAs(mockSecondLevelShopTwo.getUsername(), passWord);
        MvcResult resultShop2 = mockMvc.perform(
                get(BASE_URL + "/getOrdersPage").session(sessionShop2)).andExpect(status().isOk()).andReturn();
        List<MallOrder> mallOrderList2 = (List<MallOrder>) resultShop2.getModelAndView().getModel().get("ordersList");
        Assert.assertEquals(mockSecondLevelShopTwoList.size(), mallOrderList2.size());

        //二级代理商所显示的所有下属门店的订单
        MockHttpSession sessionAgent = loginAs(mockSecondLevelAgent.getUsername(), passWord);
        MvcResult resultAgent = mockMvc.perform(
                get(BASE_URL + "/getOrdersPage").session(sessionAgent)).andExpect(status().isOk()).andReturn();
        List<MallOrder> mallOrderAgentList = (List<MallOrder>) resultAgent.getModelAndView().getModel().get("ordersList");
        Assert.assertEquals(mockSecondLevelShopOneList.size() + mockSecondLevelShopTwoList.size(), mallOrderAgentList.size());
    }

    @Test
    public void testEditRemark() throws Exception {

        MockHttpSession sessionShop1 = loginAs(mockSecondLevelShopOne.getUsername(), passWord);
        MvcResult resultShop1 = mockMvc.perform(
                post(BASE_URL + "/remark")
                        .session(sessionShop1).param("orderId", mockSecondLevelShopOneList.get(0).getOrderId()).
                        param("agentMarkType", "p3").param("agentMarkText", "XXXPPP"))
                .andExpect(status().isOk())
                .andReturn();
        String content = new String(resultShop1.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));

    }

    @Test
    public void testShowOrderDetail() throws Exception{
        MockHttpSession sessionShop1 = loginAs(mockSecondLevelShopOne.getUsername(), passWord);
        MvcResult resultShop1 = mockMvc.perform(
                get(BASE_URL + "/showOrderDetail")
                        .session(sessionShop1).param("orderId", mockSecondLevelShopOneList.get(0).getOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        OrderDetailModel orderDetailModel = (OrderDetailModel) resultShop1.getModelAndView().getModel().get("orderDetail");
        Assert.assertTrue(orderDetailModel!=null);
    }

}
