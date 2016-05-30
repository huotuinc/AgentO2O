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
import com.huotu.agento2o.service.entity.purchase.AgentDelivery;
import com.huotu.agento2o.service.model.order.DeliveryInfo;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import org.springframework.data.domain.Page;

/**
 * Created by helloztt on 2016/5/19.
 */
public interface AgentDeliveryService {

    ApiResult pushDelivery(DeliveryInfo deliveryInfo, Integer customerId, Integer agentId) throws Exception;

    Page<AgentDelivery> showPurchaseDeliveryList(DeliverySearcher deliverySearcher);

    Page<AgentDelivery> showReturnDeliveryList(DeliverySearcher deliverySearcher);
}
