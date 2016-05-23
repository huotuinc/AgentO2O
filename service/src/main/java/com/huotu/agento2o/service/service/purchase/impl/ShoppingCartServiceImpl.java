/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.purchase.ShoppingCartRepository;
import com.huotu.agento2o.service.service.purchase.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Transient;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/16.
 */
@Service
public class ShoppingCartServiceImpl implements ShoppingCartService {
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private MallGoodsRepository goodsRepository;


    @Override
    @Transactional
    public ShoppingCart createShoppingCart(ShoppingCart shoppingCart) {
        //根据货品IP查找购物车
        ShoppingCart old = shoppingCartRepository.findByAuthorAndProduct(shoppingCart.getAuthor(), shoppingCart.getProduct());
        if (old == null) {
            shoppingCart = shoppingCartRepository.save(shoppingCart);
        } else {
            //若购物车中已经存在该货品，则直接加数量
            old.setNum(old.getNum() + shoppingCart.getNum());
            shoppingCart = shoppingCartRepository.save(old);
        }
        return shoppingCart;
    }

    @Override
    public ShoppingCart editShoppingCart(ShoppingCart shoppingCart) {
        return shoppingCartRepository.save(shoppingCart);
    }

    @Override
    public List<ShoppingCart> findByAgentId(Integer agentId) {
        List<ShoppingCart> shoppingCartList = shoppingCartRepository.findByAuthor_IdOrderByCreateTimeDesc(agentId);
        return shoppingCartList;
    }

    @Override
    public ShoppingCart findById(Integer id, Author author) {
        return shoppingCartRepository.findByIdAndAuthor(id, author);
    }

    @Override
    public List<ShoppingCart> findById(List<Integer> ids, Author author) {
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        ids.forEach(p->{
            ShoppingCart cart = findById(p,author);
            if(cart != null){
                shoppingCartList.add(cart);
            }
        });
        return shoppingCartList;
    }

    @Override
    @Transactional
    public void deleteShoppingCart(ShoppingCart shoppingCart) {
        shoppingCartRepository.delete(shoppingCart);
    }

    @Override
    @Transactional
    public void deleteAllShoppingCartByAgentId(Integer agentId) {
        shoppingCartRepository.deleteByAgentId(agentId);
    }
}
