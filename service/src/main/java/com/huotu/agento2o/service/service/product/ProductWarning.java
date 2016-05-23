package com.huotu.agento2o.service.service.product;

import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by elvis on 2016/5/16.
 */
@Service
public class ProductWarning {

    @Autowired
    private AgentProductService agentProductService;

    @Autowired
    private SendEmailService sendEmailService;

    List<String> getWaringAgent(){


        return null;
    }

    /**
     * 每个一小时进行一次查询提醒
     */
 /*   @Scheduled(cron = "0 *//*2 * * * ?")
    public void productWarningService() {

        //查询出需要提醒的库存信息

        for(int i=1;i<300;i++){
            System.out.println("put in executor"+i);
            try {
                sendEmailService.sayNumber(i);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }

    }*/







}
