/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.purchase;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;

import java.util.List;

/**
 * Created by helloztt on 2016/5/13.
 */
public interface AgentProductService {

    List<AgentProduct> findByAgentId(Author author);

    ApiResult updateWarning(Author author, Integer agentProductId, Integer warning);

    /**
     * 查找出需要发送库存警告的信息代理商
     *
     * @return
     */

    List<Object> findNeedWarningAgent();
    List<Object> findNeedWarningShop();

    List<AgentProduct> findWarningAgentInfo(Integer agentId);
    List<AgentProduct> findWarningShopInfo(Integer shopId);

    AgentProduct findAgentProduct(Agent agent,MallProduct product);
    AgentProduct findAgentProduct(Author author, MallProduct product);

    AgentProduct findByAgentProductId(Integer agentProductId);


}
