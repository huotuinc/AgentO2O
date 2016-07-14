/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.huobanmall.level;

import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.config.Address;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.service.author.AgentService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import javax.servlet.http.Cookie;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;


/**
 * Created by WangJie on 2016/6/7.
 */
public class HbmAgentLevelControllerTest extends CommonTestBase {

    @Autowired
    private AgentService agentService;

    private static String BASE_URL = "/huobanmall/level";
    //平台
    private MallCustomer mockCustomer;

    //代理商的收货地址
    List<AgentLevel> agentLevels = new ArrayList<>();

    @Before
    public void init() {
        //初始化模拟数据
        mockCustomer = mockMallCustomer();

        for (int i = 0; i <= random.nextInt(5)+1; i++) {
            AgentLevel mockAgentLevel = mockAgentLevel(mockCustomer);
            agentLevels.add(mockAgentLevel);
        }
        //排序
        Collections.sort(agentLevels,(o1, o2) -> {
            return o1.getLevel().compareTo(o2.getLevel());
        });
    }

    @Test
    public void testShowLevelList() throws Exception{
        String controllerUrl = BASE_URL + "/levelList";
        Cookie cookie = new Cookie("UserID", String.valueOf(mockCustomer.getCustomerId()));
        MvcResult agentResult = mockMvc.perform(get(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("agentLevels"))
                .andReturn();
        List<AgentLevel> agentLevelList = (List<AgentLevel>) agentResult.getModelAndView().getModel().get("agentLevels");
        Assert.assertEquals("huobanmall/level/agentLevelList", agentResult.getModelAndView().getViewName());
        Assert.assertNotNull(agentLevelList);
        Assert.assertTrue(agentLevelList.size() == agentLevels.size());
        for (int i = 0; i < agentLevels.size(); i++) {
            Assert.assertEquals(agentLevels.get(i).getLevelId(), agentLevelList.get(i).getLevelId());
        }
    }

    @Test
    public void testsShowLevel() throws Exception{
        String controllerUrl = BASE_URL + "/-1";
        AgentLevel expectAgentLevel = agentLevels.get(0);
        Cookie cookie = new Cookie("UserID", String.valueOf(mockCustomer.getCustomerId()));
        MvcResult agentResult = mockMvc.perform(get(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        String result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obResult = JSONObject.parseObject(result);
        Assert.assertEquals("没有传输数据",obResult.getString("msg"));
        controllerUrl = BASE_URL + "/"+expectAgentLevel.getLevelId();
        agentResult = mockMvc.perform(get(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        JSONObject realAgentLevel = obResult.getJSONObject("data") ;
        Assert.assertEquals(expectAgentLevel.getLevelId(),realAgentLevel.get("levelId"));
        Assert.assertEquals("请求成功",obResult.getString("msg"));
    }

    @Test
    public void testDeleteLevel() throws Exception{
        String controllerUrl = BASE_URL + "/delete";
        AgentLevel expectAgentLevel = agentLevels.get(0);
        Cookie cookie = new Cookie("UserID", String.valueOf(mockCustomer.getCustomerId()));
        MvcResult agentResult = mockMvc.perform(post(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        String result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obResult = JSONObject.parseObject(result);
        Assert.assertEquals("没有传输数据",obResult.getString("msg"));
        agentResult = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("levelId",String.valueOf(expectAgentLevel.getLevelId())))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请求成功",obResult.getString("msg"));
        expectAgentLevel = agentLevels.get(1);
        MallCustomer mockAgent = mockAgent(mockCustomer,null);
        mockAgent.getAgent().setAgentLevel(expectAgentLevel);
        agentResult = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("levelId",String.valueOf(expectAgentLevel.getLevelId())))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("等级已被绑定",obResult.getString("msg"));

    }

    @Test
    public void testAddAndSaveLevel() throws Exception{
        String controllerUrl = BASE_URL + "/save";
        AgentLevel expectAgentLevel = agentLevels.get(0);
        Cookie cookie = new Cookie("UserID", String.valueOf(mockCustomer.getCustomerId()));
        //等级名为空
        MvcResult agentResult = mockMvc.perform(post(controllerUrl).cookie(cookie))
                .andExpect(status().isOk())
                .andReturn();
        String result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请输入等级名称",obResult.getString("msg"));
        //levelId为空
        agentResult = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("levelName","levelName"))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("没有传输数据",obResult.getString("msg"));
        //修改
        agentResult = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("levelId",String.valueOf(expectAgentLevel.getLevelId()))
                .param("levelName","levelName"))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请求成功",obResult.getString("msg"));

        //增加
        agentResult = mockMvc.perform(post(controllerUrl).cookie(cookie)
                .param("levelId","0")
                .param("levelName","levelName"))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请求成功",obResult.getString("msg"));
    }
}
