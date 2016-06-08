/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.settlement.impl;

import com.huotu.agento2o.service.entity.settlement.Settlement;
import com.huotu.agento2o.service.entity.settlement.SettlementOrder;
import com.huotu.agento2o.service.repository.settlement.SettlementOrderRepository;
import com.huotu.agento2o.service.service.settlement.SettlementOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by helloztt on 2016/6/8.
 */
@Service
public class SettlementOrderServiceImpl implements SettlementOrderService {

    @Autowired
    private SettlementOrderRepository settlementOrderRepository;

    @Override
    public SettlementOrder findById(Integer id) {
        return settlementOrderRepository.findOne(id);
    }

    @Override
    public SettlementOrder findByOrderId(String orderId) {
        return null;
    }

    @Override
    public List<SettlementOrder> findBySettlementId(Integer settlementId) {
        return settlementOrderRepository.findBySettlement_Id(settlementId);
    }

    @Override
    public Page<SettlementOrder> getPage(Settlement settlement, Integer pageNo, Integer pageSize) {
        Specification<SettlementOrder> specification =
                (root, query, cb) -> cb.and(cb.equal(root.get("settlement").as(Settlement.class),settlement));
        return settlementOrderRepository.findAll(specification,new PageRequest(pageNo-1, pageSize,new Sort(Sort.Direction.DESC,"payDateTime")));
    }

    @Override
    public void save(SettlementOrder settlementOrder) {
        settlementOrderRepository.save(settlementOrder);
    }
}
