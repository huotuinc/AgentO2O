package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.SerialNo;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrder;
import com.huotu.agento2o.service.entity.purchase.AgentReturnedOrderItem;
import com.huotu.agento2o.service.model.purchase.ReturnOrderDeliveryInfo;
import com.huotu.agento2o.service.model.purchase.ReturnOrderInfo;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.goods.MallProductService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import com.huotu.agento2o.service.service.purchase.AgentReturnOrderItemService;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.ArrayList;
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
    @Autowired
    private AgentProductService agentProductService;

    @Autowired
    private MallProductService mallProductService;
    @Autowired
    private AuthorService authorService;

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
        agentProductList = agentProductService.findByAgentId(author.getId());
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
        agentReturnedOrder.setShipStatus(PurchaseEnum.ShipStatus.NOT_DELIVER);
        agentReturnedOrder.setStatus(PurchaseEnum.OrderStatus.CHECKING);
        agentReturnedOrder.setPayStatus(PurchaseEnum.PayStatus.NOT_PAYED);
        agentReturnedOrder.setCreateTime(new Date());

        double finalPrice = 0.0;
        for(int i=0;i<productIds.length;i++){
            MallProduct mallProduct = mallProductService.findByProductId(productIds[i]);
            finalPrice += mallProduct.getPrice()*productNums[i];
        }
        agentReturnedOrder.setFinalAmount(finalPrice);

        AgentReturnedOrder savedAgentReturnedOrder= agentReturnedOrderService.addReturnOrder(agentReturnedOrder);
        if(savedAgentReturnedOrder == null){
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
        List<AgentReturnedOrderItem> agentReturnedOrderItems = agentReturnOrderItemService.addReturnOrderItemList(author,agentReturnedOrder,productIds,productNums);
        if(agentReturnedOrderItems.size() == 0){
            return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     *  显示退货单列表
     * @param author 代理商
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showReturnedOrderList")
    public ModelAndView showReturnedOrderList(
            @AgtAuthenticationPrincipal Author author,
            ReturnedOrderSearch searchCondition) throws Exception {

        searchCondition.setAgentId(author.getId());
        Page<AgentReturnedOrder> agentReturnedOrderPage  = agentReturnedOrderService.findAll(searchCondition);
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/returned_product_list");
        int totalPages = agentReturnedOrderPage.getTotalPages();

        model.addObject("payStatusEnums", PurchaseEnum.PayStatus.values());
        model.addObject("shipStatusEnums",PurchaseEnum.ShipStatus.values());
        model.addObject("orderStatusEnums",PurchaseEnum.OrderStatus.values());
        model.addObject("agentReturnedOrderList", agentReturnedOrderPage.getContent());
        model.addObject("totalPages", totalPages);
        model.addObject("agentId", author.getId());
        model.addObject("totalRecords", agentReturnedOrderPage.getTotalElements());
        model.addObject("pageSize", agentReturnedOrderPage.getSize());
        model.addObject("searchCondition", searchCondition);
        model.addObject("pageIndex", searchCondition.getPageIndex());
        model.addObject("authorType", author.getClass().getSimpleName());

        return model;
    }

    @RequestMapping(value = "/cancelReturnOrder")
    @ResponseBody
    public ApiResult cancelReturnOrer(String rOrderId) throws Exception {
        ApiResult apiResult = agentReturnedOrderService.cancelReturnOrder(rOrderId);
        return apiResult;
    }

    @RequestMapping(value = "/showReturnedOrderDetail")
    public ModelAndView showReturnOrderDetail(String rOrderId) throws Exception{
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("purchase/returned_product_detail");
        AgentReturnedOrder agentReturnedOrder = agentReturnedOrderService.findOne(rOrderId);
        List<AgentReturnedOrderItem> agentReturnedOrderItems = new ArrayList<>();
        agentReturnedOrderItems = agentReturnOrderItemService.findAll(rOrderId);

        modelAndView.addObject("agentReturnOrder",agentReturnedOrder);
        modelAndView.addObject("agentReturnedOrderItems",agentReturnedOrderItems);
        return modelAndView;
    }

    @RequestMapping(value = "/showDelivery")
    public ModelAndView showDelivery(@AgtAuthenticationPrincipal Agent agent,
                               @RequestParam(required = true) String rOrderId) throws Exception{

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("purchase/return_order_delivery");
        AgentReturnedOrder agentReturnedOrder = agentReturnedOrderService.findOne(rOrderId);
        List<AgentReturnedOrderItem> agentReturnedOrderItems = agentReturnOrderItemService.findAll(rOrderId);
//        if(agentReturnedOrder.getAuthor().getParentAuthor() != null && agentReturnedOrder.getAuthor().getParentAuthor().getId().equals(agent.getId())){
            modelAndView.addObject("agentReturnedOrder",agentReturnedOrder);
            modelAndView.addObject("agentReturnedOrderItems",agentReturnedOrderItems);
//        }else{
//            throw new Exception("没有权限！");
//        }
        return modelAndView;
    }


    @RequestMapping(value = "/delivery")
    @ResponseBody
    public ApiResult deliveryReturnOrder(
            @AgtAuthenticationPrincipal Agent agent,
            ReturnOrderDeliveryInfo deliveryInfo) throws Exception {
        return agentReturnedOrderService.pushReturnOrderDelivery(deliveryInfo,agent.getId());
    }

    @RequestMapping(value = "/editReturnNum")
    @ResponseBody
    public ApiResult editReturnNum(
            @AgtAuthenticationPrincipal Agent agent,
            @RequestParam(required = true) Integer productId,
            @RequestParam(required = true) Integer num) throws Exception {

        if(productId == null){
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (num == null || num == 0) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }

        return agentReturnedOrderService.editReturnNum(agent,productId,num);

    }

    /**
     *  显示退货单列表
     * @param agent 代理商
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showAgentReturnedOrderList")
    public ModelAndView showAgentReturnedOrderList(
            @AgtAuthenticationPrincipal Agent agent,
            ReturnedOrderSearch searchCondition) throws Exception {

        searchCondition.setParentAgentId(agent.getId());
        Page<AgentReturnedOrder> agentReturnedOrderPage  = agentReturnedOrderService.findAll(searchCondition);

        List<Author> authorList = authorService.findByParentAgentId(agent);

        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/agent_return_order_list");
        int totalPages = agentReturnedOrderPage.getTotalPages();

        model.addObject("payStatusEnums", PurchaseEnum.PayStatus.values());
        model.addObject("shipStatusEnums",PurchaseEnum.ShipStatus.values());
        model.addObject("orderStatusEnums",PurchaseEnum.OrderStatus.values());
        model.addObject("agentReturnedOrderList", agentReturnedOrderPage.getContent());
        model.addObject("totalPages", totalPages);
        model.addObject("agentId", agent.getId());
        model.addObject("totalRecords", agentReturnedOrderPage.getTotalElements());
        model.addObject("pageSize", agentReturnedOrderPage.getSize());
        model.addObject("searchCondition", searchCondition);
        model.addObject("pageIndex", searchCondition.getPageIndex());
        model.addObject("authorType", agent.getClass().getSimpleName());
        model.addObject("authorList",authorList);
        return model;
    }

    /**
     * 审核下级退货单
     * @param rOrderId
     * @param checkStatus
     * @param statusComment
     * @return
     */
    @RequestMapping(value = "/checkAgentReturnOrder")
    @ResponseBody
    public ApiResult checkAgentReturnOrder(@AgtAuthenticationPrincipal Author author,
                                           @RequestParam(required = true) String rOrderId,
                                           @RequestParam(required = true) String checkStatus,
                                           String statusComment){

        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(rOrderId) || StringUtil.isEmptyStr(checkStatus)) {
            return result;
        }

        //审核不通过时需输入备注
        if (!(String.valueOf(PurchaseEnum.OrderStatus.CHECKED.getCode()).equals(checkStatus) ||
                (String.valueOf(PurchaseEnum.OrderStatus.RETURNED.getCode()).equals(checkStatus) && !StringUtil.isEmptyStr(statusComment)))) {
            return result;
        }
        return agentReturnedOrderService.checkReturnOrder(null,author.getId(),rOrderId, EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(checkStatus)), statusComment);
    }

    /**
     *   确认收货
     * @param author
     * @param rOrderId
     * @return
     */
    @RequestMapping(value = "/receiveAgentReturnOrder")
    @ResponseBody
    public ApiResult receiveReturnOrder(@AgtAuthenticationPrincipal Author author,
                                        @RequestParam(required = true) String rOrderId){
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(rOrderId)) {
            return result;
        }
        return agentReturnedOrderService.receiveReturnOrder(null,author.getId(),null,rOrderId);

    }

    @RequestMapping(value = "/payAgentReturnOrder")
    @ResponseBody
    public ApiResult payReturnOrder(@AgtAuthenticationPrincipal Author author,
                                    @RequestParam(required = true) String rOrderId){
        return agentReturnedOrderService.payReturnOrder(null,author.getId(),rOrderId);

    }



}
