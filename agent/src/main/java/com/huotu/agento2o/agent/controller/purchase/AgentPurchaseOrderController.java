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
import com.huotu.agento2o.agent.controller.common.UploadController;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrderItem;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
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

    /**
     * 显示我的采购单
     * @param author
     * @param purchaseOrderSearcher
     * @return
     * @throws Exception
     */
    @RequestMapping("/showPurchaseOrderList")
    public ModelAndView showPurchaseOrderList(@AuthenticationPrincipal Author author, PurchaseOrderSearcher purchaseOrderSearcher) throws Exception{
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/purchase_order_list");
        purchaseOrderSearcher.setAgentId(author.getId());
        Page<AgentPurchaseOrder> purchaseOrderPage = purchaseOrderService.findAll(purchaseOrderSearcher);
        model.addObject("purchaseOrderList",purchaseOrderPage.getContent());
        model.addObject("pageSize", Constant.PAGESIZE);
        model.addObject("pageNo",purchaseOrderSearcher.getPageIndex());
        model.addObject("totalPages",purchaseOrderPage.getTotalPages());
        model.addObject("totalRecords",purchaseOrderPage.getTotalElements());
        model.addObject("payStatusEnums", PurchaseEnum.PayStatus.values());
        model.addObject("shipStatusEnums", PurchaseEnum.ShipStatus.values());
        return model;
    }

    /**
     * 显示采购单详细
     */
    @RequestMapping("/showPurchaseOrderDetail")
    public ModelAndView showPurchaseOrderDetail(@AgtAuthenticationPrincipal Author author,String pOrderId) throws Exception{
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/purchase_order_detail");
        AgentPurchaseOrder purchaseOrder =  purchaseOrderService.findByPOrderId(pOrderId);
        resourceService.setListUri(purchaseOrder.getOrderItemList(),"thumbnailPic","picUri");
        model.addObject("purchaseOrder",purchaseOrder);
        model.addObject("sendmentEnum", PurchaseEnum.SendmentStatus.values());
        model.addObject("taxTypeEnum",PurchaseEnum.TaxType.values());
        return model;
    }


}
