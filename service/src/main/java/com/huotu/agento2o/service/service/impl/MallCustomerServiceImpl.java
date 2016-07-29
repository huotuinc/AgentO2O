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

import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.CustomerTypeEnum;
import com.huotu.agento2o.service.config.MallPasswordEncoder;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.service.MallCustomerService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;

/**
 * Created by helloztt on 2016/5/14.
 */
@Service("mallCustomerService")
public class MallCustomerServiceImpl implements MallCustomerService {
    @Autowired
    private MallCustomerRepository customerRepository;
    @Autowired
    private MallPasswordEncoder passwordEncoder;
    private Random random = new Random();

    /**
     * 用于单元测试
     *
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
    public MallCustomer newCustomer(String userName, String password, CustomerTypeEnum customerType) {
        String key = StringUtil.createRandomStr(6);
        Integer token = random.nextInt(900000) + 100000;
        //COOKIE_DOMAIN start with .
        String mainDomain = SysConstant.COOKIE_DOMAIN;
        String url = String.format("http://distribute%s/index.aspx?key=%s&t=huotu", mainDomain, key);
        MallCustomer customer = new MallCustomer();
        customer.setUsername(userName);
        customer.setPassword(passwordEncoder.encode(password));
        customer.setIndustryType(0);
        customer.setUserActivate(1);
        customer.setRoleID(-2);
        customer.setBelongManagerID(3);
        customer.setEmail("");
        customer.setIsOld(1);
        customer.setDeveloperUrl(url);
        customer.setDeveloperToken(String.valueOf(token));
        customer.setScType(1);
        customer.setScore(0.0);
        customer.setCityID(0);
        customer.setCustomerType(customerType);
        return customerRepository.saveAndFlush(customer);
    }

    @Override
    public MallCustomer findByCustomerId(Integer customerId) {
        return customerId == null ? null : customerRepository.findOne(customerId);
    }

    @Override
    public UserDetails loadUserByUsername(String userName) throws UsernameNotFoundException {
        MallCustomer mallCustomer = customerRepository.findByUsername(userName);
        if (mallCustomer == null) {
            throw new UsernameNotFoundException("没有该代理商");
        }
        if(mallCustomer.getType() == Shop.class){
            if(mallCustomer.getShop().getStatus() != AgentStatusEnum.CHECKED){
                throw new UsernameNotFoundException("门店未审核");
            }
        }
        return mallCustomer;
    }

    @Override
    @Transactional
    public int resetPassword(Integer customerId, String password) {
        return customerRepository.resetPassword(customerId, passwordEncoder.encode((password)));
    }

    @Override
    public MallCustomer findByUserName(String userName) {
        return customerRepository.findByUsername(userName);
    }

    @Override
    public MallCustomer save(MallCustomer mallCustomer) {
        return customerRepository.saveAndFlush(mallCustomer);
    }
}
