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
import org.junit.Before;
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

    private MallCustomer mockCustomer;

    private Agent mockAgent;

    @Before
    public void init() {
        mockCustomer = mockMallCustomer();
        mockAgent = mockAgent(mockCustomer, null);
    }


    @Test
    public void testFindById() throws Exception {
        Agent agent = agentService.findById(mockAgent.getId(), mockCustomer.getCustomerId());
        Assert.assertNotNull(agent);
        agent = agentService.findById(-1, mockCustomer.getCustomerId());
        Assert.assertNull(agent);
        agent = agentService.findById(mockAgent.getId(), -1);
        Assert.assertNull(agent);
        agent = agentService.findById(mockAgent.getId(), null);
        Assert.assertNull(agent);
    }

    @Test
    public void testFindByAgentId() throws Exception {
        Agent agent = agentService.findByAgentId(mockAgent.getId());
        Assert.assertNotNull(agent);
        agent = agentService.findByAgentId(-1);
        Assert.assertNull(agent);
        agent = agentService.findByAgentId(null);
        Assert.assertNull(agent);
    }

    @Test
    public void testFindByUserName() throws Exception {
        Agent agent = agentService.findByUserName(mockAgent.getUsername());
        Assert.assertNotNull(agent);
        agent = agentService.findByUserName("");
        Assert.assertNull(agent);
        agent = agentService.findByUserName(null);
        Assert.assertNull(agent);
    }


    @Test
    public void testFindByAgentLevel() {
        AgentLevel agentLevel = mockAgentLevel(mockCustomer);
        mockAgent.setAgentLevel(agentLevel);
        agentService.addAgent(mockAgent);
        agentService.flush();
        List<Agent> agentList = agentService.findByAgentLevelId(agentLevel.getLevelId());
        Assert.assertTrue(agentList.size() > 0);
        agentList = agentService.findByAgentLevelId(-1);
        Assert.assertTrue(agentList.size() == 0);
        agentList = agentService.findByAgentLevelId(null);
        Assert.assertTrue(agentList.size() >= 0);
    }

    @Test
    public void testIsEnable() {
        boolean result = agentService.isEnableAgent(UUID.randomUUID().toString());
        Assert.assertTrue(result);
        result = agentService.isEnableAgent(mockAgent.getUsername());
        Assert.assertTrue(!result);
    }

    @Test
    public void testAddAgent() throws Exception {
        Agent agent = new Agent();
        agent.setUsername(UUID.randomUUID().toString());
        agent.setPassword(UUID.randomUUID().toString());
        agent.setDeleted(false);
        agent.setDisabled(false);
        agent.setStatus(AgentStatusEnum.CHECKED);
        agent = agentService.addAgent(agent);
        Assert.assertNotNull(agent);
        Agent agent2 = new Agent();
        agent2.setUsername(agent.getUsername());
        agent2.setPassword(UUID.randomUUID().toString());
        agent2.setDeleted(false);
        agent2.setDisabled(false);
        agent2.setStatus(AgentStatusEnum.CHECKED);
        agent2 = agentService.addAgent(agent2);
        Assert.assertNull(agent2);
        agent2 = agentService.addAgent(null);
        Assert.assertNull(agent2);
    }

    @Test
    public void testGetAgentList() {
        AgentSearcher searcher = new AgentSearcher();
        Page<Agent> agentPage = agentService.getAgentList(mockCustomer.getCustomerId(), searcher);
        Assert.assertTrue(agentPage.getTotalElements() == 1);
        Assert.assertEquals(agentPage.getContent().get(0).getName(), mockAgent.getName());
        searcher.setAgentName(mockAgent.getName());
        agentPage = agentService.getAgentList(mockCustomer.getCustomerId(), searcher);
        Assert.assertTrue(agentPage.getTotalElements() == 1);
        Assert.assertEquals(agentPage.getContent().get(0).getName(), mockAgent.getName());
        searcher.setAgentName(UUID.randomUUID().toString());
        agentPage = agentService.getAgentList(mockCustomer.getCustomerId(), searcher);
        Assert.assertTrue(agentPage.getTotalElements() == 0);
    }

    @Test
    public void testDeleteAgent() {
        Assert.assertTrue(!mockAgent.isDeleted());
        int result = agentService.deleteAgent(mockAgent.getId());
        Assert.assertTrue(result > 0);
        mockAgent = agentService.findById(mockAgent.getId(), mockCustomer.getCustomerId());
        agentService.flush();
        Assert.assertTrue(mockAgent.isDeleted());
        result = agentService.deleteAgent(-1);
        Assert.assertTrue(result == 0);
        result = agentService.deleteAgent(null);
        Assert.assertTrue(result == 0);
    }

    @Test
    public void testUpdateDisabledStatus() {
        Assert.assertTrue(!mockAgent.isDisabled());
        int result = agentService.freezeAgent(mockAgent.getId());
        Assert.assertTrue(result > 0);
        mockAgent = agentService.findById(mockAgent.getId(), mockCustomer.getCustomerId());
        Assert.assertTrue(mockAgent.isDisabled());
        result = agentService.unfreezeAgent(mockAgent.getId());
        Assert.assertTrue(result > 0);
        mockAgent = agentService.findById(mockAgent.getId(), mockCustomer.getCustomerId());
        Assert.assertTrue(!mockAgent.isDisabled());
        result = agentService.freezeAgent(-1);
        Assert.assertTrue(result == 0);
        result = agentService.freezeAgent(null);
        Assert.assertTrue(result == 0);
    }

    @Test
    public void testFindByParentAgentId() {
        Agent agent = mockAgent(mockCustomer, mockAgent);
        List<Agent> list = agentService.findByParentAgentId(mockAgent.getId());
        Assert.assertTrue(list.size() > 0);
        list = agentService.findByParentAgentId(-1);
        Assert.assertTrue(list.size() == 0);
        list = agentService.findByParentAgentId(null);
        Assert.assertTrue(list.size() >= 0);
    }

    @Test
    public void testAddOrUpdate() {
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
//       Assert.assertEquals("请求成功",result.getMsg());
        Assert.assertEquals("用户名已存在", result.getMsg());
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
        Assert.assertEquals("请求成功", result.getMsg());
        //测试平台方或者等级不存在的情况
        customerId = -1;
        result = agentService.addOrUpdate(customerId, levelId, parentAgentId, hotUserName, agent);
        Assert.assertEquals("没有传输数据", result.getMsg());
        customerId = mockCustomer.getCustomerId();
        levelId = -1;
        result = agentService.addOrUpdate(customerId, levelId, parentAgentId, hotUserName, agent);
        Assert.assertEquals("没有传输数据", result.getMsg());
        result = agentService.addOrUpdate(customerId, levelId, parentAgentId, hotUserName, null);
        Assert.assertEquals("没有传输数据", result.getMsg());
    }

    @Test
    public void testResetPassword() {
        String password = UUID.randomUUID().toString();
        int result = agentService.resetPassword(mockAgent.getId(), password);
        Assert.assertTrue(result > 0);
        Agent agent = agentService.findById(mockAgent.getId(), mockCustomer.getCustomerId());
        Assert.assertEquals(passwordEncoder.encode(password), agent.getPassword());
        result = agentService.resetPassword(-1, password);
        Assert.assertTrue(result == 0);
        result = agentService.resetPassword(null, password);
        Assert.assertTrue(result == 0);
    }

    @Test
    public void testSaveAgentConfig() {
        mockAgent.setUsername(UUID.randomUUID().toString());
        mockAgent.setPassword(UUID.randomUUID().toString());
        mockAgent.setName(UUID.randomUUID().toString());
        mockAgent.setContact(UUID.randomUUID().toString());
        mockAgent.setMobile(UUID.randomUUID().toString());
        mockAgent.setTelephone(UUID.randomUUID().toString());
        mockAgent.setAddress(UUID.randomUUID().toString());
        ApiResult result = agentService.saveAgentConfig(mockAgent.getId(), mockAgent,null);
       Assert.assertEquals("请求成功",result.getMsg());
        result = agentService.saveAgentConfig(-1, mockAgent,null);
        Assert.assertEquals("该账号已失效",result.getMsg());
        result = agentService.saveAgentConfig(mockAgent.getId(), null,null);
        Assert.assertEquals("没有传输数据",result.getMsg());
        mockAgent.setDeleted(true);
        agentService.addAgent(mockAgent);
        agentService.flush();
        result = agentService.saveAgentConfig(mockAgent.getId(), mockAgent,null);
        Assert.assertEquals("该账号已失效",result.getMsg());
    }
}