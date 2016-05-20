package com.huotu.agento2o.service.searchable;

import lombok.Data;

/**
 * Created by wuxiongliu on 2016/5/19.
 */
@Data
public class ReturnedOrderSearch {

    /**
     *  代理商id
     */
    private Integer agentId;

    /**
     *  退单号
     */
    private String rOrderId;

    /**
     *  付款状态
     */
    private int payStatus=-1;

    /**
     *  审核状态
     */
    private int orderStatus=-1;

    /**
     *  退货状态
     */
    private int shipStatus=-1;

    /**
     *  搜索起始时间
     */
    private String beginTime;

    /**
     *  搜索截止时间
     */
    private String endTime;


}
