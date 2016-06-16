/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.log.impl;

import com.huotu.agento2o.service.entity.log.AgentLog;
import com.huotu.agento2o.service.repository.log.AgentLogRepository;
import com.huotu.agento2o.service.service.log.AgentLogService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Created by helloztt on 2016/6/16.
 */
@Service
public class AgentLogServiceImpl implements AgentLogService {
    @Autowired
    private AgentLogRepository logRepository;

    @Override
    @Transactional
    public void save(AgentLog log) {
        logRepository.save(log);
    }
}
