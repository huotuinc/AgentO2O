/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.goods.impl;

import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.common.CustomerTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;

import static org.junit.Assert.*;

/**
 * Created by helloztt on 2016/5/14.
 */
public class MallGoodsServiceImplTest extends CommonTestBase {
    @Autowired
    private MallGoodsService mallGoodsService;


    @Test
    public void testFindByCustomerIdAndAgentId() throws Exception {
        //平台方
        MallCustomer mockCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        //代理商
        MallCustomer mockAgent = mockAgent(mockCustomer,null);
        MallGoods goods = mockMallGoods(mockCustomer.getCustomerId(),null);
        GoodsSearcher searcher = new GoodsSearcher();
        searcher.setPageNo(1);
        Page<MallGoods> customerGoodsList = mallGoodsService.findByCustomerIdAndAgentId(mockCustomer.getCustomerId(),mockAgent,searcher);
        Assert.assertEquals(customerGoodsList.getTotalElements(),1);
        MallGoods searchGoods = customerGoodsList.getContent().get(0);
        Assert.assertEquals(goods.getName(),searchGoods.getName());
    }

    @Test
    public void testFindByAgentId() throws Exception {
        //平台方
        MallCustomer mockCustomer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        //代理商
        MallCustomer parentMockAgent = mockAgent(mockCustomer,null);
        MallCustomer mockAgent = mockAgent(mockCustomer,parentMockAgent.getAgent());
        //平台方商品
        MallGoods mockGoods = mockMallGoods(mockCustomer.getCustomerId(),null);
        //平台方货品
        MallProduct mockProduct = mockMallProduct(mockGoods);
        //代理商货品
        AgentProduct mockAgentProduct = mockAgentProduct(mockProduct,parentMockAgent.getAgent());
        GoodsSearcher searcher = new GoodsSearcher();
        searcher.setPageNo(1);
        Page<MallGoods> agentGoodsList = mallGoodsService.findByAgentId(mockAgent,searcher);
        Assert.assertEquals(agentGoodsList.getTotalElements(),1);
        MallGoods searchGoods = agentGoodsList.getContent().get(0);
        Assert.assertEquals(mockGoods.getName(),searchGoods.getName());

        //根据商品名称搜索
        searcher.setGoodsName(mockGoods.getName());
        Page<MallGoods> agentGoodsListByName = mallGoodsService.findByAgentId(mockAgent,searcher);
        Assert.assertEquals(agentGoodsListByName.getTotalElements(),1);
        Assert.assertEquals(mockGoods.getName(),agentGoodsListByName.getContent().get(0).getName());
    }
}