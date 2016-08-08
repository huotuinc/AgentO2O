/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.settlement.impl;

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.SettlementEnum;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.*;
import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.entity.settlement.Settlement;
import com.huotu.agento2o.service.entity.settlement.SettlementOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.repository.config.InvoiceConfigRepository;
import com.huotu.agento2o.service.repository.order.*;
import com.huotu.agento2o.service.repository.settlement.AccountRepository;
import com.huotu.agento2o.service.repository.settlement.SettlementOrderRepository;
import com.huotu.agento2o.service.repository.settlement.SettlementRepository;
import com.huotu.agento2o.service.searchable.SettlementSearcher;
import com.huotu.agento2o.service.service.settlement.SettlementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
 * Created by helloztt on 2016/6/8.
 */
@Service
public class SettlementServiceImpl implements SettlementService {
    private static Log log = LogFactory.getLog(SettlementServiceImpl.class);

    @Autowired
    private SettlementRepository settlementRepository;
    @Autowired
    private SettlementOrderRepository settlementOrderRepository;
    @Autowired
    private MallOrderRepository orderRepository;
    @Autowired
    private MallOrderItemRepository orderItemRepository;
    @Autowired
    private MallDeliveryRepository deliveryRepository;
    @Autowired
    private MallRefundsRepository refundsRepository;
    @Autowired
    private MallPaymentsRepository paymentsRepository;
    @Autowired
    private MallCustomerRepository customerRepository;
    @Autowired
    private AccountRepository accountRepository;
    @Autowired
    private InvoiceConfigRepository invoiceConfigRepository;

    /**
     * 得到结算单
     *
     * @param shop
     * @param settleTime
     * @return
     */
    @Override
    @Transactional
    public void settle(Shop shop, Date settleTime) throws Exception {
        List<SettlementOrder> settlementOrders = settlementRepository.findSettlementOrder(shop.getId(), shop.getCustomer().getCustomerId(), settleTime);
        if (settlementOrders == null || settlementOrders.size() == 0) {
            return;
        }
        //即使结算金额为0也结算
        double finalAmount = settlementOrders.stream().mapToDouble(p -> p.getFinalAmount()).sum();
        double freight = settlementOrders.stream().mapToDouble(p -> p.getFreight()).sum();
        Settlement settlement = new Settlement();
        settlement.setSettlementNo(SerialNo.create());
        settlement.setShop(shop);
        settlement.setCustomerId(shop.getCustomer().getCustomerId());
        settlement.setCustomerStatus(SettlementEnum.SettlementCheckStatus.NOT_CHECK);
        settlement.setAuthorStatus(SettlementEnum.SettlementCheckStatus.NOT_CHECK);
        settlement.setCreateDateTime(settleTime);
        settlement.setFinalAmount((double) Math.round(finalAmount * 100) / 100);
        settlement.setFreight((double) Math.round(freight * 100) / 100);
        settlement.setSettlementOrders(settlementOrders);
        settlement = settlementRepository.save(settlement);
        for (SettlementOrder settlementOrder : settlementOrders) {
            settlementOrder.setSettlement(settlement);
        }
        settlementOrderRepository.save(settlementOrders);
        log.info("shopId:" + shop.getId() + ",customerId:" + shop.getCustomer().getCustomerId() + "settlementNo:" + settlement.getSettlementNo() + ",finalAmount:" + finalAmount + ",freight:" + freight);
        orderRepository.updateSettle(shop.getId(), shop.getCustomer().getCustomerId(), settleTime, OrderEnum.PayStatus.REFUNDING);
        log.info("updateSettle:" + shop.getId() + "," + shop.getCustomer().getCustomerId() + "," + settlement + OrderEnum.PayStatus.REFUNDING.getCode());
    }

    @Override
    public Settlement findById(Integer id) {
        return settlementRepository.findOne(id);
    }

    @Override
    public Settlement findBySettlementNo(String settlementNo) {
        return settlementRepository.findBySettlementNo(settlementNo);
    }

    @Override
    public int unhandledSettlementCount(Integer shopId) {
        return settlementRepository.countByShop_IdAndAuthorStatusAndCustomerStatusNot(shopId, SettlementEnum.SettlementCheckStatus.NOT_CHECK, SettlementEnum.SettlementCheckStatus.RETURNED);
    }

