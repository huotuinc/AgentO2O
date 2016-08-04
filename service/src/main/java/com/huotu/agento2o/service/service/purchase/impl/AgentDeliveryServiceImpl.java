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

import com.huotu.agento2o.common.util.*;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.purchase.*;
import com.huotu.agento2o.service.model.order.DeliveryInfo;
import com.huotu.agento2o.service.model.purchase.AgentProductStoreInfo;
import com.huotu.agento2o.service.repository.purchase.AgentDeliveryRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderRepository;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    /**
     * 校验采购单权限
     *
     * @param customerId
     * @param purchaseOrder
     */
    public void checkPurchaseOrder(Integer customerId, Integer agentId, AgentPurchaseOrder purchaseOrder) throws Exception {
        if (customerId != null && !(purchaseOrder.getParentAgent() == null && purchaseOrder.getMallCustomer().getCustomerId().equals(customerId))) {
            throw new Exception("没有权限");
        } else if (agentId != null && !(purchaseOrder.getParentAgent() != null && purchaseOrder.getParentAgent().getId().equals(agentId))) {
            throw new Exception("没有权限");
        }

    }

    @Override
    @Transactional
    public ApiResult pushDelivery(DeliveryInfo deliveryInfo, Integer customerId, Integer agentId) throws Exception {
        AgentPurchaseOrder purchaseOrder = purchaseOrderRepository.findOne(deliveryInfo.getOrderId());
        //判断用户是否有操作权限
        if (purchaseOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        checkPurchaseOrder(customerId, agentId, purchaseOrder);
        //判断是否可发货
        if (purchaseOrder.deliverable()) {
            AgentDelivery agentDelivery = new AgentDelivery();
            agentDelivery.setDeliveryId(SerialNo.create());
            agentDelivery.setPurchaseOrder(purchaseOrder);
            agentDelivery.setAgentId(purchaseOrder.getAgent() != null ? purchaseOrder.getAgent().getId() : null);
            agentDelivery.setShopId(purchaseOrder.getShop() != null ? purchaseOrder.getShop().getId() : null);
            if (purchaseOrder.getParentAgent() != null) {
                agentDelivery.setParentAgentId(purchaseOrder.getParentAgent().getId());
            }
            agentDelivery.setCustomerId(customerId);
            agentDelivery.setType(OrderEnum.DeliveryType.DEVERY.getCode());
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
                if (StringUtil.isEmptyStr(itemId)) {
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
                    AgentProductStoreInfo agentProduct = agentProductRepository.findUsableNumByAgentAndProduct(purchaseOrder.getParentAgent().getId(), orderItem.getProduct().getProductId());
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

    @Override
    public Page<AgentDelivery> showPurchaseDeliveryList(DeliverySearcher deliverySearcher) {
        Specification<AgentDelivery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (deliverySearcher.getAgentId() != null && deliverySearcher.getAgentId() != 0) {
                predicates.add(cb.equal(root.get("agentId").as(Integer.class), deliverySearcher.getAgentId()));
            }else if(deliverySearcher.getShopId() != null && deliverySearcher.getShopId() != 0){
                predicates.add(cb.equal(root.get("shopId").as(Integer.class),deliverySearcher.getShopId()));
            }
            if (deliverySearcher.getParentAgentId() != null && deliverySearcher.getParentAgentId() != 0) {
                predicates.add(cb.equal(root.get("parentAgentId").as(Integer.class), deliverySearcher.getParentAgentId()));
            } else if (deliverySearcher.getCustomerId() != null && deliverySearcher.getCustomerId() != 0) {
                predicates.add(cb.equal(root.get("customerId").as(Integer.class), deliverySearcher.getCustomerId()));
                predicates.add(cb.isNull(root.get("parentAgentId").as(Integer.class)));
            }
            if (!StringUtils.isEmpty(deliverySearcher.getDeliveryId())) {
                predicates.add(cb.like(root.get("deliveryId").as(String.class), "%" + deliverySearcher.getDeliveryId() + "%"));
            }

            if (!StringUtils.isEmpty(deliverySearcher.getLogiNo())) {
                predicates.add(cb.like(root.get("logisticsNo").as(String.class), "%" + deliverySearcher.getLogiNo() + "%"));
            }

            if (!StringUtils.isEmpty(deliverySearcher.getOrderId())) {
                predicates.add(cb.like(root.get("purchaseOrder").get("pOrderId").as(String.class), "%" + deliverySearcher.getOrderId() + "%"));
            }

            if (!StringUtils.isEmpty(deliverySearcher.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(deliverySearcher.getBeginTime(), StringUtil.TIME_PATTERN)));
            }

            if (!StringUtil.isEmpty(deliverySearcher.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(deliverySearcher.getEndTime(), StringUtil.TIME_PATTERN)));
            }

            predicates.add(cb.equal(root.get("type").as(String.class), OrderEnum.DeliveryType.DEVERY.getCode()));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //排序

        return agentDeliveryRepository.findAll(specification, new PageRequest(deliverySearcher.getPageIndex() - 1, Constant.PAGESIZE, new Sort(Sort.Direction.DESC, "createTime")));
    }

    @Override
    public Page<AgentDelivery> showReturnDeliveryList(DeliverySearcher deliverySearcher) {
        Specification<AgentDelivery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (deliverySearcher.getAgentId() != null && deliverySearcher.getAgentId() != 0) {
                predicates.add(cb.equal(root.get("agentId").as(Integer.class), deliverySearcher.getAgentId()));
            }
            if (deliverySearcher.getShopId() != null && deliverySearcher.getShopId() != 0) {
                predicates.add(cb.equal(root.get("shopId").as(Integer.class),deliverySearcher.getShopId()));
            }
            if (deliverySearcher.getParentAgentId() != null && deliverySearcher.getParentAgentId() != 0) {
                predicates.add(cb.equal(root.get("parentAgentId").as(Integer.class), deliverySearcher.getParentAgentId()));
            } else if (deliverySearcher.getCustomerId() != null && deliverySearcher.getCustomerId() != 0) {
                predicates.add(cb.equal(root.get("customerId").as(Integer.class), deliverySearcher.getCustomerId()));
                predicates.add(cb.isNull(root.get("parentAgentId").as(Integer.class)));
            }
            if (!StringUtils.isEmpty(deliverySearcher.getDeliveryId())) {
                predicates.add(cb.like(root.get("deliveryId").as(String.class), "%" + deliverySearcher.getDeliveryId() + "%"));
            }

            if (!StringUtils.isEmpty(deliverySearcher.getLogiNo())) {
                predicates.add(cb.like(root.get("logisticsNo").as(String.class), "%" + deliverySearcher.getLogiNo() + "%"));
            }

            if (!StringUtils.isEmpty(deliverySearcher.getOrderId())) {
                predicates.add(cb.like(root.get("agentReturnedOrder").get("rOrderId").as(String.class), "%" + deliverySearcher.getOrderId() + "%"));
            }

            if (!StringUtils.isEmpty(deliverySearcher.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(deliverySearcher.getBeginTime(), StringUtil.TIME_PATTERN)));
            }

            if (!StringUtil.isEmpty(deliverySearcher.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(deliverySearcher.getEndTime(), StringUtil.TIME_PATTERN)));
            }

            predicates.add(cb.equal(root.get("type").as(String.class), OrderEnum.DeliveryType.RETURN.getCode()));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //排序

        return agentDeliveryRepository.findAll(specification, new PageRequest(deliverySearcher.getPageIndex() - 1, Constant.PAGESIZE, new Sort(Sort.Direction.DESC, "createTime")));
    }
}
