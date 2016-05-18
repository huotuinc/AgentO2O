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

import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by helloztt on 2016/5/13.
 */
public interface AgentProductService {

    /**
     * 查找上级代理商（除平台）商品
     * @param author
     * @return
     */
    List<AgentProduct> findByParentAgentProduct(Author author);

    /**
     * 查找指定代理商的库存信息
     * @param agentId
     * @return
     */
    Page<AgentProduct> findByAgentId(int pageIndex, int pageSize,Integer agentId);

    boolean updateWaring(Integer agentId,Integer productId,Integer warning);

    /**
     * 查找出需要发送库存警告的信息
     * @param pageIndex
     * @param pageSize
     * @return
     */
    //Page<AgentProduct> findByStoreLessThanWarning(int pageIndex, int pageSize);



}
