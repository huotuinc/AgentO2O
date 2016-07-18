/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.repository.user;

import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by allan on 12/31/15.
 */
@Repository
public interface UserBaseInfoRepository extends JpaRepository<UserBaseInfo, Integer> {

    UserBaseInfo findByLoginNameAndMallCustomer_customerId(String loginName, Integer customerId);

    @Query(value = "select top 10 d.UB_UserLoginName from (select  a.UB_UserLoginName,b.Agent_ID,c.Shop_ID from Hot_UserBaseInfo a left join Agt_Agent b on a.UB_UserID = b.UB_UserID left join Agt_Shop c on a.UB_UserID = c.UB_UserID where a.UB_UserLoginName like ?1 and a.UB_CustomerID = ?2)d where d.Agent_ID is null and d.Shop_ID is null", nativeQuery = true)
    List<String> findByLoginNameLikeAndMallCustomer_customerId(String logingName,Integer customerId);

}
