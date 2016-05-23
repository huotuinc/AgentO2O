/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.user;


import com.huotu.agento2o.service.entity.user.UserBaseInfo;

/**
 * Created by allan on 12/31/15.
 */
public interface UserBaseInfoService {

    /**
     * 通过唯一id查找小伙伴信息
     * @param id
     * @return
     */
    UserBaseInfo findById(Integer id);

    /**
     * 通过登录名和平台方查找小伙伴信息
     * @param name
     * @param id
     * @return
     */
    UserBaseInfo findByNameAndCustomerId(String name,Integer id);
    
}
