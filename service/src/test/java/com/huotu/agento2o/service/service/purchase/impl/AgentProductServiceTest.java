package com.huotu.agento2o.service.service.purchase.impl;


import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.service.product.SendEmailService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.web.context.WebApplicationContext;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Created by admin on 2016/5/23.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = {ServiceConfig.class})
@WebAppConfiguration
@RunWith(SpringJUnit4ClassRunner.class)
public class AgentProductServiceTest {


    @Autowired
    private WebApplicationContext wac;
    private MockMvc mockMvc;
    @Before
    public void setup() {
        this.mockMvc = MockMvcBuilders.webAppContextSetup(this.wac).build();
    }
    @Test
    public void getAccount() throws Exception {
        this.mockMvc.perform(get("").accept(MediaType.parseMediaType("application/ json;charset=UTF-8")))
                .andExpect(status().isOk())
                .andExpect(content().contentType("application/json"))
                .andExpect(jsonPath("").value(""));
    }




    @Autowired
    private AgentProductService agentProductService;

    @Autowired
    private SendEmailService sendEmailService;

    @Test
    public void testGetNeedWaring(){
        List<Object> agents = agentProductService.findNeedWaringAgent();
       /* for(int i=0;i<agents.size();i++){
            System.out.println(agents.get(i).toString());
        }*/
    }

    @Test
    public void testGetWaringAgentInfo() throws InterruptedException {

        /*List<Object> agents = agentProductService.findNeedWaringAgent();
        for(int i=0;i<agents.size();i++){
            List<AgentProduct> info = agentProductService.findWaringAgentInfo(Integer.parseInt(agents.get(i).toString()));
            sendEmailService.sendEmail(info);
        }

        Thread.sleep(5000);*/

    }
}
