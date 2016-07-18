package com.huotu.agento2o.service.service.author.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.config.MallPasswordEncoder;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.searchable.AgentSearcher;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;

import java.util.List;
import java.util.UUID;

/**
 * Created by helloztt on 2016/5/9.
 */

public class AgentServiceImplTest extends CommonTestBase {
    @Autowired
    private AgentService agentService;

    @Autowired
    private MallPasswordEncoder passwordEncoder;

    private CustomerAuthor mockCustomer;

    private CustomerAuthor mockAgent;
    @Autowired
    private MallCustomerRepository customerRepository;
    @Autowired
    private MallCustomerService mallCustomerService;

    @Before
    public void init() {
        mockCustomer = mockMallCustomer();
        mockAgent = mockAgent(mockCustomer, null);
    }

//    @Test
//    @Rollback(false)
    public void mockTopAgent(){
        CustomerAuthor customer = customerService.findByCustomerId(4886);
        CustomerAuthor agentCustomer = new CustomerAuthor();
        agentCustomer.setNickName("代理商1");
        agentCustomer.setUsername("agent");
        agentCustomer.setPassword("123456");
        agentCustomer = customerRepository.save(agentCustomer);
        customerRepository.flush();
        Agent agent = new Agent();
        agent.setId(agentCustomer.getId());
        agent.setStatus(AgentStatusEnum.CHECKED);
        agent.setName("代理商1");
        agent.setContact("联系人");
        agent.setMobile("123");
        agent.setTelephone("123");
        agent.setAddress("地址");
        agent.setDisabled(false);
        agent.setDeleted(false);
        agent.setCustomer(customer);
        agentCustomer.setAgent(agent);
        customerService.newCustomer(agentCustomer);
    }

//    @Test
//    @Rollback(false)
    public void testMockAgent(){
        CustomerAuthor customer = customerService.findByCustomerId(4471);
        CustomerAuthor agentCustomer = mockMallCustomer();
        Agent agent = new Agent();
        agent.setId(agentCustomer.getId());
        agent.setStatus(AgentStatusEnum.CHECKED);
        agent.setName(UUID.randomUUID().toString());
        agent.setContact(UUID.randomUUID().toString());
        agent.setMobile(UUID.randomUUID().toString());
        agent.setTelephone(UUID.randomUUID().toString());
        agent.setAddress(UUID.randomUUID().toString());
        agent.setStatus(AgentStatusEnum.CHECKED);
        agent.setDisabled(false);
        agent.setDeleted(false);
        agent.setCustomer(customer);
        agentCustomer.setAgent(agent);
        customerService.newCustomer(customer);
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
    public void testFindByAgentLevel() {
        AgentLevel agentLevel = mockAgentLevel(mockCustomer);
        mockAgent.getAgent().setAgentLevel(agentLevel);
        customerRepository.save(mockAgent);
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
        Assert.assertTrue(!mockAgent.getAgent().isDeleted());
        int result = agentService.deleteAgent(mockAgent.getId());
        Assert.assertTrue(result > 0);
        Agent agent;
        agent = agentService.findById(mockAgent.getAgent().getId(), mockCustomer.getCustomerId());
        agentService.flush();
        Assert.assertTrue(agent.isDeleted());
        result = agentService.deleteAgent(-1);
        Assert.assertTrue(result == 0);
        result = agentService.deleteAgent(null);
        Assert.assertTrue(result == 0);
    }

    @Test
    public void testUpdateDisabledStatus() {
        Assert.assertTrue(!mockAgent.getAgent().isDisabled());
        int result = agentService.freezeAgent(mockAgent.getId());
        Assert.assertTrue(result > 0);
        Agent agent;
        agent = agentService.findById(mockAgent.getId(), mockCustomer.getCustomerId());
        Assert.assertTrue(agent.isDisabled());
        result = agentService.unfreezeAgent(agent.getId());
        Assert.assertTrue(result > 0);
        agent = agentService.findById(agent.getId(), mockCustomer.getCustomerId());
        Assert.assertTrue(!agent.isDisabled());
        result = agentService.freezeAgent(-1);
        Assert.assertTrue(result == 0);
        result = agentService.freezeAgent(null);
        Assert.assertTrue(result == 0);
    }

    @Test
    public void testFindByParentAgentId() {
        mockAgent(mockCustomer, mockAgent.getAgent());
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
        int result = mallCustomerService.resetPassword(mockAgent.getId(), password);
        Assert.assertTrue(result > 0);
        CustomerAuthor agent = mallCustomerService.findByCustomerId(mockAgent.getId());
        Assert.assertEquals(passwordEncoder.encode(password), agent.getPassword());
        result = mallCustomerService.resetPassword(-1, password);
        Assert.assertTrue(result == 0);
        result = mallCustomerService.resetPassword(null, password);
        Assert.assertTrue(result == 0);
    }

    @Test
    public void testSaveAgentConfig() {
        mockAgent.getAgent().setName(UUID.randomUUID().toString());
        mockAgent.getAgent().setContact(UUID.randomUUID().toString());
        mockAgent.getAgent().setMobile(UUID.randomUUID().toString());
        mockAgent.getAgent().setTelephone(UUID.randomUUID().toString());
        mockAgent.getAgent().setAddress(UUID.randomUUID().toString());
        ApiResult result = agentService.saveAgentConfig(mockAgent.getId(), mockAgent.getAgent(), null);
       Assert.assertEquals("请求成功",result.getMsg());
        result = agentService.saveAgentConfig(-1, mockAgent.getAgent(), null);
        Assert.assertEquals("该账号已失效",result.getMsg());
        result = agentService.saveAgentConfig(mockAgent.getId(), null,null);
        Assert.assertEquals("没有传输数据",result.getMsg());
        mockAgent.getAgent().setDeleted(true);
        mallCustomerService.save(mockAgent);
        result = agentService.saveAgentConfig(mockAgent.getId(), mockAgent.getAgent(), null);
        Assert.assertEquals("该账号已失效", result.getMsg());
    }
}