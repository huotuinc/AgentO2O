/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by helloztt on 2016/5/13.
 */
@Service
public class AgentProductServiceImpl implements AgentProductService {
    @Override
    public List<AgentProduct> findByParentAgentProduct(Author author) {
        return null;
    }
}
