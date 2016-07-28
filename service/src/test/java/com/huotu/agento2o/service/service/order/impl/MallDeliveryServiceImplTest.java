/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallDelivery;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.order.MallDeliveryService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

/**
 * Created by AiWelv on 2016/5/25.
 */
public class MallDeliveryServiceImplTest extends CommonTestBase {

    @Autowired
    private MallDeliveryService deliveryService;

//    @Test
    public void testGetPage() throws Exception {
        // TODO: 2016/7/28
        /*int pageIndex = 1;
        Shop shop = new Shop();
//        shop.setId(8);
        int pageSize = 20;
        DeliverySearcher deliverySearcher = new DeliverySearcher();
//        deliverySearcher.setAgentId(shop.getId());
        String type = "delivery";
        Page<MallDelivery> mallDeliveries = deliveryService.getPage(pageIndex, shop, pageSize, deliverySearcher, type);

        Assert.assertNotNull(mallDeliveries);*/
    }
}
