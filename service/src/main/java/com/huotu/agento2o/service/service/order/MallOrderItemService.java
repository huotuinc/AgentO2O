package com.huotu.agento2o.service.service.order;

import com.huotu.agento2o.service.common.OrderEnum;

/**
 * Created by AiWelv on 2016/5/11.
 */
public interface MallOrderItemService {
    /**
     * 修改订单货品的发货状态
     *
     * @param itemId
     * @param shipStatus
     */
    void updateShipStatus(int itemId, OrderEnum.ShipStatus shipStatus);
}
