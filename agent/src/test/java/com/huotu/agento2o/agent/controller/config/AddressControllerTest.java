/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.controller.config;

import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.config.Address;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.util.ArrayList;
import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.model;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by WangJie on 2016/6/3.
 */
public class AddressControllerTest extends CommonTestBase {

    private static String BASE_URL = "/config";
    //代理商的收货地址
    List<Address> agentAddress = new ArrayList<>();
    //门店的收货地址
    List<Address> shopAddress = new ArrayList<>();
    //平台
    private CustomerAuthor mockCustomer;
    //代理商
    private CustomerAuthor mockAgent;
    //门店
    private ShopAuthor mockShop;

    @Before
    public void init() {
        //初始化模拟数据
        mockCustomer = mockMallCustomer();
        mockAgent = mockAgent(mockCustomer, null);
        mockShop = mockShop(mockCustomer, mockAgent.getAgent());
        for (int i = 0; i <= random.nextInt(5); i++) {
            Address mockAddress = mockAddress(mockAgent);
            agentAddress.add(mockAddress);
        }
        for (int i = 0; i <= random.nextInt(5); i++) {
            Address mockAddress = mockAddress(mockShop);
            shopAddress.add(mockAddress);
        }
    }

    @Test
    public void testShowAddressList() throws Exception {
        String controllerUrl = BASE_URL + "/addressList";
        //代理商登录
        MockHttpSession agentSession = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult agentResult = mockMvc.perform(get(controllerUrl).session(agentSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("addressList"))
                .andReturn();
        List<Address> addressList = (List<Address>) agentResult.getModelAndView().getModel().get("addressList");
        Assert.assertEquals("config/addressList", agentResult.getModelAndView().getViewName());
        Assert.assertNotNull(addressList);
        Assert.assertTrue(addressList.size() == agentAddress.size());
        for (int i = 0; i < agentAddress.size(); i++) {
            Assert.assertEquals(agentAddress.get(i).getId(), addressList.get(i).getId());
        }
        //门店登录
        MockHttpSession shopSession = loginAs(mockShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult shopResult = mockMvc.perform(get(controllerUrl).session(shopSession))
                .andExpect(status().isOk())
                .andExpect(model().attributeExists("addressList"))
                .andReturn();
        addressList = (List<Address>) shopResult.getModelAndView().getModel().get("addressList");
        Assert.assertEquals("config/addressList", shopResult.getModelAndView().getViewName());
        Assert.assertNotNull(addressList);
        Assert.assertTrue(addressList.size() == shopAddress.size());
        for (int i = 0; i < shopAddress.size(); i++) {
            Assert.assertEquals(shopAddress.get(i).getId(), addressList.get(i).getId());
        }
    }
    @Test
    public void testShowAddress() throws Exception {
        String controllerUrl = BASE_URL + "/showAddress";
        Address expectAddress = agentAddress.get(0);
        MockHttpSession session = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult agentResult = mockMvc.perform(post(controllerUrl).session(session))
                .andExpect(status().isOk())
                .andReturn();
        String result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obResult = JSONObject.parseObject(result);
        Assert.assertEquals("没有传输数据",obResult.getString("msg"));
        agentResult = mockMvc.perform(post(controllerUrl).session(session)
                .param("addressId",String.valueOf(expectAddress.getId())))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        JSONObject realAddress = obResult.getJSONObject("data") ;
        Assert.assertEquals(expectAddress.getId(),realAddress.get("id"));
        Assert.assertEquals("请求成功",obResult.getString("msg"));
    }
    @Test
    public void testConfigDefault() throws Exception {
        String controllerUrl = BASE_URL + "/configDefault";
        Address expectAddress = agentAddress.get(0);
        MockHttpSession session = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult agentResult = mockMvc.perform(post(controllerUrl).session(session))
                .andExpect(status().isOk())
                .andReturn();
        String result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obResult = JSONObject.parseObject(result);
        Assert.assertEquals("没有传输数据",obResult.getString("msg"));
        agentResult = mockMvc.perform(post(controllerUrl).session(session)
                .param("addressId",String.valueOf(expectAddress.getId())))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请求成功",obResult.getString("msg"));

    }

    @Test
    public void testDeleteAddress() throws Exception {
        String controllerUrl = BASE_URL + "/deleteAddress";
        Address expectAddress = agentAddress.get(0);
        MockHttpSession session = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult agentResult = mockMvc.perform(post(controllerUrl).session(session))
                .andExpect(status().isOk())
                .andReturn();
        String result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obResult = JSONObject.parseObject(result);
        Assert.assertEquals("没有传输数据",obResult.getString("msg"));
        agentResult = mockMvc.perform(post(controllerUrl).session(session)
                .param("addressId",String.valueOf(expectAddress.getId())))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请求成功",obResult.getString("msg"));
        agentResult = mockMvc.perform(post(controllerUrl).session(session)
                .param("addressId","-1"))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("没有传输数据",obResult.getString("msg"));
    }

    @Test
    public void testAddOrSaveAddress() throws Exception {
        String controllerUrl = BASE_URL + "/saveAddress";
        Address expectAddress = agentAddress.get(0);
        MockHttpSession session = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult agentResult = mockMvc.perform(post(controllerUrl).session(session))
                .andExpect(status().isOk())
                .andReturn();
        String result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        JSONObject obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请输入收货人",obResult.getString("msg"));
        //电话为空时
        agentResult = mockMvc.perform(post(controllerUrl).session(session)
                .param("receiver","receiver")
                .param("province","Province")
                .param("city","city")
                .param("district","district")
                .param("address","address"))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请输入联系电话",obResult.getString("msg"));

        //增加
        agentResult = mockMvc.perform(post(controllerUrl).session(session)
                .param("id","0")
                .param("receiver","receiver")
                .param("telephone","Telephone")
                .param("province","Province")
                .param("city","city")
                .param("district","district")
                .param("address","address"))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请求成功",obResult.getString("msg"));

        //修改
        agentResult = mockMvc.perform(post(controllerUrl).session(session)
                .param("id",String.valueOf(expectAddress.getId()))
                .param("receiver","receiver")
                .param("telephone","Telephone")
                .param("province","Province")
                .param("city","city")
                .param("district","district")
                .param("address","address"))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("请求成功",obResult.getString("msg"));

        //id为空
        agentResult = mockMvc.perform(post(controllerUrl).session(session)
                .param("receiver","receiver")
                .param("telephone","Telephone")
                .param("province","Province")
                .param("city","city")
                .param("district","district")
                .param("address","address"))
                .andExpect(status().isOk())
                .andReturn();
        result = new String(agentResult.getResponse().getContentAsByteArray(),"UTF-8");
        obResult = JSONObject.parseObject(result);
        Assert.assertEquals("没有传输数据",obResult.getString("msg"));
    }

}
