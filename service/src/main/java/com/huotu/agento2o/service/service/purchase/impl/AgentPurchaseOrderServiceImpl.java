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
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

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
     * @param purchaseOrderSearcher
     * @return
     */
    @Override
    public Page<AgentPurchaseOrder> findAll(PurchaseOrderSearcher purchaseOrderSearcher) {
        return null;
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
    public ApiResult addPurchaseOrder(AgentPurchaseOrder purchaseOrder, Author author, String... shoppingCartIds) {
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
            if (shoppingCart.getNum() > (shoppingCart.getProduct().getStore() - shoppingCart.getProduct().getFreez())) {
                return new ApiResult("库存不足，无法下单！");
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
                customerProduct.setFreez(customerProduct.getFreez() + shoppingCart.getNum());
                productRepository.save(customerProduct);
            } else {
                //修改代理商库存
                AgentProduct agentProduct = agentProductRepository.findByAgentAndProduct((Agent) author.getParentAuthor(), shoppingCart.getProduct());
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
}
