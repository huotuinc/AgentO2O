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
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.common.SettlementEnum;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.settlement.Settlement;
import com.huotu.agento2o.service.entity.settlement.SettlementOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.repository.order.MallOrderRepository;
import com.huotu.agento2o.service.repository.settlement.SettlementRepository;
import com.huotu.agento2o.service.searchable.SettlementSearcher;
import com.huotu.agento2o.service.service.settlement.SettlementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.ss.formula.functions.Na;
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
    private MallOrderRepository orderRepository;

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
        if(settlementOrders == null || settlementOrders.size() == 0){
            return ;
        }
        //即使结算金额为0也结算
        double finalAmount = settlementOrders.stream().mapToDouble(p->p.getFinalAmount()).sum();
        double freight = settlementOrders.stream().mapToDouble(p->p.getFreight()).sum();
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
        // TODO: 2016/6/8 test
        settlementRepository.save(settlement);
        log.info("shopId:" + shop.getId() + ",customerId:" + shop.getCustomer().getCustomerId() + "settlementNo:" + settlement.getSettlementNo() + ",finalAmount:" + finalAmount + ",freight:" + freight);
        orderRepository.updateSettle(shop.getId(),shop.getCustomer().getCustomerId(),settleTime, OrderEnum.PayStatus.REFUNDING);
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
    public Page<Settlement> getPage(Integer shopId, SettlementSearcher settlementSearcher) {
        Specification<Settlement> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            predicates.add(criteriaBuilder.equal(root.get("shop").get("id").as(Integer.class), shopId));
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
        return null;
    }

    @Override
    public Settlement save(Settlement settlement) {
        return settlementRepository.save(settlement);
    }

    @Override
    @Transactional
    public ApiResult updateSettlementStatus(String settlementNo, int customerStatus, int supplierStatus, String settlementComment) throws Exception {
        return null;
    }
}
