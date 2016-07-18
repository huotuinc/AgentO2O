package com.huotu.agento2o.service.service.level.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.List;
import java.util.UUID;

/**
 * Created by WangJie on 2016/5/11.
 */

public class AgentLevelServiceImplTest extends CommonTestBase {
    @Autowired
    private AgentLevelService agentLevelService;

    @Autowired
    private AgentService agentService;

    private CustomerAuthor mockCustomer;

    private AgentLevel mockAgentLevel;

    @Before
    public void init() {
        mockCustomer = mockMallCustomer();
        mockAgentLevel = mockAgentLevel(mockCustomer);
    }

    @Test
    public void testFindByCustomerId() throws Exception {
        //不存在的id查询
        List<AgentLevel> list = agentLevelService.findByCustomertId(-1);
        Assert.assertTrue(list.size() == 0);
        list = agentLevelService.findByCustomertId(null);
        Assert.assertTrue(list.size() == 0);
        //存在的id查询
        list = agentLevelService.findByCustomertId(mockCustomer.getCustomerId());
        Assert.assertTrue(list.size() > 0);
    }

    @Test
    public void testFindById() throws Exception {
        AgentLevel agentLevel = agentLevelService.findById(mockAgentLevel.getLevelId(), mockCustomer.getCustomerId());
        Assert.assertNotNull(agentLevel);
        agentLevel = agentLevelService.findById(mockAgentLevel.getLevelId(), -1);
        Assert.assertNull(agentLevel);
        agentLevel = agentLevelService.findById(-1, mockCustomer.getCustomerId());
        Assert.assertNull(agentLevel);
        agentLevel = agentLevelService.findById(mockAgentLevel.getLevelId(), null);
        Assert.assertNull(agentLevel);
        agentLevel = agentLevelService.findById(null, mockCustomer.getCustomerId());
        Assert.assertNull(agentLevel);
    }

    @Test
    public void testFindLastLevel() throws Exception {
        Integer num = agentLevelService.findLastLevel(mockCustomer.getCustomerId());
        Assert.assertTrue(num > 0);
        num = agentLevelService.findLastLevel(-1);
        Assert.assertNull(num);
        num = agentLevelService.findLastLevel(null);
        Assert.assertNull(num);
    }


    @Test
    public void testAddOrUpdate() throws Exception {
        AgentLevel requestAgentLevel = new AgentLevel();
        requestAgentLevel.setLevelName(UUID.randomUUID().toString());
        ApiResult result = agentLevelService.addOrUpdate(mockAgentLevel.getLevelId(), mockCustomer.getCustomerId(), requestAgentLevel);
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultMsg(), result.getMsg());
        Assert.assertEquals(requestAgentLevel.getLevelName(), agentLevelService.findById(mockAgentLevel.getLevelId(), mockCustomer.getCustomerId()).getLevelName());
        result = agentLevelService.addOrUpdate(null, mockCustomer.getCustomerId(), requestAgentLevel);
        Assert.assertEquals(ResultCodeEnum.DATA_NULL.getResultMsg(), result.getMsg());
        result = agentLevelService.addOrUpdate(mockAgentLevel.getLevelId() + 1, mockCustomer.getCustomerId(), requestAgentLevel);
        Assert.assertEquals(ResultCodeEnum.DATA_NULL.getResultMsg(), result.getMsg());
    }

    @Test
    public void testDeleteAgentLevel() throws Exception {
        ApiResult result = agentLevelService.deleteAgentLevel(mockAgentLevel.getLevelId(), mockCustomer.getCustomerId());
        Assert.assertEquals(ResultCodeEnum.SUCCESS.getResultMsg(), result.getMsg());
        AgentLevel agentLevel = agentLevelService.findById(mockAgentLevel.getLevelId(), mockCustomer.getCustomerId());
        Assert.assertNull(agentLevel);
        result = agentLevelService.deleteAgentLevel(-1, mockCustomer.getCustomerId());
        Assert.assertEquals(ResultCodeEnum.DATA_NULL.getResultMsg(), result.getMsg());
        mockAgentLevel = mockAgentLevel(mockCustomer);
        CustomerAuthor agent = mockAgent(mockCustomer, null);
        agent.getAgent().setAgentLevel(mockAgentLevel);
        customerService.newCustomer(agent);
//        agentService.addAgent(agent);
        result = agentLevelService.deleteAgentLevel(mockAgentLevel.getLevelId(), mockCustomer.getCustomerId());
        Assert.assertEquals("等级已被绑定", result.getMsg());
    }


    @Test
    public void testAddAgentLevel() throws Exception {
        AgentLevel agentLevel = new AgentLevel();
        agentLevel.setCustomer(mockCustomer);
        agentLevel.setLevelName(UUID.randomUUID().toString());
        agentLevel.setComment(UUID.randomUUID().toString());
        agentLevel = agentLevelService.addAgentLevel(agentLevel);
        agentLevelService.flush();
        agentLevel = agentLevelService.findById(agentLevel.getLevelId(), mockCustomer.getCustomerId());
        Assert.assertNotNull(agentLevel);
        agentLevel = agentLevelService.addAgentLevel(null);
        agentLevelService.flush();
        Assert.assertNull(agentLevel);
    }
}