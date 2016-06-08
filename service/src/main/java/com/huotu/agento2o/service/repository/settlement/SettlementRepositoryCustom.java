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

import com.huotu.agento2o.service.common.SettlementEnum;
import com.huotu.agento2o.service.entity.settlement.SettlementOrder;

import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/6/8.
 */
public interface SettlementRepositoryCustom {
    /**
     * 得到所有T+1进入结算的订单列表
     *
     * @param shopId
     * @param customerId
     * @param settleTime 订单结束时间查询条件
     * @return
     */
    List<SettlementOrder> findSettlementOrder(Integer shopId, Integer customerId, Date settleTime);
}
