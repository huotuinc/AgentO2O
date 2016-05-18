/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.repository.goods;

import com.huotu.agento2o.service.entity.goods.MallGoods;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;


/**
 * Created by allan on 3/21/16.
 */
@Repository
public interface MallGoodsRepository extends JpaRepository<MallGoods, Integer>,JpaSpecificationExecutor<MallGoods> {

    /**
     * 根据 CustomerId 和 AgentId 查找指定门店商品，AgentId=0 表示平台方商品
     * @param customerId 平台方ID
     * @param agentId 门店ID
     * @return
     */
    List<MallGoods> findByCustomerIdAndAgentId(Integer customerId,Integer agentId);

    /**
     * 根据 AgentId 查找指定代理商商品
     * @param agentId 代理商ID
     * @return
     */
    @Query("SELECT DISTINCT goods FROM MallGoods goods,MallProduct product,AgentProduct agtProduct " +
            "WHERE goods.goodsId = product.goods.goodsId AND product.productId = agtProduct.product.productId AND agtProduct.agent.id = ?1")
    Page<MallGoods> findByAgentId(Integer agentId, Pageable pageable);

    /**
     * 根据 AgentId 和 商品名称 查找指定代理商商品
     * @param agentId 代理商ID
     * @return
     */
    @Query("SELECT DISTINCT goods FROM MallGoods goods,MallProduct product,AgentProduct agtProduct " +
            "WHERE goods.goodsId = product.goods.goodsId AND product.productId = agtProduct.product.productId AND agtProduct.agent.id = ?1 AND goods.name like ?2")
    Page<MallGoods> findByAgentIdAndName(Integer agentId,String name, Pageable pageable);
}
