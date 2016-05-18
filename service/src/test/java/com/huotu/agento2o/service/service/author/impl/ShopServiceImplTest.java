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
import com.huotu.agento2o.service.service.author.ShopService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
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
    private ShopService shopService ;
    @Autowired
    private ShopRepository shopRepository ;

    @Test
    public void testAddShop() throws Exception {

    }

    @Test
//    @Rollback(value = false)
    public void  findByUserName() throws  Exception{
        Shop shop =  shopService.findByUserName("123");
        Assert.assertTrue(shop!=null);

    }

    @Test
    public  void addShop(){
        Shop shop = new Shop();
        shop.setStatus(AgentStatusEnum.CHECKED);
        shop.setPassword("1");
        shop.setUsername("zc");

        shop.setDeleted(false);
        shop.setDisabled(false);
        shopService.addShop(shop);
    }

    @Test
    @Rollback(value = false)
    public void subToFac(){
        int id = 50 ;
        shopService.updateStatus(AgentStatusEnum.RETURNED,id);
    }

    @Test
    @Rollback(value = false)
    public void del(){
        int id = 57 ;
        shopService.deleteById(id);
    }

}