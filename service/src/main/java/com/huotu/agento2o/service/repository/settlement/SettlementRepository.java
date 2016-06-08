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
import com.huotu.agento2o.service.entity.settlement.Settlement;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

/**
 * Created by helloztt on 2016/6/8.
 */
@Repository
public interface SettlementRepository extends JpaRepository<Settlement, Integer>,JpaSpecificationExecutor,SettlementRepositoryCustom {
    Settlement findBySettlementNo(String settlementNo);

    int countByShop_IdAndAuthorStatusAndCustomerStatusNot(Integer shopId, SettlementEnum.SettlementCheckStatus authorStatus, SettlementEnum.SettlementCheckStatus customerStatus);
}
