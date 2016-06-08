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
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.service.author.ShopService;
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
    private ShopService shopService;
    @Autowired
    private SettlementService settlementService;

    @Override
    //每天早上10点获取库存预警信息
//    @Scheduled(cron = "0 0 10 * * ?")
    @Scheduled(cron = "0 */5 * * * ?")//用于测试，每隔6分钟结算一次
    public void settlementSchedule() {
        log.info("start settle . . .");
        Date now = new Date();
        List<Shop> shopList = shopService.findAll();
        if(shopList != null && shopList.size() > 0){
            shopList.forEach(shop->{
                try {
                    settlementService.settle(shop,now);
                } catch (Exception e) {
                    log.error("结算异常",e);
                }
            });
        }
        log.info("end settle . . .");
    }
}
