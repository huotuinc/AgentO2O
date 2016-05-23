/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.entity.order.MallOrderItem;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.order.MallOrderItemService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;

/**
 * Created by AiWelv on 2016/5/18.
 */
public class MallOrderItemServiceImplTest extends CommonTestBase {

    @Autowired
    private MallOrderItemService orderItemService;

    @Test
    public void testFindMallOrderItemByOrderId() throws Exception {
        List<MallOrderItem> orderItemList = orderItemService.findMallOrderItemByOrderId("2014081986758464");
        Assert.assertTrue(orderItemList.size()==3);
    }

}
