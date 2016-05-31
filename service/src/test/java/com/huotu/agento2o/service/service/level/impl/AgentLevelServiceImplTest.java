package com.huotu.agento2o.service.service.level.impl;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.common.CommonTestBase;
import com.huotu.agento2o.service.service.level.AgentLevelService;
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

/**
 * Created by WangJie on 2016/5/11.
 */

public class AgentLevelServiceImplTest extends CommonTestBase {
    @Autowired
    private AgentLevelService agentLevelService;

    @Test
    public void testFindById() throws Exception {
        AgentLevel agentLevel = agentLevelService.findById(1,6340);
        Assert.assertNotNull(agentLevel);
        agentLevel = agentLevelService.findById(6340,1);
        Assert.assertNull(agentLevel);
    }

    @Test
    public void testFindLastLevel() throws Exception {
        Integer num = agentLevelService.findLastLevel(6340);
        ;
        Assert.assertTrue(num > 0);
        num = agentLevelService.findLastLevel(-1);
        ;
        Assert.assertNull(num);
    }

    @Test
    public void testFindByCustomerId() throws Exception {
        //不存在的id查询
        List<AgentLevel> list = agentLevelService.findByCustomertId(-1);
        Assert.assertTrue(list.size() == 0);
        //存在的id查询
        list = agentLevelService.findByCustomertId(6340);
        Assert.assertTrue(list.size() >= 0);
    }

    @Test
    public void testAddOrUpdate() throws Exception {
        AgentLevel agentLevel = new AgentLevel();
        agentLevel.setComment("8折进货");
        agentLevel.setLevelName("一级代理商");
        agentLevelService.addOrUpdate(0, -1, agentLevel);
    }

    @Test
    public void testDeleteAgentLevel() throws Exception {
        AgentLevel agentLevel = new AgentLevel();
        agentLevel.setLevel(0);
        agentLevel.setLevelName("一级代理商");
        agentLevel.setComment("5折进货");
        agentLevel = agentLevelService.addAgentLevel(agentLevel);
        agentLevelService.flush();
        agentLevel = agentLevelService.findById(agentLevel.getLevelId(),4471);
        Assert.assertNotNull(agentLevel);
        agentLevelService.deleteAgentLevel(agentLevel.getLevelId(),null);
        agentLevel = agentLevelService.findById(agentLevel.getLevelId(),4471);
        Assert.assertNull(agentLevel);
    }


    @Test
//    @Rollback(value = false)
    public void testAddAgentLevel() throws Exception {
        AgentLevel agentLevel = new AgentLevel();
        MallCustomer customer = new MallCustomer();
        customer.setCustomerId(6340);
        agentLevel.setCustomer(customer);
        agentLevel.setLevel(0);
        agentLevel.setLevelName("一级代理商");
        agentLevel.setComment("5折进货");
        agentLevel = agentLevelService.addAgentLevel(agentLevel);
        agentLevelService.flush();
        agentLevel = agentLevelService.findById(agentLevel.getLevelId(),4471);
        Assert.assertNotNull(agentLevel);
    }
}