package com.huotu.agento2o.service.service.order.impl;


import com.alibaba.fastjson.JSON;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ExcelHelper;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallDelivery;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.entity.order.MallOrderItem;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.model.order.GoodCustomField;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.model.purchase.AgentProductStoreInfo;
import com.huotu.agento2o.service.repository.order.MallDeliveryRepository;
import com.huotu.agento2o.service.repository.order.MallOrderRepository;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.searchable.OrderSearchCondition;
import com.huotu.agento2o.service.service.order.MallOrderService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AiWelv on 2016/5/12.
 */
@Service
public class MallOrderServiceImpl implements MallOrderService {

    @Autowired
    private MallOrderRepository orderRepository;

    @Autowired
    private MallDeliveryRepository deliveryRepository;

    @Autowired
    private AgentProductRepository agentProductRepository;
    @Autowired
    private AgentProductService agentProductService;

    @Override
    public MallOrder findByOrderId(String orderId) {
        return orderRepository.getOne(orderId);
    }

    @Override
    public MallOrder findByShopAndOrderId(Shop shop, String orderId) {
        return orderRepository.findByShopAndOrderId(shop, orderId);
    }

    @Override
    public void updatePayStatus(String orderId, OrderEnum.PayStatus payStatus) {
        orderRepository.updatePayStatus(payStatus, orderId);
    }

