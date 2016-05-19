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

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.*;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrderItem;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderRepository;
import com.huotu.agento2o.service.repository.purchase.ShoppingCartRepository;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.Transient;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/18.
 */
@Service
public class AgentPurchaseOrderServiceImpl implements AgentPurchaseOrderService {
    @Autowired
    private AgentPurchaseOrderRepository purchaseOrderRepository;
    @Autowired
    private AgentPurchaseOrderItemRepository itemRepository;
    @Autowired
    private ShoppingCartRepository shoppingCartRepository;
    @Autowired
    private AgentProductRepository agentProductRepository;
    @Autowired
    private MallProductRepository productRepository;

    /**
     * 采购单列表
     *
     * @param purchaseOrderSearcher
     * @return
     */
    @Override
    public Page<AgentPurchaseOrder> findAll(PurchaseOrderSearcher purchaseOrderSearcher) {
        Specification<AgentPurchaseOrder> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (purchaseOrderSearcher.getAgentId() != null && purchaseOrderSearcher.getAgentId() != 0) {
                predicates.add(cb.equal(root.get("author").get("id").as(Integer.class), purchaseOrderSearcher.getAgentId()));
            }
            if (purchaseOrderSearcher.getParentAgentId() != null) {
                if (purchaseOrderSearcher.getParentAgentId() == 0) {
                    predicates.add(cb.isNull(root.get("author").get("parentAuthor").as(Author.class)));
                } else {
                    predicates.add(cb.equal(root.get("author").get("parentAuthor").get("id").as(Integer.class), purchaseOrderSearcher.getParentAgentId()));
                }
            }
            if (purchaseOrderSearcher.getStatusCode() != -1) {
                predicates.add(cb.equal(root.get("status").as(PurchaseEnum.OrderStatus.class),
                        EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, purchaseOrderSearcher.getStatusCode())));
            }
            if (purchaseOrderSearcher.getShipStatusCode() != -1) {
                predicates.add(cb.equal(root.get("shipStatus").as(PurchaseEnum.ShipStatus.class),
                        EnumHelper.getEnumType(PurchaseEnum.ShipStatus.class, purchaseOrderSearcher.getShipStatusCode())));
            }
            if (purchaseOrderSearcher.getPayStatusCode() != -1) {
                predicates.add(cb.equal(root.get("payStatus").as(PurchaseEnum.PayStatus.class),
                        EnumHelper.getEnumType(PurchaseEnum.PayStatus.class, purchaseOrderSearcher.getPayStatusCode())));
            }
            if (!StringUtil.isEmptyStr(purchaseOrderSearcher.getPOrderId())) {
                predicates.add(cb.like(root.get("pOrderId").as(String.class), "%" + purchaseOrderSearcher.getPOrderId() + "%"));
            }
            if (!StringUtil.isEmptyStr(purchaseOrderSearcher.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(purchaseOrderSearcher.getBeginTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmptyStr(purchaseOrderSearcher.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(purchaseOrderSearcher.getEndTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmptyStr(purchaseOrderSearcher.getOrderItemName())) {
                predicates.add(cb.like(root.get("orderItemList").get("name").as(String.class), "%" + purchaseOrderSearcher.getOrderItemName() + "%"));
            }
            predicates.add(cb.isFalse(root.get("disabled")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return purchaseOrderRepository.findAll(specification, new PageRequest(purchaseOrderSearcher.getPageIndex() - 1, Constant.PAGESIZE, new Sort(Sort.Direction.DESC, "createTime")));
    }

    /**
     * 增加采购单,增加相应货品预占库存,删除购物车中相应货品
     *
     * @param purchaseOrder
     * @param shoppingCartIds
     * @return
     */
    @Override
    @Transactional
    public ApiResult addPurchaseOrder(AgentPurchaseOrder purchaseOrder, Author author, String... shoppingCartIds) throws Exception {
        purchaseOrder.setPOrderId(SerialNo.create());
        purchaseOrder.setStatus(PurchaseEnum.OrderStatus.CHECKING);
        purchaseOrder.setAuthor(author);
        purchaseOrder.setDisabled(false);
        purchaseOrder.setCreateTime(new Date());
        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        List<AgentPurchaseOrderItem> itemList = new ArrayList<>();
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (String shoppingCartId : shoppingCartIds) {
            ShoppingCart shoppingCart = shoppingCartRepository.findByIdAndAuthor(Integer.valueOf(shoppingCartId), author);
            if (shoppingCart == null) {
                continue;
            }
            shoppingCartList.add(shoppingCart);
        }
        for (ShoppingCart shoppingCart : shoppingCartList) {
            //增加 采购货品
            AgentPurchaseOrderItem orderItem = new AgentPurchaseOrderItem();
            orderItem.setProduct(shoppingCart.getProduct());
            orderItem.setPurchaseOrder(purchaseOrder);
            orderItem.setNum(shoppingCart.getNum());
            orderItem.setBn(shoppingCart.getProduct().getBn());
            orderItem.setName(shoppingCart.getProduct().getName());
            orderItem.setPdtDesc(shoppingCart.getProduct().getStandard());
            orderItem.setUnit(shoppingCart.getProduct().getUnit());
            // TODO: 2016/5/18 根据 Agent_Id 获取相应销售价（进货价）
            orderItem.setPrice(shoppingCart.getProduct().getPrice());
            orderItem.setThumbnailPic(shoppingCart.getProduct().getGoods().getThumbnailPic());
            orderItem = itemRepository.save(orderItem);
            itemList.add(orderItem);
            //增加 预占库存
            if (author.getParentAuthor() == null) {
                //修改平台方货品预占库存
                MallProduct customerProduct = shoppingCart.getProduct();
                if (customerProduct.getFreez() + shoppingCart.getNum() > customerProduct.getStore()) {
                    // TODO: 2016/5/19 单元测试，库存不足 
                    throw new Exception("库存不足，下单失败！");
                }
                customerProduct.setFreez(customerProduct.getFreez() + shoppingCart.getNum());
                productRepository.save(customerProduct);
            } else {
                //修改代理商库存
                AgentProduct agentProduct = agentProductRepository.findByAgentAndProduct((Agent) author.getParentAuthor(), shoppingCart.getProduct());
                if (agentProduct.getFreez() + shoppingCart.getNum() > agentProduct.getStore()) {
                    // TODO: 2016/5/19 单元测试，库存不足 
                    throw new Exception("库存不足，下单失败！");
                }
                agentProduct.setFreez(agentProduct.getFreez() + shoppingCart.getNum());
                agentProductRepository.save(agentProduct);
            }
            //删除购物车
            shoppingCartRepository.delete(shoppingCart);
        }
        purchaseOrder.setFinalAmount(itemList.stream().mapToDouble(p -> p.getNum() * p.getPrice()).sum());
        // TODO: 2016/5/18 邮费
        purchaseOrder.setCostFreight(0);
        purchaseOrder.setOrderItemList(itemList);
        purchaseOrderRepository.save(purchaseOrder);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public AgentPurchaseOrder findByPOrderId(String pOrderId) {
        return purchaseOrderRepository.findOne(pOrderId);
    }

    /**
     * 逻辑删除采购单 设置采购单 disable 为 true
     * 减少相应库存
     *
     * @param agentPurchaseOrder
     */
    @Override
    @Transactional
    public void disableAgentPurchaseOrder(AgentPurchaseOrder agentPurchaseOrder, Author author) throws Exception {
        List<AgentPurchaseOrderItem> purchaseOrderItemList = agentPurchaseOrder.getOrderItemList();
        for (AgentPurchaseOrderItem item : purchaseOrderItemList) {
            //减少 预占库存
            if (author.getParentAuthor() == null) {
                //修改平台方货品预占库存
                MallProduct customerProduct = item.getProduct();
                if (customerProduct.getFreez() - item.getNum() <= 0) {
                    // TODO: 2016/5/19 单元测试，库存不足 
                    throw new Exception("库存不足，无法删除！");
                }
                customerProduct.setFreez(customerProduct.getFreez() - item.getNum());
                productRepository.save(customerProduct);
            } else {
                //修改代理商库存
                AgentProduct agentProduct = agentProductRepository.findByAgentAndProduct((Agent) author.getParentAuthor(), item.getProduct());
                if (agentProduct.getFreez() - item.getNum() > 0) {
                    // TODO: 2016/5/19 单元测试 库存不足 
                    throw new Exception("库存不足，无法删除！");
                }
                agentProduct.setFreez(agentProduct.getFreez() - item.getNum());
                agentProductRepository.save(agentProduct);
            }
        }
        agentPurchaseOrder.setDisabled(true);
        purchaseOrderRepository.save(agentPurchaseOrder);
        return;
    }
}
