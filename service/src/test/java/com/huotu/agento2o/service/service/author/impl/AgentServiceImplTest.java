package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.annotation.Rollback;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */

public class AgentServiceImplTest extends CommonTestBase {
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
    public void testIfEnable(){
        boolean bn = agentService.ifEnable("wwww");
        Assert.assertTrue(bn);
        bn = agentService.ifEnable("wj");
        Assert.assertTrue(!bn);
    }

    @Test
//   @Rollback(value = false)
    public void testAddAgent() throws Exception{
//        String userName = UUID.randomUUID().toString();
//        String passWord = UUID.randomUUID().toString();
        String userName = "ylv2";
        String passWord = "1";
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