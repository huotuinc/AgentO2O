package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.*;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.*;
import com.huotu.agento2o.service.model.purchase.ReturnOrderDeliveryInfo;
import com.huotu.agento2o.service.model.purchase.ReturnOrderInfo;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import com.huotu.agento2o.service.searchable.ReturnedOrderSearch;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.goods.MallProductService;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import com.huotu.agento2o.service.service.purchase.AgentReturnOrderItemService;
import com.huotu.agento2o.service.service.purchase.AgentReturnedOrderService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/18.
 */

@Controller
@PreAuthorize("hasAnyRole('AGENT','SHOP') or hasAnyAuthority('PURCHASE')")
@RequestMapping("/returnedOrder")
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

    @Autowired
    private AgentDeliveryService agentDeliveryService;

    /**
     *  显示已采购商品列表(代理商/门店)
     * @param author 代理商
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showPurchasedProductList")
    public ModelAndView showPurchasedProductList(
            @AgtAuthenticationPrincipal Author author) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/returned/purchased_product_list");
        List<AgentProduct> agentProductList = null;
        agentProductList = agentProductService.findByAgentId(author.getId());
        model.addObject("agentProductList", agentProductList);
        return model;
    }

    /**
     *  增加退货单（代理商/门店）
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
        model.setViewName("purchase/returned/returned_order_list");
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

    /**
     * 取消退货单
     * @param rOrderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/cancelReturnOrder")
    @ResponseBody
    public ApiResult cancelReturnOrder(@AgtAuthenticationPrincipal Author author,String rOrderId) throws Exception {
        ApiResult apiResult = agentReturnedOrderService.cancelReturnOrder(author,rOrderId);
        return apiResult;
    }

    /**
     * 显示退货单详情
     * @param author
     * @param rOrderId
     * @param subAuthorId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showReturnedOrderDetail")
    public ModelAndView showReturnOrderDetail(@AgtAuthenticationPrincipal Author author,String rOrderId,Integer subAuthorId) throws Exception{
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("purchase/returned/returned_order_detail");
        AgentReturnedOrder agentReturnedOrder = agentReturnedOrderService.findOne(rOrderId);
        List<AgentReturnedOrderItem> agentReturnedOrderItems = new ArrayList<>();
        agentReturnedOrderItems = agentReturnOrderItemService.findAll(rOrderId);

        DeliverySearcher deliverySearcher = new DeliverySearcher();
        List<AgentDelivery> agentDeliveryList = null;
        //获取本级代理商/门店采购退货发货信息
        if(subAuthorId == null){
            deliverySearcher.setOrderId(rOrderId);
            deliverySearcher.setAgentId(author.getId());
            agentDeliveryList = agentDeliveryService.showReturnDeliveryList(deliverySearcher).getContent();

        } else{//获取下级代理商/门店采购退货发货信息
            deliverySearcher.setAgentId(subAuthorId);
            deliverySearcher.setParentAgentId(author.getId());
            deliverySearcher.setOrderId(rOrderId);
            agentDeliveryList = agentDeliveryService.showReturnDeliveryList(deliverySearcher).getContent();
        }

        modelAndView.addObject("deliveryList",agentDeliveryList);
        modelAndView.addObject("agentReturnOrder",agentReturnedOrder);
        modelAndView.addObject("agentReturnedOrderItems",agentReturnedOrderItems);
        return modelAndView;
    }

    /**
     * 显示退货单
     * @param author
     * @param rOrderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showDelivery")
    public ModelAndView showDelivery(@AgtAuthenticationPrincipal(type = Author.class) Author author,
                               @RequestParam(required = true) String rOrderId) throws Exception{

        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("purchase/delivery/return_order_delivery");
        AgentReturnedOrder agentReturnedOrder = agentReturnedOrderService.findOne(rOrderId);
        List<AgentReturnedOrderItem> agentReturnedOrderItems = agentReturnOrderItemService.findAll(rOrderId);
        modelAndView.addObject("agentReturnedOrder",agentReturnedOrder);
        modelAndView.addObject("agentReturnedOrderItems",agentReturnedOrderItems);
        return modelAndView;
    }


    @RequestMapping(value = "/delivery")
    @ResponseBody
    public ApiResult deliveryReturnOrder(
            @AgtAuthenticationPrincipal Author author,
            ReturnOrderDeliveryInfo deliveryInfo) throws Exception {
        return agentReturnedOrderService.pushReturnOrderDelivery(deliveryInfo, author.getId());
    }

    @RequestMapping(value = "/editReturnNum")
    @ResponseBody
    public ApiResult editReturnNum(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true) Integer productId,
            @RequestParam(required = true) Integer num) throws Exception {

        if(productId == null){
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (num == null || num == 0) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }

        return agentReturnedOrderService.editReturnNum(author,productId,num);

    }

    /**
     *  显示下级退货单列表
     * @param agent 代理商
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyRole('AGENT') or hasAnyAuthority('AGENT_PURCHASE')")
    @RequestMapping(value = "/showAgentReturnedOrderList")
    public ModelAndView showAgentReturnedOrderList(
            @AgtAuthenticationPrincipal(type = Agent.class) Agent agent,
            ReturnedOrderSearch searchCondition) throws Exception {

        searchCondition.setParentAgentId(agent.getId());
        Page<AgentReturnedOrder> agentReturnedOrderPage  = agentReturnedOrderService.findAll(searchCondition);

        List<Author> authorList = authorService.findByParentAgentId(agent);

        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/returned/agent_return_order_list");
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
    @PreAuthorize("hasAnyRole('AGENT') or hasAnyAuthority('AGENT_PURCHASE')")
    @RequestMapping(value = "/checkAgentReturnOrder")
    @ResponseBody
    public ApiResult checkAgentReturnOrder(@AgtAuthenticationPrincipal(type=Agent.class) Agent agent,
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
        return agentReturnedOrderService.checkReturnOrder(null,agent.getId(),rOrderId, EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(checkStatus)), statusComment);
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
        return agentReturnedOrderService.receiveReturnOrder(null,author.getId(),rOrderId);

    }

    @RequestMapping(value = "/payAgentReturnOrder")
    @ResponseBody
    public ApiResult payReturnOrder(@AgtAuthenticationPrincipal Author author,
                                    @RequestParam(required = true) String rOrderId){
        return agentReturnedOrderService.payReturnOrder(null,author.getId(),rOrderId);
    }

    @RequestMapping("/exportExcel")
    @SuppressWarnings("Duplicates")
    public void exportExcel(@AgtAuthenticationPrincipal Author author,
                            int beginPage,
                            int endPage,
                            ReturnedOrderSearch returnedOrderSearch,
                            HttpSession session,
                            HttpServletResponse response) {
        int pageSize = Constant.PAGESIZE * (endPage - beginPage + 1);
        returnedOrderSearch.setAgentId(author.getId());
        returnedOrderSearch.setPageIndex(beginPage);
        returnedOrderSearch.setPageSize(pageSize);
        Page<AgentReturnedOrder> returnedOrderPage = agentReturnedOrderService.findAll(returnedOrderSearch);
        List<AgentReturnedOrder> returnedOrderList = returnedOrderPage.getContent();
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = author.getName() + "-采购退货单-"
                    + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = agentReturnedOrderService.createWorkBook(returnedOrderList);
            fOut = response.getOutputStream();
            workbook.write(fOut);
        } catch (Exception ignored) {
        } finally {
            try {
                assert fOut != null;
                fOut.flush();
                fOut.close();
            } catch (IOException ignored) {
            }
            session.setAttribute("state", "open");
        }
    }

    /**
     * 导出下级门店/代理商列表
     * @param author
     * @param beginPage
     * @param endPage
     * @param returnedOrderSearch
     * @param session
     * @param response
     */
    @RequestMapping("/agent/exportExcel")
    public void exportAgentExcel(@AgtAuthenticationPrincipal Author author,
                            int beginPage,
                            int endPage,
                            ReturnedOrderSearch returnedOrderSearch,
                            HttpSession session,
                            HttpServletResponse response) {
        int pageSize = Constant.PAGESIZE * (endPage - beginPage + 1);
        returnedOrderSearch.setParentAgentId(author.getId());
        returnedOrderSearch.setPageIndex(beginPage);
        returnedOrderSearch.setPageSize(pageSize);
        Page<AgentReturnedOrder> returnedOrderPage = agentReturnedOrderService.findAll(returnedOrderSearch);
        List<AgentReturnedOrder> returnedOrderList = returnedOrderPage.getContent();
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = author.getName() + "-下级采购退货单-"
                    + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = agentReturnedOrderService.createWorkBook(returnedOrderList);
            fOut = response.getOutputStream();
            workbook.write(fOut);
        } catch (Exception ignored) {
        } finally {
            try {
                assert fOut != null;
                fOut.flush();
                fOut.close();
            } catch (IOException ignored) {
            }
            session.setAttribute("state", "open");
        }
    }



}
