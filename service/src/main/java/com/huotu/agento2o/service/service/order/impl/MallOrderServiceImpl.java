package com.huotu.agento2o.service.service.order.impl;


import com.alibaba.fastjson.JSON;
import com.hot.datacenter.common.EnumHelper;
import com.hot.datacenter.entity.order.Delivery;
import com.hot.datacenter.entity.order.MallOrder;
import com.hot.datacenter.entity.order.OrderItem;
import com.hot.datacenter.ienum.OrderEnum;
import com.hot.datacenter.repository.order.DeliveryRepository;
import com.hot.datacenter.service.AbstractCusCrudService;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ExcelHelper;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.model.order.GoodCustomField;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.repository.order.CusOrderRepository;
import com.huotu.agento2o.service.searchable.CusOrderSearch;
import com.huotu.agento2o.service.service.order.MallOrderService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.annotation.PostConstruct;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AiWelv on 2016/5/12.
 */
@Service
public class MallOrderServiceImpl extends AbstractCusCrudService<MallOrder, String, CusOrderSearch> implements MallOrderService {

    @Autowired
    private CusOrderRepository orderRepository;

    @Autowired
    private DeliveryRepository deliveryRepository;

    @PostConstruct
    private void init() {
        initRepository(MallOrder.class);
    }

    @Override
    public MallOrder findByShopAndOrderId(ShopAuthor shop, String orderId) {
        return orderRepository.findByShopAndOrderId(shop, orderId);
    }

    @Override
    public void updatePayStatus(String orderId, OrderEnum.PayStatus payStatus) {
        orderRepository.updatePayStatus(payStatus, orderId);
    }

    @Override
    public ApiResult updateRemark(ShopAuthor shop, String orderId, String agentMarkType, String agentMarkText) {
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
    public Page<MallOrder> findAll(int pageIndex, Author author, int pageSize, CusOrderSearch orderSearch) {

        //排序
        Sort sort;
        Sort.Direction direction = orderSearch.getOrderRule() == 0 ? Sort.Direction.DESC : Sort.Direction.ASC;
        switch (orderSearch.getOrderType()) {
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
        return findAll(orderSearch, new PageRequest(pageIndex - 1, pageSize, sort));
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
    private void judgeShipMode(CusOrderSearch searchCondition, CriteriaBuilder cb, List<Predicate> predicates, Predicate shop, Predicate beneficiaryShop) {
        if (searchCondition.getShipMode() == 0) {
            predicates.add(shop);
        } else if (searchCondition.getShipMode() == 1) {
            predicates.add(beneficiaryShop);
        } else {
            predicates.add(cb.or(shop, beneficiaryShop));
        }
    }

    @Override
    public OrderDetailModel findOrderDetail(String orderId) {
        OrderDetailModel orderDetailModel = new OrderDetailModel();
        MallOrder orders = orderRepository.findOne(orderId);
        List<OrderItem> mallOrderItem = orders.getOrderItems();
        List<Delivery> deliveryList = deliveryRepository.findByOrder_OrderIdAndTypeIgnoreCase(orderId, OrderEnum.DeliveryType.DEVERY.getCode());
        List<Delivery> refundList = deliveryRepository.findByOrder_OrderIdAndTypeIgnoreCase(orderId, OrderEnum.DeliveryType.RETURN.getCode());
        orderDetailModel.setOrderId(orders.getOrderId());
        if (deliveryList != null && deliveryList.size() > 0) {
            orderDetailModel.setDeliveryList(deliveryList);
        }
        if (refundList != null && refundList.size() > 0) {
            orderDetailModel.setRefundsList(refundList);
        }
        orderDetailModel.setShipName(orders.getShipName());
        orderDetailModel.setShipTel(orders.getShipTel());
        orderDetailModel.setShipMobile(orders.getShipMobile());
        orderDetailModel.setShipArea(orders.getShipArea());
        orderDetailModel.setShipAddr(orders.getShipAddr());
        orderDetailModel.setFinalAmount(orders.getFinalAmount());
        orderDetailModel.setCostFreight(orders.getCostFreight());
        orderDetailModel.setCreateTime(StringUtil.DateFormat(orders.getCreateTime(), StringUtil.TIME_PATTERN));
        orderDetailModel.setPayTime(StringUtil.DateFormat(orders.getPayTime(), StringUtil.TIME_PATTERN));
        orderDetailModel.setSupOrderItemList(mallOrderItem);
        orderDetailModel.setRemark(orders.getRemark());
        orderDetailModel.setMemo(orders.getMemo());
        orderDetailModel.setAgentRemark(orders.getAgentMarkText());

        double costPrice = 0;
        for (OrderItem orderItem : mallOrderItem) {
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


    @Override
    public Specification<MallOrder> specification(CusOrderSearch orderSearch) {

        Specification<MallOrder> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            String field = null;
            if (orderSearch.getAuthor() != null && orderSearch.getAuthor().getAuthType() == ShopAuthor.class) {
                field = "shopId";
            } else if (orderSearch.getAuthor() != null && orderSearch.getAuthor().getAuthType() == Agent.class) {
                field = "agentId";
            }
            predicates.add(cb.equal(root.get(field).as(Integer.class), orderSearch.getAuthor().getId()));
            cb.in(root.get("agentShopType")).value(OrderEnum.ShipMode.SHOP_DELIVERY).value(OrderEnum.ShipMode.PLATFORM_DELIVERY);
            if (!StringUtils.isEmpty(orderSearch.getOrderId())) {
                predicates.add(cb.like(root.get("orderId").as(String.class), "%" + orderSearch.getOrderId() + "%"));
            }

            if (!StringUtil.isEmptyStr(orderSearch.getOrderItemName())) {
                predicates.add(cb.like(root.get("orderItems").get("name").as(String.class), "%" + orderSearch.getOrderItemName() + "%"));
            }
            if (!StringUtils.isEmpty(orderSearch.getShipName())) {
                predicates.add(cb.like(root.get("shipName").as(String.class), "%" + orderSearch.getShipName() + "%"));
            }
            if (!StringUtils.isEmpty(orderSearch.getShipMobile())) {
                predicates.add(cb.like(root.get("shipMobile").as(String.class), "%" + orderSearch.getShipMobile() + "%"));
            }
            if (orderSearch.getPayStatus() != -1) {
                predicates.add(cb.equal(root.get("payStatus").as(OrderEnum.PayStatus.class),
                        EnumHelper.getEnumType(OrderEnum.PayStatus.class, orderSearch.getPayStatus())));
            }
            if (orderSearch.getShipStatus() != -1) {
                predicates.add(cb.equal(root.get("shipStatus").as(OrderEnum.ShipStatus.class),
                        EnumHelper.getEnumType(OrderEnum.ShipStatus.class, orderSearch.getShipStatus())));
            }
            if (!StringUtils.isEmpty(orderSearch.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(orderSearch.getBeginTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmpty(orderSearch.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(orderSearch.getEndTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmpty(orderSearch.getBeginPayTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("payTime").as(Date.class),
                        StringUtil.DateFormat(orderSearch.getBeginPayTime(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtil.isEmpty(orderSearch.getEndPayTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("payTime").as(Date.class),
                        StringUtil.DateFormat(orderSearch.getEndPayTime(), StringUtil.TIME_PATTERN)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };

        return specification;
    }
}
