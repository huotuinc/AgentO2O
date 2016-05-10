/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.statistics.impl;

import com.huotu.agento2o.service.model.statistics.IndexStatistics;
import com.huotu.agento2o.service.service.statistics.IndexStatisticsService;
import org.springframework.stereotype.Service;

/**
 * Created by helloztt on 2016/5/10.
 */
@Service
public class IndexStatisticsServiceImpl implements IndexStatisticsService {
    public IndexStatistics orderStatistics(Integer authorId) {
        IndexStatistics indexStatistics = new IndexStatistics();
        // TODO: 2016/5/10 增加业务数据 
        return indexStatistics;
    }
}
