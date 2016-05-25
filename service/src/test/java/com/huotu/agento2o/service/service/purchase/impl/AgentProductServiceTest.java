package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.service.product.SendEmailService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

import java.util.List;


/**
 * Created by admin on 2016/5/23.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = {ServiceConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class AgentProductServiceTest {

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
    public void testGetWaringAgentInfo(){

        List<Object> agents = agentProductService.findNeedWaringAgent();
        for(int i=0;i<agents.size();i++){
            List<AgentProduct> info = agentProductService.findWaringAgentInfo(Integer.parseInt(agents.get(i).toString()));
            sendEmailService.sendEmail(info);
        }
    }
}
