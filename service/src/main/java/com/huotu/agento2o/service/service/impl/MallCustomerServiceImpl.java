/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.impl;

import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.service.MallCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2016/5/14.
 */
@Service
public class MallCustomerServiceImpl implements MallCustomerService {
    @Autowired
    private MallCustomerRepository customerRepository;
    @Autowired
    private PasswordEncoder passwordEncoder;

    /**
     * 用于单元测试
     * @param customer
     * @return
     */
    @Override
    @Transactional
    public MallCustomer newCustomer(MallCustomer customer) {
        customer.setPassword(passwordEncoder.encode(customer.getPassword()));
        customer = customerRepository.save(customer);
        customerRepository.flush();
        return customer;
    }

    @Override
    public MallCustomer findByCustomerId(Integer customerId) {
        return customerRepository.findOne(customerId);
    }
}
