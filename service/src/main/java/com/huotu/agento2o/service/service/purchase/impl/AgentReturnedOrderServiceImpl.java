package com.huotu.agento2o.service.service.purchase.impl;

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.repository.purchase.AgentReturnOrderItemRepository;
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

    @Autowired
    private AgentReturnOrderItemRepository agentReturnOrderItemRepository;

    @Override
    public List<AgentProduct> findAgentProductsByAgentId(Integer agentId) {
        return agentProductRepository.findByAuthor_IdAndDisabledFalse(agentId);
    }

    @Override
    public AgentReturnedOrder findOne(String rOrderId) {
        return agentReturnOrderRepository.findOne(rOrderId);
    }

    @Override
    public AgentReturnedOrder addReturnOrder(AgentReturnedOrder agentReturnedOrder) {
        return agentReturnOrderRepository.save(agentReturnedOrder);
    }

    @Override
    public Page<AgentReturnedOrder> findAll(int pageIndex, int pageSize, Author author, ReturnedOrderSearch returnedOrderSearch) {


        Specification<AgentReturnedOrder> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (returnedOrderSearch.getAgentId() != null && returnedOrderSearch.getAgentId() != 0) {
                predicates.add(cb.equal(root.get("author").get("id").as(Integer.class), returnedOrderSearch.getAgentId()));
            }

            if (returnedOrderSearch.getParentAgentId() != null) {
                if (returnedOrderSearch.getParentAgentId() == 0) {
                    predicates.add(cb.isNull(root.get("author").get("parentAuthor").as(Author.class)));
                } else {
                    predicates.add(cb.equal(root.get("author").get("parentAuthor").get("id").as(Integer.class), returnedOrderSearch.getParentAgentId()));
                }
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

            predicates.add(cb.equal(root.get("disabled").as(Boolean.class), false));

            return cb.and(predicates.toArray(new Predicate[predicates.size()]));
        };
        //排序

        return agentReturnOrderRepository.findAll(specification, new PageRequest(pageIndex - 1, pageSize));

    }

    @Override
    public ApiResult cancelReturnOrder(String rOrderId) {
        AgentReturnedOrder agentReturnedOrder = agentReturnOrderRepository.findByROrderIdAndDisabledFalse(rOrderId);

        if (agentReturnedOrder != null) {
            if (agentReturnedOrder.getStatus().equals(PurchaseEnum.OrderStatus.CHECKING)) {// 待审核状态才可以退货
                agentReturnedOrder.setDisabled(true);
                agentReturnOrderRepository.save(agentReturnedOrder);
                resetPreStore(agentReturnedOrder.getAuthor(), rOrderId);
                return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
            } else {
                return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST,"该退货单不可取消退货",null);
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
        Agent agent = (Agent) author;
        List<AgentProduct> agentProducts = new ArrayList<>();
        agentReturnedOrderItems.forEach(agentReturnedOrderItem -> {
            MallProduct product = agentReturnedOrderItem.getProduct();
            Integer num = agentReturnedOrderItem.getNum();
            AgentProduct agentProduct = agentProductRepository.findByAuthorAndProductAndDisabledFalse(agent, product);
            agentProduct.setFreez(agentProduct.getFreez() - num);
            agentProducts.add(agentProduct);
        });
        return agentProductRepository.save(agentProducts);
    }
}