    @Override
    public Page<Settlement> getPage(SettlementSearcher settlementSearcher) {
        Specification<Settlement> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (settlementSearcher.getShopId() != null && settlementSearcher.getShopId() != 0) {
                predicates.add(criteriaBuilder.equal(root.get("shop").get("id").as(Integer.class), settlementSearcher.getShopId()));
            } else if (settlementSearcher.getAgentId() != null && settlementSearcher.getAgentId() != 0) {
                predicates.add(criteriaBuilder.equal(root.get("agent").get("id").as(Integer.class), settlementSearcher.getShopId()));
            }
            if (settlementSearcher != null && !StringUtils.isEmpty(settlementSearcher.getSettlementNo())) {
                predicates.add(criteriaBuilder.like(root.get("settlementNo").as(String.class), "%" + settlementSearcher.getSettlementNo() + "%"));
            }
            if (settlementSearcher != null && settlementSearcher.getCustomerStatus() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("customerStatus").as(SettlementEnum.SettlementCheckStatus.class),
                        EnumHelper.getEnumType(SettlementEnum.SettlementCheckStatus.class, settlementSearcher.getCustomerStatus())));
            }
            if (settlementSearcher != null && settlementSearcher.getAuthorStatus() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("authorStatus").as(SettlementEnum.SettlementCheckStatus.class),
                        EnumHelper.getEnumType(SettlementEnum.SettlementCheckStatus.class, settlementSearcher.getAuthorStatus())));
            }
            if (!StringUtils.isEmpty(settlementSearcher.getCreateStartDate())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDateTime").as(Date.class),
                        StringUtil.DateFormat(settlementSearcher.getCreateStartDate(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtils.isEmpty(settlementSearcher.getCreateEndDate())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDateTime").as(Date.class),
                        StringUtil.DateFormat(settlementSearcher.getCreateEndDate(), StringUtil.TIME_PATTERN)));
            }
            if (settlementSearcher != null && !StringUtils.isEmpty(settlementSearcher.getStartDate())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("settlementDate").as(Date.class),
                        StringUtil.DateFormat(settlementSearcher.getStartDate(), StringUtil.TIME_PATTERN)));
            }
            if (settlementSearcher != null && !StringUtils.isEmpty(settlementSearcher.getEndDate())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("settlementDate").as(Date.class),
                        StringUtil.DateFormat(settlementSearcher.getEndDate(), StringUtil.TIME_PATTERN)));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return settlementRepository.findAll(specification, new PageRequest(settlementSearcher.getPageNo() - 1, settlementSearcher.getPageSize(), new Sort(Sort.Direction.DESC, "createDateTime")));
    }

    @Override
    public Page<Settlement> getCustomerPage(Integer customerId, SettlementSearcher settlementSearcher) {
        Specification<Settlement> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("customerId").as(Integer.class), customerId));
            if (settlementSearcher != null && !StringUtils.isEmpty(settlementSearcher.getSettlementNo())) {
                predicates.add(criteriaBuilder.like(root.get("settlementNo").as(String.class), "%" + settlementSearcher.getSettlementNo() + "%"));
            }
            if (settlementSearcher != null && settlementSearcher.getCustomerStatus() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("customerStatus").as(SettlementEnum.SettlementCheckStatus.class),
                        EnumHelper.getEnumType(SettlementEnum.SettlementCheckStatus.class, settlementSearcher.getCustomerStatus())));
            }
            if (settlementSearcher != null && settlementSearcher.getAuthorStatus() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("authorStatus").as(SettlementEnum.SettlementCheckStatus.class),
                        EnumHelper.getEnumType(SettlementEnum.SettlementCheckStatus.class, settlementSearcher.getAuthorStatus())));
            }
            if (!StringUtils.isEmpty(settlementSearcher.getStartDate())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("settlementDate").as(Date.class),
                        StringUtil.DateFormat(settlementSearcher.getStartDate(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtils.isEmpty(settlementSearcher.getEndDate())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("settlementDate").as(Date.class),
                        StringUtil.DateFormat(settlementSearcher.getEndDate(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtils.isEmpty(settlementSearcher.getCreateStartDate())) {
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createDateTime").as(Date.class),
                        StringUtil.DateFormat(settlementSearcher.getCreateStartDate(), StringUtil.TIME_PATTERN)));
            }
            if (!StringUtils.isEmpty(settlementSearcher.getCreateEndDate())) {
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createDateTime").as(Date.class),
                        StringUtil.DateFormat(settlementSearcher.getCreateEndDate(), StringUtil.TIME_PATTERN)));
            }
            if (settlementSearcher.getShopId() != null && settlementSearcher.getShopId() != 0) {
                predicates.add(criteriaBuilder.equal(root.get("shop").get("id").as(Integer.class), settlementSearcher.getShopId()));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return settlementRepository.findAll(specification, new PageRequest(settlementSearcher.getPageNo() - 1, settlementSearcher.getPageSize(), new Sort(Sort.Direction.DESC, "createDateTime")));
    }

    @Override
    public OrderDetailModel findOrderDetail(String orderId) {
        OrderDetailModel orderDetailModel = new OrderDetailModel();
        MallOrder orders = orderRepository.findOne(orderId);
        List<MallOrderItem> mallOrderItem = orderItemRepository.findByOrder_OrderId(orderId);
        List<MallDelivery> deliveryList = deliveryRepository.findByOrder_OrderIdAndTypeIgnoreCase(orderId, OrderEnum.DeliveryType.DEVERY.getCode());
        List<MallDelivery> refundList = deliveryRepository.findByOrder_OrderIdAndTypeIgnoreCase(orderId, OrderEnum.DeliveryType.RETURN.getCode());
        List<MallRefunds> refundsMoneyList = refundsRepository.findByOrderId(orderId);
        List<MallPayments> paymentsList = paymentsRepository.findByOrderId(orderId);
        orderDetailModel.setOrderId(orders.getOrderId());
        if (deliveryList != null && deliveryList.size() > 0) {
            orderDetailModel.setDeliveryList(deliveryList);
        }
        if (refundList != null && refundList.size() > 0) {
            orderDetailModel.setRefundsList(refundList);
        }
        if (refundsMoneyList != null && refundsMoneyList.size() > 0) {
            orderDetailModel.setRefundsMoneyList(refundsMoneyList);
        }
        if (paymentsList != null && paymentsList.size() > 0) {
            orderDetailModel.setPaymentsList(paymentsList);
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
        for (MallOrderItem orderItem : mallOrderItem) {
            costPrice += orderItem.getCost() * orderItem.getNums();
        }
        orderDetailModel.setCostPrice((double) Math.round(costPrice * 100) / 100);
        return orderDetailModel;
    }

    @Override
    public Settlement save(Settlement settlement) {
        return settlementRepository.save(settlement);
    }

    @Override
    @Transactional
    public ApiResult updateSettlementStatus(String settlementNo, int customerStatus, int authorStatus, String settlementComment) throws Exception {
        Settlement settlement = settlementRepository.findBySettlementNo(settlementNo);
        SettlementEnum.SettlementCheckStatus customerSettlementCheckStatus = settlement.getCustomerStatus(), authorSettlementCheckStatus = settlement.getAuthorStatus();
        if (settlement == null) {
            return new ApiResult("结算单编号错误！");
        }
        if ((customerStatus != -1 && settlement.getCustomerStatus() != SettlementEnum.SettlementCheckStatus.NOT_CHECK)
                || (authorStatus != -1 && settlement.getAuthorStatus() != SettlementEnum.SettlementCheckStatus.NOT_CHECK)) {
            return new ApiResult("结算单已审核，无法操作！");
        }
        //修改分销商和门店的审核状态
        if (customerStatus != -1) {
            customerSettlementCheckStatus = EnumHelper.getEnumType(SettlementEnum.SettlementCheckStatus.class, customerStatus);
            //如果门店已审核不通过，则不做处理
            if (settlement.getAuthorStatus() == SettlementEnum.SettlementCheckStatus.RETURNED) {
                return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
            }
        }
        if (authorStatus != -1) {
            authorSettlementCheckStatus = EnumHelper.getEnumType(SettlementEnum.SettlementCheckStatus.class, authorStatus);
            //如果分销商已审核不通过，则不做处理
            if (settlement.getCustomerStatus() == SettlementEnum.SettlementCheckStatus.RETURNED) {
                return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
            }
        }
        //如果审核不通过，记录备注
        if (settlementComment != null && settlementComment.length() > 0) {
            settlement.setRemark(settlementComment);
        }
        //如果分销商或门店审核不通过，则通知伙伴商城
        if (customerSettlementCheckStatus == SettlementEnum.SettlementCheckStatus.RETURNED || authorSettlementCheckStatus == SettlementEnum.SettlementCheckStatus.RETURNED) {
            orderRepository.resetSettle(settlement.getShop().getId(), settlement.getCustomerId(),
                    settlement.getCreateDateTime(), SettlementEnum.SettlementStatusEnum.READY_SETTLE.getCode());
            log.info("returned:" + settlement.getShop().getId() + "," + settlement.getCustomerId() + "," +
                    settlement.getCreateDateTime() + "," + SettlementEnum.SettlementStatusEnum.READY_SETTLE);
        }
        //分销商和门店均审核通过
        Shop shop = settlement.getShop();
        if (customerSettlementCheckStatus == SettlementEnum.SettlementCheckStatus.CHECKED && authorSettlementCheckStatus == SettlementEnum.SettlementCheckStatus.CHECKED) {
            Account shopAccount = accountRepository.findByShop(shop);
            //审核通过，待提货款增加
            shopAccount.setBalance((double) Math.round((shopAccount.getBalance() + settlement.getFinalAmount()) * 100) / 100);
            accountRepository.save(shopAccount);
            settlement.setSettlementDate(new Date());
            orderRepository.updateSettle(settlement.getShop().getId(), settlement.getCustomerId(),
                    settlement.getCreateDateTime(), OrderEnum.PayStatus.REFUNDING);
            log.info("shopId:" + settlement.getShop().getId() + ",customerId:" + settlement.getCustomerId() + "date:" +
                    settlement.getCreateDateTime());
        }
        settlement.setCustomerStatus(customerSettlementCheckStatus);
        settlement.setAuthorStatus(authorSettlementCheckStatus);
        settlementRepository.save(settlement);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
