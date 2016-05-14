package com.huotu.agento2o.service.repository.order;

import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by allan on 12/30/15.
 */
@Repository
public interface MallAfterSalesRepository extends JpaRepository<MallAfterSales, String>, JpaSpecificationExecutor {

    @Query("update MallAfterSales afterSales set afterSales.afterSaleStatus=?1 where afterSales.afterId=?2")
    @Modifying
    void updateStatus(AfterSaleEnum.AfterSaleStatus afterSaleStatus, String afterId);

    MallAfterSales findByProductId(int productId);

    List<MallAfterSales> findByOrderId(String orderId);

    int countByOrderIdAndAfterSaleStatusNot(String orderId, AfterSaleEnum.AfterSaleStatus afterSaleStatus);

    int countByOrderIdAndAfterSaleStatus(String orderId, AfterSaleEnum.AfterSaleStatus afterSaleStatus);

//    @Query("select count(afterSale) from MallAfterSales afterSale where afterSale.agentId=?1 and afterSale.afterSaleStatus not in ?2")
//    int countByAgentIdAndAfterSaleStatusNotIn(int agentId, List<AfterSaleEnum.AfterSaleStatus> afterSaleStatuses);
}
