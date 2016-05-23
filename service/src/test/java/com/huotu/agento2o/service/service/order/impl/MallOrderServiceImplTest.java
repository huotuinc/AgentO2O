/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.order.MallOrderService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

/**
 * Created by Administrator on 2016/5/19.
 */
public class MallOrderServiceImplTest extends CommonTestBase {

    @Autowired
    private MallOrderService orderService;

    @Test
    public void testFindOrderDetail() throws Exception{
        String orderId = "2014022661976526";
        OrderDetailModel detail = orderService.findOrderDetail(orderId);
        Assert.assertTrue(detail!=null);
    }

}
