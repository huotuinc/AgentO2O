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
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.searchable.PurchaseOrderSearcher;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by helloztt on 2016/5/19.
 */
@Controller
@RequestMapping("/huobanmall/purchaseOrder")
public class HbmAgentPurchaseOrderController {
    @Autowired
    private AgentPurchaseOrderService purchaseOrderService;


    /**
     * 获取一级代理商采购单
     * @param customerId
     * @param purchaseOrderSearcher
     * @return
     * @throws Exception
     */
    @RequestMapping("/showAgentPurchaseOrderList")
    public ModelAndView ShowAgentPurchaseOrderList(
            @RequestAttribute(value = "customerId") Integer customerId,PurchaseOrderSearcher purchaseOrderSearcher) throws Exception{
        ModelAndView model = new ModelAndView();
        model.setViewName("huobanmall/purchase/purchase_order_list");
        purchaseOrderSearcher.setParentAgentId(0);
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
}
