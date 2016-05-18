/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.repository.order;

import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.entity.order.MallOrderItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by allan on 1/8/16.
 */
@Repository
public interface MallOrderItemRepository extends JpaRepository<MallOrderItem, Long> {

    @Query("update MallOrderItem set shipStatus=?2 where itemId=?1")
    @Modifying
    void updateShipStatus(int itemId, OrderEnum.ShipStatus shipStatus);

    List<MallOrderItem> findByOrder_OrderId(String orderId);
}
