package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallAfterSalesItem;
import com.huotu.agento2o.service.repository.order.MallAfterSalesRepository;
import com.huotu.agento2o.service.searchable.AfterSaleSearch;
import com.huotu.agento2o.service.service.order.MallAfterSalesItemService;
import com.huotu.agento2o.service.service.order.MallAfterSalesService;
import com.huotu.agento2o.service.service.order.MallOrderService;
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
 * Created by Administrator on 2016/5/12.
 */
@Service
public class MallAfterSalesServiceImpl implements MallAfterSalesService {

    @Autowired
    private MallAfterSalesRepository afterSalesRepository;
    @Autowired
    private MallAfterSalesItemService afterSalesItemService;
    @Autowired
    private MallOrderService orderService;

    @Override
    public Page<MallAfterSales> findAll(int pageIndex, Author author, int pageSize, Integer agentId, AfterSaleSearch afterSaleSearch) {
        Specification<MallAfterSales> specification = (root, criteriaQuery, criteriaBuilder) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (author != null && author instanceof Shop) {
                predicates.add(criteriaBuilder.or(criteriaBuilder.equal(root.get("shop").get("id").as(Integer.class), agentId),
                        criteriaBuilder.equal(root.get("beneficiaryShop").get("id").as(Integer.class), agentId)));
            } else if (author != null && author instanceof Agent) {
                Join<MallAfterSales,Shop> join1 = root.join(root.getModel().getSingularAttribute("shop",Shop.class), JoinType.LEFT);
                Join<MallAfterSales,Shop> join2 = root.join(root.getModel().getSingularAttribute("beneficiaryShop",Shop.class),JoinType.LEFT);
                Predicate p1 = criteriaBuilder.equal(join1.get("parentAuthor").get("id").as(Integer.class),afterSaleSearch.getAgentId());
                Predicate p2 = criteriaBuilder.equal(join2.get("parentAuthor").get("id").as(Integer.class),afterSaleSearch.getAgentId());
                predicates.add(criteriaBuilder.or(p1,p2));
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
    @Transactional(value = "transactionManager")
    public void updateStatus(AfterSaleEnum.AfterSaleStatus afterSaleStatus, String afterId) {
        afterSalesRepository.updateStatus(afterSaleStatus, afterId);
        afterSalesRepository.flush();
    }

    @Override
    public MallAfterSales findByAfterId(String afterId) {
        return afterSalesRepository.findOne(afterId);
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
    @Transactional(value = "transactionManager")
    public void afterSaleAgree(MallAfterSales afterSales, String message, AfterSaleEnum.AfterSaleStatus afterSaleStatus, AfterSaleEnum.AfterItemsStatus afterItemsStatus) {
        //更改售后表状态
        this.updateStatus(afterSaleStatus, afterSales.getAfterId());
        //更改协商表
        MallAfterSalesItem afterSalesItem = afterSalesItemService.findTopByIsLogic(afterSales, AfterSaleEnum.AfterSalesIsLogis.MESSAGE.getCode());
        afterSalesItem.setAfterItemsStatus(afterItemsStatus);
        afterSalesItem.setReply(message);
        afterSalesItem.setReplyTime(new Date());
        afterSalesItemService.save(afterSalesItem);
    }

    @Override
    @Transactional(value = "transactionManager")
    public void afterSaleRefuse(MallAfterSales afterSales, String reason) {
        Date now = new Date();
        this.updateStatus(AfterSaleEnum.AfterSaleStatus.AFTER_SALE_REFUSED, afterSales.getAfterId());
        //更改协商表
        MallAfterSalesItem afterSalesItem = afterSalesItemService.findTopByIsLogic(afterSales, AfterSaleEnum.AfterSalesIsLogis.MESSAGE.getCode());
        if (StringUtils.isEmpty(afterSalesItem.getReply())) {
            afterSalesItem.setAfterItemsStatus(AfterSaleEnum.AfterItemsStatus.AFTER_SALE_REFUSED);
            afterSalesItem.setReply(reason);
            afterSalesItem.setReplyTime(now);
        } else {
            afterSalesItem = new MallAfterSalesItem();
            afterSalesItem.setAfterContext("");
            afterSalesItem.setAfterSales(afterSales);
            afterSalesItem.setAfterMoney(0);
            afterSalesItem.setAfterMobile("");
            afterSalesItem.setAfterSalesReason(AfterSaleEnum.AfterSalesReason.UN_SELECT);
            afterSalesItem.setAfterSaleType(AfterSaleEnum.AfterSaleType.UN_SELECT);
            afterSalesItem.setAfterItemsStatus(AfterSaleEnum.AfterItemsStatus.AFTER_SALE_REFUSED);
            afterSalesItem.setApplyTime(now);
            afterSalesItem.setIsLogic(AfterSaleEnum.AfterSalesIsLogis.MESSAGE.getCode());
            afterSalesItem.setReply(reason);
            afterSalesItem.setReplyTime(now);
        }
        afterSalesItemService.save(afterSalesItem);
        //更改分销商订单状态
        orderService.updatePayStatus(afterSales.getOrderId(), afterSales.getPayStatus());
    }

    @Override
    public int UnhandledCount(int agentId) {
        return 0;
    }
}
