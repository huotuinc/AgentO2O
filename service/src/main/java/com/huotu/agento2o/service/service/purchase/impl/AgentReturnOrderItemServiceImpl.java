package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.service.purchase.AgentReturnOrderItemService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
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
    public List<AgentReturnedOrderItem> addReturnOrderItemList(AgentReturnedOrder agentReturnedOrder,Integer[]productIds,Integer[] productNums) {

        boolean flag = true;
        List<AgentReturnedOrderItem> agentReturnedOrderItems = new ArrayList<>();
        List<AgentReturnedOrderItem> savedAgentReturnedOrderItems = null;

        if(agentReturnedOrder != null){
            for(int i=0;i<productIds.length;i++){
                AgentProduct agentProduct = agentProductRepository.findByProduct_productId(productIds[i]);
                if(productNums[i] > (agentProduct.getStore()-agentProduct.getFreez()) ){
                    flag = false;
                    break;
                }

                AgentReturnedOrderItem agentReturnedOrderItem = new AgentReturnedOrderItem();
                agentReturnedOrderItem.setReturnedOrder(agentReturnedOrder);
                MallProduct mallProduct = mallProductRepository.findOne(productIds[i]);
                if(mallProduct != null){
                    agentReturnedOrderItem.setProduct(mallProduct);
                    agentReturnedOrderItem.setBn(mallProduct.getBn());
                    agentReturnedOrderItem.setName(mallProduct.getName());
                    agentReturnedOrderItem.setPrice(mallProduct.getPrice());
                    agentReturnedOrderItem.setNum(productNums[i]);
                    agentReturnedOrderItem.setPrice(5*mallProduct.getPrice());
                    agentReturnedOrderItem.setThumbnailPic("fixme");// FIXME: 2016/5/18
                    agentReturnedOrderItem.setPdtDesc(mallProduct.getStandard());
                    agentReturnedOrderItem.setUnit(mallProduct.getUnit());
                }
                agentReturnedOrderItems.add(agentReturnedOrderItem);
            }
        }

        if(flag){
            savedAgentReturnedOrderItems = agentReturnOrderItemRepository.save(agentReturnedOrderItems);
            for(int i=0;i<productIds.length;i++){
                AgentProduct agentProduct = agentProductRepository.findByProduct_productId(productIds[i]);
                agentProduct.setFreez(agentProduct.getFreez()+productNums[i]);
                agentProductRepository.save(agentProduct);
            }

        }else{
            agentReturnedOrder.setDisabled(true);
            agentReturnOrderRepository.save(agentReturnedOrder);
        }
        return savedAgentReturnedOrderItems;
    }
}
