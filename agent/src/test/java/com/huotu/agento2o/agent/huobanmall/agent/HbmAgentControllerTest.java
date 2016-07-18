/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.huobanmall.agent;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.data.domain.Page;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by WangJie on 2016/6/7.
 */
public class HbmAgentControllerTest extends CommonTestBase {

    private static String BASE_URL = "/huobanmall/agent";
    //平台下的代理商
    List<CustomerAuthor> agents = new ArrayList<>();
    //平台
    private CustomerAuthor mockCustomer;
    private Cookie cookie;

    @Before
    public void init() {
        //初始化模拟数据
        mockCustomer = mockMallCustomer();
        cookie = new Cookie("UserID", String.valueOf(mockCustomer.getCustomerId()));
        for (int i = 0; i <= random.nextInt(5) + 1; i++) {
            CustomerAuthor mockAgent = mockAgent(mockCustomer, null);
            agents.add(mockAgent);
        }
    }

    @Test
    public void testShowAgentList() throws Exception {
        String controllerUrl = BASE_URL + "/agentList";
        //不带搜索条件
        MvcResult result = mockMvc.perform(get(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentLevels"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("agentActiveEnums"))
                .andReturn();
        Page<Agent> page = (Page<Agent>) result.getModelAndView().getModel().get("page");
        List<Agent> agentList = (List<Agent>) page.getContent();
        Assert.assertEquals("huobanmall/agent/agentList", result.getModelAndView().getViewName());
        Assert.assertTrue(agents.size() == page.getTotalElements());
        for (int i = 0; i < Math.min(agents.size(), page.getSize()); i++) {
            Assert.assertEquals(agents.get(i).getId(), agentList.get(i).getId());
        }
        //根据代理商账号查询
        CustomerAuthor mallAgent = agents.get(0);
        Agent expectAgent = agents.get(0).getAgent();
        result = mockMvc.perform(get(controllerUrl).cookie(cookie)
                .param("agentLoginName", mallAgent.getUsername()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentLevels"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("agentActiveEnums"))
                .andReturn();
        page = (Page<Agent>) result.getModelAndView().getModel().get("page");
        Assert.assertEquals(1, page.getTotalElements());
        Assert.assertEquals(expectAgent.getUsername(), page.getContent().get(0).getUsername());

        result = mockMvc.perform(get(controllerUrl).cookie(cookie)
                .param("agentName", expectAgent.getName()))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentLevels"))
                .andExpect(model().attributeExists("page"))
                .andExpect(model().attributeExists("agentActiveEnums"))
                .andReturn();
        page = (Page<Agent>) result.getModelAndView().getModel().get("page");
        Assert.assertEquals(1, page.getTotalElements());
        Assert.assertEquals(expectAgent.getName(), page.getContent().get(0).getName());
        //......
    }

    @Test
    public void testDeleteAgent() throws Exception {
        String controllerUrl = BASE_URL + "/delete";
        MvcResult result = mockMvc.perform(post(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        String resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("没有传输数据", obResult.getString("msg"));
        //不存在的agentId
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("agentId", "-1"))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("系统请求失败", obResult.getString("msg"));
        Agent expectAgent = agents.get(0).getAgent();
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("agentId", String.valueOf(expectAgent.getId())))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请求成功", obResult.getString("msg"));
        expectAgent = agents.get(0).getAgent();
        CustomerAuthor newMallAgent = mockAgent(mockCustomer, expectAgent);
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("agentId", String.valueOf(expectAgent.getId())))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("代理商已被绑定", obResult.getString("msg"));
    }

    @Test
    public void testUpdateDisabledStatus() throws Exception {
        String controllerUrl = BASE_URL + "/updateStatus";
        MvcResult result = mockMvc.perform(post(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        String resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("没有传输数据", obResult.getString("msg"));
        Agent expectAgent = agents.get(0).getAgent();
        //冻结
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("status", "0")
                .param("agentId", String.valueOf(expectAgent.getId())))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请求成功", obResult.getString("msg"));
        //解冻
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("status", "1")
                .param("agentId", String.valueOf(expectAgent.getId())))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请求成功", obResult.getString("msg"));
        //不存在的agentId
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("status", "1")
                .param("agentId", "-1"))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("系统请求失败", obResult.getString("msg"));
    }

    @Test
    public void testShowAgent() throws Exception {
        String controllerUrl = BASE_URL + "/showAgent";
        Agent parentAgent = agents.get(0).getAgent();
        //显示增加页面
        MvcResult result = mockMvc.perform(get(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentLevels"))
                .andExpect(model().attributeExists("parentAgentLevelId"))
                .andExpect(model().attributeExists("agent"))
                .andReturn();
        List<AgentLevel> agentLevels = (List<AgentLevel>) result.getModelAndView().getModel().get("agentLevels");
        Integer parentAgentLevelId = (Integer) result.getModelAndView().getModel().get("parentAgentLevelId");
        Assert.assertEquals("huobanmall/agent/addAgent", result.getModelAndView().getViewName());
        Assert.assertEquals(agents.size(), agentLevels.size());
        Assert.assertTrue(parentAgentLevelId == -1);
        //显示修改页面
        AgentLevel agentLevel = parentAgent.getAgentLevel();
        CustomerAuthor agent = mockAgent(mockCustomer, parentAgent);
        result = mockMvc.perform(get(controllerUrl).cookie(cookie)
                .param("id", String.valueOf(agent.getId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentLevels"))
                .andExpect(model().attributeExists("parentAgentLevelId"))
                .andExpect(model().attributeExists("agent"))
                .andReturn();
        agentLevels = (List<AgentLevel>) result.getModelAndView().getModel().get("agentLevels");
        parentAgentLevelId = (Integer) result.getModelAndView().getModel().get("parentAgentLevelId");
        Assert.assertEquals("huobanmall/agent/addAgent", result.getModelAndView().getViewName());
        Assert.assertEquals(agents.size() + 1, agentLevels.size());
        Assert.assertTrue(parentAgentLevelId == agentLevel.getLevelId());
        //显示查看页面
        result = mockMvc.perform(get(controllerUrl).cookie(cookie)
                .param("ifShow", "true")
                .param("id", String.valueOf(agent.getId())))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentLevels"))
                .andExpect(model().attributeExists("parentAgentLevelId"))
                .andExpect(model().attributeExists("agent"))
                .andReturn();
        Assert.assertEquals("huobanmall/agent/showAgent", result.getModelAndView().getViewName());

    }

    @Test
    public void testGetParentAgents() throws Exception {
        String controllerUrl = BASE_URL + "/getParentAgents";
        AgentLevel agentLevel = mockAgentLevel(mockCustomer);
        agents.forEach(agent -> {
            agent.getAgent().setAgentLevel(agentLevel);
        });
        MvcResult result = mockMvc.perform(get(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        String resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请求成功", obResult.getString("msg"));
        JSONArray obData = obResult.getJSONArray("data");
        Assert.assertNull(obData);
        result = mockMvc.perform(get(controllerUrl).cookie(cookie)
                .param("parentAgentLevelId", String.valueOf(agentLevel.getLevelId())))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请求成功", obResult.getString("msg"));
        obData = obResult.getJSONArray("data");
        Assert.assertTrue(obData.size() == agents.size());
        for (int i = 0; i < agents.size(); i++) {
            Assert.assertEquals(agents.get(i).getId(), ((JSONObject) obData.get(i)).get("id"));
        }

    }

    @Test
    public void testAddOrSaveAgent() throws Exception {
        String controllerUrl = BASE_URL + "/save";
        //不传参
        MvcResult result = mockMvc.perform(post(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        String resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请输入用户名", obResult.getString("msg"));
        //传用户名
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("username", "Username")
                .param("id", "0"))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请输入密码", obResult.getString("msg"));
        AgentLevel agentLevel = mockAgentLevel(mockCustomer);

        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("username", "Username"))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请选择区域", obResult.getString("msg"));
        //......
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("username", "Username")
                .param("password", "Password")
                .param("provinceCode", "Province")
                .param("cityCode", "City")
                .param("districtCode", "District")
                .param("agentLevelId", String.valueOf(agentLevel.getLevelId()))
                .param("name", "Name")
                .param("contact", "Contact")
                .param("mobile", "Mobile")
                .param("email", "Email")
                .param("address", "Address")
                .param("id", "0"))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请求成功", obResult.getString("msg"));

        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("username", "Username")
                .param("provinceCode", "Province")
                .param("cityCode", "City")
                .param("districtCode", "District")
                .param("agentLevelId", String.valueOf(agentLevel.getLevelId()))
                .param("name", "Name")
                .param("contact", "Contact")
                .param("mobile", "Mobile")
                .param("email", "Email")
                .param("address", "Address")
                .param("customerId", String.valueOf(mockCustomer.getCustomerId())))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("没有传输数据", obResult.getString("msg"));

    }

    @Test
    public void testResetPassword() throws Exception {
        String controllerUrl = BASE_URL + "/reset";
        Agent expectAgent = agents.get(0).getAgent();
        MvcResult result = mockMvc.perform(post(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        String resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("系统请求失败", obResult.getString("msg"));
        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("agentId", String.valueOf(expectAgent.getId())))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请求成功", obResult.getString("msg"));

        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("password", "password")
                .param("agentId", "-1"))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("系统请求失败", obResult.getString("msg"));

        result = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("password", "password")
                .param("agentId", String.valueOf(expectAgent.getId())))
                .andExpect(status().isOk())
                .andReturn();
        resultString = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        obResult = JSONObject.parseObject(resultString);
        Assert.assertEquals("请求成功", obResult.getString("msg"));
    }


}
