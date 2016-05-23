package com.huotu.agento2o.service.service.purchase;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
public interface AgentReturnedOrderService {

    /**
     *
     * @param agentId
     * @return
     */
    List<AgentProduct> findAgentProductsByAgentId(Integer agentId);

    /**
     *  查找退货单
     * @param rOrderId
     * @return
     */
    AgentReturnedOrder findOne(String rOrderId);

    /**
     *  增加退货单
     * @param agentReturnedOrder
     * @return
     */
    AgentReturnedOrder addReturnOrder(AgentReturnedOrder agentReturnedOrder);

    /**
     *  分页查询所有的退货单列表
     * @param pageIndex
     * @param pageSize
     * @param author
     * @param returnedOrderSearch
     * @return
     */
    Page<AgentReturnedOrder> findAll(int pageIndex, int pageSize, Author author, ReturnedOrderSearch returnedOrderSearch);

    /**
     *  取消退货单
     * @param rOrderId
     * @return
     */
    ApiResult cancelReturnOrder(String rOrderId);
}
