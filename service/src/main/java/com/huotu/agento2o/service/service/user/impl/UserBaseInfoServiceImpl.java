/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.user.impl;


import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import com.huotu.agento2o.service.repository.user.UserBaseInfoRepository;
import com.huotu.agento2o.service.service.user.UserBaseInfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * Created by allan on 12/31/15.
 */
@Service("userBaseInfoService")
public class UserBaseInfoServiceImpl implements UserBaseInfoService {
    @Autowired
    private UserBaseInfoRepository userBaseInfoRepository;

    @Override
    public UserBaseInfo findById(Integer id) {
        return userBaseInfoRepository.findOne(id);
    }

    @Override
    public UserBaseInfo findByNameAndCustomerId(String name,Integer id) {
        return userBaseInfoRepository.findByLoginNameAndMallCustomer_customerId(name,id);
    }
}
