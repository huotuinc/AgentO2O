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

import com.huotu.agento2o.agent.common.ExceptionHandler;
import com.huotu.agento2o.agent.common.WebTest;
import com.huotu.agento2o.agent.config.SecurityConfig;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.ShopService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by helloztt on 2016/5/9.
 */
@Transactional
public class IndexControllerTest extends WebTest{

    @Autowired
    private AgentService agentService;
    @Autowired
    private ShopService shopService;

    /**
     * 用代理商账号以代理商角色登录
     * @throws Exception
     */
    @Test
    public void agentLoginAsAgentTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Agent agent = new Agent();
        agent.setUsername(userName);
        agent.setPassword(password);
        agent.setDisabled(false);
        agent.setDeleted(false);
        agent.setStatus(AgentStatusEnum.CHECKED);
        this.mockAgent = agentService.addAgent(agent);
        agentService.flush();

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.AGENT.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));


    }

    /**
     * 用代理商账号以门店角色登录,返回 用户名或者密码错误
     */
    @Test
    public void agentLoginAsShopTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Agent agent = new Agent();
        agent.setUsername(userName);
        agent.setPassword(password);
        agent.setDisabled(false);
        agent.setDeleted(false);
        agent.setStatus(AgentStatusEnum.CHECKED);
        this.mockAgent = agentService.addAgent(agent);
        agentService.flush();

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.SHOP.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertEquals(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString(), ExceptionHandler.BAD_CREDENTIALS_MSG);
    }

    /**
     * 用已冻结的代理商账号以代理商角色登录，返回 该账户已被冻结
     */
    @Test
    public void disabledAgentLoginAsAgentTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Agent agent = new Agent();
        agent.setUsername(userName);
        agent.setPassword(password);
        agent.setDisabled(true);
        agent.setDeleted(false);
        agent.setStatus(AgentStatusEnum.CHECKED);
        this.mockAgent = agentService.addAgent(agent);
        agentService.flush();

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.AGENT.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertEquals(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString(), ExceptionHandler.LOCKED_MSG);
    }

    /**
     * 用已删除的代理商账号以代理商角色登录，返回 该账户已被冻结
     */
    @Test
    public void deletedAgentLoginAsAgentTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Agent agent = new Agent();
        agent.setUsername(userName);
        agent.setPassword(password);
        agent.setDisabled(false);
        agent.setDeleted(true);
        agent.setStatus(AgentStatusEnum.CHECKED);
        this.mockAgent = agentService.addAgent(agent);
        agentService.flush();

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.AGENT.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertEquals(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString(), ExceptionHandler.EXPIRED_MSG);
    }

    /**
     * 用门店账号以门店角色登录
     * @throws Exception
     */
    @Test
    public void shopLoginAsShopTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Shop shop = new Shop();
        shop.setUsername(userName);
        shop.setPassword(password);
        shop.setDeleted(false);
        shop.setDisabled(false);
        shop.setStatus(AgentStatusEnum.CHECKED);
        this.mockShop = shopService.addShop(shop);
        shopService.flush();

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.SHOP.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));


    }

    /**
     * 用门店账号以代理商角色登录,返回 用户名或者密码错误
     */
    @Test
    public void shopLoginAsAgentTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Shop shop = new Shop();
        shop.setUsername(userName);
        shop.setPassword(password);
        shop.setDeleted(false);
        shop.setDisabled(false);
        shop.setStatus(AgentStatusEnum.CHECKED);
        this.mockShop = shopService.addShop(shop);
        shopService.flush();

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.AGENT.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertEquals(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString(), ExceptionHandler.BAD_CREDENTIALS_MSG);
    }

    /**
     * 用已冻结的门店账号以门店角色登录，返回 该账户已被冻结
     */
    @Test
    public void disabledShopLoginAsShopTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Shop shop = new Shop();
        shop.setUsername(userName);
        shop.setPassword(password);
        shop.setDeleted(false);
        shop.setDisabled(true);
        shop.setStatus(AgentStatusEnum.CHECKED);
        this.mockShop = shopService.addShop(shop);
        shopService.flush();

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.SHOP.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertEquals(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString(), ExceptionHandler.LOCKED_MSG);
    }

    /**
     * 用已删除的门店账号以门店角色登录，返回 该账户已被冻结
     */
    @Test
    public void deletedShopLoginAsShopTest() throws Exception{
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Shop shop = new Shop();
        shop.setUsername(userName);
        shop.setPassword(password);
        shop.setDeleted(true);
        shop.setDisabled(false);
        shop.setStatus(AgentStatusEnum.CHECKED);
        this.mockShop = shopService.addShop(shop);
        shopService.flush();

        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",String.valueOf(RoleTypeEnum.SHOP.getCode())))
                .andReturn().getRequest().getSession();
        Assert.assertNotNull(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        Assert.assertEquals(session.getAttribute("SPRING_SECURITY_LAST_EXCEPTION").toString(), ExceptionHandler.EXPIRED_MSG);
    }

}