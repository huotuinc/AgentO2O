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
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import java.util.Date;

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
        //平台方
        MallCustomer mockCustomer = mockMallCustomer();
        //代理商
        Agent parentMockAgent = mockAgent(mockCustomer, null);
        //门店
        Shop getShop = mockShop(parentMockAgent);

        Shop shop = shopService.findByUserName(getShop.getUsername());
        Assert.assertTrue(shop != null);
    }

    @Test
    public void testFindById() {
        //平台方
        MallCustomer mockCustomer = mockMallCustomer();
        //代理商
        Agent parentMockAgent = mockAgent(mockCustomer, null);
        //门店
        Shop getShop = mockShop(parentMockAgent);
        Shop shop = shopService.findById(getShop.getId());
        Assert.assertTrue(shop != null);
    }

    @Test
    public void testAddshop() {
        //平台方
        MallCustomer mockCustomer = mockMallCustomer();
        //代理商
        Agent parentMockAgent = mockAgent(mockCustomer, null);
        Shop shop = new Shop();
        shop.setUsername("testShop01");
        shop.setPassword("1");
        shop.setName("testShop");
        shop.setContact("test");
        shop.setMobile("18666666666");
        shop.setTelephone("18666666666");
        shop.setEmail("10010@126.com");
        shop.setProvince("浙江");
        shop.setCity("杭州");
        shop.setDistrict("滨江");
        shop.setAddress("杭州滨江千陌路智慧e谷");
        shop.setLan(111.22222);
        shop.setLat(3.44);
        shop.setComment("备注");
        shop.setAuditComment("审核备注。。");
        shop.setAfterSalQQ("10010");
        shop.setAfterSalTel("18666666666");
        shop.setServeiceTel("18666666666");
        shop.setStatus(AgentStatusEnum.NOT_CHECK);
        shop.setBankName("杭州银行");
        shop.setAccountName("thz");
        shop.setAccountNo("1234567899");
        shop.setCreateTime(new Date());
        shop.setParentAuthor(parentMockAgent);
        shopService.addShop(shop, "1234");
        Shop curShop = shopService.findByUserName(shop.getUsername());
        Assert.assertTrue(curShop != null && curShop.getUserBaseInfo() != null);
    }

    @Test
//    @Rollback(value = false)
    public void updateStatus(){
        //平台方
        MallCustomer mockCustomer = mockMallCustomer();
        //代理商
        Agent parentMockAgent = mockAgent(mockCustomer, null);
        //门店
        Shop getShop = mockShop(parentMockAgent);
        shopService.updateStatus(AgentStatusEnum.RETURNED,getShop.getId());
        shopService.flush();
        Shop curShop = shopService.findByUserName(getShop.getUsername());
        Assert.assertTrue(curShop != null && curShop.getStatus() == AgentStatusEnum.RETURNED);
    }

}