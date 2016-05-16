/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.goods;

import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import org.springframework.data.domain.Page;

/**
 * Created by helloztt on 2016/5/12.
 */
public interface MallGoodsService {
    /**
     * 根据 CustomerId 和 AgentId 查找指定门店商品，AgentId=0 表示平台方商品
     *
     * @param customerId 平台方ID
     * @param agentId    门店ID
     * @return
     */
    Page<MallGoods> findByCustomerIdAndAgentId(Integer customerId, Integer agentId, GoodsSearcher goodsSearcher);

    /**
     * 根据 AgentId 查找指定代理商商品
     * @param agentId 代理商ID
     * @return
     */
    Page<MallGoods> findByAgentId(Integer agentId, GoodsSearcher goodsSearcher);

    /**
     * 根据 商品ID 查找商品
     * @param goodsId
     * @return
     */
    MallGoods findByGoodsId(Integer goodsId);
}
