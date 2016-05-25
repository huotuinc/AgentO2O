/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.searchable.OrderSearchCondition;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.order.MallOrderService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.test.annotation.Rollback;

/**
 * Created by Administrator on 2016/5/19.
 */
public class MallOrderServiceImplTest extends CommonTestBase {

    @Autowired
    private MallOrderService orderService;

    @Test
    public void testFindOrderDetail() throws Exception {
        String orderId = "2014022661976526";
        OrderDetailModel detail = orderService.findOrderDetail(orderId);
        Assert.assertTrue(detail != null);
    }

    @Test
    public void testFindAll() throws Exception {
        Shop shop1 = new Shop();
        shop1.setId(8);
        OrderSearchCondition searchCondition1 = new OrderSearchCondition();
        searchCondition1.setAgentId(shop1.getId());
        Page<MallOrder> page1 = orderService.findAll(1, shop1, 20, searchCondition1);
        int num1 = page1.getContent().size();

        Shop shop2 = new Shop();
        shop2.setId(10);
        OrderSearchCondition searchCondition2 = new OrderSearchCondition();
        searchCondition2.setAgentId(shop2.getId());
        Page<MallOrder> page2 = orderService.findAll(1, shop2, 20, searchCondition2);
        int num2 = page2.getContent().size();

        Agent agent = new Agent();
        agent.setId(2);
        OrderSearchCondition searchCondition3 = new OrderSearchCondition();
        searchCondition3.setAgentId(agent.getId());
        Page<MallOrder> page3 = orderService.findAll(1, agent, 20, searchCondition3);
        int num3 = page3.getContent().size();

        System.out.println(num1 + "  " + num2 + "  " + num3);

        Assert.assertTrue(num3 == (num1 + num2));

    }

    @Test
    @Rollback(value = false)
    public void testFindByOrderId() throws Exception{
        MallOrder mallOrder = orderService.findByOrderId("2014022661976526");
        Assert.assertTrue(mallOrder!=null);
    }

}
