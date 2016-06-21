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
 * Created by AiWelv on 2016/6/6.
 */
public class DeliveryControllerTest extends CommonTestBase {

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

    private List<MallProduct> mockSecondLevelShopMallProductOneList = new ArrayList();
    private List<AgentProduct> mockSecondLevelShopAgentProductOneList = new ArrayList();

    private List<MallOrderItem> mockSecondMallOrderItemList = new ArrayList();

    @Before
    @SuppressWarnings("Duplicates")
    public void init() {
        //模拟数据
        //用户相关
        System.out.println(passWord + "____");
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


        //创建MallProduct
        for (int i = 0; i < 5; i++) {
            mockSecondLevelShopMallProductOneList.add(mockMallProduct());
        }

        //创建AgentProduct
        for (int i = 0; i < 5; i++) {
            mockSecondLevelShopAgentProductOneList.add(mockAgentProduct(mockSecondLevelShopMallProductOneList.get(i), mockSecondLevelShopOne));
        }

        //创建item
        for (int i = 0; i < 5; i++) {
            mockSecondMallOrderItemList.add(mockMallOrderItem(mockSecondLevelShopOneList.get(0), mockSecondLevelShopMallProductOneList.get(i),
                    mockMallAfterSales(mockSecondLevelShopOne, mockSecondLevelShopOneList.get(0).getOrderId()),i+random.nextInt(20)));
        }

    }

    /**
     * 点击发货功能是，测试其是否有相应的库存，其库存的判断为nums<=freez<=store
     * @throws Exception
     */
    @Test
        public void testJudgeStock() throws Exception {
        MockHttpSession sessionShop1 = loginAs(mockSecondLevelShopOne.getUsername(), passWord);
        MvcResult resultShop1 = mockMvc.perform(
                get(BASE_URL + "/judgeStock")
                        .session(sessionShop1).param("orderId", mockSecondLevelShopOneList.get(0).getOrderId()))
                .andExpect(status().isOk())
                .andReturn();
        String contentWithNoId = new String(resultShop1.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject objWithNoId = JSONObject.parseObject(contentWithNoId);
        if (objWithNoId.get("code") == "200"){
            Assert.assertEquals(200, objWithNoId.get("code"));
        }else{
            Assert.assertEquals(505, objWithNoId.get("code"));
        }


    }

}
