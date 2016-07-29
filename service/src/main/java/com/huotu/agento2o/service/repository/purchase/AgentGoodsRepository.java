/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.repository.purchase;

import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.purchase.AgentGoods;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Created by helloztt on 2016/7/29.
 */
@Repository
public interface AgentGoodsRepository extends JpaRepository<AgentGoods,Integer> {
    AgentGoods findByShopAndGoodsIdAndDisabledFalse(Shop authorShop, Integer goodsId);
}
