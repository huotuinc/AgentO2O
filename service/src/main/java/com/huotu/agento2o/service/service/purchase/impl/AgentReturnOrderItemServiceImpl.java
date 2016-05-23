package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.service.entity.author.Author;
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
    public List<AgentReturnedOrderItem> addReturnOrderItemList(Author author, AgentReturnedOrder agentReturnedOrder, Integer[]productIds, Integer[] productNums) {

        boolean flag = true;
        List<AgentReturnedOrderItem> agentReturnedOrderItems = new ArrayList<>();
        List<AgentReturnedOrderItem> savedAgentReturnedOrderItems = null;

        if(agentReturnedOrder != null){
            for(int i=0;i<productIds.length;i++){
                MallProduct mallProduct = new MallProduct();
                mallProduct.setProductId(productIds[i]);
                AgentProduct agentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(author,mallProduct);
                if(productNums[i] > (agentProduct.getStore()-agentProduct.getFreez()) ){
                    flag = false;
                    break;
                }

                AgentReturnedOrderItem agentReturnedOrderItem = new AgentReturnedOrderItem();
                agentReturnedOrderItem.setReturnedOrder(agentReturnedOrder);
                MallProduct mallProduct2 = mallProductRepository.findOne(productIds[i]);
                if(mallProduct != null){
                    agentReturnedOrderItem.setProduct(mallProduct2);
                    agentReturnedOrderItem.setBn(mallProduct2.getBn());
                    agentReturnedOrderItem.setName(mallProduct2.getName());
                    agentReturnedOrderItem.setPrice(mallProduct2.getPrice());
                    agentReturnedOrderItem.setNum(productNums[i]);
                    agentReturnedOrderItem.setPrice(mallProduct2.getPrice());
                    agentReturnedOrderItem.setThumbnailPic("fixme");// FIXME: 2016/5/18
                    agentReturnedOrderItem.setPdtDesc(mallProduct2.getStandard());
                    agentReturnedOrderItem.setUnit(mallProduct2.getUnit());
                }
                agentReturnedOrderItems.add(agentReturnedOrderItem);
            }
        }

        if(flag){
            savedAgentReturnedOrderItems = agentReturnOrderItemRepository.save(agentReturnedOrderItems);
            for(int i=0;i<productIds.length;i++){
                MallProduct mallProduct = new MallProduct();
                mallProduct.setProductId(productIds[i]);
                AgentProduct agentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(author,mallProduct);
                agentProduct.setFreez(agentProduct.getFreez()+productNums[i]);
                agentProductRepository.save(agentProduct);
            }

        }else{
            agentReturnedOrder.setDisabled(true);
            agentReturnOrderRepository.save(agentReturnedOrder);
        }
        return savedAgentReturnedOrderItems;
    }

    @Override
    public List<AgentReturnedOrderItem> findAll(String rOrderId) {
        return agentReturnOrderItemRepository.findByReturnedOrder_rOrderId(rOrderId);
    }
}
