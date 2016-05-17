package com.huotu.agento2o.service.service.product;

import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by elvis on 2016/5/16.
 */
@Service
public class ProductWarning {

    @Autowired
    private AgentProductService agentProductService;

    /**
     * 每个一小时进行一次查询提醒
     */
    @Scheduled(cron = "*/1 * * * * ?")
    @Transactional
    public void productWarningService() {

        //查询出需要提醒的库存信息




        //调用邮件服务邮件提醒
        // System.out.println("hello");

    }



}
