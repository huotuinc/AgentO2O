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
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;


/**
 * Created by helloztt on 2016/5/9.
 */

public class ShopServiceImplTest extends CommonTestBase {

    @Autowired
    private ShopService shopService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testFindByUserName() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());
        Shop shop = shopService.findByUserName(getShop.getUsername());
        Assert.assertTrue(shop != null);

    }

    @Test
    public void testFindById() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());
        Shop shop = shopService.findById(getShop.getId());
        Assert.assertTrue(shop != null);
    }

    @Test
    public void testFindByIdAndParentAuthor() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());
        Shop shop = shopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(shop != null);

        shop = shopService.findByIdAndParentAuthor(getShop.getId(), null);
        Assert.assertTrue(shop == null);

        shop = shopService.findByIdAndParentAuthor(null, null);
        Assert.assertTrue(shop == null);
    }

    @Test
    public void testFindByIdAndCustomer_Id() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());
        getShop.setCustomer(mockCustomer);
        shopService.saveOrUpdateShop(getShop, null,parentMockAgent.getAgent());

        Shop curShop = shopService.findByIdAndCustomer_Id(getShop.getId(), mockCustomer.getCustomerId());
        Assert.assertTrue(curShop.getCustomer().equals(mockCustomer));
    }

    @Test
    public void updateStatus() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());

        shopService.updateStatus(AgentStatusEnum.CHECKED, getShop.getId(),parentMockAgent.getAgent());
        Shop curShop = shopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.getStatus() == AgentStatusEnum.CHECKED);

        getShop.setDisabled(true);
        ApiResult apiResult = shopService.updateStatus(AgentStatusEnum.CHECKED, getShop.getId(),parentMockAgent.getAgent());
        Assert.assertTrue(apiResult.getMsg().equals("该门店已被冻结"));

        getShop.setDeleted(true);
        apiResult = shopService.updateStatus(AgentStatusEnum.CHECKED, getShop.getId(),parentMockAgent.getAgent());
        Assert.assertTrue(apiResult.getMsg().equals("该门店已被删除"));
    }

    @Test
    public void testUpdateStatusAndAuditComment() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());

        shopService.updateStatusAndAuditComment(AgentStatusEnum.CHECKED, "1113", getShop.getId());
        Shop curShop = shopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.getStatus() == AgentStatusEnum.CHECKED && curShop.getAuditComment().equals("1113"));
    }

    @Test
    public void testDeleteById() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());

        shopService.deleteById(getShop.getId(),parentMockAgent.getAgent());
        Shop curShop = shopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.isDeleted() == true);
    }

    @Test
    public void testUpdateIsDisabledById() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());

        shopService.updateIsDisabledById(getShop.getId());
        Shop curShop = shopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.isDisabled() == true);
    }

    @Test
    public void testUpdatePasswordById() {
        MallCustomer mockCustomer = mockMallCustomer();
        MallCustomer parentMockAgent = mockAgent(mockCustomer, null);
        Shop getShop = mockShop(parentMockAgent.getAgent());

        shopService.updatePasswordById("3", getShop.getId());
        Shop curShop = shopService.findByIdAndParentAuthor(getShop.getId(), parentMockAgent.getAgent());
        Assert.assertTrue(curShop != null && curShop.getPassword().equals(passwordEncoder.encode("3")));
    }

    @Test
    public void testFindAll() {
        ShopSearchCondition shopSearchCondition = new ShopSearchCondition();
        Page<Shop> shops = shopService.findAll(1, 2, shopSearchCondition);
        Assert.assertTrue(shops.getContent().size() > 0);
    }
}