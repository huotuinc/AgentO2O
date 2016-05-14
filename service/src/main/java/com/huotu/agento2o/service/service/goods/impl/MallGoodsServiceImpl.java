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
import com.huotu.agento2o.service.entity.goods.MallGoods;
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
     * @param agentId    门店ID
     * @return
     */
    @Override
    public Page<MallGoods> findByCustomerIdAndAgentId(Integer customerId, Integer agentId, GoodsSearcher goodsSearcher) {
        Specification<MallGoods> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(cb.equal(root.get("customerId").as(Integer.class), customerId));
            predicates.add(cb.equal(root.get("agentId").as(Integer.class), agentId));
            if (!StringUtil.isEmptyStr(goodsSearcher.getGoodsName())) {
                predicates.add(cb.like(root.get("name").as(String.class), goodsSearcher.getGoodsName()));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return goodsRepository.findAll(specification, new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE));
    }

    /**
     * 根据 AgentId 查找指定代理商商品
     *
     * @param agentId 代理商ID
     * @return
     */
    @Override
    public Page<MallGoods> findByAgentId(Integer agentId, GoodsSearcher goodsSearcher) {
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
        if (StringUtil.isEmptyStr(goodsSearcher.getGoodsName())) {
            return goodsRepository.findByAgentId(agentId, new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE));
        } else {
            return goodsRepository.findByAgentIdAndName(agentId, "%" + goodsSearcher.getGoodsName() + "%", new PageRequest(goodsSearcher.getPageNo() - 1, Constant.PAGESIZE));
        }
    }
}
