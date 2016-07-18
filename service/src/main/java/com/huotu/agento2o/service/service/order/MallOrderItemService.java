/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order;


import com.hot.datacenter.entity.order.OrderItem;
import com.hot.datacenter.ienum.OrderEnum;

import java.util.List;

/**
 * Created by AiWelv on 5/18/16.
 */
public interface MallOrderItemService {
    /**
     * 修改订单货品的发货状态
     *
     * @param itemId
     * @param shipStatus
     */
    void updateShipStatus(int itemId, OrderEnum.ShipStatus shipStatus);

    List<OrderItem> findMallOrderItemByOrderId(String orderId);

}
