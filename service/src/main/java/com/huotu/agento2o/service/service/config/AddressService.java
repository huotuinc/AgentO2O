/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.config;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.entity.config.Address;

import java.util.List;

/**
 * Created by WangJie on 2016/5/24.
 */
public interface AddressService {

    /**
     * 根据代理商或门店id获取收货地址
     *
     * @param id
     * @return
     */
    List<Address> findAddressByAuthorId(Integer id);

    /**
     * 根据唯一id获取某地址
     *
     * @param id
     * @return
     */
    Address findById(Integer id);

    Address findDefaultByAuthorId(Integer authorId);

    /**
     * 根据addressId增加或修改收货地址
     *
     * @param addressId      >0时为修改
     * @param authorId
     * @param requestAddress
     * @return
     */
    ApiResult addOrUpdate(Integer addressId, Integer authorId, Address requestAddress);

    /**
     * 删除收货地址
     *
     * @param addressId
     * @param authorId
     * @return
     */
    ApiResult deleteAddress(Integer addressId, Integer authorId);

    /**
     * 设置默认地址
     *
     * @param addressId
     * @param authorId
     * @return
     */
    ApiResult configDefault(Integer addressId, Integer authorId);
}
