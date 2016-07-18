package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.service.purchase.AgentReturnOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
@Service
public class AgentReturnOrderItemServiceImpl implements AgentReturnOrderItemService {

    @Autowired
    private AgentReturnOrderItemRepository agentReturnOrderItemRepository;

    @Autowired
    private AgentReturnOrderRepository agentReturnOrderRepository;

    @Autowired
    private MallProductRepository mallProductRepository;

    @Autowired
    private AgentProductRepository agentProductRepository;

    @Override
    public List<AgentReturnedOrderItem> addReturnOrderItemList(List<AgentReturnedOrderItem> agentReturnedOrderItems) {
        return agentReturnOrderItemRepository.save(agentReturnedOrderItems);
    }

    @Override
    public List<AgentReturnedOrderItem> findAll(String rOrderId) {
        return agentReturnOrderItemRepository.findByReturnedOrder_rOrderId(rOrderId);
    }
}
