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

import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/14.
 */
@Service
public class MallGoodsServiceImpl implements MallGoodsService {
    @Autowired
    private MallGoodsRepository goodsRepository;
    @Autowired
    private AgentProductRepository agentProductRepository;

    /**
     * 根据 CustomerId 和 AgentId 查找指定门店商品，AgentId=0 表示平台方商品
     *
     * @param customerId 平台方ID
     * @param author     门店ID
     * @return
     */
    @Override
    public Page<MallGoods> findByCustomerIdAndAgentId(Integer customerId, Author author, GoodsSearcher goodsSearcher) {
        Specification<MallGoods> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("customerId").as(Integer.class), customerId));
            predicates.add(cb.equal(root.get("agentId").as(Integer.class), 0));
            if (!StringUtil.isEmptyStr(goodsSearcher.getGoodsName())) {
                predicates.add(cb.like(root.get("name").as(String.class), "%" + goodsSearcher.getGoodsName() + "%"));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        Page<MallGoods> goodsPage = goodsRepository.findAll(specification, new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE));
        if (goodsPage.getContent() != null && goodsPage.getContent().size() > 0) {
            goodsPage.getContent().forEach(goods -> {
                goods.getProducts().forEach(product -> {
                    AgentProduct agentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(author, product);
                    if (agentProduct != null) {
                        product.setAuthorStore(agentProduct.getStore() - agentProduct.getFreez());
                    }
                    product.setUsableStore(product.getStore() - product.getFreez());
                });
            });
        }
        return goodsPage;
    }

    /**
     * 根据 AgentId 查找指定代理商商品
     *
     * @param author 代理商/门店
     * @return
     */
    @Override
    public Page<MallGoods> findByAgentId(Author author, GoodsSearcher goodsSearcher) {
        //// TODO: 2016/5/14 cb.in() error to solve
        /*List<Integer> goodsIdList = agentProductRepository.findGoodsListByAgentId(agentId);
        Specification<MallGoods> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("agentId").as(Integer.class), agentId));
            predicates.add(cb.isTrue(root.get("goodId").in(goodsIdList)));
            if(!StringUtil.isEmptyStr(goodsSearcher.getGoodsName())){
                predicates.add(cb.like(root.get("name").as(String.class),goodsSearcher.getGoodsName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return goodsRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize));*/
        Page<MallGoods> goodsPage = null;
        if (StringUtil.isEmptyStr(goodsSearcher.getGoodsName())) {
            goodsPage = goodsRepository.findByAgentId(author.getParentAuthor().getId(), new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE));
        } else {
            goodsPage = goodsRepository.findByAgentIdAndName(author.getParentAuthor().getId(), "%" + goodsSearcher.getGoodsName() + "%", new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE));
        }
        if (goodsPage.getContent() != null && goodsPage.getContent().size() > 0) {
            goodsPage.getContent().forEach(goods -> {
                goods.getProducts().forEach(product -> {
                    AgentProduct parentAgentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(author.getParentAuthor(), product);
                    AgentProduct agentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(author, product);
                    if (agentProduct != null) {
                        product.setAuthorStore(agentProduct.getStore() - agentProduct.getFreez());
                    }
                    if (parentAgentProduct != null) {
                        product.setUsableStore(parentAgentProduct.getStore() - parentAgentProduct.getFreez());
                    }
                });
            });
        }
        return goodsPage;
    }

    @Override
    public MallGoods findByGoodsId(Integer goodsId) {
        return goodsRepository.findOne(goodsId);
    }
}
