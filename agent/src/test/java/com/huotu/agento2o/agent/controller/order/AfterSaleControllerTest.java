/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.controller.order;

import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.service.common.CustomerTypeEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
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
 * Created by AiWelv on 2016/5/30.
 */
public class AfterSaleControllerTest extends CommonTestBase {

    private static String BASE_URL = "/agent/afterSale";

    //平台方
    private MallCustomer mockCustomer;
    //一级代理商
    private MallCustomer mockFirstLevelAgent;
    //二级代理商
    private MallCustomer mockSecondLevelAgent;
    //一级代理商下级门店
    private MallCustomer mockFirstLevelShop;
    //二级代理商下级门店1,门店2
    private MallCustomer mockSecondLevelShopOne;
    private MallCustomer mockSecondLevelShopTwo;

    //二级代理商下级门店的售后单
    private List<MallAfterSales> mockSecondLevelShopOneList = new ArrayList();
    private List<MallAfterSales> mockSecondLevelShopTwoList = new ArrayList();
    //二级代理商所能看见的售后单
    private List<MallAfterSales> mockSecondLevelAgentList = new ArrayList();

    @Before
    @SuppressWarnings("Duplicates")
    public void init() {
        //模拟数据
        //用户相关
        System.out.println(passWord + "____");
        mockCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockCustomer, mockFirstLevelAgent.getAgent(),null);
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelShopOne = mockShop(mockCustomer, mockSecondLevelAgent.getAgent(),null);
        mockSecondLevelShopTwo = mockShop(mockCustomer, mockSecondLevelAgent.getAgent(),null);

        //二级代理商下级门店1的订单
        for (int i = 0; i <= random.nextInt(10) + 1; i++) {
            mockSecondLevelShopOneList.add(mockMallAfterSales(mockSecondLevelShopOne.getShop()));
        }

        //二级代理商下级门店2的订单
        for (int i = 0; i < random.nextInt(5) + 1; i++) {
            mockSecondLevelShopTwoList.add(mockMallAfterSales(mockSecondLevelShopTwo.getShop()));
        }
    }

    @Test
    public void testAfterSaleList() throws Exception {
        MockHttpSession sessionShop1 = loginAs(mockSecondLevelShopOne.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult resultShop1 = mockMvc.perform(
                get(BASE_URL + "/afterSaleList")
                        .session(sessionShop1))
                .andExpect(status().isOk())
                .andReturn();
        List<MallAfterSales> mallAfterSales1 = (List<MallAfterSales>) resultShop1.getModelAndView().getModel().get("afterSales");
        Assert.assertEquals(mockSecondLevelShopOneList.size(), mallAfterSales1.size());

        MockHttpSession sessionShop2 = loginAs(mockSecondLevelShopTwo.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult resultShop2 = mockMvc.perform(
                get(BASE_URL + "/afterSaleList")
                        .session(sessionShop2))
                .andExpect(status().isOk())
                .andReturn();
        List<MallAfterSales> mallAfterSales2 = (List<MallAfterSales>) resultShop2.getModelAndView().getModel().get("afterSales");
        Assert.assertEquals(mockSecondLevelShopTwoList.size(), mallAfterSales2.size());

        MockHttpSession sessionAgent = loginAs(mockSecondLevelAgent.getUsername(),passWord,String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult resultAgent = mockMvc.perform(get(BASE_URL + "/afterSaleList")
                .session(sessionAgent))
                .andExpect(status().isOk())
                .andReturn();
        List<MallAfterSales> mallAfterSales3 = (List<MallAfterSales>)resultAgent.getModelAndView().getModel().get("afterSales");
        Assert.assertEquals(mockSecondLevelShopOneList.size()+mockSecondLevelShopTwoList.size(),mallAfterSales3.size());
    }


}
