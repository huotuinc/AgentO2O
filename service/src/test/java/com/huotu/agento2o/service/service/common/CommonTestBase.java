/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.common;

import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.Random;
import java.util.UUID;

/**
 * Created by helloztt on 2016/5/14.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = ServiceConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public abstract class CommonTestBase {
    protected Random random = new Random();
    @Autowired
    protected MallCustomerService customerService;
    @Autowired
    protected AgentService agentService;
    @Autowired
    protected MallGoodsRepository goodsRepository;
    @Autowired
    protected MallProductRepository productRepository;
    @Autowired
    protected AgentProductRepository agentProductRepository;
    @Autowired
    private AgentLevelService agentLevelService;


    protected MallCustomer mockMallCustomer(){
        MallCustomer customer = new MallCustomer();
        customer.setNickName(UUID.randomUUID().toString());
        customer.setUsername(UUID.randomUUID().toString());
        customer.setPassword(UUID.randomUUID().toString());
        return customerService.newCustomer(customer);
    }

    protected Agent mockAgent(MallCustomer mockCustomer, Agent parentAgent){
        Agent agent = new Agent();
        agent.setCustomer(mockCustomer);
        agent.setUsername(UUID.randomUUID().toString());
        agent.setPassword(UUID.randomUUID().toString());
        agent.setName(UUID.randomUUID().toString());
        agent.setContact(UUID.randomUUID().toString());
        agent.setMobile(UUID.randomUUID().toString());
        agent.setTelephone(UUID.randomUUID().toString());
        agent.setAddress(UUID.randomUUID().toString());
        agent.setDisabled(false);
        agent.setDeleted(false);
        if(parentAgent != null){
            agent.setParentAuthor(parentAgent);
        }
        agent = agentService.addAgent(agent);
        agentService.flush();
        return agent;
    }

    protected MallGoods mockMallGoods(Integer customerId,Integer agentId){
        MallGoods mockMallGoods = new MallGoods();
        mockMallGoods.setCost(random.nextDouble());
        mockMallGoods.setPrice(random.nextDouble());
        mockMallGoods.setCustomerId(customerId);
        //平台方商品
        if(agentId == null){
            mockMallGoods.setAgentId(0);
        }else {
            mockMallGoods.setAgentId(agentId);
        }
        mockMallGoods.setDisabled(false);
        mockMallGoods.setStore(random.nextInt());
        mockMallGoods.setName(UUID.randomUUID().toString());
        return goodsRepository.saveAndFlush(mockMallGoods);
    }

    protected MallProduct mockMallProduct(MallGoods mockGoods){
        MallProduct mockMallProduct = new MallProduct();
        mockMallProduct.setGoods(mockGoods);
        mockMallProduct.setStandard(UUID.randomUUID().toString());
        mockMallProduct.setCost(random.nextDouble());
        mockMallProduct.setPrice(random.nextDouble());
        mockMallProduct.setName(mockGoods.getName());
        mockMallProduct.setMarketable(true);
        mockMallProduct.setLocalStock(false);
        return productRepository.saveAndFlush(mockMallProduct);
    }

    /**
     * 代理商货品
     * @param mockMallProduct
     * @param mockAgent
     * @return
     */
    protected AgentProduct mockAgentProduct(MallProduct mockMallProduct,Agent mockAgent){
        AgentProduct agentProduct = new AgentProduct();
        agentProduct.setAuthor(mockAgent);
        agentProduct.setProduct(mockMallProduct);
        agentProduct.setGoodsId(mockMallProduct.getGoods().getGoodsId());
        agentProduct.setStore(random.nextInt());
        agentProduct.setFreez(0);
        agentProduct.setWarning(0);
        agentProduct.setDisabled(false);
        return agentProductRepository.saveAndFlush(agentProduct);
    }

    protected AgentLevel mockAgentLevel(MallCustomer mockCustomer){
        AgentLevel agentLevel = new AgentLevel();
        agentLevel.setLevelName(UUID.randomUUID().toString());
        agentLevel.setComment(UUID.randomUUID().toString());
        agentLevel.setLevel(random.nextInt());
        agentLevel.setCustomer(mockCustomer);
        agentLevel = agentLevelService.addAgentLevel(agentLevel);
        agentLevelService.flush();
        return agentLevel;
    }
}
