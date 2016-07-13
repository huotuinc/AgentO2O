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

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
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
    @Autowired
    private AgentProductRepository agentProductRepository;


    @Override
    @Transactional
    public ShoppingCart createShoppingCart(ShoppingCart shoppingCart) {
        //根据货品IP查找购物车
        ShoppingCart old = null;
        if (shoppingCart.getAgent() != null) {
            old = shoppingCartRepository.findByAgentAndProduct(shoppingCart.getAgent(), shoppingCart.getProduct());
        } else if (shoppingCart.getShop() != null) {
            old = shoppingCartRepository.findByShopAndProduct(shoppingCart.getShop(), shoppingCart.getProduct());
        }
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
    public ApiResult editShoppingCart(Author author, Integer shoppingCartId, int num) {
        ShoppingCart shoppingCart = findById(shoppingCartId, author);
        if (shoppingCart == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        Agent parentAgent = shoppingCart.getParentAgent();
        //判断上级库存是否充足
        if (parentAgent == null) {
            if (shoppingCart.getProduct().getStore() - shoppingCart.getProduct().getFreez() < num) {
                return new ApiResult("库存不足");
            }
        } else {
            AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getParentAgent(), shoppingCart.getProduct());
            if (parentAgentProduct.getStore() - parentAgentProduct.getFreez() < num) {
                return new ApiResult("库存不足");
            }
        }
        shoppingCart.setNum(num);
        shoppingCartRepository.save(shoppingCart);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public List<ShoppingCart> findByAgentId(Author author) {
        List<ShoppingCart> shoppingCartList = null;
        if (author.getType() == Agent.class) {
            shoppingCartList = shoppingCartRepository.findByAgent_IdOrderByCreateTimeDesc(author.getId());
        } else if (author.getType() == Shop.class) {
            shoppingCartList = shoppingCartRepository.findByShop_IdOrderByCreateTimeDesc(author.getId());
        }
        //设置可用库存和当前库存
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            shoppingCartList.forEach(p -> {
                if (p.getProduct() != null) {
                    AgentProduct agentProduct = null;
                    if (author.getType() == Agent.class) {
                        agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(p.getAgent(), p.getProduct());
                    } else if (author.getType() == Shop.class) {
                        agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(p.getShop(), p.getProduct());
                    }
                    if (agentProduct != null) {
                        p.getProduct().setAuthorStore(agentProduct.getStore() - agentProduct.getFreez());
                    }
                    if (author.getParentAgent() == null) {
                        //上级为 平台方
                        p.getProduct().setUsableStore(p.getProduct().getStore() - p.getProduct().getFreez());
                    } else {
                        AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getParentAgent(), p.getProduct());
                        p.getProduct().setUsableStore(parentAgentProduct.getStore() - parentAgentProduct.getFreez());
                    }
                }
            });
        }
        return shoppingCartList;
    }

    @Override
    public ShoppingCart findById(Integer id, Author author) {
        if (author.getType() == Agent.class) {
            return shoppingCartRepository.findByIdAndAgent(id, (Agent) author);
        } else if (author.getType() == Shop.class) {
            return shoppingCartRepository.findByIdAndShop(id, (Shop) author);
        }
        return null;
    }

    @Override
    public List<ShoppingCart> findById(List<Integer> ids, Author author) {
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        ids.forEach(p -> {
            ShoppingCart cart = findById(p, author);
            if (cart != null) {
                AgentProduct agentProduct = null;
                if(author.getType() == Agent.class){
                    agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(cart.getAgent(),cart.getProduct());
                }else if(author.getType() == Shop.class){
                    agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(cart.getShop(),cart.getProduct());
                }
                if (agentProduct != null) {
                    cart.getProduct().setAuthorStore(agentProduct.getStore() - agentProduct.getFreez());
                }
                //上级为平台方，判断商品可用库存
                if (author.getParentAgent() == null) {
                    if (cart.getProduct().getStore() - cart.getProduct().getFreez() >= cart.getNum()) {
                        shoppingCartList.add(cart);
                    }
                } else {
                    AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getParentAgent(), cart.getProduct());
                    if (parentAgentProduct != null && parentAgentProduct.getStore() - parentAgentProduct.getFreez() >= cart.getNum()) {
                        shoppingCartList.add(cart);
                    }
                }
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
