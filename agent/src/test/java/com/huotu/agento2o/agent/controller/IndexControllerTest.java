/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.controller;

import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.agent.config.SecurityConfig;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.CustomerTypeEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.repository.author.AgentRepository;
import com.huotu.agento2o.service.repository.author.ShopRepository;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.ShopService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by helloztt on 2016/5/9.
 */
@Transactional
public class IndexControllerTest extends CommonTestBase {

    @Autowired
    private AgentService agentService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private AgentRepository agentRepository;
    @Autowired
    private ShopRepository shopRepository;

    /**
     * 用代理商账号
     * @throws Exception
     */
    @Test
    public void agentLoginAsAgentTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        //平台方
        MallCustomer customer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        //一级代理商
        MallCustomer agentCustomer = new MallCustomer();
        agentCustomer.setNickName(UUID.randomUUID().toString());
        agentCustomer.setUsername(userName);
        agentCustomer.setPassword(passwordEncoder.encode(password));
        agentCustomer.setCustomerType(CustomerTypeEnum.AGENT);
        agentCustomer = customerRepository.saveAndFlush(agentCustomer);
        Agent agent = new Agent();
        agent.setId(agentCustomer.getId());
        agent.setCustomer(customer);
        agent.setDisabled(false);
        agent.setDeleted(false);
        agent.setStatus(AgentStatusEnum.CHECKED);
        agentCustomer.setAgent(agent);
        customerRepository.saveAndFlush(agentCustomer);
//        agentService.addAgent(agent);
//        agentService.flush();

        //以代理商角色登录
        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.AGENT.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));


        //以门店角色登录,返回 用户名或者密码错误
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.SHOP.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertTrue(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().indexOf("用户名或密码错误") > -1);

        //用已冻结的代理商账号以代理商角色登录
        agent.setDisabled(true);
        agentRepository.saveAndFlush(agent);

        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.AGENT.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertTrue(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().indexOf("该账户已冻结") > -1);

        //用已删除的代理商账号以代理商角色登录
        agent.setDisabled(false);
        agent.setDeleted(true);
        agentRepository.saveAndFlush(agent);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.AGENT.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertTrue(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().indexOf("该账户已失效") > -1);
    }

    /**
     * 用门店账号登录
     * @throws Exception
     */
    @Test
    public void shopLoginAsShopTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        //平台方
        MallCustomer customer = mockMallCustomer(CustomerTypeEnum.HUOBAN_MALL);
        //一级代理商
        MallCustomer agent = mockAgent(customer,null);
        //门店
        MallCustomer shopCustomer = new MallCustomer();
        shopCustomer.setNickName(UUID.randomUUID().toString());
        shopCustomer.setUsername(userName);
        shopCustomer.setPassword(password);
        shopCustomer.setCustomerType(CustomerTypeEnum.AGENT_SHOP);
        shopCustomer = customerService.newCustomer(shopCustomer);
        Shop shop = new Shop();
        shop.setId(shopCustomer.getId());
        shop.setUsername(userName);
        shop.setDeleted(false);
        shop.setDisabled(false);
        shop.setStatus(AgentStatusEnum.CHECKED);
        shopCustomer.setShop(shop);
        shopCustomer = customerRepository.save(shopCustomer);

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        //以门店角色登录
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.SHOP.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));

        //以代理商角色登录
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.AGENT.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));


        shop.setDisabled(true);
        shopRepository.save(shop);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.SHOP.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertTrue(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().indexOf("该账户已冻结") > -1);

        shop.setDisabled(false);
        shop.setDeleted(true);
        shopRepository.save(shop);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.SHOP.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertTrue(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString().indexOf("该账户已失效") > -1);
    }

}