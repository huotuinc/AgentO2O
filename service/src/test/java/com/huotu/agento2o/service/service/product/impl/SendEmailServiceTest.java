package com.huotu.agento2o.service.service.product.impl;

import com.huotu.agento2o.service.config.ServiceConfig;
import com.huotu.agento2o.service.service.product.SendEmailService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;

/**
 * Created by elvis on 2016/5/16.
 */
@ActiveProfiles("test")
@ContextConfiguration(classes = {ServiceConfig.class})
@RunWith(SpringJUnit4ClassRunner.class)
public class SendEmailServiceTest {


    @Autowired
    private SendEmailService sendEmailService;

    @Test
    public void testSendEmail(){
        sendEmailService.sendEmail();
    }


    @Test
    public void testAsync() throws InterruptedException {
        for(int i=1;i<1000;i++){
            sendEmailService.sayNumber(i);
        }

        Thread.sleep(10000);
    }




}
