/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.repository.config;

import com.huotu.agento2o.service.entity.config.Address;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by WangJie on 2016/5/24.
 */
@Repository
public interface AddressRepository extends JpaRepository<Address, Integer> {

    List<Address> findByShop_id(Integer shopId);

    Address findByIdAndShop_id(Integer addressId,Integer shopId);

    Address findByShop_idAndIsDefaultTrue(Integer Shop_id);

    List<Address> findByAgent_id(Integer agentId);

    Address findByIdAndAgent_id(Integer addressId,Integer agentId);

    Address findByAgent_idAndIsDefaultTrue(Integer agentId);

}
