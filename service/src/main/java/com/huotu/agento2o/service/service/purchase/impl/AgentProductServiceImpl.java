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

import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/13.
 */
@Service
public class AgentProductServiceImpl implements AgentProductService {

    @Autowired
    AgentProductRepository agentProductRepository;


    @Override
    public List<AgentProduct> findByParentAgentProduct(Author author) {
        return null;
    }

   /* @Override
    public Page<AgentProduct> findByAgentId(int pageIndex, int pageSize,Integer agentId) {
        Specification<AgentProduct> specification = new Specification<AgentProduct>() {
            @Override
            public Predicate toPredicate(Root<AgentProduct> root, CriteriaQuery<?> query, CriteriaBuilder cb) {
                List<Predicate> predicates = new ArrayList<>();
                predicates.add(cb.equal(root.get("agent").get("id").as(Integer.class), agentId));
                return cb.and(predicates.toArray(new Predicate[predicates.size()]));
            }
        };
        return agentProductRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize));
    }*/

    @Override
    public List<AgentProduct> findByAgentId(Integer agentId) {
        return agentProductRepository.findByAuthor_IdAndDisabledFalse(agentId);
    }

    @Override
    @Transactional
    public boolean updateWaring(List<String> info) {
        for (String str : info) {
            String _item = str.substring(1, str.length() - 1);
            String[] item = _item.split(",");
            if (item.length == 3) {
                //更新
                Integer agentId = Integer.parseInt(item[0].trim());
                Integer productId = Integer.parseInt(item[1].trim());
                Integer warning = Integer.parseInt(item[2].trim());
                if (agentProductRepository.updateWaring(agentId, productId, warning) <= 0) {
                    return false;
                }
            }
        }
        return true;
    }

    @Override
    public List<String> findNeedWaringAgent() {
        return null;
    }
}
