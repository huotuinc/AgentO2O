package com.huotu.agento2o.service.service.order.impl;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.httputil.HttpClientUtil;
import com.huotu.agento2o.common.httputil.HttpResult;
import com.huotu.agento2o.common.util.*;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallDelivery;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.entity.order.MallOrderItem;
import com.huotu.agento2o.service.model.order.BatchDeliverResult;
import com.huotu.agento2o.service.model.order.DeliveryInfo;
import com.huotu.agento2o.service.model.order.LogiModel;
import com.huotu.agento2o.service.model.order.OrderForDelivery;
import com.huotu.agento2o.service.repository.order.MallDeliveryRepository;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.service.order.MallDeliveryService;
import com.huotu.agento2o.service.service.order.MallOrderService;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.persistence.criteria.Predicate;
import java.io.UnsupportedEncodingException;
import java.util.*;

/**
 * Created by AiWelv on 2016/5/12.
 */
@Service
public class MallDeliveryServiceImpl implements MallDeliveryService {

    @Autowired
    private MallDeliveryRepository deliveryRepository;

    @Autowired
    private MallOrderService orderService;

    @Override
    public List<MallDelivery> findByOrderId(String orderId) {
        return null;
    }

    @Override
    public List<MallDelivery> findByAuthor(Author author) {
        return null;
    }

    @Override
    public Page<MallDelivery> getPage(int pageIndex, Author author, int pageSize, DeliverySearcher deliverySearcher, String type) {
        Specification<MallDelivery> specification = (root, query, cb) -> {
            List<Predicate> predicates = new ArrayList<>();
            if (author != null && author.getType() == Shop.class) {
                predicates.add(cb.equal(root.get("shop").get("id").as(Integer.class), author.getId()));
            } else if (author != null && author.getType() == Agent.class) {
                predicates.add(cb.equal(root.get("shop").get("agent").get("id").as(Integer.class), author.getId()));
            }
//            predicates.add(cb.in(root.get("agentShopType")).value(OrderEnum.ShipMode.SHOP_DELIVERY).value(OrderEnum.ShipMode.PLATFORM_DELIVERY));
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
    public ApiResult pushBatchDelivery(List<OrderForDelivery> orderForDeliveries, int customerId) throws UnsupportedEncodingException {
        Map<String, Object> params = new TreeMap<>();
        params.put("lstDeliveryInfoJson", JSON.toJSONString(orderForDeliveries));
        params.put("customerId", customerId);
//        String sign = SignBuilder.buildSignIgnoreEmpty(params, null, SysConstant.AGENT_KEY);
//        params.put("sign", sign);
        HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/AgentOrderApi/BatchDeliver", params);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            return JSON.parseObject(httpResult.getHttpContent(), new TypeReference<ApiResult<BatchDeliverResult>>() {
            });
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
    }

    @Override
    public ApiResult pushDelivery(DeliveryInfo deliveryInfo, int shopId) throws UnsupportedEncodingException {
        MallOrder order = orderService.findByOrderId(deliveryInfo.getOrderId());
        if (order.deliveryable()) {
            String dicDeliverItemsStr = "";
            for (MallOrderItem orderItem : order.getOrderItems()) {
                //判断货品是否发货
                if (orderItem.deliverable()) {
                    if (!StringUtils.isEmpty(deliveryInfo.getSendItems()) &&
                            deliveryInfo.getSendItems().contains("|" + orderItem.getItemId() + "|")) {
                        dicDeliverItemsStr += orderItem.getItemId() + "," + orderItem.getNums() + "|";
                    }
                }
            }
            if (StringUtils.isEmpty(dicDeliverItemsStr)) {
                return ApiResult.resultWith(ResultCodeEnum.DATA_NULL, "未找到发货信息", null);
            }
            //推送分销商进行发货
            Map<String, Object> map = new TreeMap<>();
            map.put("orderId", deliveryInfo.getOrderId());
            map.put("logisticsName", deliveryInfo.getLogiName());
            map.put("logisticsNo", deliveryInfo.getLogiNo());
            map.put("logiCode", deliveryInfo.getLogiCode());
            map.put("freight", Double.toString(deliveryInfo.getFreight()));
            if (deliveryInfo.getRemark() != null && !"".equals(deliveryInfo.getRemark())) {
                map.put("remark", deliveryInfo.getRemark());
            }
            map.put("shopId", shopId);
            map.put("dicDeliverItemsStr", dicDeliverItemsStr.substring(0, dicDeliverItemsStr.length() - 1));
//            String sign = SignBuilder.buildSignIgnoreEmpty(map, null, SysConstant.AGENT_KEY);
//            map.put("sign", sign);
            HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/AgentOrderApi/Deliver", map);
            ApiResult apiResult;
            if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                apiResult = JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
            } else {
                apiResult = ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
            }
            return apiResult;
        }
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, "该订单无法发货", null);
    }

    @Override
    public ApiResult pushRefund(String orderId, LogiModel logiModel, Integer shopId, String dicReturnItemsStr) throws UnsupportedEncodingException {
        Map<String, Object> param = new TreeMap<>();
        param.put("orderId", orderId);
        param.put("shopId", shopId);
        param.put("logiName", logiModel.getLogiCompanyChina());
        param.put("logiNo", logiModel.getLogiNo());
        param.put("logiMobile", logiModel.getLogiMobile());
        param.put("remark", logiModel.getLogiRemark());
        param.put("dicReturnItemsStr", dicReturnItemsStr);
//        String sign = SignBuilder.buildSignIgnoreEmpty(param, null, SysConstant.AGENT_KEY);
//        param.put("sign", sign);

        HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/AgentOrderApi/ReturnProduct", param);
        if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
            return JSON.parseObject(httpResult.getHttpContent(), ApiResult.class);
        } else {
            return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST);
        }
    }
}
