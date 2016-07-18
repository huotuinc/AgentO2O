package com.huotu.agento2o.service.extendfields;

/**
 * Created by allan on 7/18/16.
 */
public class OrderItemExtends {
    @Transient
    public boolean stockAdequate = false;
    private URI picUri;
    /**
     * 用于保存该条订单所对应的门店的库存和预占库存
     * 其对应的表为Agt_product
     * 对应的entity为AgentProduct
     */
    @Transient
    private Integer store = 0;
    @Transient
    private Integer freez = 0;
}
