/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.goods.impl;

import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.ShoppingCartRepository;
import com.huotu.agento2o.service.service.goods.MallProductService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import com.huotu.agento2o.service.service.purchase.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/16.
 */
@Service
public class MallProductServiceImpl implements MallProductService {
    @Autowired
    private MallProductRepository productRepository;
    @Autowired
    private AgentProductRepository agentProductRepository;
    @Autowired
    private AgentProductService agentProductService;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private ShoppingCartService shoppingCartService;


    @Override
    public MallProduct findByProductId(Integer productId) {
        return productRepository.findOne(productId);
    }

    @Override
    public List<MallProduct> findByGoodsId(Author author, Integer goodsId) {
        List<MallProduct> productList = productRepository.findByGoods_GoodsId(goodsId);
        List<MallProduct> agentProductList = new ArrayList<>();
        if (productList != null && productList.size() > 0) {
            productList.forEach(product -> {
                //当前库存
                AgentProduct agentProduct = agentProductService.findAgentProduct(author,product);
                ShoppingCart shoppingCart = shoppingCartService.findByAuthorAndProduct(author,product);
                if (agentProduct != null) {
                    product.setAuthorStore(agentProduct.getStore() - agentProduct.getFreez());
                }
                if(shoppingCart != null){
                    product.setShoppingStore(Math.max(0,shoppingCart.getNum()));
                }
                //上级可用库存
                if(author.getParentAgent() == null){
                    //平台方商品
                    product.setUsableStore(product.getStore() - product.getFreez());
                    agentProductList.add(product);
                }else{
                    AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getParentAgent(), product);
                    if (parentAgentProduct != null) {
                        product.setUsableStore(parentAgentProduct.getStore() - parentAgentProduct.getFreez());
                        agentProductList.add(product);
                    }
                }
            });
        }
        return agentProductList;
    }
}
