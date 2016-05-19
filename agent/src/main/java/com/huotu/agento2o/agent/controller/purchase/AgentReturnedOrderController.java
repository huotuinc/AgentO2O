package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.service.purchase.AgentReturnOrderItemService;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */

@Controller
@PreAuthorize("hasAnyRole('PURCHASE')")
@RequestMapping("/purchase")
public class AgentReturnedOrderController {

    @Autowired
    private AgentReturnedOrderService agentReturnedOrderService;
    @Autowired
    private AgentReturnOrderItemService agentReturnOrderItemService;

    @RequestMapping(value = "/showPurchasedProductList")
    public ModelAndView showPurchasedProductList(
            @AgtAuthenticationPrincipal Author author) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/purchasedProduct_list");
        List<AgentProduct> agentProductList = null;
        agentProductList = agentReturnedOrderService.findAgentProductsByAgentId(author.getId());
        model.addObject("agentProductList", agentProductList);
        return model;
    }

    @RequestMapping(value = "/addReturnOrder")
    @ResponseBody
    public ApiResult addReturnOrder(
            @AgtAuthenticationPrincipal Author author,Integer[] productIds,Integer[] productNums) throws Exception {

        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        agentReturnedOrder.setROrderId(SerialNo.create());
        AgentReturnedOrder agentReturnedOrder1 = agentReturnedOrderService.addReturnOrder(agentReturnedOrder);
        agentReturnOrderItemService.addReturnOrderItemList(agentReturnedOrder1,productIds,productNums);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
