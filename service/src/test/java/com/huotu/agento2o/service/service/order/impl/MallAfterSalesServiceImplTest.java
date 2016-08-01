/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.common.CustomerTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.searchable.AfterSaleSearch;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.order.MallAfterSalesService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AiWelv on 2016/5/23.
 */
public class MallAfterSalesServiceImplTest extends CommonTestBase {
    @Autowired
    MallAfterSalesService afterSalesService;
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
        mockCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockFirstLevelAgent.getAgent());
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelShopOne = mockShop(mockSecondLevelAgent.getAgent());
        mockSecondLevelShopTwo = mockShop(mockSecondLevelAgent.getAgent());

        //二级代理商下级门店1的售后单
        for (int i = 0; i <= random.nextInt(10) + 1; i++) {
            mockSecondLevelShopOneList.add(mockMallAfterSales(mockSecondLevelShopOne.getShop()));
        }

        //二级代理商下级门店2的售后单
        for (int i = 0; i < random.nextInt(5) + 1; i++) {
            mockSecondLevelShopTwoList.add(mockMallAfterSales(mockSecondLevelShopTwo.getShop()));
        }
    }
    @Test
    public void testFindAll() throws Exception {
        AfterSaleSearch afterSaleSearch = new AfterSaleSearch();
        afterSaleSearch.setAgentId(mockSecondLevelShopOne.getId());
        Page<MallAfterSales> page1 = afterSalesService.findAll(1,mockSecondLevelShopOne,20,mockSecondLevelShopOne.getId(),afterSaleSearch);


        afterSaleSearch.setAgentId(mockSecondLevelShopTwo.getId());
        Page<MallAfterSales> page2 = afterSalesService.findAll(1,mockSecondLevelShopTwo,20,mockSecondLevelShopTwo.getId(),afterSaleSearch);


        afterSaleSearch.setAgentId(mockSecondLevelAgent.getId());
        Page<MallAfterSales> page3 = afterSalesService.findAll(1,mockSecondLevelAgent,20,mockSecondLevelAgent.getId(),afterSaleSearch);

        Assert.assertTrue((page1.getNumber()+page2.getNumber())== page3.getNumber());
    }
}
