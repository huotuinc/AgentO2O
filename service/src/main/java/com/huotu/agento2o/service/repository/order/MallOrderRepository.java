package com.huotu.agento2o.service.repository.order;


import com.huotu.agento2o.common.ienum.OrderEnum;
import com.huotu.agento2o.service.entity.order.MallOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;

/**
 * Created by allan on 12/31/15.
 */
@Repository
public interface MallOrderRepository extends JpaRepository<MallOrder, String>, JpaSpecificationExecutor<MallOrder> {
//    @Query("update MallOrder set payStatus=?1 where orderId=?2")
//    @Modifying
//    void updatePayStatus(OrderEnum.PayStatus payStatus, String orderId);
//
//    @Query("update MallOrder  set shipStatus=?1 where orderId=?2")
//    @Modifying
//    void updateShipStatus(OrderEnum.ShipStatus shipStatus, String orderId);
//
//    int countByAgentIdAndCreateTimeBetween(int customerId, Date start, Date end);
//
//    int countByAgentIdAndPayStatusAndShipStatusAndCreateTimeBetween(int customerId, OrderEnum.PayStatus payStatus, OrderEnum.ShipStatus shipStatus, Date start, Date end);
//
//    MallOrder findByAgentIdAndOrderId(Integer agentId, String orderId);
}
