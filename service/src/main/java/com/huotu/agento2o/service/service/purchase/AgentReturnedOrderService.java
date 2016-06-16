package com.huotu.agento2o.service.service.purchase;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.model.purchase.ReturnOrderDeliveryInfo;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */
public interface AgentReturnedOrderService {


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

    ApiResult addReturnOrder(AgentReturnedOrder agentReturnedOrder,Author author, Integer[] agentProductIds, Integer[] productNums) throws Exception;

    /**
     * 分页查询所有的退货单列表
     * @param returnedOrderSearch
     * @return
     */
    Page<AgentReturnedOrder> findAll(ReturnedOrderSearch returnedOrderSearch);

    /**
     *  取消退货单
     * @param rOrderId
     * @return
     */
    ApiResult cancelReturnOrder(Author author,String rOrderId);

    /**
     * 审核退货单
     * @param customerId
     * @param authorId
     * @param rOrderId
     * @param status
     * @param comment
     * @return
     */
    ApiResult checkReturnOrder(Integer customerId, Integer authorId, String rOrderId, PurchaseEnum.OrderStatus status, String comment);

    /**
     *  发货
     * @param deliveryInfo
     * @return
     */
    ApiResult pushReturnOrderDelivery(ReturnOrderDeliveryInfo deliveryInfo, Integer agentId);

    /**
     *  确认收货
     * @param customerId
     * @param authorId
     * @param rOrderId
     * @return
     */
    ApiResult receiveReturnOrder(Integer customerId, Integer authorId,String rOrderId);

    /**
     * 退单退款
     * @param customerId
     * @param authorId
     * @param rOrderId
     * @return
     */
    ApiResult payReturnOrder(Integer customerId, Integer authorId, String rOrderId);

    /**
     * 编辑退货数量
     * @param author
     * @param productId
     * @param num
     * @return
     */
    ApiResult editReturnNum(Author author, Integer productId, Integer num);

    HSSFWorkbook createWorkBook(List<AgentReturnedOrder> returnedOrderList);
}
