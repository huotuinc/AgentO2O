/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.repository;

import com.huotu.agento2o.service.entity.MallCustomer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

/**
 * Created by helloztt on 2016/5/14.
 */
@Repository
public interface MallCustomerRepository extends JpaRepository<MallCustomer,Integer> {

    MallCustomer findByUsername(String username);

    @Modifying(clearAutomatically = true)
    @Query("update MallCustomer a set a.password = ?2 where a.id = ?1")
    int resetPassword(Integer customerId, String password);

    
}
