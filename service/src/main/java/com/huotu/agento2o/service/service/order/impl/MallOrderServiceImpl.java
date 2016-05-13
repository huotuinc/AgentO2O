package com.huotu.agento2o.service.service.order.impl;


import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.ienum.OrderEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.repository.order.MallOrderRepository;
import com.huotu.agento2o.service.searchable.OrderSearchCondition;
import com.huotu.agento2o.service.service.order.MallOrderService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;


import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AiWelv on 2016/5/12.
 */
@Service
public class MallOrderServiceImpl implements MallOrderService{

    @Autowired
    private MallOrderRepository orderRepository;

    @Override
    public MallOrder findByOrderId(String orderId) {
        return null;
    }

    @Override
    public MallOrder findBySupplierIdAndOrderId(Integer agentId, String orderId) {
        return null;
    }

    @Override
    public void updatePayStatus(String orderId, OrderEnum.PayStatus payStatus) {

    }

//    @Override
//    public ApiResult updateRemark(Integer supplierId, String orderId, String supplierMarkType, String supplierMarkText) {
//        return null;
//    }

    @Override
    public Page<MallOrder> findAll(int pageIndex, int pageSize, OrderSearchCondition searchCondition) {
        Specification<MallOrder> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(searchCondition.getAgentType())&&searchCondition.getAgentType().equalsIgnoreCase("shop")) {
                predicates.add(cb.equal(root.get("shop").get("id").as(Long.class), searchCondition.getAgentId()));
            }
            //去除拼团未成功的
//            Join<MallOrder, MallPintuan> join = root.join(root.getModel().getSingularAttribute("pintuan", MallPintuan.class), JoinType.LEFT);
//            Predicate p1 = cb.isNull(join.get("id").as(Integer.class));
//            Predicate p2 = cb.equal(join.get("orderPintuanStatusOption").as(ActEnum.OrderPintuanStatusOption.class),
//                    ActEnum.OrderPintuanStatusOption.GROUPED);
//            predicates.add(cb.or(p1, p2));
            if (!StringUtils.isEmpty(searchCondition.getOrderId())) {
                predicates.add(cb.like(root.get("orderId").as(String.class), "%" + searchCondition.getOrderId() + "%"));
            }
            if (!StringUtils.isEmpty(searchCondition.getAgentType())&&searchCondition.getAgentType().equalsIgnoreCase("agent")) {
                predicates.add(cb.equal(root.get("shop").get("author").get("id").as(Integer.class), searchCondition.getAgentId()));
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
        return orderRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize, sort));
    }

    @Override
    public OrderDetailModel findOrderDetail(String orderId) {
        return null;
    }

    @Override
    public HSSFWorkbook createWorkBook(List<MallOrder> orders) {
        return null;
    }
}
