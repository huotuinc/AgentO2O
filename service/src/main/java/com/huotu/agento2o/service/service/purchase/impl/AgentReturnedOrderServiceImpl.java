package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderRepository;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

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

    @Override
    public List<AgentProduct> findAgentProductsByAgentId(Integer agentId) {
        return agentProductRepository.findAgentProductByAgent_Id(agentId);
    }

    @Override
    public AgentReturnedOrder addReturnOrder(AgentReturnedOrder agentReturnedOrder) {
        return agentReturnOrderRepository.save(agentReturnedOrder);
    }

    @Override
    public Page<AgentReturnedOrder>  findAll(int pageIndex, int pageSize, Author author, ReturnedOrderSearch returnedOrderSearch) {


        Specification<AgentReturnedOrder> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (author != null) {
                predicates.add(cb.equal(root.get("author").get("id").as(Integer.class),returnedOrderSearch.getAgentId()));
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

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //排序

        return agentReturnOrderRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize));

    }
}
