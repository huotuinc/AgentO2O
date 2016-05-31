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
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.config.Address;
import com.huotu.agento2o.service.repository.author.AgentRepository;
import com.huotu.agento2o.service.repository.author.AuthorRepository;
import com.huotu.agento2o.service.repository.config.AddressRepository;
import com.huotu.agento2o.service.service.config.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by WangJie on 2016/5/24.
 */
@Service("addressService")
public class AddressServiceImpl implements AddressService {

    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private AuthorRepository authorRepository;

    @Override
    public List<Address> findAddressByAuthorId(Integer id) {
        return addressRepository.findByAuthor_id(id);
    }

    @Override
    public Address findById(Integer id) {
        return addressRepository.findOne(id);
    }

    @Override
    public Address findDefaultByAuthorId(Integer authorId) {
        return addressRepository.findByAuthor_IdAndIsDefaultTrue(authorId);
    }

    @Override
    @Transactional
    public ApiResult addOrUpdate(Integer id, Integer authorId, Address requestAddress) {
        Address address;
        Author author = authorRepository.findOne(authorId);
        if (author == null || author.isDeleted() || author.isDisabled()) {
            return new ApiResult("该账号已失效", 805);
        }
        //id>0代表是修改地址，否则是增加
        if (id > 0) {
            address = findById(id);
            if (address == null) {
                return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
            }
        } else {
            address = new Address();
            address.setAuthor(author);
        }
        address.setReceiver(requestAddress.getReceiver());
        address.setTelephone(requestAddress.getTelephone());
        address.setProvince(requestAddress.getProvince());
        address.setCity(requestAddress.getCity());
        address.setDistrict(requestAddress.getDistrict());
        address.setAddress(requestAddress.getAddress());
        address.setComment(requestAddress.getComment());
        List<Address> addressList = addressRepository.findByAuthor_id(authorId);
        //当增加第一个收货地址时设为默认
        if (addressList.size() < 1) {
            address.setDefault(true);
        } else {
            //假如设为默认需设置其它地址不为默认
            if (requestAddress.isDefault()) {
                clearDefaultAddress(addressList);
                address.setDefault(true);
            }
        }
        addressRepository.save(address);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 循环地址列表清空默认地址
     *
     * @param addressList
     */
    @Transactional
    protected void clearDefaultAddress(List<Address> addressList) {
        addressList.forEach(address -> {
            if (address.isDefault()) {
                address.setDefault(false);
                addressRepository.save(address);
            }
        });
    }

    @Override
    @Transactional
    public ApiResult deleteAddress(Integer addressId, Integer authorId) {
        Address address = addressRepository.findOne(addressId);
        if (address == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        addressRepository.delete(address);
        //假如删除的地址为默认地址，需要重新设置一个默认地址
        if (address.isDefault()) {
            List<Address> addressList = addressRepository.findByAuthor_id(authorId);
            if (addressList.size() > 0) {
                Address defaultAddress = addressList.get(addressList.size() - 1);
                defaultAddress.setDefault(true);
                addressRepository.save(defaultAddress);
            }
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult configDefault(Integer addressId, Integer authorId) {
        Address address = addressRepository.findOne(addressId);
        if (address == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //当该地址已经是默认时不需要任何操作
        if (!address.isDefault()) {
            List<Address> addressList = addressRepository.findByAuthor_id(authorId);
            clearDefaultAddress(addressList);
            address.setDefault(true);
            addressRepository.save(address);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
