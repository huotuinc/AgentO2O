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
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.entity.order.MallOrderItem;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * 点击发货功能是，测试其是否有相应的库存，其库存的判断为nums<=freez<=store
 * Created by AiWelv on 2016/6/6.
 */
public class DeliveryControllerTest extends CommonTestBase {

    private static String BASE_URL = "/order";

    //平台方
    private MallCustomer mockCustomer;
    //一级代理商
    private MallCustomer mockFirstLevelAgent;
    //一级代理商下级门店
    private Shop mockFirstLevelShop;

    private List<MallProduct> mockCustomerProduct = new ArrayList<>();
    private List<AgentProduct> mockShopProduct = new ArrayList();

    @Before
    @SuppressWarnings("Duplicates")
    public void init() {
        //模拟数据
        //用户相关
        mockCustomer = mockMallCustomer();
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent.getAgent());

        //创建平台商品(保证至少有5件货品)
        for (int i = 0; i < random.nextInt(10) + 5; i++) {
            mockCustomerProduct.add(mockMallProduct());
        }
        //门店的订单(保证至少有2件货品)
        for (int i = 0; i < random.nextInt(mockCustomerProduct.size() - 2) + 2; i++) {
            mockShopProduct.add(mockAgentProduct(mockCustomerProduct.get(i),mockFirstLevelShop));
        }

    }

    //1.订单单个货品，库存充足（临界值预计返回200）
    @Test
    public void testJudgeStockByOneItemEnough() throws Exception{
        //模拟数据
        MallOrder order = mockMallOrder(mockFirstLevelShop);
        List<MallOrderItem> orderItems = new ArrayList<>();
        AgentProduct randomProduct = mockShopProduct.get(random.nextInt(mockShopProduct.size()));
        orderItems.add(mockMallOrderItem(order,randomProduct.getProduct(),null,randomProduct.getFreez()));

        MockHttpSession sessionShop1 = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult result = mockMvc.perform(
                get(BASE_URL + "/judgeStock")
                        .session(sessionShop1).param("orderId", order.getOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoId = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoId = JSONObject.parseObject(contentWithNoId);
        Assert.assertEquals(200, objWithNoId.get("code"));

    }
    //2.订单单个货品，库存不足（预计返回505）
    @Test
    public void testJudgeStockByOneItemNotEnough() throws Exception{
        //模拟数据
        MallOrder order = mockMallOrder(mockFirstLevelShop);
        List<MallOrderItem> orderItems = new ArrayList<>();
        AgentProduct randomProduct = mockShopProduct.stream().findAny().get();
        orderItems.add(mockMallOrderItem(order,randomProduct.getProduct(),null,randomProduct.getFreez() + 1));

        MockHttpSession sessionShop1 = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult result = mockMvc.perform(
                get(BASE_URL + "/judgeStock")
                        .session(sessionShop1).param("orderId", order.getOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoId = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoId = JSONObject.parseObject(contentWithNoId);
        Assert.assertEquals(505, objWithNoId.get("code"));
    }
    //3.订单多个货品，所有货品库存充足（预计返回200）
    @Test
    public void testJudgeStockByManyItemEnough() throws Exception{
        //模拟数据
        MallOrder order = mockMallOrder(mockFirstLevelShop);
        List<MallOrderItem> orderItems = new ArrayList<>();
        for(int i = 0 ; i < mockShopProduct.size() ; i++){
            AgentProduct agentProduct = mockShopProduct.get(i);
            orderItems.add(mockMallOrderItem(order,agentProduct.getProduct(),null,1));
        }
        MockHttpSession sessionShop1 = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult result = mockMvc.perform(
                get(BASE_URL + "/judgeStock")
                        .session(sessionShop1).param("orderId", order.getOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoId = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoId = JSONObject.parseObject(contentWithNoId);
        Assert.assertEquals(200, objWithNoId.get("code"));
    }
    //4.订单多个货品，存在货品库存不足（预计返回505）
    @Test
    public void testJudgeStockByManyItemNotEnough() throws Exception{
        //模拟数据
        MallOrder order = mockMallOrder(mockFirstLevelShop);
        List<MallOrderItem> orderItems = new ArrayList<>();
        //2个item,第一个库存充足，第二个库存不足
        AgentProduct agentProduct1 = mockShopProduct.get(0);
        orderItems.add(mockMallOrderItem(order,agentProduct1.getProduct(),null,1));
        AgentProduct agentProduct2 = mockShopProduct.get(1);
        orderItems.add(mockMallOrderItem(order,agentProduct2.getProduct(),null,agentProduct2.getFreez()+1));

        MockHttpSession sessionShop1 = loginAs(mockFirstLevelShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult result = mockMvc.perform(
                get(BASE_URL + "/judgeStock")
                        .session(sessionShop1).param("orderId", order.getOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoId = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoId = JSONObject.parseObject(contentWithNoId);
        Assert.assertEquals(505, objWithNoId.get("code"));
    }

}
