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
import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.agent.controller.common.UploadController;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrderItem;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

/**
 * Created by helloztt on 2016/5/18.
 */
@Controller
@PreAuthorize("hasAnyRole('PURCHASE')")
@RequestMapping("/purchaseOrder")
public class AgentPurchaseOrderController {
    @Autowired
    private AgentPurchaseOrderService purchaseOrderService;
    @Autowired
    private StaticResourceService resourceService;
    @Autowired
    private AuthorService authorService;

    /**
     * 显示我的采购单
     *
     * @param author
     * @param purchaseOrderSearcher
     * @return
     * @throws Exception
     */
    @RequestMapping("/showPurchaseOrderList")
    public ModelAndView showPurchaseOrderList(@AgtAuthenticationPrincipal Author author, PurchaseOrderSearcher purchaseOrderSearcher) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/purchase_order_list");
        purchaseOrderSearcher.setAgentId(author.getId());
        Page<AgentPurchaseOrder> purchaseOrderPage = purchaseOrderService.findAll(purchaseOrderSearcher);
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

    /**
     * 显示采购单详细
     */
    @RequestMapping("/showPurchaseOrderDetail")
    public ModelAndView showPurchaseOrderDetail(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true) String pOrderId) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/purchase_order_detail");
        AgentPurchaseOrder purchaseOrder = purchaseOrderService.findByPOrderId(pOrderId);
        if (purchaseOrder != null) {
            resourceService.setListUri(purchaseOrder.getOrderItemList(), "thumbnailPic", "picUri");
        }
        model.addObject("purchaseOrder", purchaseOrder);
        model.addObject("sendmentEnum", PurchaseEnum.SendmentStatus.values());
        model.addObject("taxTypeEnum", PurchaseEnum.TaxType.values());
        return model;
    }

    /**
     * 取消采购单
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

    @RequestMapping(value = "/receive" ,method = RequestMethod.POST)
    @ResponseBody
    public ApiResult receivePurchaseOrder(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true) String pOrderId) throws Exception{
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(pOrderId)) {
            return result;
        }
        result = purchaseOrderService.receiveAgentPurchaseOrder(pOrderId, author);
        return result;
    }

    /**
     * 显示下级采购单
     *
     * @param agent
     * @param purchaseOrderSearcher
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyRole('AGENT_PURCHASE')")
    @RequestMapping("/showAgentPurchaseOrderList")
    public ModelAndView showAgentPurchaseOrderList(@AgtAuthenticationPrincipal Agent agent, PurchaseOrderSearcher purchaseOrderSearcher) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/agent_purchase_order_list");
        purchaseOrderSearcher.setParentAgentId(agent.getId());
        Page<AgentPurchaseOrder> purchaseOrderPage = purchaseOrderService.findAll(purchaseOrderSearcher);
        List<Author> authorList = authorService.findByParentAgentId(agent);
        model.addObject("purchaseOrderList", purchaseOrderPage.getContent());
        model.addObject("pageSize", Constant.PAGESIZE);
        model.addObject("pageNo", purchaseOrderSearcher.getPageIndex());
        model.addObject("totalPages", purchaseOrderPage.getTotalPages());
        model.addObject("totalRecords", purchaseOrderPage.getTotalElements());
        model.addObject("payStatusEnums", PurchaseEnum.PayStatus.values());
        model.addObject("shipStatusEnums", PurchaseEnum.ShipStatus.values());
        model.addObject("orderStatusEnums", PurchaseEnum.OrderStatus.values());
        model.addObject("authorList", authorList);
        return model;
    }

    /**
     * 审核采购单
     *
     * @param agent
     * @param pOrderId
     * @param checkStatus
     * @param parentComment
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyRole('AGENT_PURCHASE')")
    @RequestMapping("/checkAgentPurchaseOrder")
    @ResponseBody
    public ApiResult checkAgentPurchaseOrder(
            @AgtAuthenticationPrincipal Agent agent,
            @RequestParam(required = true) String pOrderId,
            @RequestParam(required = true) String checkStatus,
            String parentComment) throws Exception {
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(pOrderId) || StringUtil.isEmptyStr(checkStatus)) {
            return result;
        }
        //审核不通过时需输入备注
        if (!(String.valueOf(PurchaseEnum.OrderStatus.CHECKED.getCode()).equals(checkStatus) ||
                (String.valueOf(PurchaseEnum.OrderStatus.RETURNED.getCode()).equals(checkStatus) && !StringUtil.isEmptyStr(parentComment)))) {
            return result;
        }
        result = purchaseOrderService.checkPurchaseOrder(null, agent.getId(), pOrderId, EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(checkStatus)), parentComment);
        return result;
    }

    /**
     * 采购单发货
     * @param agent
     * @param pOrderId
     * @return
     * @throws Exception
     */
    @PreAuthorize("hasAnyRole('AGENT_PURCHASE')")
    @RequestMapping("delivery")
    @ResponseBody
    public ApiResult deliveryAgentPurchaseOrder(
            @AgtAuthenticationPrincipal Agent agent,
            @RequestParam(required = true) String pOrderId) throws Exception {
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        if (StringUtil.isEmptyStr(pOrderId)) {
            return result;
        }
        result = purchaseOrderService.deliveryAgentPurchaseOrder(null,agent.getId(),pOrderId);
        return result;
    }


}
