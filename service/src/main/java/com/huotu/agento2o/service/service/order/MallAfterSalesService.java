package com.huotu.agento2o.service.service.order;

import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.searchable.AfterSaleSearch;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by allan on 12/30/15.
 */
public interface MallAfterSalesService {
    /**
     * 分页按条件查询
     *
     * @param pageIndex
     * @param pageSize
     * @param afterSaleSearch
     * @return
     */
    Page<MallAfterSales> findAll(int pageIndex, int pageSize, Integer agentId, AfterSaleSearch afterSaleSearch);

    /**
     * 修改售后单状态
     *
     * @param afterSaleStatus
     * @param afterId
     */
    void updateStatus(AfterSaleEnum.AfterSaleStatus afterSaleStatus, String afterId);

    /**
     * 根据id得到售后单实体
     *
     * @param afterId
     * @return
     */
    MallAfterSales findByAfterId(String afterId);

    /**
     * 根据货品id得到售后单实体
     *
     * @param productId
     * @return
     */
    MallAfterSales findByProductId(int productId);

    /**
     * 得到某个订单的所有售后单
     *
     * @param orderId
     * @return
     */
    List<MallAfterSales> findByOrderId(String orderId);

    /**
     * 某订单是否有活动的售后单（是否需要退货）
     *
     * @param orderId
     * @param afterSaleStatus
     * @return
     */
    int countByOrderIdAndStatus(String orderId, AfterSaleEnum.AfterSaleStatus afterSaleStatus);

    void afterSaleAgree(
            MallAfterSales afterSales,
            String message,
            AfterSaleEnum.AfterSaleStatus afterSaleStatus,
            AfterSaleEnum.AfterItemsStatus afterItemsStatus
    );

    void afterSaleRefuse(MallAfterSales afterSales, String reason);

    int UnhandledCount(int agentId);
}
