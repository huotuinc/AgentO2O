/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.entity.order.MallOrderItem;
import com.huotu.agento2o.service.repository.order.MallOrderItemRepository;
import com.huotu.agento2o.service.service.order.MallOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by allan on 1/8/16.
 */
@Service
public class MallOrderItemServiceImpl implements MallOrderItemService {
    @Autowired
    private MallOrderItemRepository orderItemRepository;


    @Override
    public void updateShipStatus(int itemId, OrderEnum.ShipStatus shipStatus) {
        orderItemRepository.updateShipStatus(itemId, shipStatus);
    }

    @Override
    public List<MallOrderItem> findMallOrderItemByOrderId(String orderId) {
        return orderItemRepository.findByOrder_OrderId(orderId);
    }
}
