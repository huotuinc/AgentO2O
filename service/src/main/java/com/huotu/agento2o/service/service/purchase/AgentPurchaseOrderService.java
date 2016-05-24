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
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import org.springframework.data.domain.Page;

/**
 * Created by helloztt on 2016/5/18.
 */
public interface AgentPurchaseOrderService {

    Page<AgentPurchaseOrder> findAll(PurchaseOrderSearcher purchaseOrderSearcher);

    ApiResult addPurchaseOrder(AgentPurchaseOrder purchaseOrder, Author author, String... shoppingCartIds) throws Exception;

    AgentPurchaseOrder findByPOrderId(String pOrderId);

    AgentPurchaseOrder findByPOrderIdAndAuthor(String pOrderId, Author author);

    /**
     * 代理商/门店 取消采购单
     *
     * @param pOrderId
     * @param author
     * @return
     * @throws Exception
     */
    ApiResult disableAgentPurchaseOrder(String pOrderId, Author author) throws Exception;

    /**
     * 代理商/门店 支付采购单
     *
     * @param pOrderId
     * @param author
     * @return
     */
    ApiResult payAgentPurchaseOrder(String pOrderId, Author author);

    /**
     * 代理商/门店 确认收货
     *
     * @param pOrderId
     * @param author
     * @return
     */
    ApiResult receiveAgentPurchaseOrder(String pOrderId, Author author)throws Exception;

    /**
     * 平台方/上级代理商 发货
     *
     * @param customerId
     * @param authorId
     * @param pOrderId
     * @return
     */
    ApiResult deliveryAgentPurchaseOrder(Integer customerId, Integer authorId, String pOrderId);

    /**
     * 平台方/上级代理商 审核采购单
     *
     * @param customerId
     * @param authorId
     * @param pOrderId
     * @param status
     * @param comment
     * @return
     */
    ApiResult checkPurchaseOrder(Integer customerId, Integer authorId, String pOrderId, PurchaseEnum.OrderStatus status, String comment) throws Exception;
}
