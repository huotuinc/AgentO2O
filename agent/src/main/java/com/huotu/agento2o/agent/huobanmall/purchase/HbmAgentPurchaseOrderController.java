/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.huobanmall.purchase;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.purchase.AgentDelivery;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by helloztt on 2016/5/19.
 */
@Controller
@RequestMapping("/huobanmall/purchaseOrder")
public class HbmAgentPurchaseOrderController {
    @Autowired
    private AgentPurchaseOrderService purchaseOrderService;
    @Autowired
    private StaticResourceService resourceService;
    @Autowired
    private AgentDeliveryService agentDeliveryService;


    /**
     * 获取一级代理商采购单
     *
     * @param customerId
     * @param purchaseOrderSearcher
     * @return
     * @throws Exception
     */
    @RequestMapping("/showAgentPurchaseOrderList")
    public ModelAndView ShowAgentPurchaseOrderList(
            @RequestAttribute(value = "customerId") Integer customerId, PurchaseOrderSearcher purchaseOrderSearcher) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("huobanmall/purchase/purchase_order_list");
        purchaseOrderSearcher.setParentAgentId(0);
        purchaseOrderSearcher.setCustomerId(customerId);
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
            @RequestParam(required = true) String pOrderId) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("huobanmall/purchase/purchase_order_detail");
        AgentPurchaseOrder purchaseOrder = purchaseOrderService.findByPOrderId(pOrderId);
        if (purchaseOrder != null) {
            resourceService.setListUri(purchaseOrder.getOrderItemList(), "thumbnailPic", "picUri");
        }
        //获取发货信息
        DeliverySearcher deliverySearcher = new DeliverySearcher();
        deliverySearcher.setOrderId(pOrderId);
        List<AgentDelivery> agentDeliveryList = agentDeliveryService.showPurchaseDeliveryList(deliverySearcher).getContent();
        model.addObject("purchaseOrder", purchaseOrder);
        model.addObject("deliveryList",agentDeliveryList);
        model.addObject("sendmentEnum", PurchaseEnum.SendmentStatus.values());
        model.addObject("taxTypeEnum", PurchaseEnum.TaxType.values());
        return model;
    }

    /**
     * 审核采购单
     * @param customerId
     * @param pOrderId
     * @param checkStatus
     * @param statusComment
     * @return
     * @throws Exception
     */
    @RequestMapping("/checkAgentPurchaseOrder")
    @ResponseBody
    public ApiResult checkAgentPurchaseOrder(
            @RequestAttribute(value = "customerId") Integer customerId,
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
        result = purchaseOrderService.checkPurchaseOrder(customerId,null,pOrderId, EnumHelper.getEnumType(PurchaseEnum.OrderStatus.class, Integer.valueOf(checkStatus)), statusComment);
        return result;
    }
}
