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
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.model.purchase.AgentProductStoreInfo;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.ShoppingCartRepository;
import com.huotu.agento2o.service.service.goods.MallProductService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import com.huotu.agento2o.service.service.purchase.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    @Autowired
    private AgentProductService agentProductService;
    @Autowired
    private MallProductService productService;


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
            //若购物车中已经存在该货品，如果购物车数量+购买数量大于总数量，则保存为最大数量
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
        if (author != null && author.getType() == Agent.class) {
            shoppingCartList = shoppingCartRepository.findByAgent_IdOrderByCreateTimeDesc(author.getId());
        } else if (author != null && author.getType() == Shop.class) {
            shoppingCartList = shoppingCartRepository.findByShop_IdOrderByCreateTimeDesc(author.getId());
        }
        //设置可用库存和当前库存
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            shoppingCartList.forEach(p -> {
                if (p.getProduct() != null) {
                    productService.setProductPrice(p.getProduct(),author);
                    AgentProduct agentProduct = agentProductService.findAgentProduct(author,p.getProduct());
                    if (agentProduct != null) {
                        p.getProduct().setAuthorStore(agentProduct.getStore() - agentProduct.getFreez());
                    }
                    if (author.getParentAgent() == null) {
                        //上级为 平台方
                        p.getProduct().setUsableStore(p.getProduct().getStore() - p.getProduct().getFreez());
                    } else {
                        AgentProductStoreInfo parentAgentProduct = agentProductRepository.findUsableNumByAgentAndProduct(author.getParentAgent().getId(), p.getProduct().getProductId());
                        p.getProduct().setUsableStore(parentAgentProduct.getStore() - parentAgentProduct.getFreeze());
                    }
                }
            });
        }
        return shoppingCartList;
    }

    @Override
    public ShoppingCart findById(Integer id, Author author) {
        if (author != null && author.getType() == Agent.class) {
            return shoppingCartRepository.findByIdAndAgent(id, author.getAuthorAgent());
        } else if (author != null && author.getType() == Shop.class) {
            return shoppingCartRepository.findByIdAndShop(id, author.getAuthorShop());
        }
        return null;
    }

    @Override
    public ShoppingCart findByAuthorAndProduct(Author author, MallProduct product) {
        if (author != null && author.getType() == Agent.class) {
            return shoppingCartRepository.findByAgentAndProduct(author.getAuthorAgent(),product);
        } else if (author != null && author.getType() == Shop.class) {
            return shoppingCartRepository.findByShopAndProduct(author.getAuthorShop(),product);
        }
        return null;
    }

    @Override
    public Integer findNumByAuthorAndProduct(Author author, MallProduct product) {
        Integer shoppingNum = 0;
        if (author != null && author.getType() == Agent.class) {
            return shoppingCartRepository.findNumByAgentAndProduct(author.getAuthorAgent().getId(),product.getProductId());
        } else if (author != null && author.getType() == Shop.class) {
            return shoppingCartRepository.findNumByShopAndProduct(author.getAuthorShop().getId(),product.getProductId());
        }
        return shoppingNum;
    }

    @Override
    public List<ShoppingCart> findById(List<Integer> ids, Author author) {
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        ids.forEach(p -> {
            ShoppingCart cart = findById(p, author);
            if (cart != null) {
                productService.setProductPrice(cart.getProduct(),author);
                AgentProductStoreInfo agentProduct = null;
                if (author != null && author.getType() == Agent.class) {
                    agentProduct = agentProductRepository.findUsableNumByAgentAndProduct(cart.getAgent().getId(),cart.getProduct().getProductId());
                } else if (author != null && author.getType() == Shop.class) {
                    agentProduct = agentProductRepository.findUsableNumByShopAndProduct(cart.getShop().getId(),cart.getProduct().getProductId());
                }
                if (agentProduct != null) {
                    cart.getProduct().setAuthorStore(agentProduct.getStore() - agentProduct.getFreeze());
                }
                //上级为平台方，判断商品可用库存
                if (author.getParentAgent() == null) {
                    if (cart.getProduct().getStore() - cart.getProduct().getFreez() >= cart.getNum()) {
                        shoppingCartList.add(cart);
                    }
                } else {
                    AgentProductStoreInfo parentAgentProduct = agentProductRepository.findUsableNumByAgentAndProduct(author.getParentAgent().getId(), cart.getProduct().getProductId());
                    if (parentAgentProduct != null && parentAgentProduct.getStore() - parentAgentProduct.getFreeze() >= cart.getNum()) {
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
