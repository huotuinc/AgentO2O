/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.order;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.order.MallDelivery;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.model.order.DeliveryInfo;
import com.huotu.agento2o.service.model.order.LogiModel;
import com.huotu.agento2o.service.model.order.OrderForDelivery;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import org.springframework.data.domain.Page;
import org.springframework.transaction.annotation.Transactional;

import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Created by helloztt on 2016/3/25.
 */
public interface MallDeliveryService {
    List<MallDelivery> findByOrderId(String orderId);

    List<MallDelivery> findByAuthor(Author author);

    Page<MallDelivery> getPage(int pageIndex, Author author, int pageSize, DeliverySearcher deliverySearcher, String type);

    @Transactional(value = "transactionManager")
    MallDelivery deliver(Author author, MallOrder order, MallDelivery deliveryInfo, String sendBn);

    /**
     * 批量发货
     *
     * @param orderForDeliveries
     * @param customerId         门店所属平台方id
     * @return
     * @throws UnsupportedEncodingException
     */
    ApiResult pushBatchDelivery(List<OrderForDelivery> orderForDeliveries, int customerId) throws UnsupportedEncodingException;

    ApiResult pushDelivery(DeliveryInfo deliveryInfo, int shopId) throws UnsupportedEncodingException;

    ApiResult pushRefund(String orderId, LogiModel logiModel, Integer shopId, String dicReturnItemsStr) throws UnsupportedEncodingException;
}
