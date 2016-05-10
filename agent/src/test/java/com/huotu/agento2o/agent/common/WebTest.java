/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.common;

import com.huotu.agento2o.agent.config.MVCConfig;
import com.huotu.agento2o.agent.config.SecurityConfig;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.ShopService;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;

/**
 * Created by helloztt on 2016/5/9.
 */
@ActiveProfiles("test")
@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = {MVCConfig.class, ServiceConfig.class})
@WebAppConfiguration
@Transactional
public abstract class WebTest extends SpringWebTest{
    protected MockHttpSession session;
    protected Agent mockAgent;
    protected Shop mockShop;

    @Autowired
    private AgentService agentService;
    @Autowired
    private ShopService shopService;

    protected MockHttpSession loginAs(String userName, String password,String roleType) throws Exception {
        MockHttpSession session = (MockHttpSession) this.mockMvc.perform(get("/"))
                .andReturn().getRequest().getSession(true);
        session = (MockHttpSession) this.mockMvc.perform(post(SecurityConfig.LOGIN_PAGE).session(session)
                .param("username", userName).param("password", password).param("roleType",roleType))
                .andReturn().getRequest().getSession();

        saveAuthedSession(session);
        return session;
    }

    protected void mockAgent(){
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
    }

    protected void mockShop(){
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
    }
}
