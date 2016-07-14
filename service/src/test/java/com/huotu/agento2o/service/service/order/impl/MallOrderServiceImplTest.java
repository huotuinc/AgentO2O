/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.searchable.OrderSearchCondition;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.order.MallOrderService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by AiWelv on 2016/5/19.
 */
public class MallOrderServiceImplTest extends CommonTestBase {

    @Autowired
    private MallOrderService orderService;

    //平台方
    private MallCustomer mockCustomer;
    //一级代理商
    private MallCustomer mockFirstLevelAgent;
    //二级代理商
    private MallCustomer mockSecondLevelAgent;
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
        mockCustomer = mockMallCustomer();
        mockFirstLevelAgent = mockAgent(mockCustomer, null);
        mockFirstLevelShop = mockShop(mockFirstLevelAgent.getAgent());
        mockSecondLevelAgent = mockAgent(mockCustomer, mockFirstLevelAgent.getAgent());
        mockSecondLevelShopOne = mockShop(mockSecondLevelAgent.getAgent());
        mockSecondLevelShopTwo = mockShop(mockSecondLevelAgent.getAgent());

        //二级代理商下级门店1的订单
        for (int i = 0; i <= random.nextInt(10) + 1; i++) {
            mockSecondLevelShopOneList.add(mockMallOrder(mockSecondLevelShopOne));
        }

        //二级代理商下级门店2的订单
        for (int i = 0; i < random.nextInt(5) + 1; i++) {
            mockSecondLevelShopTwoList.add(mockMallOrder(mockSecondLevelShopTwo));
        }

    }

    @Test
    public void testFindOrderDetail() throws Exception {
        OrderDetailModel detail = orderService.findOrderDetail(mockSecondLevelShopTwoList.get(0).getOrderId());
        Assert.assertTrue(detail != null);
    }

    @Test
    public void testFindAll() throws Exception {

        OrderSearchCondition searchCondition1 = new OrderSearchCondition();
        searchCondition1.setAgentId(mockSecondLevelShopOne.getId());
        Page<MallOrder> page1 = orderService.findAll(1, mockSecondLevelShopOne, 20, searchCondition1);
        int num1 = page1.getContent().size();


        OrderSearchCondition searchCondition2 = new OrderSearchCondition();
        searchCondition2.setAgentId(mockSecondLevelShopTwo.getId());
        Page<MallOrder> page2 = orderService.findAll(1, mockSecondLevelShopTwo, 20, searchCondition2);
        int num2 = page2.getContent().size();


        OrderSearchCondition searchCondition3 = new OrderSearchCondition();
        searchCondition3.setAgentId(mockSecondLevelAgent.getId());
        Page<MallOrder> page3 = orderService.findAll(1, mockSecondLevelAgent, 20, searchCondition3);
        int num3 = page3.getContent().size();

        System.out.println(num1 + "  " + num2 + "  " + num3);

        Assert.assertTrue(num3 == (num1 + num2));

    }

    @Test
    @Rollback(value = false)
    public void testFindByOrderId() throws Exception {
        MallOrder mallOrder = null;
        mallOrder = orderService.findByOrderId(mockSecondLevelShopTwoList.get(0).getOrderId());
        Assert.assertTrue(mallOrder != null);
    }

    @Test
    public void testFindByShopAndOrderId() throws Exception {
        MallOrder mallOrder = null;
        mallOrder = orderService.findByShopAndOrderId(mockSecondLevelShopTwo, mockSecondLevelShopTwoList.get(0).getOrderId());
        Assert.assertTrue(mallOrder != null);
    }

}
