package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import com.huotu.agento2o.service.searchable.AgentSearcher;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.user.UserBaseInfoService;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.annotation.Rollback;

import javax.crypto.AEADBadTagException;
import java.util.Date;
import java.util.List;
import java.util.UUID;

/**
 * Created by helloztt on 2016/5/9.
 */

public class AgentServiceImplTest extends CommonTestBase {
    @Autowired
    private AgentService agentService;

    @Autowired
    private PasswordEncoder passwordEncoder;

//    @Autowired
//    private UserBaseInfoService userBaseInfoService;

    @Test
    public void testFindById() throws Exception {
        Agent agent = agentService.findById(null,null);
        Assert.assertNotNull(agent);
    }

    @Test
    public void testFindByAgentLevel() {
        List<Agent> agentList = agentService.findByAgentLevelId(null);
        Assert.assertTrue(agentList.size() > 0);
    }

    @Test
    public void testIfEnable() {
        boolean bn = agentService.isEnableAgent("wwww");
        Assert.assertTrue(bn);
        bn = agentService.isEnableAgent("wj");
        Assert.assertTrue(!bn);
    }

    @Test
//    @Rollback(value = false)
    public void testAddAgent() throws Exception {
//        String userName = UUID.randomUUID().toString();
//        String passWord = UUID.randomUUID().toString();
        String userName = "ceshi10";
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

    @Test
    public void testGetAgentList() {
        //平台方
        MallCustomer mockCustomer = mockMallCustomer();
        //代理商
        Agent mockAgent = mockAgent(mockCustomer, null);
        AgentSearcher searcher = new AgentSearcher();
        searcher.setPageNo(1);
        Page<Agent> agentPage = agentService.getAgentList(mockCustomer.getCustomerId(), searcher);
        Assert.assertTrue(agentPage.getTotalElements() == 1);
        Assert.assertEquals(agentPage.getContent().get(0).getName(), mockAgent.getName());
    }

    @Test
    public void testDeleteAgent() {
        MallCustomer mockCustomer = mockMallCustomer();
        Agent mockAgent = mockAgent(mockCustomer, null);
        Assert.assertTrue(!mockAgent.isDeleted());
        agentService.deleteAgent(mockAgent.getId());
        mockAgent = agentService.findById(mockAgent.getId(),mockCustomer.getCustomerId());
        agentService.flush();
        Assert.assertTrue(mockAgent.isDeleted());
        Assert.assertTrue(agentService.isEnableAgent(mockAgent.getUsername()));
    }

    @Test
    public void testUpdateDisabledStatus() {
        MallCustomer mockCustomer = mockMallCustomer();
        Agent mockAgent = mockAgent(mockCustomer, null);
        Assert.assertTrue(!mockAgent.isDisabled());
        agentService.freezeAgent(mockAgent.getId());
        mockAgent = agentService.findById(mockAgent.getId(),mockCustomer.getCustomerId());
        Assert.assertTrue(mockAgent.isDisabled());
        agentService.unfreezeAgent(mockAgent.getId());
        mockAgent = agentService.findById(mockAgent.getId(),mockCustomer.getCustomerId());
        Assert.assertTrue(!mockAgent.isDisabled());
    }

    @Test
    public void testFindByParentAgentid() {
        List<Agent> list = agentService.findByParentAgentId(4);
        Assert.assertTrue(list.size() == 1);
        list = agentService.findByParentAgentId(-1);
        Assert.assertTrue(list.size() == 0);
    }

    @Test
    public void testAddOrUpdate() {
        MallCustomer mockCustomer = mockMallCustomer();
        Agent mockAgent = mockAgent(mockCustomer, null);
        Integer customerId = mockCustomer.getCustomerId();
        AgentLevel agentLevel = mockAgentLevel(mockCustomer);
        String hotUserName = null;
        Integer levelId = agentLevel.getLevelId();
        Integer parentAgentId = mockAgent.getId();
        //测试增加
        Agent agent = new Agent();
        agent.setId(0);
//        agent.setUsername(UUID.randomUUID().toString());
        agent.setUsername(mockAgent.getUsername());
        agent.setPassword(UUID.randomUUID().toString());
        agent.setName(UUID.randomUUID().toString());
        agent.setContact(UUID.randomUUID().toString());
        agent.setMobile(UUID.randomUUID().toString());
        agent.setTelephone(UUID.randomUUID().toString());
        agent.setAddress(UUID.randomUUID().toString());
        ApiResult result = agentService.addOrUpdate(customerId, levelId, parentAgentId, hotUserName, agent);
//        Assert.assertTrue(result.getCode()==200);
        Assert.assertTrue(result.getCode() == 100);
        //测试修改
        agentService.flush();
        agent.setUsername(UUID.randomUUID().toString());
        agent.setPassword(UUID.randomUUID().toString());
        agent.setName(UUID.randomUUID().toString());
        agent.setContact(UUID.randomUUID().toString());
        agent.setMobile(UUID.randomUUID().toString());
        agent.setTelephone(UUID.randomUUID().toString());
        agent.setAddress(UUID.randomUUID().toString());
        result = agentService.addOrUpdate(customerId, levelId, parentAgentId, hotUserName, agent);
        Assert.assertTrue(result.getCode() == 200);
        //测试平台方或者等级不存在的情况
        customerId = -1;
        result = agentService.addOrUpdate(customerId, levelId, parentAgentId, hotUserName, agent);
        Assert.assertTrue(result.getCode() == 400);
        customerId = mockCustomer.getCustomerId();
        levelId = -1;
        result = agentService.addOrUpdate(customerId, levelId, parentAgentId, hotUserName, agent);
        Assert.assertTrue(result.getCode() == 400);
    }

    @Test
    public void testUserBaseInfoId() {
        Agent agent = agentService.findByUserBaseInfoId(12);
        Assert.assertNotNull(agent);
    }

    @Test
    public void testResetPassword() {
        String password = UUID.randomUUID().toString();
        MallCustomer mockCustomer = mockMallCustomer();
        Agent agent = mockAgent(mockCustomer, null);
        int num = agentService.resetPassword(null, password);
        agent = agentService.findById(agent.getId(),mockCustomer.getCustomerId());
        Assert.assertEquals(passwordEncoder.encode(password), agent.getPassword());
    }

}