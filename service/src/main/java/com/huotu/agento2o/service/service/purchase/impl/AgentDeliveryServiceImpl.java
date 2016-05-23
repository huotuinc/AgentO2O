/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.purchase.*;
import com.huotu.agento2o.service.model.order.DeliveryInfo;
import com.huotu.agento2o.service.repository.purchase.AgentDeliveryRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderRepository;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * Created by helloztt on 2016/5/19.
 */
@Service
public class AgentDeliveryServiceImpl implements AgentDeliveryService {
    @Autowired
    private AgentDeliveryRepository agentDeliveryRepository;
    @Autowired
    private AgentPurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private AgentPurchaseOrderItemRepository purchaseOrderItemRepository;
    @Autowired
    private AgentProductRepository agentProductRepository;


    @Override
    @Transactional
    public ApiResult pushDelivery(DeliveryInfo deliveryInfo, Integer customerId, Integer agentId) throws Exception {
        AgentPurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(deliveryInfo.getOrderId());
        //判断用户是否有操作权限
        if (purchaseOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (customerId != null && !(purchaseOrder.getAuthor().getParentAuthor() == null && purchaseOrder.getAuthor().getCustomer().getCustomerId().equals(customerId))) {
            return new ApiResult("没有权限");
        } else if (agentId != null && !(purchaseOrder.getAuthor().getParentAuthor() != null && purchaseOrder.getAuthor().getParentAuthor().getId().equals(agentId))) {
            return new ApiResult("没有权限");
        }
        //判断是否可发货
        if (purchaseOrder.deliverable()) {
            AgentDelivery agentDelivery = new AgentDelivery();
            agentDelivery.setDeliveryId(SerialNo.create());
            agentDelivery.setPurchaseOrder(purchaseOrder);
            agentDelivery.setAgentId(purchaseOrder.getAuthor().getId());
            agentDelivery.setCustomerId(customerId);
            agentDelivery.setType(OrderEnum.DeliveryType.DEVERY.getValue());
            agentDelivery.setLogisticsName(deliveryInfo.getLogiName());
            agentDelivery.setLogisticsNo(deliveryInfo.getLogiNo());
            agentDelivery.setFreight(deliveryInfo.getFreight());
            agentDelivery.setCreateTime(new Date());
            agentDelivery.setShipName(purchaseOrder.getShipName());
            agentDelivery.setShipAddr(purchaseOrder.getShipAddr());
            agentDelivery.setShipMobile(purchaseOrder.getShipMobile());
            agentDelivery.setMemo(deliveryInfo.getRemark());
            String[] itemIds = deliveryInfo.getSendItems().split("\\|");
            List<AgentDeliveryItem> deliveryItemList = new ArrayList<>();
            for (String itemId : itemIds) {
                if(StringUtil.isEmptyStr(itemId)){
                    continue;
                }
                AgentPurchaseOrderItem orderItem = purchaseOrderItemRepository.findOne(Integer.parseInt(itemId));
                if (orderItem == null || orderItem.getProduct() == null) {
                    // TODO: 2016/5/19 单元测试
                    throw new Exception("不存在该货品！");
                }
                if (!orderItem.deliverable()) {
                    throw new Exception("存在货品不可发货！");
                }
                AgentDeliveryItem deliveryItem = new AgentDeliveryItem();
                deliveryItem.setDelivery(agentDelivery);
                if (agentId != null) {
                    //代理商货品
                    AgentProduct agentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(purchaseOrder.getAuthor().getParentAuthor(), orderItem.getProduct());
                    deliveryItem.setAgentProductId(agentProduct.getId());
                }
                //平台货品
                deliveryItem.setProductId(orderItem.getProduct().getProductId());
                deliveryItem.setProductBn(orderItem.getProduct().getBn());
                deliveryItem.setProductName(orderItem.getProduct().getName());
                deliveryItem.setNum(orderItem.getNum());
                //增加发货数量
                orderItem.setSendNum(orderItem.getNum());
                purchaseOrderItemRepository.save(orderItem);
                deliveryItemList.add(deliveryItem);
            }
            agentDelivery.setDeliveryItems(deliveryItemList);
            agentDeliveryRepository.save(agentDelivery);
            purchaseOrder.setShipStatus(PurchaseEnum.ShipStatus.DELIVERED);
            purchaseOrderRepository.save(purchaseOrder);
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "该订单无法发货", null);
    }
}
