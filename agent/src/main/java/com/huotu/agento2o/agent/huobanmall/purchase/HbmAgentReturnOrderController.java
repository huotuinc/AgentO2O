package com.huotu.agento2o.agent.huobanmall.purchase;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.purchase.AgentDelivery;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import com.huotu.agento2o.service.service.purchase.AgentReturnOrderItemService;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/23.
 */

@Controller
@RequestMapping("/huobanmall/returnOrder")
public class HbmAgentReturnOrderController {

    @Autowired
    private AgentReturnedOrderService agentReturnedOrderService;
    @Autowired
    private AgentReturnOrderItemService agentReturnOrderItemService;
    @Autowired
    private AgentDeliveryService agentDeliveryService;
    @Autowired
    private StaticResourceService resourceService;
    @Autowired
    private AgentService agentService;


    /**
     * 显示一级代理商退货单列表
     * @param customerId
     * @param searchCondition
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showReturnedOrderList")
    public ModelAndView showReturnedOrderList(
            @RequestAttribute(value = "customerId") Integer customerId,
            ReturnedOrderSearch searchCondition) throws Exception {

        searchCondition.setParentAgentId(0);
        searchCondition.setCustomerId(customerId);
        Page<AgentReturnedOrder> agentReturnedOrderPage  = agentReturnedOrderService.findAll(searchCondition);
        setPicUri(agentReturnedOrderPage.getContent());
        //下级代理商列表
        List<Agent> agentList = agentService.findByCustomerId(customerId);
        ModelAndView model = new ModelAndView();
        model.setViewName("huobanmall/purchase/returned_product_list");
        int totalPages = agentReturnedOrderPage.getTotalPages();
        model.addObject("payStatusEnums", PurchaseEnum.PayStatus.values());
        model.addObject("shipStatusEnums",PurchaseEnum.ShipStatus.values());
        model.addObject("orderStatusEnums",PurchaseEnum.OrderStatus.values());
        model.addObject("agentReturnedOrderList", agentReturnedOrderPage.getContent());
        model.addObject("totalPages", totalPages);
        model.addObject("totalRecords", agentReturnedOrderPage.getTotalElements());
        model.addObject("pageSize", agentReturnedOrderPage.getSize());
        model.addObject("searchCondition", searchCondition);
        model.addObject("pageIndex", searchCondition.getPageIndex());
        model.addObject("agentList", agentList);
        return model;
    }

    public void setPicUri(List<AgentReturnedOrder> agentReturnedOrderList){
        if(agentReturnedOrderList != null && agentReturnedOrderList.size() > 0){
            agentReturnedOrderList.forEach(order->{
                try {
                    resourceService.setListUri(order.getOrderItemList(), "thumbnailPic", "picUri");
                } catch (NoSuchFieldException e) {
                }
            });
        }
    }

    /**
     * 显示退货单详情
     *
     * @param customerId
     * @param rOrderId
     * @param agentId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showReturnedOrderDetail")
    public ModelAndView showReturnOrderDetail(@RequestAttribute(value = "customerId") Integer customerId,@RequestParam(required = true) String rOrderId,Integer agentId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("huobanmall/purchase/returned_product_detail");
        AgentReturnedOrder agentReturnedOrder = agentReturnedOrderService.findOne(rOrderId);
        List<AgentReturnedOrderItem> agentReturnedOrderItems = new ArrayList<>();
        agentReturnedOrderItems = agentReturnOrderItemService.findAll(rOrderId);

        DeliverySearcher deliverySearcher = new DeliverySearcher();
        List<AgentDelivery> agentDeliveryList = null;
        //获取本级代理商/门店采购退货发货信息

        deliverySearcher.setCustomerId(customerId);
        deliverySearcher.setOrderId(rOrderId);
        deliverySearcher.setAgentId(agentId);
        agentDeliveryList = agentDeliveryService.showReturnDeliveryList(deliverySearcher).getContent();


        modelAndView.addObject("deliveryList",agentDeliveryList);
        modelAndView.addObject("agentReturnOrder",agentReturnedOrder);
        modelAndView.addObject("agentReturnedOrderItems",agentReturnedOrderItems);
        return modelAndView;
    }

    /**
     * 审核退货单
     * @param customerId
     * @param rOrderId
     * @param checkStatus
     * @param parentComment
     * @return
     */
    @RequestMapping(value = "/checkAgentReturnOrder")
    @ResponseBody
    public ApiResult checkAgentReturnOrder(@RequestAttribute(value = "customerId") Integer customerId,
                                           @RequestParam(required = true) String rOrderId,
                                           @RequestParam(required = true) String checkStatus,
                                           String parentComment){

        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(rOrderId) || StringUtil.isEmptyStr(checkStatus)) {
            return result;
        }
        //审核不通过时需输入备注
        if (!(String.valueOf(PurchaseEnum.OrderStatus.CHECKED.getCode()).equals(checkStatus) ||
                (String.valueOf(PurchaseEnum.OrderStatus.RETURNED.getCode()).equals(checkStatus) && !StringUtil.isEmptyStr(parentComment)))) {
            return result;
        }
        return agentReturnedOrderService.checkReturnOrder(customerId,null,rOrderId, EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(checkStatus)), parentComment);
    }

    /**
     *   确认收货
     * @param customerId
     * @param rOrderId
     * @return
     */
    @RequestMapping(value = "/receiveReturnOrder")
    @ResponseBody
    public ApiResult receiveReturnOrder(@RequestAttribute(value = "customerId") Integer customerId,
                                        @RequestParam(required = true) String rOrderId){
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(rOrderId)) {
            return result;
        }
        return agentReturnedOrderService.receiveReturnOrder(customerId,null,rOrderId);

    }

    /**
     * 支付
     * @param customerId
     * @param rOrderId
     * @return
     */
    /*@RequestMapping(value = "/payReturnOrder")
    @ResponseBody
    public ApiResult payReturnOrder(@RequestAttribute(value = "customerId") Integer customerId,
                                    @RequestParam(required = true) String rOrderId){
        return agentReturnedOrderService.payReturnOrder(customerId,null,rOrderId);
    }*/


}
