package com.huotu.agento2o.service.service.order.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallDelivery;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.model.order.LogiModel;
import com.huotu.agento2o.service.repository.order.MallDeliveryRepository;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.service.order.MallDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by AiWelv on 2016/5/12.
 */
@Service
public class MallDeliveryServiceImpl implements MallDeliveryService{

    @Autowired
    private MallDeliveryRepository deliveryRepository;

    @Override
    public List<MallDelivery> findByOrderId(String orderId) {
        return null;
    }

    @Override
    public List<MallDelivery> findByAutor(Author author) {
        return null;
    }

    @Override
    public Page<MallDelivery> getPage(int pageIndex, Author author, int pageSize,  DeliverySearcher deliverySearcher, String type) {
        Specification<MallDelivery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (author!=null && author instanceof Shop) {
                predicates.add(cb.equal(root.get("shop").get("id").as(Long.class), deliverySearcher.getAgentId()));
            }else if (author!=null && author instanceof Agent) {
                predicates.add(cb.equal(root.get("shop").get("parentAuthor").get("id").as(Long.class), deliverySearcher.getAgentId()));
            }
            predicates.add(cb.equal(cb.lower(root.get("type").as(String.class)), type.toLowerCase()));
            if (!StringUtils.isEmpty(deliverySearcher.getOrderId())) {
                predicates.add(cb.like(root.get("order").get("orderId").as(String.class), "%" + deliverySearcher.getOrderId() + "%"));
            }
            if (!StringUtils.isEmpty(deliverySearcher.getUsername())) {
                predicates.add(cb.like(root.get("userBaseInfo").get("loginName").as(String.class),
                        "%" + deliverySearcher.getUsername() + "%"));
            }
            if (!StringUtil.isEmpty(deliverySearcher.getDeliveryNo())) {
                predicates.add(cb.like(root.get("deliveryId").as(String.class),
                        "%" + deliverySearcher.getDeliveryNo() + "%"));
            }
            if (!StringUtils.isEmpty(deliverySearcher.getBeginTime())) {
                predicates.add(cb.greaterThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(deliverySearcher.getBeginTime(), Constant.DATETIME_PATTERN)));
            }
            if (!StringUtils.isEmpty(deliverySearcher.getEndTime())) {
                predicates.add(cb.lessThanOrEqualTo(root.get("createTime").as(Date.class),
                        StringUtil.DateFormat(deliverySearcher.getEndTime(), Constant.DATETIME_PATTERN)));
            }
            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        return deliveryRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize, new Sort(Sort.Direction.DESC, "createTime")));

    }

    @Override
    public MallDelivery deliver(Author author, MallOrder order, MallDelivery deliveryInfo, String sendBn) {
        return null;
    }

    @Override
    public ApiResult pushRefund(String orderId, LogiModel logiModel, int supplierId, String dicReturnItemsStr) throws UnsupportedEncodingException {
        return null;
    }
}
