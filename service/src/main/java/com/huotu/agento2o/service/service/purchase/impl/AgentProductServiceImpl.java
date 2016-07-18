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
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.author.Agent;
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
    public List<AgentProduct> findByAgentId(Author author) {
        if (author != null && author.getType() == Agent.class) {
            return agentProductRepository.findByAgent_IdAndDisabledFalse(author.getId());
        } else if (author != null && author.getType() == ShopAuthor.class) {
            return agentProductRepository.findByShop_IdAndDisabledFalse(author.getId());
        }
        return null;
    }

    @Override
    @Transactional
    public ApiResult updateWarning(Author author, Integer agentProductId, Integer warning) {
        AgentProduct agentProduct = agentProductRepository.findOne(agentProductId);
        if (agentProduct == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if ((author != null && author.getType() == Agent.class && author.getId().equals(agentProduct.getAgent().getId()))
                || (author != null && author.getType() == ShopAuthor.class && author.getId().equals(agentProduct.getShop().getId()))) {
            agentProduct.setWarning(warning);
            agentProductRepository.save(agentProduct);
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        } else {
            return new ApiResult("没有权限！");
        }
    }

    @Override
    public List<Object> findNeedWarningAgent() {
        return agentProductRepository.findNeedWaringAgent();
    }

    @Override
    public List<Object> findNeedWarningShop() {
        return agentProductRepository.findNeedWaringShop();
    }

    @Override
    public List<AgentProduct> findWarningAgentInfo(Integer agentId) {
        return agentProductRepository.findWarningAgentInfo(agentId);
    }

    @Override
    public List<AgentProduct> findWarningShopInfo(Integer shopId) {
        return agentProductRepository.findWarningShopInfo(shopId);
    }

    @Override
    public AgentProduct findAgentProduct(Agent agent, MallProduct product) {
        return agentProductRepository.findByAgentAndProductAndDisabledFalse(agent, product);
    }

    @Override
    public AgentProduct findAgentProduct(Author author, MallProduct product) {
        if (author != null && author.getType() == Agent.class) {
            return agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getAuthorAgent(), product);
        } else if (author != null && author.getType() == ShopAuthor.class) {
            return agentProductRepository.findByShopAndProductAndDisabledFalse(author.getAuthorShop(), product);
        }
        return null;
    }

    @Override
    public AgentProduct findByAgentProductId(Integer agentProductId) {
        return agentProductRepository.findOne(agentProductId);
    }
}