    @Override
    public ApiResult updateRemark(Shop shop, String orderId, String agentMarkType, String agentMarkText) {
        MallOrder order = orderRepository.findByShopAndOrderId(shop, orderId);
        if (order == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        } else {
            order.setAgentMarkText(agentMarkText);
            order.setAgentMarkType(agentMarkType);
            orderRepository.save(order);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

//    @Override
//    public ApiResult updateRemark(Integer supplierId, String orderId, String supplierMarkType, String supplierMarkText) {
//        return null;
//    }

    @Override
    public Page<MallOrder> findAll(int pageIndex, Author author, int pageSize, OrderSearchCondition searchCondition) {
        Specification<MallOrder> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (author != null && author.getType() == Shop.class) {
//                Predicate p1 = cb.equal(root.get("shop").get("id").as(Integer.class), searchCondition.getAgentId());
//                Predicate p2 = cb.equal(root.get("beneficiaryShop").get("id").as(Integer.class), searchCondition.getAgentId());
//                judgeShipMode(searchCondition, cb, predicates, p1, p2);
                predicates.add(cb.equal(root.get("shop").get("id").as(Integer.class), author.getId()));
            } else if (author != null && author.getType() == Agent.class) {
//                Join<MallOrder, Shop> join1 = root.join(root.getModel().getSingularAttribute("shop", Shop.class), JoinType.LEFT);
//                Join<MallOrder, Shop> join2 = root.join(root.getModel().getSingularAttribute("beneficiaryShop", Shop.class), JoinType.LEFT);
//                Predicate p1 = cb.equal(join1.get("parentAuthor").get("id").as(Integer.class), searchCondition.getAgentId());
//                Predicate p2 = cb.equal(join2.get("parentAuthor").get("id").as(Integer.class), searchCondition.getAgentId());
//                judgeShipMode(searchCondition, cb, predicates, p1, p2);
                predicates.add(cb.equal(root.get("shop").get("agent").get("id").as(Integer.class), author.getId()));
            }
            predicates.add(cb.in(root.get("agentShopType")).value(OrderEnum.ShipMode.SHOP_DELIVERY).value(OrderEnum.ShipMode.PLATFORM_DELIVERY).value(OrderEnum.ShipMode.SHOP_PART_BENEFIT));
            //去除拼团未成功的
//            Join<MallOrder, MallPintuan> join = root.join(root.getModel().getSingularAttribute("pintuan", MallPintuan.class), JoinType.LEFT);
//            Predicate p1 = cb.isNull(join.get("id").as(Integer.class));
//            Predicate p2 = cb.equal(join.get("orderPintuanStatusOption").as(ActEnum.OrderPintuanStatusOption.class),
//                    ActEnum.OrderPintuanStatusOption.GROUPED);
//            predicates.add(cb.or(p1, p2));
            if (!StringUtils.isEmpty(searchCondition.getOrderId())) {
                predicates.add(cb.like(root.get("orderId").as(String.class), "%" + searchCondition.getOrderId() + "%"));
            }

            if (!StringUtil.isEmptyStr(searchCondition.getOrderItemName())) {
                predicates.add(cb.like(root.get("orderItems").get("name").as(String.class), "%" + searchCondition.getOrderItemName() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getShipName())) {
                predicates.add(cb.like(root.get("shipName").as(String.class), "%" + searchCondition.getShipName() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getShipMobile())) {
                predicates.add(cb.like(root.get("shipMobile").as(String.class), "%" + searchCondition.getShipMobile() + "%"));
            }
            if (searchCondition.getShipMode() != -1) {
                predicates.add(cb.equal(root.get("agentShopType").as(OrderEnum.ShipMode.class),
                        EnumHelper.getEnumType(OrderEnum.ShipMode.class, searchCondition.getShipMode())));
            }
            if (searchCondition.getPayStatus() != -1) {
                predicates.add(cb.equal(root.get("payStatus").as(OrderEnum.PayStatus.class),
                        EnumHelper.getEnumType(OrderEnum.PayStatus.class, searchCondition.getPayStatus())));
            }
            if (searchCondition.getShipStatus() != -1) {
                predicates.add(cb.equal(root.get("shipStatus").as(OrderEnum.ShipStatus.class),
                        EnumHelper.getEnumType(OrderEnum.ShipStatus.class, searchCondition.getShipStatus())));
            }
            if (!StringUtils.isEmpty(searchCondition.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(searchCondition.getBeginTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmpty(searchCondition.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(searchCondition.getEndTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmpty(searchCondition.getBeginPayTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("payTime").as(Date.class),
                        StringUtil.DateFormat(searchCondition.getBeginPayTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmpty(searchCondition.getEndPayTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("payTime").as(Date.class),
                        StringUtil.DateFormat(searchCondition.getEndPayTime(), StringUtil.TIME_PATTERN)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //排序
        Sort sort;
        Sort.Direction direction = searchCondition.getOrderRule() == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
        switch (searchCondition.getOrderType()) {
            case 1:
                //按支付时间
                sort = new Sort(direction, "payTime");
                break;
            case 2:
                sort = new Sort(direction, "finalAmount");
                break;
            default:
                sort = new Sort(direction, "createTime");
                break;
        }
        Page<MallOrder> page = orderRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize, sort));
        //如果是门店部分收益，只需显示收益部分
        if (page.getContent() != null && page.getContent().size() > 0) {
            page.getContent().forEach(mallOrder -> {
                if (mallOrder.getAgentShopType() != null && mallOrder.getAgentShopType() == OrderEnum.ShipMode.SHOP_PART_BENEFIT) {
                    filterMallOrder(mallOrder);
                }
            });
        }
        return page;
    }

    /**
     * 门店部分收益过滤不受益部分
     *
     * @param mallOrder
     */
    private void filterMallOrder(MallOrder mallOrder) {
        if (mallOrder != null && mallOrder.getOrderItems() != null && mallOrder.getOrderItems().size() > 0) {
            List<MallOrderItem> oldItems = mallOrder.getOrderItems();
            List<MallOrderItem> newItems = new ArrayList<>();
            oldItems.forEach(mallOrderItem -> {
                if (mallOrderItem.getShopId() != null && mallOrderItem.getShopId() > 0) {
                    newItems.add(mallOrderItem);
                }
            });
            mallOrder.setOrderItems(newItems);
            //按百分比计算订单门店部分实际总金额
            double oldAmount = oldItems.stream().mapToDouble(p -> p.getPrice() * p.getNums()).sum();
            double newAmount = newItems.stream().mapToDouble(p -> p.getPrice() * p.getNums()).sum();
            double finalAmount = (double) Math.round(newAmount / oldAmount * mallOrder.getFinalAmount() * 100) / 100;
            mallOrder.setFinalAmount(finalAmount);
        }
    }


    /**
     * 用于分页查询时判断订单发货的方式
     *
     * @param searchCondition
     * @param cb
     * @param predicates
     * @param shop
     * @param beneficiaryShop
     */
    private void judgeShipMode(OrderSearchCondition searchCondition, CriteriaBuilder cb, List<Predicate> predicates, Predicate shop, Predicate beneficiaryShop) {
        if (searchCondition.getShipMode() == 0) {
            predicates.add(shop);
        } else if (searchCondition.getShipMode() == 1) {
            predicates.add(beneficiaryShop);
        } else {
            predicates.add(cb.or(shop, beneficiaryShop));
        }
    }

    @Override
    public OrderDetailModel findOrderDetail(Author author,String orderId) {
        OrderDetailModel orderDetailModel = new OrderDetailModel();
        MallOrder orders = orderRepository.findOne(orderId);
        if (orders.getAgentShopType() != null && orders.getAgentShopType() == OrderEnum.ShipMode.SHOP_PART_BENEFIT) {
            filterMallOrder(orders);
        }
        List<MallOrderItem> mallOrderItem = orders.getOrderItems();
        List<MallDelivery> deliveryList = deliveryRepository.findByOrder_OrderIdAndTypeIgnoreCase(orderId, OrderEnum.DeliveryType.DEVERY.getCode());
        List<MallDelivery> refundList = deliveryRepository.findByOrder_OrderIdAndTypeIgnoreCase(orderId, OrderEnum.DeliveryType.RETURN.getCode());
        orderDetailModel.setOrderId(orders.getOrderId());
        if (deliveryList != null && deliveryList.size() > 0) {
            orderDetailModel.setDeliveryList(deliveryList);
        }
        if (refundList != null && refundList.size() > 0) {
            orderDetailModel.setRefundsList(refundList);
        }
        if(mallOrderItem != null && mallOrderItem.size() > 0){
            //如果当前用户是门店，则显示库存信息
            if(author.getType() == Shop.class){
                mallOrderItem.forEach(p->{
                    AgentProductStoreInfo agentProductStoreInfo = agentProductRepository.findUsableNumByShopAndProduct(p.getShopId(),p.getProduct().getProductId());
                    if(agentProductStoreInfo != null){
                        p.setStore(agentProductStoreInfo.getStore());
                        p.setFreez(agentProductStoreInfo.getFreeze());
                    }
                });
            }
        }
        orderDetailModel.setShipName(orders.getShipName());
        orderDetailModel.setShipTel(orders.getShipTel());
        orderDetailModel.setShipMobile(orders.getShipMobile());
        orderDetailModel.setShipArea(orders.getShipArea());
        orderDetailModel.setShipAddr(orders.getShipAddr());
        orderDetailModel.setFinalAmount(orders.getFinalAmount());
        orderDetailModel.setCostFreight(orders.getCostFreight());
        orderDetailModel.setPmtAmount(orders.getPmtAmount());
        orderDetailModel.setOrderSourceType(orders.getOrderSourceType());
        orderDetailModel.setCreateTime(StringUtil.DateFormat(orders.getCreateTime(), StringUtil.TIME_PATTERN));
        orderDetailModel.setPayTime(StringUtil.DateFormat(orders.getPayTime(), StringUtil.TIME_PATTERN));
        orderDetailModel.setSupOrderItemList(mallOrderItem);
        orderDetailModel.setRemark(orders.getRemark());
        orderDetailModel.setMemo(orders.getMemo());
        orderDetailModel.setAgentRemark(orders.getAgentMarkText());

        double costPrice = 0;
        for (MallOrderItem orderItem : mallOrderItem) {
            costPrice += orderItem.getCost() * orderItem.getNums();
        }
        orderDetailModel.setCostPrice((double) Math.round(costPrice * 100) / 100);
        return orderDetailModel;
    }

    @Override
    public HSSFWorkbook createWorkBook(List<MallOrder> orders) {
        List<List<ExcelHelper.CellDesc>> rowAndCells = new ArrayList<>();
        orders.forEach(order -> {
            List<ExcelHelper.CellDesc> cellDescList = new ArrayList<>();
            cellDescList.add(ExcelHelper.asCell(order.getOrderId()));
            cellDescList.add(ExcelHelper.asCell(order.getOrderName()));
            cellDescList.add(ExcelHelper.asCell(order.getOrderStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getItemNum(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(StringUtil.DateFormat(order.getCreateTime(), StringUtil.TIME_PATTERN)));
            cellDescList.add(ExcelHelper.asCell(StringUtil.DateFormat(order.getPayTime(), StringUtil.TIME_PATTERN)));
            cellDescList.add(ExcelHelper.asCell(order.getPayStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getShipStatus().getValue()));
            cellDescList.add(ExcelHelper.asCell(order.getFinalAmount(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(order.getCostFreight(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(order.getPmtAmount(), Cell.CELL_TYPE_NUMERIC));
            cellDescList.add(ExcelHelper.asCell(order.getShipName()));
            cellDescList.add(ExcelHelper.asCell(order.getShipMobile()));
            cellDescList.add(ExcelHelper.asCell(order.getShipAddr()));
            cellDescList.add(ExcelHelper.asCell(order.getBnList()));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getMemo())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getRemark())));
            cellDescList.add(ExcelHelper.asCell(StringUtil.getNullStr(order.getAgentMarkText())));
            List<GoodCustomField> goodCustomFields = new ArrayList<>();
            order.getOrderItems().forEach(item -> {
                if (!StringUtils.isEmpty(item.getCustomFieldValues())) {
                    GoodCustomField goodCustomField = JSON.parseObject(item.getCustomFieldValues(), GoodCustomField.class);
                    goodCustomFields.add(goodCustomField);
                }
            });
            cellDescList.add(ExcelHelper.asCell(JSON.toJSONString(goodCustomFields)));

            rowAndCells.add(cellDescList);
        });
        return ExcelHelper.createWorkbook("订单列表", SysConstant.ORDER_EXPORT_HEADER, rowAndCells);
    }

}
