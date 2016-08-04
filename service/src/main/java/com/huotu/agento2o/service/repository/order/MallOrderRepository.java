package com.huotu.agento2o.service.repository.order;


import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.SettlementEnum;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Created by allan on 12/31/15.
 */
@Repository
public interface MallOrderRepository extends JpaRepository<MallOrder, String>, JpaSpecificationExecutor<MallOrder> {
    @Query("update MallOrder set payStatus=?1 where orderId=?2")
    @Modifying
    void updatePayStatus(OrderEnum.PayStatus payStatus, String orderId);

    //    @Query("update MallOrder  set shipStatus=?1 where orderId=?2")
//    @Modifying
//    void updateShipStatus(OrderEnum.ShipStatus shipStatus, String orderId);
//
//    int countByAgentIdAndCreateTimeBetween(int customerId, Date start, Date end);
//
//    int countByAgentIdAndPayStatusAndShipStatusAndCreateTimeBetween(int customerId, OrderEnum.PayStatus payStatus, OrderEnum.ShipStatus shipStatus, Date start, Date end);
//
    MallOrder findByShopAndOrderId(Shop shop, String orderId);

    /**
     * 按日期统计由门店发货或受益方为门店的订单数
     *
     * @param shopId
     * @param agentShopTypeList
     * @param start
     * @param end
     * @return
     */
    @Query("SELECT COUNT(a) FROM MallOrder a WHERE a.shop.id = ?1 and a.agentShopType in ?2 and a.createTime between ?3 and ?4")
    int countByShop_IdAndAgentShopTypeInAndShop_IdAndCreateTimeBetween(Integer shopId, List<OrderEnum.ShipMode> agentShopTypeList, Date start, Date end);

    /**
     * 按日期统计由下级门店发货或受益方为下级门店的订单数
     *
     * @param agentId
     * @param agentShopTypeList
     * @param start
     * @param end
     * @return
     */
    @Query("SELECT COUNT(a) FROM MallOrder a WHERE (a.shop.agent.id = ?1 and a.agentShopType in ?2) and a.createTime between ?3 and ?4")
    int countByShop_ParentAuthor_IdAndAgentShopTypeInAndCreateTimeBetween(Integer agentId, List<OrderEnum.ShipMode> agentShopTypeList, Date start, Date end);

    /**
     * 按日期统计门店待发货订单数
     * @param shopId
     * @param payStatus
     * @param shipStatus
     * @return
     */
    int countByShop_IdAndPayStatusAndShipStatus(Integer shopId, OrderEnum.PayStatus payStatus, OrderEnum.ShipStatus shipStatus);

    /**
     * 将结算状态改为已结算，每次获取结算单时调用，下次获取结算单就不会再获取
     * @param shopId
     * @param customerId
     * @param settleTime
     */
    @Query("UPDATE MallOrder SET settleStatus=2 , actualSettleDate=?3 " +
            "WHERE settleStatus=1 AND payStatus <> ?4 " +
            "AND preSettleDate<=?3 AND customerId=?2 AND shop.id=?1")
    @Modifying(clearAutomatically = true)
    void updateSettle(Integer shopId, Integer customerId, Date settleTime,OrderEnum.PayStatus payStatus);


    /**
     * 修改结算单状态，分销商或供应商审核拒绝时调用
     * @param authorId
     * @param customerId
     * @param settleTime
     * @param settleStatus
     */
    @Query("UPDATE MallOrder SET settleStatus=?4 " +
            "WHERE settleStatus=2 AND actualSettleDate=?3 AND customerId=?2 AND shop.id=?1")
    @Modifying(clearAutomatically = true)
    void resetSettle(Integer authorId, Integer customerId, Date settleTime, Integer settleStatus);
}
