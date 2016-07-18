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

import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.*;
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.config.annotation.SystemServiceLog;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrderItem;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.repository.goods.MallGoodsRepository;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentPurchaseOrderRepository;
import com.huotu.agento2o.service.repository.purchase.ShoppingCartRepository;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
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
    @Autowired
    private MallGoodsRepository goodsRepository;

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
                predicates.add(cb.equal(root.get("agent").get("id").as(Integer.class), purchaseOrderSearcher.getAgentId()));
            }
            if (purchaseOrderSearcher.getShopId() != null && purchaseOrderSearcher.getShopId() != 0) {
                predicates.add(cb.equal(root.get("shop").get("id").as(Integer.class), purchaseOrderSearcher.getShopId()));
            }
            if (purchaseOrderSearcher.getParentAgentId() != null) {
                if (purchaseOrderSearcher.getParentAgentId() == 0) {
                    //只有代理商的上级代理才可能为平台方
                    predicates.add(cb.isNull(root.get("parentAgent").as(Agent.class)));
                } else {
                    predicates.add(cb.equal(root.get("parentAgent").get("id").as(Agent.class),purchaseOrderSearcher.getParentAgentId()));
                    /*predicates.add(cb.or(
                            cb.equal(root.get("agent").get("parentAgent").get("id").as(Integer.class), purchaseOrderSearcher.getParentAgentId()),
                            cb.equal(root.get("shop").get("agent").get("id").as(Integer.class), purchaseOrderSearcher.getParentAgentId())
                    ));*/
                }
            }
            if (purchaseOrderSearcher.getCustomerId() != null && purchaseOrderSearcher.getCustomerId() != 0) {
                Join<AgentPurchaseOrder, Agent> join1 = root.join(root.getModel().getSingularAttribute("agent", Agent.class), JoinType.LEFT);
                Join<AgentPurchaseOrder, ShopAuthor> join2 = root.join(root.getModel().getSingularAttribute("shop", ShopAuthor.class), JoinType.LEFT);
                Predicate p1 = cb.equal(join1.get("customer").get("customerId").as(Integer.class), purchaseOrderSearcher.getCustomerId());
                Predicate p2 = cb.equal(join2.get("customer").get("customerId").as(Integer.class), purchaseOrderSearcher.getCustomerId());
                predicates.add(cb.or(p1, p2));
                /*predicates.add(cb.or(
                        cb.equal(root.get("agent").get("customer").get("customerId").as(Integer.class),purchaseOrderSearcher.getCustomerId()),
                        cb.equal(root.get("shop").get("customer").get("customerId").as(Integer.class),purchaseOrderSearcher.getCustomerId())));*/
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
//            predicates.add(cb.isFalse(root.get("disabled")));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return purchaseOrderRepository.findAll(specification, new PageRequest(purchaseOrderSearcher.getPageIndex() - 1, purchaseOrderSearcher.getPageSize(), new Sort(Sort.Direction.DESC, "createTime")));
    }

    /**
     * 增加采购单,增加相应货品预占库存,删除购物车中相应货品
     *
     * @param purchaseOrder
     * @param shoppingCartIds
     * @return
     */
    @Override
    @SystemServiceLog(value = "新增采购单")
    @Transactional(rollbackFor = RuntimeException.class)
    public ApiResult addPurchaseOrder(AgentPurchaseOrder purchaseOrder, Author author, String... shoppingCartIds) throws Exception {
        purchaseOrder.setPOrderId(SerialNo.create());
        purchaseOrder.setStatus(PurchaseEnum.OrderStatus.CHECKING);
        purchaseOrder.setAgent(author.getAuthorAgent());
        purchaseOrder.setShop(author.getAuthorShop());
        purchaseOrder.setParentAgent(author.getParentAgent());
        purchaseOrder.setDisabled(false);
        purchaseOrder.setCreateTime(new Date());
        purchaseOrder.setLastUpdateTime(new Date());
//        purchaseOrder = purchaseOrderRepository.save(purchaseOrder);
        List<AgentPurchaseOrderItem> itemList = new ArrayList<>();
        List<ShoppingCart> shoppingCartList = new ArrayList<>();
        for (String shoppingCartId : shoppingCartIds) {
            ShoppingCart shoppingCart = null;
            if (author != null && author.getType() == Agent.class) {
                shoppingCart = shoppingCartRepository.findByIdAndAgent(Integer.valueOf(shoppingCartId), author.getAuthorAgent());
            } else if (author != null && author.getType() == ShopAuthor.class) {
                shoppingCart = shoppingCartRepository.findByIdAndShop(Integer.valueOf(shoppingCartId), author.getAuthorShop());
            }
            if (shoppingCart == null) {
                continue;
            }
            shoppingCartList.add(shoppingCart);
        }
        if (shoppingCartList == null || shoppingCartList.size() == 0) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
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
            if (author.getParentAgent() == null) {
                //修改平台方货品预占库存
                MallProduct customerProduct = shoppingCart.getProduct();
                if (customerProduct.getFreez() + shoppingCart.getNum() > customerProduct.getStore()) {
                    throw new RuntimeException("库存不足，下单失败！");
                }
                customerProduct.setFreez(customerProduct.getFreez() + shoppingCart.getNum());
                productRepository.save(customerProduct);
            } else {
                //修改代理商库存
                AgentProduct agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getParentAgent(), shoppingCart.getProduct());
                if (agentProduct.getFreez() + shoppingCart.getNum() > agentProduct.getStore()) {
                    throw new RuntimeException("库存不足，下单失败！");
                }
                agentProduct.setFreez(agentProduct.getFreez() + shoppingCart.getNum());
                agentProductRepository.save(agentProduct);
            }
            //删除购物车
            shoppingCartRepository.delete(shoppingCart);
        }
        double finalAmount = itemList.stream().mapToDouble(p -> p.getNum() * p.getPrice()).sum();
        purchaseOrder.setFinalAmount((double) Math.round(finalAmount * 100) / 100);
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

    @Override
    public AgentPurchaseOrder findByPOrderIdAndAuthor(String pOrderId, Author author) {
        return purchaseOrderRepository.findByPOrderIdAndAgentAndShop(pOrderId, author.getAuthorAgent(), author.getAuthorShop());
    }

    /**
     * 逻辑删除采购单 设置采购单 disable 为 true
     * 减少相应库存
     *
     * @param pOrderId
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult disableAgentPurchaseOrder(String pOrderId, Author author) throws Exception {
        AgentPurchaseOrder agentPurchaseOrder = findByPOrderIdAndAuthor(pOrderId, author);
        if (agentPurchaseOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (!agentPurchaseOrder.deletable()) {
            return new ApiResult("采购单已审核或已支付，无法删除！");
        }
        //审核不通过，已经减少预占库存
        if (!PurchaseEnum.OrderStatus.RETURNED.equals(agentPurchaseOrder.getStatus())) {
            List<AgentPurchaseOrderItem> purchaseOrderItemList = agentPurchaseOrder.getOrderItemList();
            for (AgentPurchaseOrderItem item : purchaseOrderItemList) {
                //如果采购单未审核 减少 预占库存
                if (author.getParentAgent() == null) {
                    //修改平台方货品预占库存
                    MallProduct customerProduct = item.getProduct();
                    if (customerProduct.getFreez() - item.getNum() < 0) {
                        throw new Exception("库存不足，无法删除！");
                    }
                    customerProduct.setFreez(customerProduct.getFreez() - item.getNum());
                    productRepository.save(customerProduct);
                } else {
                    //如果采购单未审核 修改代理商库存
                    AgentProduct agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getParentAgent(), item.getProduct());
                    if (agentProduct.getFreez() - item.getNum() < 0) {
                        throw new Exception("库存不足，无法删除！");
                    }
                    agentProduct.setFreez(agentProduct.getFreez() - item.getNum());
                    agentProductRepository.save(agentProduct);
                }
            }
        }
        agentPurchaseOrder.setDisabled(true);
        agentPurchaseOrder.setLastUpdateTime(new Date());
        purchaseOrderRepository.save(agentPurchaseOrder);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    @Transactional
    public ApiResult payAgentPurchaseOrder(String pOrderId, Author author) {
        AgentPurchaseOrder agentPurchaseOrder = findByPOrderIdAndAuthor(pOrderId, author);
        if (agentPurchaseOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (!agentPurchaseOrder.payabled()) {
            return new ApiResult("采购单未审核或已已支付，无法支付！");
        }
        agentPurchaseOrder.setPayStatus(PurchaseEnum.PayStatus.PAYED);
        agentPurchaseOrder.setPayTime(new Date());
        agentPurchaseOrder.setLastUpdateTime(new Date());
        purchaseOrderRepository.save(agentPurchaseOrder);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 采购单确认收货后修改相关库存信息：
     * 1.判断AgentProduct是否有该货品信息，若没有则新增
     * 2.增加当前代理商/门店货品的库存数量
     * 3.减少上级代理商/平台方货品的预占库存及库存
     *
     * @param pOrderId
     * @param author
     * @return
     * @throws Exception
     */
    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult receiveAgentPurchaseOrder(String pOrderId, Author author) throws Exception {
        AgentPurchaseOrder agentPurchaseOrder = findByPOrderIdAndAuthor(pOrderId, author);
        if (agentPurchaseOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (!agentPurchaseOrder.receivable()) {
            return new ApiResult("采购单无法确认收货！");
        }
        for (AgentPurchaseOrderItem item : agentPurchaseOrder.getOrderItemList()) {
            AgentProduct agentProduct = null;
            if (author != null && author.getType() == Agent.class) {
                agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getAuthorAgent(), item.getProduct());
            } else if (author != null && author.getType() == ShopAuthor.class) {
                agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(author.getAuthorShop(), item.getProduct());
            }
            MallProduct product = item.getProduct();
            //判断AgentProduct是否有该货品信息，若没有则新增
            //增加当前代理商/门店货品的库存数量
            if (agentProduct == null) {
                agentProduct = new AgentProduct();
                agentProduct.setAgent(author.getAuthorAgent());
                agentProduct.setShop(author.getAuthorShop());
                agentProduct.setProduct(product);
                agentProduct.setGoodsId(product.getGoods().getGoodsId());
                agentProduct.setStore(item.getNum());
                agentProduct.setFreez(0);
                agentProduct.setWarning(0);
                agentProduct.setDisabled(false);
            } else {
                agentProduct.setStore(agentProduct.getStore() + item.getNum());
            }
            agentProductRepository.save(agentProduct);
            //减少上级代理商/平台方货品的预占库存及库存
            if (agentPurchaseOrder.getParentAgent() == null) {
                //上级为平台方 获取平台方商品
                if (product == null || product.getFreez() - item.getNum() < 0 || product.getStore() - item.getNum() < 0) {
                    throw new Exception("保存异常！");
                }
                product.setStore(product.getStore() - item.getNum());
                product.setFreez(product.getFreez() - item.getNum());
                productRepository.save(product);
            } else {
                //上级为代理商 获取代理商货品
                AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(agentPurchaseOrder.getParentAgent(), product);
                if (parentAgentProduct == null || parentAgentProduct.getFreez() - item.getNum() < 0 || parentAgentProduct.getStore() - item.getNum() < 0) {
                    throw new Exception("保存异常！");
                }
                parentAgentProduct.setStore(parentAgentProduct.getStore() - item.getNum());
                parentAgentProduct.setFreez(parentAgentProduct.getFreez() - item.getNum());
                agentProductRepository.save(parentAgentProduct);
            }
        }
        agentPurchaseOrder.setReceivedTime(new Date());
        purchaseOrderRepository.save(agentPurchaseOrder);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    @Transactional
    public ApiResult deliveryAgentPurchaseOrder(Integer customerId, Integer authorId, String pOrderId) {
        AgentPurchaseOrder agentPurchaseOrder = findByPOrderId(pOrderId);
        if (agentPurchaseOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //若上级代理商为平台，判断平台是否有修改该采购单的权限
        //判断该平台是否有修改该采购单的权限
        if (customerId != null && !(agentPurchaseOrder.getParentAgent() == null && agentPurchaseOrder.getMallCustomer().getCustomerId() == customerId)) {
            return new ApiResult("对不起，您没有操作权限！");
        } else if (authorId != null && !(agentPurchaseOrder.getParentAgent() != null && agentPurchaseOrder.getParentAgent().getId() == authorId)) {
            //判断该代理商是否有修改该采购单的权限
            return new ApiResult("对不起，您没有操作权限！");
        }
        if (!agentPurchaseOrder.deletable()) {
            return new ApiResult("该采购单已审核，无法再次审核！");
        }
        agentPurchaseOrder.setShipStatus(PurchaseEnum.ShipStatus.DELIVERED);
        agentPurchaseOrder.setLastUpdateTime(new Date());
        purchaseOrderRepository.save(agentPurchaseOrder);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public ApiResult checkPurchaseOrder(Integer customerId, Integer authorId, String pOrderId, PurchaseEnum.OrderStatus status, String comment) throws Exception {
        AgentPurchaseOrder agentPurchaseOrder = findByPOrderId(pOrderId);
        if (agentPurchaseOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //若上级代理商为平台，判断平台是否有修改该采购单的权限
        //判断该平台是否有修改该采购单的权限
        if (customerId != null && !(agentPurchaseOrder.getParentAgent() == null && agentPurchaseOrder.getMallCustomer().getCustomerId().equals(customerId))) {
            return new ApiResult("对不起，您没有操作权限！");
        } else if (authorId != null && !(agentPurchaseOrder.getParentAgent() != null && agentPurchaseOrder.getParentAgent().getId().equals(authorId))) {
            //判断该代理商是否有修改该采购单的权限
            return new ApiResult("对不起，您没有操作权限！");
        }
        if (!agentPurchaseOrder.checkable()) {
            return new ApiResult("该采购单已审核，无法再次审核！");
        }
        //如果审核不通过，减少预占库存
        if (status == PurchaseEnum.OrderStatus.RETURNED) {
            List<AgentPurchaseOrderItem> itemList = agentPurchaseOrder.getOrderItemList();
            for (AgentPurchaseOrderItem item : itemList) {
                if (agentPurchaseOrder.getParentAgent() == null) {
                    //减少平台方预占库存
                    MallProduct customerProduct = item.getProduct();
                    if (item.getNum() > customerProduct.getFreez()) {
                        throw new Exception("库存不足！");
                    }
                    customerProduct.setFreez(customerProduct.getFreez() - item.getNum());
                    productRepository.save(customerProduct);
                } else {
                    //修改代理商货品预占库存
                    AgentProduct product = agentProductRepository.findByAgentAndProductAndDisabledFalse(agentPurchaseOrder.getParentAgent(), item.getProduct());
                    if (product != null) {
                        if (item.getNum() > product.getFreez()) {
                            throw new Exception("库存不足！");
                        }
                        product.setFreez(product.getFreez() - item.getNum());
                        agentProductRepository.save(product);
                    }
                }
            }
        }
        agentPurchaseOrder.setStatus(status);
        if (status == PurchaseEnum.OrderStatus.RETURNED) {
            agentPurchaseOrder.setParentComment(comment);
        }
        agentPurchaseOrder.setLastUpdateTime(new Date());
        purchaseOrderRepository.save(agentPurchaseOrder);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public HSSFWorkbook createWorkBook(List<AgentPurchaseOrder> purchaseOrderList) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        purchaseOrderList.forEach(order -> {
            StringBuffer sb = new StringBuffer("");
            order.getOrderItemList().forEach(item -> {
                if (sb.length() != 0) {
                    sb.append("\r\n");
                }
                sb.append(item.getName());
            });
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();
            cellDescList.add(ExcelHelper.asCell(order.getPOrderId()));
            cellDescList.add(ExcelHelper.asCell(sb.toString()));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getAuthorName())));
            if (order.getParentAgent() != null) {
                cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getParentAgent().getName())));
            } else {
                cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getMallCustomer().getNickName())));
            }
            cellDescList.add(ExcelHelper.asCell(StringUtil.DateFormat(order.getCreateTime(), StringUtil.TIME_PATTERN)));
            cellDescList.add(ExcelHelper.asCell(StringUtil.DateFormat(order.getPayTime(), StringUtil.TIME_PATTERN)));
            cellDescList.add(ExcelHelper.asCell(order.getFinalAmount(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(order.getCostFreight(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(order.getStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getPayStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getShipStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getShipName()));
            cellDescList.add(ExcelHelper.asCell(order.getShipMobile()));
            cellDescList.add(ExcelHelper.asCell(order.getShipAddr()));
            cellDescList.add(ExcelHelper.asCell(order.getSendMode().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getTaxType().getValue()));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getTaxTitle())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getTaxContent())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getTaxpayerCode())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getBankName())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getAccountNo())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getStatusComment())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getAuthorComment())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getParentComment())));
            cellDescList.add(ExcelHelper.asCell(order.isDisabled() ? "已取消" : "活动"));
            rowAndCells.add(cellDescList);
        });
        return ExcelHelper.createWorkbook("采购单列表", SysConstant.AGENT_ORDER_EXPORT_HEADER, rowAndCells);
    }
}
