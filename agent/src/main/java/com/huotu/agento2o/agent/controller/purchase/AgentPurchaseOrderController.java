/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.purchase.AgentDelivery;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.AgentShopService;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/18.
 */
@Controller
@PreAuthorize("hasAnyRole('AGENT','SHOP') or hasAnyAuthority('PURCHASE')")
@RequestMapping("/purchaseOrder")
public class AgentPurchaseOrderController {
    @Autowired
    private AgentPurchaseOrderService purchaseOrderService;
    @Autowired
    private StaticResourceService resourceService;
    @Autowired
    private AgentService agentService;
    @Autowired
    private AgentShopService agentShopService;
    @Autowired
    private AgentDeliveryService agentDeliveryService;

    /**
     * 新增采购单(代理商/门店)
     *
     * @param author
     * @param agentPurchaseOrder
     * @param shoppingCartIds
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addPurchase",method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addPurchase(
            @AgtAuthenticationPrincipal Author author, HttpServletRequest request,
            AgentPurchaseOrder agentPurchaseOrder, String... shoppingCartIds) throws Exception {
        String sendModeCode = request.getParameter("sendModeCode");
        String taxTypeCode = request.getParameter("taxTypeCode");
        //采购信息校验
        if (shoppingCartIds == null || shoppingCartIds.length == 0) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (StringUtil.isEmptyStr(agentPurchaseOrder.getShipName()) || StringUtil.isEmptyStr(agentPurchaseOrder.getShipMobile())
                || StringUtil.isEmptyStr(agentPurchaseOrder.getShipAddr())) {
            return new ApiResult("请输入收货信息！");
        }
        if (StringUtil.isEmptyStr(sendModeCode)) {
            return new ApiResult("请选择配送方式！");
        }
        if (StringUtil.isEmptyStr(taxTypeCode)) {
            return new ApiResult("请选择发票类型！");
        }
        if ("1".equals(taxTypeCode)) {
            if (StringUtil.isEmptyStr(agentPurchaseOrder.getTaxTitle()) || StringUtil.isEmptyStr(agentPurchaseOrder.getTaxContent())) {
                return new ApiResult("请输入普通发票信息！");
            }
        } else if ("2".equals(taxTypeCode)) {
            if (StringUtil.isEmptyStr(agentPurchaseOrder.getTaxTitle()) || StringUtil.isEmptyStr(agentPurchaseOrder.getTaxContent())
                    || StringUtil.isEmptyStr(agentPurchaseOrder.getTaxpayerCode()) || StringUtil.isEmptyStr(agentPurchaseOrder.getBankName())
                    || StringUtil.isEmptyStr(agentPurchaseOrder.getAccountNo())) {
                return new ApiResult("请输入增值税发票信息！");
            }
        }else if(!"0".equals(taxTypeCode)){
            return new ApiResult("请选择发票类型！");
        }
        if(!"0".equals(sendModeCode) && !"1".equals(sendModeCode)){
            return new ApiResult("请选择配送方式！");
        }
        agentPurchaseOrder.setSendMode(EnumHelper.getEnumType(PurchaseEnum.SendmentStatus.class, Integer.parseInt(sendModeCode)));
        agentPurchaseOrder.setTaxType(EnumHelper.getEnumType(PurchaseEnum.TaxType.class, Integer.parseInt(taxTypeCode)));
        ApiResult result = purchaseOrderService.addPurchaseOrder(agentPurchaseOrder, author, shoppingCartIds);
        return result;
    }

    /**
     * 显示我的采购单（代理商/门店）
     *
     * @param author
     * @param purchaseOrderSearcher
     * @return
     * @throws Exception
     */
    @RequestMapping("/showPurchaseOrderList")
    public ModelAndView showPurchaseOrderList(@AgtAuthenticationPrincipal Author author, PurchaseOrderSearcher purchaseOrderSearcher) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/purchase/purchase_order_list");
        if(author.getType() == Agent.class){
            purchaseOrderSearcher.setAgentId(author.getId());
        } else if (author.getType() == ShopAuthor.class) {
            purchaseOrderSearcher.setShopId(author.getId());
        }
        Page<AgentPurchaseOrder> purchaseOrderPage = purchaseOrderService.findAll(purchaseOrderSearcher);
        setPicUri(purchaseOrderPage.getContent());
        model.addObject("purchaseOrderList", purchaseOrderPage.getContent());
        model.addObject("pageSize", Constant.PAGESIZE);
        model.addObject("pageNo", purchaseOrderSearcher.getPageIndex());
        model.addObject("totalPages", purchaseOrderPage.getTotalPages());
        model.addObject("totalRecords", purchaseOrderPage.getTotalElements());
        model.addObject("payStatusEnums", PurchaseEnum.PayStatus.values());
        model.addObject("shipStatusEnums", PurchaseEnum.ShipStatus.values());
        model.addObject("orderStatusEnums", PurchaseEnum.OrderStatus.values());
        return model;
    }

    private void setPicUri(List<AgentPurchaseOrder> purchaseOrderList){
        if(purchaseOrderList != null && purchaseOrderList.size() > 0){
            purchaseOrderList.forEach(order->{
                try {
                    resourceService.setListUri(order.getOrderItemList(), "thumbnailPic", "picUri");
                } catch (NoSuchFieldException e) {
                }
            });
        }
    }

    /**
     * 显示采购单详细（代理商/门店）
     */
    @RequestMapping("/showPurchaseOrderDetail")
    public ModelAndView showPurchaseOrderDetail(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true) String pOrderId) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/purchase/purchase_order_detail");
        AgentPurchaseOrder purchaseOrder = purchaseOrderService.findByPOrderId(pOrderId);
        if (purchaseOrder == null) {
            throw new Exception("采购单不存在！");
        }
        resourceService.setListUri(purchaseOrder.getOrderItemList(), "thumbnailPic", "picUri");
        //获取发货信息
        DeliverySearcher deliverySearcher = new DeliverySearcher();
        deliverySearcher.setOrderId(pOrderId);
        /*if(author.getType() == Agent.class){
            deliverySearcher.setAgentId(author.getId());
        }else if(author.getType() == ShopAuthor.class){
            deliverySearcher.setShopId(author.getId());
        }*/
        List<AgentDelivery> agentDeliveryList = agentDeliveryService.showPurchaseDeliveryList(deliverySearcher).getContent();
        model.addObject("deliveryList",agentDeliveryList);
        model.addObject("purchaseOrder", purchaseOrder);
        model.addObject("sendmentEnum", PurchaseEnum.SendmentStatus.values());
        model.addObject("taxTypeEnum", PurchaseEnum.TaxType.values());
        return model;
    }

    /**
     * 取消采购单（代理商/门店）
     */
    @RequestMapping(value = "/deletePurchaseOrder", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deletePurchaseOrder(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true) String pOrderId) throws Exception {
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(pOrderId)) {
            return result;
        }
        result = purchaseOrderService.disableAgentPurchaseOrder(pOrderId, author);
        return result;
    }

    /**
     * 支付采购单（代理商/门店）
     * @param author
     * @param pOrderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/payPurchaseOrder", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult payPurchaseOrder(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true) String pOrderId) throws Exception {
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(pOrderId)) {
            return result;
        }
        result = purchaseOrderService.payAgentPurchaseOrder(pOrderId, author);
        return result;
    }

    /**
     * 采购单确认收货（代理商/门店）
     * @param author
     * @param pOrderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/receive", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult receivePurchaseOrder(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true) String pOrderId) throws Exception {
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(pOrderId)) {
            return result;
        }
        result = purchaseOrderService.receiveAgentPurchaseOrder(pOrderId, author);
        return result;
    }

    /**
     * 显示下级采购单（代理商）
     *
     * @param agent
     * @param purchaseOrderSearcher
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyRole('AGENT') or hasAnyAuthority('AGENT_PURCHASE')")
    @RequestMapping("/showAgentPurchaseOrderList")
    public ModelAndView showAgentPurchaseOrderList(@AgtAuthenticationPrincipal(type = Agent.class) Agent agent, PurchaseOrderSearcher purchaseOrderSearcher) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/purchase/agent_purchase_order_list");
        purchaseOrderSearcher.setParentAgentId(agent.getId());
        Page<AgentPurchaseOrder> purchaseOrderPage = purchaseOrderService.findAll(purchaseOrderSearcher);
        setPicUri(purchaseOrderPage.getContent());
        List<Agent> agentList = agentService.findByParentAgentId(agent.getId());
        List<ShopAuthor> shopList = agentShopService.findByAgentId(agent.getId());
        model.addObject("purchaseOrderList", purchaseOrderPage.getContent());
        model.addObject("pageSize", Constant.PAGESIZE);
        model.addObject("pageNo", purchaseOrderSearcher.getPageIndex());
        model.addObject("totalPages", purchaseOrderPage.getTotalPages());
        model.addObject("totalRecords", purchaseOrderPage.getTotalElements());
        model.addObject("payStatusEnums", PurchaseEnum.PayStatus.values());
        model.addObject("shipStatusEnums", PurchaseEnum.ShipStatus.values());
        model.addObject("orderStatusEnums", PurchaseEnum.OrderStatus.values());
        model.addObject("agentList", agentList);
        model.addObject("shopList", shopList);
        return model;
    }

    /**
     * 审核采购单(代理商)
     *
     * @param agent
     * @param pOrderId
     * @param checkStatus
     * @param statusComment
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyRole('AGENT') or hasAnyAuthority('AGENT_PURCHASE')")
    @RequestMapping("/checkAgentPurchaseOrder")
    @ResponseBody
    public ApiResult checkAgentPurchaseOrder(
            @AgtAuthenticationPrincipal(type = Agent.class) Agent agent,
            @RequestParam(required = true) String pOrderId,
            @RequestParam(required = true) String checkStatus,
            String statusComment) throws Exception {
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(pOrderId) || StringUtil.isEmptyStr(checkStatus)) {
            return result;
        }
        //审核不通过时需输入备注
        if (!(String.valueOf(PurchaseEnum.OrderStatus.CHECKED.getCode()).equals(checkStatus) ||
                (String.valueOf(PurchaseEnum.OrderStatus.RETURNED.getCode()).equals(checkStatus) && !StringUtil.isEmptyStr(statusComment)))) {
            return result;
        }
        result = purchaseOrderService.checkPurchaseOrder(null, agent.getId(), pOrderId, EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(checkStatus)), statusComment);
        return result;
    }

    @RequestMapping("/exportExcel")
    @SuppressWarnings("Duplicates")
    public void exportExcel(@AgtAuthenticationPrincipal Author author,
                            int beginPage,
                            int endPage,
                            PurchaseOrderSearcher purchaseOrderSearcher,
                            HttpSession session,
                            HttpServletResponse response) {
        int pageSize = Constant.PAGESIZE * (endPage - beginPage + 1);
        purchaseOrderSearcher.setAgentId(author.getId());
        purchaseOrderSearcher.setPageIndex(beginPage);
        purchaseOrderSearcher.setPageSize(pageSize);
        Page<AgentPurchaseOrder> purchaseOrderPage = purchaseOrderService.findAll(purchaseOrderSearcher);
        List<AgentPurchaseOrder> purchaseOrderList = purchaseOrderPage.getContent();
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = author.getName() + "-采购单-"
                    + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = purchaseOrderService.createWorkBook(purchaseOrderList);
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

    @RequestMapping("/agent/exportExcel")
    public void exportAgentExcel(@AgtAuthenticationPrincipal Author author,
                            int beginPage,
                            int endPage,
                            PurchaseOrderSearcher purchaseOrderSearcher,
                            HttpSession session,
                            HttpServletResponse response) {
        int pageSize = Constant.PAGESIZE * (endPage - beginPage + 1);
        purchaseOrderSearcher.setParentAgentId(author.getId());
        purchaseOrderSearcher.setPageIndex(beginPage);
        purchaseOrderSearcher.setPageSize(pageSize);
        Page<AgentPurchaseOrder> purchaseOrderPage = purchaseOrderService.findAll(purchaseOrderSearcher);
        List<AgentPurchaseOrder> purchaseOrderList = purchaseOrderPage.getContent();
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = author.getName() + "-下级采购单-"
                    + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = purchaseOrderService.createWorkBook(purchaseOrderList);
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
