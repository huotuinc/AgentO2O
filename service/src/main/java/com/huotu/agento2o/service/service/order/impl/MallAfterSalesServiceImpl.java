package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.repository.order.MallAfterSalesRepository;
import com.huotu.agento2o.service.searchable.AfterSaleSearch;
import com.huotu.agento2o.service.service.order.MallAfterSalesService;
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
 * Created by Administrator on 2016/5/12.
 */
@Service
public class MallAfterSalesServiceImpl implements MallAfterSalesService {

    @Autowired
    private MallAfterSalesRepository afterSalesRepository;

    @Override
    public Page<MallAfterSales> findAll(int pageIndex, int pageSize, Integer agentId, AfterSaleSearch afterSaleSearch) {
        Specification<MallAfterSales> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (!StringUtils.isEmpty(afterSaleSearch.getAgentType())&&afterSaleSearch.getAgentType().equalsIgnoreCase("shop")) {
                predicates.add(criteriaBuilder.equal(root.get("shop").get("id").as(Integer.class), agentId));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getAgentType())&&afterSaleSearch.getAgentType().equalsIgnoreCase("agent")) {
                predicates.add(criteriaBuilder.equal(root.get("shop").get("author").get("id").as(Integer.class), afterSaleSearch.getAgentId()));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getBeginTime())) {
                Date beginTime = StringUtil.DateFormat(afterSaleSearch.getBeginTime(), StringUtil.TIME_PATTERN);
                predicates.add(criteriaBuilder.greaterThanOrEqualTo(root.get("createTime").as(Date.class), beginTime));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getEndTime())) {
                Date endTime = StringUtil.DateFormat(afterSaleSearch.getEndTime(), StringUtil.TIME_PATTERN);
                predicates.add(criteriaBuilder.lessThanOrEqualTo(root.get("createTime").as(Date.class), endTime));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getAfterId())) {
                predicates.add(criteriaBuilder.like(root.get("afterId").as(String.class), "%" + afterSaleSearch.getAfterId() + "%"));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getOrderId())) {
                predicates.add(criteriaBuilder.like(root.get("orderId").as(String.class), "%" + afterSaleSearch.getOrderId() + "%"));
            }
            if (!StringUtils.isEmpty(afterSaleSearch.getMobile())) {
                predicates.add(criteriaBuilder.like(root.get("applyMobile").as(String.class), "%" + afterSaleSearch.getMobile() + "%"));
            }
            if (afterSaleSearch.getAfterSaleStatus() != null && afterSaleSearch.getAfterSaleStatus() != -1) {
                predicates.add(criteriaBuilder.equal(root.get("afterSaleStatus").as(AfterSaleEnum.AfterSaleStatus.class),
                        EnumHelper.getEnumType(AfterSaleEnum.AfterSaleStatus.class, afterSaleSearch.getAfterSaleStatus())));
            }
            return criteriaBuilder.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return afterSalesRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize,
                new Sort(Sort.Direction.DESC, "createTime")));
    }

    @Override
    public void updateStatus(AfterSaleEnum.AfterSaleStatus afterSaleStatus, String afterId) {

    }

    @Override
    public MallAfterSales findByAfterId(String afterId) {
        return null;
    }

    @Override
    public MallAfterSales findByProductId(int productId) {
        return null;
    }

    @Override
    public List<MallAfterSales> findByOrderId(String orderId) {
        return null;
    }

    @Override
    public int countByOrderIdAndStatus(String orderId, AfterSaleEnum.AfterSaleStatus afterSaleStatus) {
        return 0;
    }

    @Override
    public void afterSaleAgree(MallAfterSales afterSales, String message, AfterSaleEnum.AfterSaleStatus afterSaleStatus, AfterSaleEnum.AfterItemsStatus afterItemsStatus) {

    }

    @Override
    public void afterSaleRefuse(MallAfterSales afterSales, String reason) {

    }

    @Override
    public int UnhandledCount(int agentId) {
        return 0;
    }
}
