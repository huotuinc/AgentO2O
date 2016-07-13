package com.huotu.agento2o.agent.controller.author;

import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.common.CommonTestBase;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.RoleTypeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.service.author.ShopService;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpSession;
import org.springframework.test.web.servlet.MvcResult;

import java.util.UUID;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Created by admin on 2016/5/26.
 */
public class ShopControllerTest extends CommonTestBase {
    private static String BASE_URL = "/shop";

    private MallCustomer mockCustomer;
    private Agent mockAgent;
    private Shop mockShop;

    @Autowired
    private ShopService shopService;

    @Before
    @SuppressWarnings("Duplicates")
    public void init() {
        mockCustomer = mockMallCustomer();
        mockAgent = mockAgent(mockCustomer, null);
        mockShop = mockShop(mockCustomer, mockAgent);
    }

    @Test
    public void testAddShopPage() throws Exception {
        MockHttpSession sessionAgent = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(get(BASE_URL + "/addShopPage").session(sessionAgent)).andExpect(status().isOk()).andReturn();
        Assert.assertTrue(result.getModelAndView().getViewName().equals("shop/addShop"));
    }

    @Test
    public void testSaveShop() throws Exception {
        MockHttpSession sessionAgent = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Shop shop = new Shop();
        shop.setUsername(userName);
        shop.setPassword(password);
        shop.setDeleted(false);
        shop.setDisabled(true);
        shop.setStatus(AgentStatusEnum.CHECKED);
        MvcResult result = mockMvc.perform(get(BASE_URL + "/addShop").session(sessionAgent)
                .param("username", userName)
                .param("password", password)
        ).andExpect(status().isOk()).andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
    }

    @Test
    public void testBaseConfig() throws Exception {
        MockHttpSession sessionShop = loginAs(mockShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        MvcResult result = mockMvc.perform(get(BASE_URL + "/baseConfig").session(sessionShop)).andExpect(status().isOk()).andReturn();
        Assert.assertTrue(result.getModelAndView().getViewName().equals("shop/BaseConfigShop"));
    }

    @Test
    public void testUpdateShop() throws Exception {
        MockHttpSession sessionShop = loginAs(mockShop.getUsername(), passWord, String.valueOf(RoleTypeEnum.SHOP.getCode()));
        mockShop.setAfterSalQQ("12345678911");
        mockShop.setComment("更新");
        MvcResult result = mockMvc.perform(post(BASE_URL + "/updateShop").session(sessionShop)
                .param("id", mockShop.getId().toString())).andExpect(status().isOk()).andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
    }

    @Test
    public void testShowShopList() throws Exception {
        MockHttpSession sessionAgent = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(get(BASE_URL + "/shopList").session(sessionAgent)).andExpect(status().isOk()).andReturn();
        Assert.assertTrue(result.getModelAndView().getViewName().equals("shop/shopList"));
    }

    @Test
    public void testChangeStatus() throws Exception {
        MockHttpSession sessionAgent = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));

        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Shop shop = new Shop();
        shop.setParentAuthor(mockAgent);
        shop.setUsername(userName);
        shop.setPassword(password);
        shop.setDeleted(false);
        shop.setDisabled(false);
        shop = shopService.addShop(shop);

        MvcResult result = mockMvc.perform(get(BASE_URL + "/changeStatus").session(sessionAgent).param("id", shop.getId().toString())).andExpect(status().isOk()).andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
    }

    @Test
    public void testDeleteById() throws Exception {
        MockHttpSession sessionAgent = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Shop shop = new Shop();
        shop.setParentAuthor(mockAgent);
        shop.setUsername(userName);
        shop.setPassword(password);
        shop.setDeleted(false);
        shop.setDisabled(false);
        shop = shopService.addShop(shop);

        MvcResult result = mockMvc.perform(get(BASE_URL + "/delete").session(sessionAgent).param("id", shop.getId().toString())).andExpect(status().isOk()).andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
    }

    @Test
    public void testResetPassword() throws Exception {
        MockHttpSession sessionAgent = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        String userName = UUID.randomUUID().toString();
        String password = UUID.randomUUID().toString();
        Shop shop = new Shop();
        shop.setParentAuthor(mockAgent);
        shop.setUsername(userName);
        shop.setPassword(password);
        shop.setDeleted(false);
        shop.setDisabled(false);
        shop = shopService.addShop(shop);

        MvcResult result = mockMvc.perform(get(BASE_URL + "/resetpassword").session(sessionAgent).param("id", shop.getId().toString())
                .param("password", UUID.randomUUID().toString())).andExpect(status().isOk()).andReturn();
        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);

        Assert.assertEquals("200", obj.getString("code"));
    }

    @Test
    public void testGetUserNames() throws Exception {
        MockHttpSession sessionAgent = loginAs(mockAgent.getUsername(), passWord, String.valueOf(RoleTypeEnum.AGENT.getCode()));
        MvcResult result = mockMvc.perform(post(BASE_URL + "/getUserNames").session(sessionAgent).param("hotUserName", "a")).andExpect(status().isOk()).andReturn();

        String content = new String(result.getResponse().getContentAsByteArray(), "UTF-8");
        JSONObject obj = JSONObject.parseObject(content);
        Assert.assertEquals("200", obj.getString("code"));
    }
}
