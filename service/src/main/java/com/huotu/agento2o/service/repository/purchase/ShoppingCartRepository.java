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
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/5/16.
 */
@Repository
public interface ShoppingCartRepository extends JpaRepository<ShoppingCart, Integer> {

//    List<ShoppingCart> findByAuthor_IdOrderByCreateTimeDesc(Integer agentId);

    List<ShoppingCart> findByAgent_IdOrderByCreateTimeDesc(Integer agentId);

    List<ShoppingCart> findByShop_IdOrderByCreateTimeDesc(Integer shopId);

//    ShoppingCart findByIdAndAuthor(Integer id, Author author);
    ShoppingCart findByIdAndAgent(Integer id, Agent agent);
    ShoppingCart findByIdAndShop(Integer id, Shop shop);

    ShoppingCart findByAgentAndProduct(Agent agent,MallProduct product);
    ShoppingCart findByShopAndProduct(Shop shop,MallProduct product);

//    ShoppingCart findByAuthorAndProduct(Author author, MallProduct product);

    @Modifying
    @Query("DELETE FROM ShoppingCart A WHERE A.agent.id = ?1")
    void deleteByAgentId(Integer agentId);

    @Modifying
    @Query("DELETE FROM ShoppingCart A WHERE A.shop.id = ?1")
    void deleteByShopId(Integer shopId);

//    @Modifying
//    @Query("DELETE FROM ShoppingCart A WHERE A.author.id = ?1")
//    void deleteByAgentId(Integer agentId);
}
