/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.purchase;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;

import java.util.List;

/**
 * Created by helloztt on 2016/5/16.
 */
public interface ShoppingCartService {

    /**
     * 加入购物车
     *
     * @param shoppingCart
     * @return
     */
    ShoppingCart createShoppingCart(ShoppingCart shoppingCart);

    /**
     * 购物车数量修改
     *
     * @param shoppingCartId
     * @return
     */
    ApiResult editShoppingCart(Author author, Integer shoppingCartId, int num);

    /**
     * 根据 author 获取采购购物车信息
     *
     * @param author
     * @return
     */
    List<ShoppingCart> findByAgentId(Author author);

    /**
     * 根据 ID 和 当前登录用户 查找购物车货品信息
     *
     * @param id
     * @param author
     * @return
     */
    ShoppingCart findById(Integer id, Author author);

    ShoppingCart findByAuthorAndProduct(Author author, MallProduct product);

    Integer findNumByAuthorAndProduct(Author author,MallProduct product);

    /**
     * 根据 ID列表 和 当前登录用户 查找勾选的购物车货品信息
     *
     * @param ids
     * @param author
     * @return
     */
    List<ShoppingCart> findById(List<Integer> ids, Author author);

    /**
     * 删除购物车货品信息
     *
     * @param shoppingCart
     */
    void deleteShoppingCart(ShoppingCart shoppingCart);

    /**
     * 清空购物车
     *
     * @param agentId
     */
    void deleteAllShoppingCartByAgentId(Integer agentId);
}
