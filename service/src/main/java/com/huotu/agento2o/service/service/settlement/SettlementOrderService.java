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

import com.huotu.agento2o.service.entity.settlement.Settlement;
import com.huotu.agento2o.service.entity.settlement.SettlementOrder;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by helloztt on 2016/6/8.
 */
public interface SettlementOrderService {
    SettlementOrder findById(Integer id);

    SettlementOrder findByOrderId(String orderId);

    List<SettlementOrder> findBySettlementId(Integer settlementId);

    Page<SettlementOrder> getPage(Settlement settlement, Integer pageNo, Integer pageSize);

    void save(SettlementOrder settlementOrder);
}
