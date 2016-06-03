package com.huotu.agento2o.service.service.product;

import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

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

    /**
     * 废弃
     * 每个一小时进行一次查询提醒
     */
//    @Scheduled(cron = "0 */5 * * * ?")
    public void productWarningService() {

        //查询出需要提醒的库存信息
        List<Object> agents = agentProductService.findNeedWaringAgent();
        for(int i=0;i<agents.size();i++){
            Integer agentId = Integer.parseInt(agents.get(i).toString());
            List<AgentProduct> agentProducts = agentProductService.findWaringAgentInfo(agentId);
            sendEmailService.sendEmail(agentProducts);
        }
    }
}
