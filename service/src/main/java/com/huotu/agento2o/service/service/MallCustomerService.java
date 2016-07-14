/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service;

import com.huotu.agento2o.service.entity.MallCustomer;
import org.springframework.security.core.userdetails.UserDetailsService;

/**
 * Created by helloztt on 2016/5/14.
 */
public interface MallCustomerService extends UserDetailsService {
    MallCustomer newCustomer(MallCustomer customer);

    MallCustomer findByCustomerId(Integer customerId);

    int resetPassword(Integer customerId, String password);

    MallCustomer findByUserName(String userName);

    MallCustomer save(MallCustomer mallCustomer);
}
