/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.config.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.entity.config.Address;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.config.AddressService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * Created by WangJie on 2016/5/11.
 */

public class AddresslServiceImplTest extends CommonTestBase {

    @Autowired
    private AddressService addressService;

    private Address mockAddress;

    private CustomerAuthor mockAgent;

    @Before
    public void init() {
        CustomerAuthor mockCustomer = mockMallCustomer();
        mockAgent = mockAgent(mockCustomer, null);
        mockAddress = mockAddress(mockAgent);
    }

    @Test
    public void testFindById() throws Exception {
        Address address = addressService.findById(mockAddress.getId(), mockAgent);
        Assert.assertNotNull(address);
        address = addressService.findById(null, mockAgent);
        Assert.assertNull(address);
        address = addressService.findById(mockAddress.getId(), null);
        Assert.assertNull(address);
        address = addressService.findById(null, null);
        Assert.assertNull(address);
        address = addressService.findById(-1, null);
        Assert.assertNull(address);
    }

    @Test
    public void testFindAddressByAuthorId() throws Exception {
        List<Address> addressList = addressService.findAddressByAuthorId(mockAgent);
        Assert.assertTrue(addressList.size() > 0);
        addressList = addressService.findAddressByAuthorId(null);
        Assert.assertNull(addressList);
    }

    @Test
    public void testAddOrUpdate() throws Exception {
        Address requestAddress = new Address();
        requestAddress.setReceiver(UUID.randomUUID().toString());
        ApiResult result = addressService.addOrUpdate(mockAddress.getId(), mockAgent, requestAddress);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.SUCCESS.getResultMsg()));
        Assert.assertEquals(requestAddress.getReceiver(), addressService.findById(mockAddress.getId(), mockAgent).getReceiver());
        result = addressService.addOrUpdate(null, null, requestAddress);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.DATA_NULL.getResultMsg()));
        result = addressService.addOrUpdate(mockAddress.getId()+1, mockAgent, requestAddress);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.DATA_NULL.getResultMsg()));
        mockAgent.getAgent().setDeleted(true);
        result = addressService.addOrUpdate(mockAddress.getId(), mockAgent, requestAddress);
        Assert.assertTrue(result.getMsg().equals("该账号已失效"));
    }

    @Test
    public void testDeleteAddress() throws Exception {
        ApiResult result = addressService.deleteAddress(mockAddress.getId(),mockAgent);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.SUCCESS.getResultMsg()));
        Address address = addressService.findById(mockAddress.getId(), mockAgent);
        Assert.assertNull(address);
        result = addressService.deleteAddress(null,mockAgent);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.DATA_NULL.getResultMsg()));
        result = addressService.deleteAddress(-1,mockAgent);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.DATA_NULL.getResultMsg()));
        result = addressService.deleteAddress(mockAddress.getId(),null);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.DATA_NULL.getResultMsg()));
    }

    @Test
    public void testConfigDefault() throws Exception {
        Address address = addressService.findById(mockAddress.getId(), mockAgent);
        Assert.assertTrue(!address.isDefault());
        ApiResult result = addressService.configDefault(mockAddress.getId(), mockAgent);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.SUCCESS.getResultMsg()));
        address = addressService.findById(mockAddress.getId(), mockAgent);
        Assert.assertTrue(address.isDefault());
        result = addressService.configDefault(null, mockAgent);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.DATA_NULL.getResultMsg()));
        result = addressService.configDefault(-1, mockAgent);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.DATA_NULL.getResultMsg()));
        result = addressService.configDefault(mockAddress.getId(), null);
        Assert.assertTrue(result.getMsg().equals(ResultCodeEnum.DATA_NULL.getResultMsg()));
    }

}