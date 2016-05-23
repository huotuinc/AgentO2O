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

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.repository.author.ShopRepository;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.ShopService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2016/5/9.
 */

@ActiveProfiles("test")
@ContextConfiguration(classes = ServiceConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class ShopServiceImplTest {

    @Autowired
    private ShopService shopService;
    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    public void testAddShop() throws Exception {

    }

    @Test
    public void findByUserName() throws Exception {
        Shop shop = shopService.findByUserName("xl");
        Assert.assertTrue(shop != null);
    }

    @Test
    public void findById() throws Exception {
        Shop shop = shopService.findById(64);
        Assert.assertTrue(shop != null);
    }

    @Test
    public void addShop() {
        Shop shop = new Shop();
        shop.setStatus(AgentStatusEnum.CHECKED);
        shop.setPassword("1");
        shop.setUsername("QWE");
        shop.setDeleted(false);
        shop.setDisabled(false);
        shopService.addShop(shop, "7VGVEU1X9T3");
        Shop findShop = shopService.findByUserName("QWE");
        Assert.assertTrue(findShop != null);
    }

    @Test
    public void updateStatus() {
        int id = 110;
        shopService.updateStatus(AgentStatusEnum.RETURNED, id);
        Shop shop = shopService.findById(id);
        Assert.assertTrue(shop.getStatus() == AgentStatusEnum.RETURNED);
    }

    @Test
    public void updateStatusAndAuditComment() {
        int id = 110;
        shopService.updateStatusAndAuditComment(AgentStatusEnum.RETURNED, "不通过2321", id);
        Shop shop = shopService.findById(id);
        Assert.assertTrue(shop.getStatus() == AgentStatusEnum.RETURNED &&
                shop.getAuditComment().equals("不通过2321"));

    }

    @Test
    public void deleteById() {
        int id = 110;
        shopService.deleteById(id);
        Shop shop = shopService.findById(id);
        Assert.assertTrue(shop.isDeleted() == true);
    }

    @Test
    public void updateIsDisabledById() {
        int id = 110;
        shopService.updateIsDisabledById(true, id);
        Shop shop = shopService.findById(id);
        Assert.assertTrue(shop.isDisabled() == true);
    }

    @Test
    public void updatePasswordById() {
        int id = 110;
        shopService.updatePasswordById("2", id);
        Shop shop = shopService.findById(id);
        Assert.assertTrue(shop.getPassword().equals(passwordEncoder.encode("2")));
    }

    @Test
    public void findAll() {
        ShopSearchCondition searchCondition = new ShopSearchCondition();
        searchCondition.setParent_agentLevel(41);
        Page<Shop> shops = shopService.findAll(1, 20, searchCondition);
        Assert.assertTrue(shops.getContent().size() > 0);
    }

}