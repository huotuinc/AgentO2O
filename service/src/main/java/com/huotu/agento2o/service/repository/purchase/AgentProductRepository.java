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
import com.huotu.agento2o.service.entity.author.Shop;
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

//    List<AgentProduct> findByAuthor_IdAndDisabledFalse(Integer agentId);

    List<AgentProduct> findByAgent_IdAndDisabledFalse(Integer agentId);

    List<AgentProduct> findByShop_IdAndDisabledFalse(Integer shopId);

    //统计代理商商品的货品个数
    int countByAgent_IdAndGoodsId(Integer agentId,Integer goodsId);
    //统计门店商品的货品个数
    int countByShop_IdAndGoodsId(Integer shopId,Integer goodsId);


    @Query("update AgentProduct  set warning=?3 where agent.id=?1 and product.productId=?2")
    @Modifying
    int updateAgentWaring(Integer agentId, Integer productId, Integer warning);

    @Query("update AgentProduct  set warning=?3 where shop.id = ?1 and product.productId = ?2")
    @Modifying
    int updateShopWaring(Integer shopId, Integer productId, Integer warning);


//    AgentProduct findByAuthorAndProductAndDisabledFalse(Author author, MallProduct product);
    AgentProduct findByAgentAndProductAndDisabledFalse(Agent agent,MallProduct product);
    AgentProduct findByShopAndProductAndDisabledFalse(Shop shop,MallProduct product);

    /**
     * 查询出需要提醒的用户
     *
     * @return 用户ID
     */
    @Query("select DISTINCT product.agent.id from AgentProduct product where  product.agent is not null and product.warning>=product.store-product.freez")
    @Modifying
    List<Object> findNeedWaringAgent();
    @Query("select DISTINCT product.shop.id from AgentProduct product where  product.shop is not null and product.warning>=product.store-product.freez")
    @Modifying
    List<Object> findNeedWaringShop();

    /**
     * 查出需要提醒用户对应的所有需要提醒的商品信息
     *
     * @param agentId
     * @return
     */
    @Query("select a from AgentProduct a where a.warning is not null and a.warning>0 and a.warning>=a.store-a.freez  and a.agent.id =?1")
    List<AgentProduct> findWarningAgentInfo(Integer agentId);
    @Query("select a from AgentProduct a where a.warning is not null and a.warning>0 and a.warning>=a.store-a.freez  and a.shop.id =?1")
    List<AgentProduct> findWarningShopInfo(Integer shopId);

    @Query("select count(a) from AgentProduct a where a.warning is not null and a.warning>0 and a.warning>=a.store-a.freez and a.agent.id =?1")
    int countByWarningAgentInfo(Integer authorId);
    @Query("select count(a) from AgentProduct a where a.warning is not null and a.warning>0 and a.warning>=a.store-a.freez and a.shop.id =?1")
    int countByWarningShopInfo(Integer authorId);

   /* List<AgentProduct> findAgentProductByAuthor_IdAndStoreLessThanWarning(Integer authorId);*/


}
