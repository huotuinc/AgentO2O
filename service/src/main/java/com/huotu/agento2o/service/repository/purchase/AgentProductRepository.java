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

import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/12.
 */
@Repository
public interface AgentProductRepository extends JpaRepository<AgentProduct,Integer>, JpaSpecificationExecutor<AgentProduct> {

    @Query("SELECT DISTINCT product.product.goods.goodsId FROM AgentProduct product WHERE product.agent.id = ?1")
    List<Integer> findGoodsListByAgentId(Integer agentId);

    @Query
    List<AgentProduct> findByAgentId(Integer agentId);

    @Query("update AgentProduct  set warning=?3 where agent.id=?1 and product.productId=?2")
    @Modifying
    int updateWaring(Integer agentId,Integer productId,Integer warning);


}
