package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.*;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.*;
import com.huotu.agento2o.service.model.purchase.ReturnOrderDeliveryInfo;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentDeliveryRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderItemRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Join;
import javax.persistence.criteria.JoinType;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
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

    @Autowired
    private AgentReturnOrderItemRepository agentReturnOrderItemRepository;

    @Autowired
    private MallProductRepository mallProductRepository;

    @Autowired
    private AgentDeliveryRepository agentDeliveryRepository;


    @Override
    public AgentReturnedOrder findOne(String rOrderId) {
        if (rOrderId == null) {
            return null;
        }
        return agentReturnOrderRepository.findOne(rOrderId);
    }

    @Override
    public AgentReturnedOrder addReturnOrder(AgentReturnedOrder agentReturnedOrder) {

        if (agentReturnedOrder == null) {
            return null;
        }
        agentReturnedOrder = agentReturnOrderRepository.save(agentReturnedOrder);
        return agentReturnedOrder;
    }

    /**
     * 增加退货单
     *
     * @param agentReturnedOrder
     * @param author
     * @param agentProductIds
     * @param productNums
     * @return
     */
    @Override
    @Transactional
    public ApiResult addReturnOrder(AgentReturnedOrder agentReturnedOrder, Author author, Integer[] agentProductIds, Integer[] productNums) throws Exception {
        agentReturnedOrder.setROrderId(SerialNo.create());
        agentReturnedOrder.setAuthor(author);
        agentReturnedOrder.setParentAgent(author.getParentAgent());
        agentReturnedOrder.setShipStatus(PurchaseEnum.ShipStatus.NOT_DELIVER);
        agentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKING);
        agentReturnedOrder.setPayStatus(PurchaseEnum.PayStatus.NOT_PAYED);
        agentReturnedOrder.setCreateTime(new Date());
        List<AgentReturnedOrderItem> returnedOrderItemList = new ArrayList<>();
        for (int i = 0; i < agentProductIds.length; i++) {
            AgentProduct agentProduct = agentProductRepository.findOne(agentProductIds[i]);
            MallProduct mallProduct = agentProduct.getProduct();
            if (productNums[i] > (agentProduct.getStore() - agentProduct.getFreez()) || productNums[i] <= 0) {
                throw new Exception("库存不足！");
            }
            AgentReturnedOrderItem agentReturnedOrderItem = new AgentReturnedOrderItem();
            agentReturnedOrderItem.setReturnedOrder(agentReturnedOrder);
            if (mallProduct != null) {
                agentReturnedOrderItem.setProduct(mallProduct);
                agentReturnedOrderItem.setBn(mallProduct.getBn());
                agentReturnedOrderItem.setName(mallProduct.getName());
                agentReturnedOrderItem.setPrice(mallProduct.getPurchasePrice());
                agentReturnedOrderItem.setNum(productNums[i]);
                agentReturnedOrderItem.setThumbnailPic(mallProduct.getGoods().getThumbnailPic());
                agentReturnedOrderItem.setPdtDesc(mallProduct.getStandard());
                agentReturnedOrderItem.setUnit(mallProduct.getUnit());
            }
            returnedOrderItemList.add(agentReturnedOrderItem);
            //增加 预占库存
            agentProduct.setFreez(agentProduct.getFreez() + productNums[i]);
            agentProductRepository.save(agentProduct);
        }
        double finalAmount = returnedOrderItemList.stream().mapToDouble(p -> p.getPrice() * p.getNum()).sum();
        agentReturnedOrder.setFinalAmount((double) Math.round(finalAmount * 100) / 100);
        agentReturnedOrder.setOrderItemList(returnedOrderItemList);
        agentReturnedOrder = agentReturnOrderRepository.save(agentReturnedOrder);
        if (agentReturnedOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public Page<AgentReturnedOrder> findAll(ReturnedOrderSearch returnedOrderSearch) {


        Specification<AgentReturnedOrder> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (returnedOrderSearch.getAgentId() != null && returnedOrderSearch.getAgentId() != 0) {
                predicates.add(cb.equal(root.get("agent").get("id").as(Integer.class), returnedOrderSearch.getAgentId()));
            }
            if (returnedOrderSearch.getShopId() != null && returnedOrderSearch.getShopId() != 0) {
                predicates.add(cb.equal(root.get("shop").get("id").as(Integer.class), returnedOrderSearch.getShopId()));
            }
            if (returnedOrderSearch.getParentAgentId() != null) {
                if (returnedOrderSearch.getParentAgentId() == 0) {
                    predicates.add(cb.isNull(root.get("agent").get("parentAgent").as(Agent.class)));
                } else {
                    /*Join<AgentReturnedOrder, Agent> join1 = root.join(root.getModel().getSingularAttribute("agent", Agent.class), JoinType.LEFT);
                    Join<AgentReturnedOrder, Shop> join2 = root.join(root.getModel().getSingularAttribute("shop", Shop.class), JoinType.LEFT);
                    Predicate p1 = cb.equal(join1.get("parentAgent").get("id").as(Integer.class), returnedOrderSearch.getParentAgentId());
                    Predicate p2 = cb.equal(join2.get("agent").get("id").as(Integer.class), returnedOrderSearch.getParentAgentId());
                    predicates.add(cb.or(p1,p2));*/
                    predicates.add(cb.equal(root.get("parentAgent").get("id").as(Agent.class),returnedOrderSearch.getParentAgentId()));
                }
            }
            if (returnedOrderSearch.getCustomerId() != null && returnedOrderSearch.getCustomerId() != 0) {
                Join<AgentReturnedOrder, Agent> join1 = root.join(root.getModel().getSingularAttribute("agent", Agent.class), JoinType.LEFT);
                Join<AgentReturnedOrder, Shop> join2 = root.join(root.getModel().getSingularAttribute("shop", Shop.class), JoinType.LEFT);
                Predicate p1 = cb.equal(join1.get("customer").get("customerId").as(Integer.class), returnedOrderSearch.getCustomerId());
                Predicate p2 = cb.equal(join2.get("customer").get("customerId").as(Integer.class), returnedOrderSearch.getCustomerId());
                predicates.add(cb.or(p1,p2));
            }
            if (!StringUtils.isEmpty(returnedOrderSearch.getROrderId())) {
                predicates.add(cb.like(root.get("rOrderId").as(String.class), "%" + returnedOrderSearch.getROrderId() + "%"));
            }
            if (returnedOrderSearch.getPayStatus() != -1) {
                predicates.add(cb.equal(root.get("payStatus").as(PurchaseEnum.PayStatus.class),
                        EnumHelper.getEnumType(PurchaseEnum.PayStatus.class, returnedOrderSearch.getPayStatus())));
            }
            if (returnedOrderSearch.getShipStatus() != -1) {
                predicates.add(cb.equal(root.get("shipStatus").as(PurchaseEnum.ShipStatus.class),
                        EnumHelper.getEnumType(PurchaseEnum.ShipStatus.class, returnedOrderSearch.getShipStatus())));
            }
            if (returnedOrderSearch.getOrderStatus() != -1) {
                predicates.add(cb.equal(root.get("status").as(PurchaseEnum.OrderStatus.class),
                        EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, returnedOrderSearch.getOrderStatus())));
            }
            if (!StringUtils.isEmpty(returnedOrderSearch.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(returnedOrderSearch.getBeginTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmpty(returnedOrderSearch.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(returnedOrderSearch.getEndTime(), StringUtil.TIME_PATTERN)));
            }
//            predicates.add(cb.equal(root.get("disabled").as(Boolean.class), false));
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //排序
        return agentReturnOrderRepository.findAll(specification, new PageRequest(returnedOrderSearch.getPageIndex() - 1, Constant.PAGESIZE, new Sort(Sort.Direction.DESC, "createTime")));

    }

    @Override
    public ApiResult cancelReturnOrder(Author author, String rOrderId) {
        AgentReturnedOrder agentReturnedOrder = agentReturnOrderRepository.findByAgentAndShopAndROrderIdAndDisabledFalse(author.getAuthorAgent(),author.getAuthorShop(), rOrderId);

        if (agentReturnedOrder != null) {
            if (agentReturnedOrder.getStatus().equals(PurchaseEnum.OrderStatus.CHECKING)) {// 待审核状态才可以退货
                agentReturnedOrder.setDisabled(true);
                agentReturnOrderRepository.save(agentReturnedOrder);
                resetPreStore(author, rOrderId);
                return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
            } else {
                return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "该退货单不可取消退货", null);
            }

        } else {
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
    }

    /**
     * 更新预占库存
     */
    private List<AgentProduct> resetPreStore(Author author, String rOrderId) {
        List<AgentReturnedOrderItem> agentReturnedOrderItems = agentReturnOrderItemRepository.findByReturnedOrder_rOrderId(rOrderId);
        List<AgentProduct> agentProducts = new ArrayList<>();
        agentReturnedOrderItems.forEach(agentReturnedOrderItem -> {
            MallProduct product = agentReturnedOrderItem.getProduct();
            Integer num = agentReturnedOrderItem.getNum();
            AgentProduct agentProduct = null;
            if (author != null && author.getType() == Agent.class) {
                agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getAuthorAgent(), product);
            } else if (author != null && author.getType() == Shop.class) {
                agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(author.getAuthorShop(),product);
            }
            agentProduct.setFreez(agentProduct.getFreez() - num);
            agentProducts.add(agentProduct);
        });
        return agentProductRepository.save(agentProducts);
    }

    @Override
    @Transactional
    public ApiResult checkReturnOrder(Integer customerId, Integer authorId, String rOrderId, PurchaseEnum.OrderStatus status, String comment) {
        AgentReturnedOrder agentReturnedOrder = findOne(rOrderId);
        if (agentReturnedOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        //若上级代理商为平台，判断平台是否有修改该采购单的权限
        //判断该平台是否有修改该采购单的权限
        if (customerId != null && !(agentReturnedOrder.getParentAgent() == null && agentReturnedOrder.getMallCustomer().getCustomerId().equals(customerId))) {
            return new ApiResult("对不起，您没有操作权限！");
        } else if (authorId != null && !(agentReturnedOrder.getParentAgent() != null && agentReturnedOrder.getParentAgent().getId().equals(authorId))) {
            //判断该代理商是否有修改该采购单的权限
            return new ApiResult("对不起，您没有操作权限！");
        }
        if (!agentReturnedOrder.checkable()) {
            return new ApiResult("该退货单已审核，无法再次审核！");
        }
        agentReturnedOrder.setStatus(status);
        if (status == PurchaseEnum.OrderStatus.RETURNED) {
            agentReturnedOrder.setStatusComment(comment);
            // 该订单是否设置为无效？
            agentReturnedOrder.setDisabled(true);
            agentReturnedOrder.setLastUpdateTime(new Date());
            agentReturnOrderRepository.save(agentReturnedOrder);

            // 释放预占库存
            List<AgentReturnedOrderItem> agentReturnedOrderItems = agentReturnOrderItemRepository.findByReturnedOrder_rOrderId(rOrderId);
            System.out.println(agentReturnedOrderItems);

            agentReturnedOrderItems.forEach(agentReturnedOrderItem -> {
                AgentProduct agentProduct = null;
                if(agentReturnedOrder.getAgent()!= null){
                    agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(agentReturnedOrder.getAgent(), agentReturnedOrderItem.getProduct());
                }else if(agentReturnedOrder.getShop() != null){
                    agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(agentReturnedOrder.getShop(),agentReturnedOrderItem.getProduct());
                }
                if(agentProduct != null){
                    agentProduct.setFreez(agentProduct.getFreez() - agentReturnedOrderItem.getNum());
                    agentProductRepository.save(agentProduct);
                }
            });
        } else {
            agentReturnedOrder.setLastUpdateTime(new Date());
            agentReturnOrderRepository.save(agentReturnedOrder);
        }

        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult pushReturnOrderDelivery(ReturnOrderDeliveryInfo deliveryInfo, Integer agentId) {
        AgentReturnedOrder agentReturnedOrder = findOne(deliveryInfo.getOrderId());
        if (agentReturnedOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }

        AgentDelivery agentDelivery = new AgentDelivery();

        agentDelivery.setDeliveryId(SerialNo.create());
        agentDelivery.setPurchaseOrder(null);
        agentDelivery.setAgentReturnedOrder(agentReturnedOrder);
        agentDelivery.setAgentId(agentReturnedOrder.getAgent()!=null?agentReturnedOrder.getAgent().getId():null);
        agentDelivery.setShopId(agentReturnedOrder.getShop()!=null?agentReturnedOrder.getShop().getId():null);
        agentDelivery.setCustomerId(agentReturnedOrder.getMallCustomer().getCustomerId());
        agentDelivery.setType(OrderEnum.DeliveryType.RETURN.getCode());
        agentDelivery.setLogisticsName(deliveryInfo.getLogiName());
        agentDelivery.setLogisticsNo(deliveryInfo.getLogiNo());
        agentDelivery.setFreight(deliveryInfo.getFreight());
        agentDelivery.setCreateTime(new Date());
        agentDelivery.setShipName(deliveryInfo.getShipName());
        agentDelivery.setShipAddr(deliveryInfo.getShipAddr());
        agentDelivery.setShipMobile(deliveryInfo.getShipMobile());
        agentDelivery.setShipTel(deliveryInfo.getShipTel());
        agentDelivery.setMemo(deliveryInfo.getRemark());
        agentDelivery.setCustomerId(agentReturnedOrder.getMallCustomer().getCustomerId());
        if (agentReturnedOrder.getParentAgent() != null) {
            agentDelivery.setParentAgentId(agentReturnedOrder.getParentAgent().getId());
        }
        List<AgentReturnedOrderItem> agentReturnedOrderItems = agentReturnOrderItemRepository.findByReturnedOrder_rOrderId(deliveryInfo.getOrderId());
        List<AgentDeliveryItem> agentDeliveryItems = new ArrayList<>();
        for (AgentReturnedOrderItem agentReturnedOrderItem : agentReturnedOrderItems) {
            AgentDeliveryItem deliveryItem = new AgentDeliveryItem();
            AgentProduct agentProduct = null;
            if(agentReturnedOrder.getAgent() != null){
                agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(agentReturnedOrder.getAgent(), agentReturnedOrderItem.getProduct());
            }else if(agentReturnedOrder.getShop() != null){
                agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(agentReturnedOrder.getShop(),agentReturnedOrderItem.getProduct());
            }
            deliveryItem.setAgentProductId(agentProduct.getId());
            deliveryItem.setDelivery(agentDelivery);
            deliveryItem.setNum(agentReturnedOrderItem.getNum());
            deliveryItem.setProductBn(agentReturnedOrderItem.getBn());
            deliveryItem.setProductName(agentReturnedOrderItem.getName());
            deliveryItem.setProductId(deliveryItem.getProductId());
            deliveryItem.setDelivery(agentDelivery);
            agentDeliveryItems.add(deliveryItem);
        }

        agentDelivery.setDeliveryItems(agentDeliveryItems);
        agentDeliveryRepository.save(agentDelivery);

        agentReturnedOrder.setLastUpdateTime(new Date());
        agentReturnedOrder.setShipStatus(PurchaseEnum.ShipStatus.DELIVERED);
        agentReturnOrderRepository.save(agentReturnedOrder);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult receiveReturnOrder(Integer customerId, Integer authorId, String rOrderId) {
        AgentReturnedOrder subAgentReturnedOrder = findOne(rOrderId);
        if (subAgentReturnedOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }

        //若上级代理商为平台，判断平台是否有修改该采购单的权限
        //判断该平台是否有修改该采购单的权限
        if (customerId != null && !(subAgentReturnedOrder.getParentAgent() == null && subAgentReturnedOrder.getMallCustomer().getCustomerId().equals(customerId))) {
            return new ApiResult("对不起，您没有操作权限！");
        } else if (authorId != null && !(subAgentReturnedOrder.getParentAgent() != null && subAgentReturnedOrder.getParentAgent().getId().equals(authorId))) {
            //判断该代理商是否有修改该采购单的权限
            return new ApiResult("对不起，您没有操作权限！");
        }

        if (!subAgentReturnedOrder.receivable()) {
            return new ApiResult("该退货单已确认收货，无法再次确认收货！");
        }

        // 更新库存
        // 增加平台方库存，减少一级代理商库存，释放预占库存
        List<AgentReturnedOrderItem> agentReturnedOrderItems = agentReturnOrderItemRepository.findByReturnedOrder_rOrderId(rOrderId);
        if (subAgentReturnedOrder.getParentAgent() == null) {

            agentReturnedOrderItems.forEach(agentReturnedOrderItem -> {
                MallProduct mallProduct = agentReturnedOrderItem.getProduct();
                mallProduct.setFreez(mallProduct.getFreez() - agentReturnedOrderItem.getNum());
                mallProduct.setStore(mallProduct.getStore() + agentReturnedOrderItem.getNum());
                mallProductRepository.save(mallProduct);
                AgentProduct agentProduct = null;
                if(subAgentReturnedOrder.getAgent() != null){
                    agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(subAgentReturnedOrder.getAgent(),mallProduct);
                }else if(subAgentReturnedOrder.getShop() != null){
                    agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(subAgentReturnedOrder.getShop(),mallProduct);
                }
                if(agentProduct != null){
                    agentProduct.setFreez(agentProduct.getFreez() - agentReturnedOrderItem.getNum());
                    agentProduct.setStore(agentProduct.getStore() - agentReturnedOrderItem.getNum());
                    agentProductRepository.save(agentProduct);
                }
            });

        } else {// 更新上级代理商库存，上级代理商增加库存，下级代理商减少库存，释放预占库存
            agentReturnedOrderItems.forEach(agentReturnedOrderItem -> {
                MallProduct mallProduct = agentReturnedOrderItem.getProduct();
                AgentProduct parentAgentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(subAgentReturnedOrder.getParentAgent(), mallProduct);
                parentAgentProduct.setStore(parentAgentProduct.getStore() + agentReturnedOrderItem.getNum());
                agentProductRepository.save(parentAgentProduct);
                AgentProduct agentProduct = null;
                if(subAgentReturnedOrder.getAgent() != null){
                    agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(subAgentReturnedOrder.getAgent(),mallProduct);
                }else if(subAgentReturnedOrder.getShop() != null){
                    agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(subAgentReturnedOrder.getShop(),mallProduct);
                }
                if(agentProduct != null){
                    agentProduct.setFreez(agentProduct.getFreez() - agentReturnedOrderItem.getNum());
                    agentProduct.setStore(agentProduct.getStore() - agentReturnedOrderItem.getNum());
                    agentProductRepository.save(agentProduct);
                }
            });

        }
        subAgentReturnedOrder.setReceivedTime(new Date());
        agentReturnOrderRepository.save(subAgentReturnedOrder);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult payReturnOrder(Integer customerId, Integer authorId, String rOrderId) {
        AgentReturnedOrder agentReturnedOrder = findOne(rOrderId);
        if (agentReturnedOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }

        //若上级代理商为平台，判断平台是否有修改该采购单的权限
        //判断该平台是否有修改该采购单的权限
        if (customerId != null && !(agentReturnedOrder.getParentAgent() == null && agentReturnedOrder.getMallCustomer().getCustomerId().equals(customerId))) {
            return new ApiResult("对不起，您没有操作权限！");
        } else if (authorId != null && !(agentReturnedOrder.getParentAgent() != null && agentReturnedOrder.getParentAgent().getId().equals(authorId))) {
            //判断该代理商是否有修改该采购单的权限
            return new ApiResult("对不起，您没有操作权限！");
        }

        if (!agentReturnedOrder.payabled()) {
            return new ApiResult("该退货单已付款，无法再次付款！");
        }// FIXME: 2016/6/7 

        agentReturnedOrder.setPayStatus(PurchaseEnum.PayStatus.PAYED);
        agentReturnOrderRepository.save(agentReturnedOrder);

        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public ApiResult editReturnNum(Author author, Integer productId, Integer num) {
        MallProduct mallProduct = new MallProduct();
        mallProduct.setProductId(productId);
        AgentProduct agentProduct = null;
        if(author.getType() == Agent.class){
            agentProduct = agentProductRepository.findByAgentAndProductAndDisabledFalse(author.getAuthorAgent(), mallProduct);
        }else if(author.getType() == Shop.class){
            agentProduct = agentProductRepository.findByShopAndProductAndDisabledFalse(author.getAuthorShop(),mallProduct);
        }
        if(agentProduct == null){
            return new ApiResult("货品不存在");
        }
        if (agentProduct.getStore() - agentProduct.getFreez() < num) {
            return new ApiResult("库存不足");
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public HSSFWorkbook createWorkBook(List<AgentReturnedOrder> returnedOrderList) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        returnedOrderList.forEach(order -> {
            StringBuffer sb = new StringBuffer("");
            order.getOrderItemList().forEach(item -> {
                if (sb.length() != 0) {
                    sb.append("\r\n");
                }
                sb.append(item.getName());
            });
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();
            cellDescList.add(ExcelHelper.asCell(order.getROrderId()));
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
            cellDescList.add(ExcelHelper.asCell(order.getSendmentStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getStatusComment())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getAuthorComment())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getParentComment())));
            cellDescList.add(ExcelHelper.asCell(order.isDisabled() ? "已取消" : "活动"));
            rowAndCells.add(cellDescList);
        });
        return ExcelHelper.createWorkbook("退货单列表", SysConstant.RETURNED_ORDER_EXPORT_HEADER, rowAndCells);
    }
}
