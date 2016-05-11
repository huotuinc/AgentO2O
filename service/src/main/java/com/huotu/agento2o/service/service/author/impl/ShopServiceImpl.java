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
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.repository.author.ShopRepository;
import com.huotu.agento2o.service.service.author.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2016/5/9.
 */
@Service(value = "shopService")
public class ShopServiceImpl implements ShopService {
    @Autowired
    private ShopRepository shopRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 根据登录名查找未删除的门店
     * @param userName
     * @return
     */
    @Override
    public Shop findByUserName(String userName) {
        return shopRepository.findByUsernameAndStatus(userName, AgentStatusEnum.CHECKED);
    }

    @Override
    public Shop findById(Integer id) {
        return shopRepository.findOne(id);
    }

    @Override
    @Transactional
    public Shop addShop(Shop shop) {
        //判断门店登录名是否唯一
        Shop checkShop = findByUserName(shop.getUsername());
        if(checkShop != null){
            return null;
        }
        shop.setPassword(passwordEncoder.encode(shop.getPassword()));
        return shopRepository.save(shop);
    }

    @Override
    public void flush() {
        shopRepository.flush();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Shop shop = findByUserName(username);
        if(shop == null){
            throw new UsernameNotFoundException("没有该门店");
        }
        return shop;
    }
}
