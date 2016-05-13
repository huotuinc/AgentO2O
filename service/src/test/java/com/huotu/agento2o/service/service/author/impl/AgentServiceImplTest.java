package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.repository.author.AgentRepository;
import com.huotu.agento2o.service.service.author.AgentService;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

/**
 * Created by helloztt on 2016/5/9.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = ServiceConfig.class)
@RunWith(SpringJUnit4ClassRunner.class)
@Transactional
public class AgentServiceImplTest {
    @Autowired
    private AgentService agentService;

    @Test
    public void testFindById() throws Exception {
        Agent agent = agentService.findById(9);
        Assert.assertNotNull(agent);
    }

    @Test
    public void testFindByAgentLevel(){
        List<Agent> agetnList = agentService.findByAgentLevelId(1);
        Assert.assertTrue(agetnList.size()>0);
    }

    @Test
//    @Rollback(value = false)
    public void testAddAgent() throws Exception{
//        String userName = UUID.randomUUID().toString();
//        String passWord = UUID.randomUUID().toString();
        String userName = "ceshi";
        String passWord = "123456";
//        AgentLevel agentLevel = new AgentLevel();
//        agentLevel.setLevelId(1);
        Agent agent = new Agent();
//        agent.setAgentLevel(agentLevel);
        agent.setUsername(userName);
        agent.setPassword(passWord);
        agent.setDeleted(false);
        agent.setDisabled(false);
        agent.setStatus(AgentStatusEnum.CHECKED);
        agent = agentService.addAgent(agent);

    }
}