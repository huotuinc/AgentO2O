/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.model.purchase;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

/**
 * AgentProduct中关联太多，大量查询只需要库存相关
 * Created by helloztt on 2016/8/4.
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class AgentProductStoreInfo {
    private Integer id;
    private Integer store;
    private Integer freeze;
}
