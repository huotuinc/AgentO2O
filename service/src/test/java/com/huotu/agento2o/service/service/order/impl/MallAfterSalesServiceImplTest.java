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
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.searchable.AfterSaleSearch;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.order.MallAfterSalesService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

/**
 * Created by Administrator on 2016/5/23.
 */
public class MallAfterSalesServiceImplTest extends CommonTestBase {
    @Autowired
    MallAfterSalesService afterSalesService;

    @Test
    public void testFindAll() throws Exception {
        Shop shop1 = new Shop();
        shop1.setId(8);
        AfterSaleSearch afterSaleSearch = new AfterSaleSearch();
        afterSaleSearch.setAgentId(shop1.getId());
        Page<MallAfterSales> page1 = afterSalesService.findAll(1,shop1,20,shop1.getId(),afterSaleSearch);

        Shop shop2 = new Shop();
        shop2.setId(10);
        afterSaleSearch.setAgentId(shop2.getId());
        Page<MallAfterSales> page2 = afterSalesService.findAll(1,shop2,20,shop2.getId(),afterSaleSearch);

        Agent agent = new Agent();
        agent.setId(2);
        afterSaleSearch.setAgentId(agent.getId());
        Page<MallAfterSales> page3 = afterSalesService.findAll(1,agent,20,agent.getId(),afterSaleSearch);

        Assert.assertTrue((page1.getNumber()+page2.getNumber())== page3.getNumber());
    }
}
