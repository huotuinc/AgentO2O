package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.model.purchase.ReturnOrderInfo;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import com.huotu.agento2o.service.service.purchase.AgentReturnOrderItemService;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.Date;
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

    /**
     *  显示已采购商品列表
     * @param author 代理商
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showPurchasedProductList")
    public ModelAndView showPurchasedProductList(
            @AgtAuthenticationPrincipal Author author) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/purchased_product_list");
        List<AgentProduct> agentProductList = null;
        agentProductList = agentReturnedOrderService.findAgentProductsByAgentId(author.getId());
        model.addObject("agentProductList", agentProductList);
        return model;
    }

    /**
     *  增加退货单
     * @param author 代理商
     * @param productIds 货品id数组
     * @param productNums 退货数量数组
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addReturnOrder")
    @ResponseBody
    public ApiResult addReturnOrder(
            @AgtAuthenticationPrincipal Author author, Integer[] productIds, Integer[] productNums, ReturnOrderInfo returnOrderInfo) throws Exception {

        AgentReturnedOrder agentReturnedOrder = new AgentReturnedOrder();
        agentReturnedOrder.setROrderId(SerialNo.create());
        agentReturnedOrder.setAuthor(author);
        agentReturnedOrder.setAuthorComment(returnOrderInfo.getAuthorComment());
        agentReturnedOrder.setShipMobile(returnOrderInfo.getMobile());
        agentReturnedOrder.setSendmentStatus(returnOrderInfo.getSendmentStatus());
        agentReturnedOrder.setCreateTime(new Date());

        AgentReturnedOrder agentReturnedOrder1 = agentReturnedOrderService.addReturnOrder(agentReturnedOrder);
        agentReturnOrderItemService.addReturnOrderItemList(agentReturnedOrder1,productIds,productNums);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     *  显示退货单列表
     * @param author 代理商
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showReturnedOrderList")
    public String showReturnedOrderList(
            @AgtAuthenticationPrincipal Author author,
            Model model,
            ReturnedOrderSearch searchCondition,
            @RequestParam(required = false, defaultValue = "1") int pageIndex) throws Exception {

        searchCondition.setAgentId(author.getId());
        Page<AgentReturnedOrder> agentReturnedOrderPage  = agentReturnedOrderService.findAll(pageIndex,3, author, searchCondition);

        int totalPages = agentReturnedOrderPage.getTotalPages();

        model.addAttribute("payStatusEnums", PurchaseEnum.PayStatus.values());
        model.addAttribute("shipStatusEnums",PurchaseEnum.ShipStatus.values());
        model.addAttribute("orderStatusEnums",PurchaseEnum.OrderStatus.values());
        model.addAttribute("agentReturnedOrderList", agentReturnedOrderPage.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("agentId", author.getId());
        model.addAttribute("totalRecords", agentReturnedOrderPage.getTotalElements());
        model.addAttribute("pageSize", agentReturnedOrderPage.getSize());
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("authorType", author.getClass().getSimpleName());
        return "purchase/returned_product_list";
    }


}
