/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.settlement;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.settlement.Settlement;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.searchable.SettlementSearcher;
import org.springframework.data.domain.Page;

import java.util.Date;

/**
 * Created by helloztt on 2016/6/8.
 */
public interface SettlementService {

    /**
     * 得到结算单
     *
     * @param shop
     * @param settleTime
     * @return
     */
    void settle(ShopAuthor shop, Date settleTime) throws Exception;

    Settlement findById(Integer id);

    Settlement findBySettlementNo(String settlementNo);

    int unhandledSettlementCount(Integer shopId);

    Page<Settlement> getPage(SettlementSearcher settlementSearcher);

    Page<Settlement> getCustomerPage(Integer customerId, SettlementSearcher settlementSearcher);

    OrderDetailModel findOrderDetail(String orderId);

    Settlement save(Settlement settlement);

    ApiResult updateSettlementStatus(String settlementNo, int customerStatus, int supplierStatus, String settlementComment) throws Exception;
}
