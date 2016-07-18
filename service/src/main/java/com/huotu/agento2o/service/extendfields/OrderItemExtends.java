package com.huotu.agento2o.service.extendfields;

import lombok.Getter;
import lombok.Setter;

import java.net.URI;

/**
 * 订单货品的额外字段
 * Created by allan on 7/18/16.
 */
@Setter
@Getter
public class OrderItemExtends {
    public boolean stockAdequate = false;
    private URI picUri;
    /**
     * 用于保存该条订单所对应的门店的库存和预占库存
     * 其对应的表为Agt_product
     * 对应的entity为AgentProduct
     */
    private Integer store = 0;
    private Integer freez = 0;
}
