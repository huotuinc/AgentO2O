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
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
    public ApiResult updateWarning(Author author, Integer agentProductId, Integer warning) {
        AgentProduct agentProduct = agentProductRepository.findOne(agentProductId);
        if(agentProduct == null){
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if(author.getId().equals(agentProduct.getAuthor().getId())){
            agentProduct.setWarning(warning);
            agentProductRepository.save(agentProduct);
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }else{
            return new ApiResult("没有权限！");
        }
    }

    @Override
    public List<Object> findNeedWaringAgent() {
        return agentProductRepository.findNeedWaringAgent();
    }

    @Override
    public List<AgentProduct> findWaringAgentInfo(Integer autorId) {
        return agentProductRepository.findWaringAgentInfo(autorId);
    }

    @Override
    public AgentProduct findAgentPeoduct(Author author, MallProduct product) {
        return agentProductRepository.findByAuthorAndProductAndDisabledFalse(author, product);
    }
}
