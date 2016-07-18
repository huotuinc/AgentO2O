/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.config.MallPasswordEncoder;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.AgentShopService;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;


/**
 * Created by helloztt on 2016/5/9.
 */

public class AgentShopServiceImplTest extends CommonTestBase {

    @Autowired
    private AgentShopService agentShopService;
    @Autowired
    private MallPasswordEncoder passwordEncoder;


//    @Test
//    @Rollback(false)
    public void testMockShop(){
        Agent agent = agentService.findByAgentId(29756);
        mockShop(agent,115.893528,28.689578);
        mockShop(agent,120.189014,30.249473);
        mockShop(agent,120.152929,30.189956);
        mockShop(agent,120.19237,30.187588);
    }



    @Test
    public void testFindByUserName() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());
        ShopAuthor shop = agentShopService.findByUserName(getShop.getUsername());
        Assert.assertTrue(shop != null);

    }

    @Test
    public void testFindById() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());
        ShopAuthor shop = agentShopService.findById(getShop.getId());
        Assert.assertTrue(shop != null);
    }

    @Test
    public void testFindByIdAndParentAuthor() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());
        ShopAuthor shop = agentShopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(shop != null);

        shop = agentShopService.findByIdAndParentAuthor(getShop.getId(), null);
        Assert.assertTrue(shop == null);

        shop = agentShopService.findByIdAndParentAuthor(null, null);
        Assert.assertTrue(shop == null);
    }

    @Test
    public void testFindByIdAndCustomer_Id() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());
        getShop.setCustomer(mockCustomer);
        agentShopService.saveOrUpdateShop(getShop, null, parentMockAgent.getAgent());

        ShopAuthor curShop = agentShopService.findByIdAndCustomer_Id(getShop.getId(), mockCustomer.getCustomerId());
        Assert.assertTrue(curShop.getCustomer().equals(mockCustomer));
    }

    @Test
    public void updateStatus() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());

        agentShopService.updateStatus(AgentStatusEnum.CHECKED, getShop.getId(), parentMockAgent.getAgent());
        ShopAuthor curShop = agentShopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.getStatus() == AgentStatusEnum.CHECKED);

        getShop.setDisabled(true);
        ApiResult apiResult = agentShopService.updateStatus(AgentStatusEnum.CHECKED, getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(apiResult.getMsg().equals("该门店已被冻结"));

        getShop.setDeleted(true);
        apiResult = agentShopService.updateStatus(AgentStatusEnum.CHECKED, getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(apiResult.getMsg().equals("该门店已被删除"));
    }

    @Test
    public void testUpdateStatusAndAuditComment() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());

        agentShopService.updateStatusAndAuditComment(AgentStatusEnum.CHECKED, "1113", getShop.getId());
        ShopAuthor curShop = agentShopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.getStatus() == AgentStatusEnum.CHECKED && curShop.getAuditComment().equals("1113"));
    }

    @Test
    public void testDeleteById() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());

        agentShopService.deleteById(getShop.getId(), parentMockAgent.getAgent());
        ShopAuthor curShop = agentShopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.isDeleted() == true);
    }

    @Test
    public void testUpdateIsDisabledById() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());

        agentShopService.updateIsDisabledById(getShop.getId());
        ShopAuthor curShop = agentShopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.isDisabled() == true);
    }

    @Test
    public void testUpdatePasswordById() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        CustomerAuthor parentMockAgent = mockAgent(mockCustomer, null);
        ShopAuthor getShop = mockShop(parentMockAgent.getAgent());

        agentShopService.updatePasswordById("3", getShop.getId());
        ShopAuthor curShop = agentShopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.getPassword().equals(passwordEncoder.encode("3")));
    }

    @Test
    public void testFindAll() {
        ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
        Page<ShopAuthor> shops = agentShopService.findAll(1, 2, shopSearchCondition);
        Assert.assertTrue(shops.getContent().size() > 0);
    }
}