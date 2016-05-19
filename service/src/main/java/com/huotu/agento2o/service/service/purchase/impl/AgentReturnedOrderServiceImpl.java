package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
@Service
public class AgentReturnedOrderServiceImpl implements AgentReturnedOrderService {

    @Autowired
    private AgentProductRepository agentProductRepository;

    @Autowired
    private AgentReturnOrderRepository agentReturnOrderRepository;

    @Override
    public List<AgentProduct> findAgentProductsByAgentId(Integer agentId) {
        return agentProductRepository.findAgentProductByAgent_Id(agentId);
    }

    @Override
    public AgentReturnedOrder addReturnOrder(AgentReturnedOrder agentReturnedOrder) {
        return agentReturnOrderRepository.save(agentReturnedOrder);
    }
}
