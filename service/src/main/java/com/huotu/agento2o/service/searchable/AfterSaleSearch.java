package com.huotu.agento2o.service.searchable;

import lombok.Getter;
import lombok.Setter;

/**
 * Created by allan on 12/30/15.
 */
@Setter
@Getter
public class AfterSaleSearch {
    private String beginTime;
    private String endTime;
    private String mobile;
    private String afterId;
    private String orderId;
    private Integer afterSaleStatus = -1;

    private String agentType;
    private Integer agentId;
}
