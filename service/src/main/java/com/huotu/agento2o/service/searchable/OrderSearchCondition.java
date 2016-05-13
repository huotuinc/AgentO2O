package com.huotu.agento2o.service.searchable;

import lombok.Data;

/**
 * Created by liual on 2015-09-29.
 */
@Data
public class OrderSearchCondition {
    private String orderId;
    private String shipName;
    private String shipMobile;
    private int payStatus = -1;
    private int shipStatus = -1;
    /**
     * 排序类型，1按支付时间，2按订单金额
     */
    private int orderType;
    /**
     * 排序规则,0表示DESC，1表示ASC
     */
    private int orderRule;
    private String beginTime;
    private String endTime;
    private String beginPayTime;
    private String endPayTime;
    /**
     * 代理商id
     */
    private Integer agentId;
    /**
     * 订单商品名称
     */
    private String orderItemName;

    /**
     *代理商类型
     * shop or agent
     */
    private String agentType;
}
