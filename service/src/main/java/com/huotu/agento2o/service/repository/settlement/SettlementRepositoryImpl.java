/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.repository.settlement;

import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.SettlementEnum;
import com.huotu.agento2o.service.entity.settlement.SettlementOrder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/6/8.
 */
public class SettlementRepositoryImpl implements SettlementRepositoryCustom {
    @Autowired
    protected JdbcTemplate jdbcTemplate;

    /**
     * V1版本，暂时不结算返利
     * @param shopId
     * @param customerId
     * @param settleTime 订单结束时间查询条件
     * @return
     */
    @Override
    public List<SettlementOrder> findSettlementOrder(Integer shopId, Integer customerId, Date settleTime) {
        String sql = "SELECT A.Order_Id,A.Createtime,A.Paytime,ISNULL(A.Cost_Freight,0)-ISNULL(SUM(C.settleFreight),0) AS Cost_Freight,ISNULL(A.Final_Amount,0)-ISNULL(SUM(C.RefundAmount),0) AS Final_Amount " +
                " FROM Mall_Orders A LEFT JOIN Mall_Order_Items B ON A.Order_Id = B.Order_Id LEFT JOIN Mall_AfterSales C ON C.Order_Id = B.Order_Id AND B.Product_Id = C.Product_Id" +
                " WHERE A.SettleStatus=? AND A.Pay_Status<>? AND A.Pre_Received_Time IS NOT NULL AND PreSettleDate<=? AND A.Customer_Id=? AND A.Agent_SHop_Id=? AND A.Agent_Shop_Type=?" +
                " GROUP BY A.Order_Id,A.Createtime,A.Paytime,A.Cost_Freight,A.Final_Amount";
        List<SettlementOrder> settlementOrders = jdbcTemplate.query(sql, ((rs, num) -> {
                    SettlementOrder settlementOrder = new SettlementOrder();
                    settlementOrder.setOrderId(rs.getString("Order_Id"));
                    settlementOrder.setOrderDateTime(rs.getTimestamp("Createtime"));
                    settlementOrder.setFreight(rs.getDouble("Cost_Freight"));
                    settlementOrder.setPayDateTime(rs.getTimestamp("Paytime"));
                    settlementOrder.setFinalAmount(rs.getDouble("Final_Amount"));
                    return settlementOrder;
                }), SettlementEnum.SettlementStatusEnum.READY_SETTLE.getCode(),
                OrderEnum.PayStatus.REFUNDING.getCode(),
                settleTime, customerId, shopId,1);
        return settlementOrders;
    }
}
