/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.service.product.impl;

import com.huotu.agento2o.agent.service.product.ProductScheduleService;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.product.SendEmailService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by helloztt on 2016/6/3.
 */
@Service
public class ProductScheduleServiceImpl implements ProductScheduleService {
    private static final Log log = LogFactory.getLog(ProductScheduleServiceImpl.class);
    @Autowired
    private AgentProductService agentProductService;
    @Autowired
    private SendEmailService sendEmailService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private ShopService shopService;


    @Override
    //每天早上10点获取库存预警信息
//    @Scheduled(cron = "0 0 10 * * ?")
    @Scheduled(cron = "0 0 * * * ?")//用于测试，每1小时检查库存
    public void productSchedule() {
        //查询出需要提醒的库存信息
        log.info("start check product store . . .");
        List<Object> agents = agentProductService.findNeedWarningAgent();
        List<Object> shops = agentProductService.findNeedWarningShop();
        for (int i = 0; i < agents.size(); i++) {
            Integer agentId = Integer.parseInt(agents.get(i).toString());
            Agent agent = agentService.findByAgentId(agentId);
            if(agent == null || StringUtil.isEmptyStr(agent.getEmail())){
                continue;
            }
            List<AgentProduct> agentProducts = agentProductService.findWarningAgentInfo(agentId);
            sendEmail(agentProducts,agent.getEmail());
        }
        for(int i = 0 ; i < shops.size() ; i++){
            Integer shopId = Integer.parseInt(shops.get(i).toString());
            Shop shop = shopService.findById(shopId);
            if(shop == null || StringUtil.isEmptyStr(shop.getEmail())){
                continue;
            }
            List<AgentProduct> agentProducts = agentProductService.findWarningShopInfo(shopId);
            sendEmail(agentProducts,shop.getEmail());
        }
        log.info("end check product store . . .");
    }

    private void sendEmail(List<AgentProduct> productList,String email){
        int num = 3;
        while (num > 0) {
            try {
                sendEmailService.sendCloudEmail(productList, email);
                break;
            } catch (Exception e) {
                log.error("发送邮件预警失败" + (4 - num), e);
                num--;
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e1) {
                }
            }
        }
    }
}
