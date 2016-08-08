package com.huotu.agento2o.service.searchable;

import com.huotu.agento2o.common.util.Constant;
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
     * 门店id
     */
    private Integer shopId;

    /**
     *  上级代理商id
     */
    private Integer parentAgentId;

    /**
     *  平台方id
     */
    private Integer customerId;

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

    private int pageIndex = 1;
    private int pageSize = Constant.PAGESIZE;

    private int parent_agentLevel = -1;
    /**
     * 货名名称
     */
    private String orderItemName;


}
