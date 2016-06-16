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

import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
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
public interface AgentProductRepository extends JpaRepository<AgentProduct, Integer>, JpaSpecificationExecutor<AgentProduct> {

//    @Query("SELECT DISTINCT product.product.goods.goodsId FROM AgentProduct product WHERE product.author.id = ?1 AND product.disabled = false ")
//    List<Integer> findGoodsListByAgentId(Integer agentId);

    List<AgentProduct> findByAuthor_IdAndDisabledFalse(Integer agentId);

    @Query("update AgentProduct  set warning=?3 where author.id=?1 and product.productId=?2")
    @Modifying
    int updateWaring(Integer agentId, Integer productId, Integer warning);

    AgentProduct findByAuthorAndProductAndDisabledFalse(Author author, MallProduct product);

    /**
     * 查询出需要提醒的用户
     *
     * @return 用户ID
     */
    @Query("select DISTINCT product.author.id from AgentProduct product where  product.warning>=product.store-product.freez")
    @Modifying
    List<Object> findNeedWaringAgent();

    /**
     * 查出需要提醒用户对应的所有需要提醒的商品信息
     *
     * @param authorId
     * @return
     */
    @Query("select a from AgentProduct a where a.warning is not null and a.warning>0 and a.warning>=a.store-a.freez  and a.author.id =?1")
    List<AgentProduct> findWaringAgentInfo(Integer authorId);

    @Query("select count(a) from AgentProduct a where a.warning is not null and a.warning>0 and a.warning>=a.store-a.freez and a.author.id =?1")
    int countByWaringAgentInfo(Integer authorId);

   /* List<AgentProduct> findAgentProductByAuthor_IdAndStoreLessThanWarning(Integer authorId);*/


}
