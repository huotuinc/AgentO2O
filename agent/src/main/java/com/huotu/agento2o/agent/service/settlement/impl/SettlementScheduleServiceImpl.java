/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.service.settlement.impl;

import com.huotu.agento2o.agent.service.settlement.SettlementScheduleService;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.service.author.AgentShopService;
import com.huotu.agento2o.service.service.settlement.SettlementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/6/7.
 */
@Service
public class SettlementScheduleServiceImpl implements SettlementScheduleService {
    private static final Log log = LogFactory.getLog(SettlementScheduleServiceImpl.class);
    @Autowired
    private AgentShopService agentShopService;
    @Autowired
    private SettlementService settlementService;

    @Override
    //每日早上3点获取结算单
    @Scheduled(cron = "0 0 3 ? * *")
//    @Scheduled(cron = "0 */5 * * * ?")//用于测试，每0,5结尾分钟结算一次
    public void settlementSchedule() {
        log.info("start settle . . .");
        Date now = new Date();
        List<ShopAuthor> shopList = agentShopService.findAll();
        if (shopList != null && shopList.size() > 0) {
            shopList.forEach(shop -> {
                int num = 3;
                while (num > 0) {
                    try {
                        //有时会出现连接超时问题
                        settlementService.settle(shop, now);
                        break;
                    } catch (Exception e) {
                        log.error("结算异常 " + (4 - num), e);
                        //结算异常时等待 500 ms
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e1) {
                        }
                        num--;
                    }
                }
            });
        }
        log.info("end settle . . .");
    }
}
