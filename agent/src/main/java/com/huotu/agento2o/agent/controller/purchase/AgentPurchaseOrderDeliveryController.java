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
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by helloztt on 2016/5/19.
 */
@Controller
@PreAuthorize("hasAnyRole('PURCHASE')")
@RequestMapping("/purchaseOrder/delivery")
public class AgentPurchaseOrderDeliveryController {

    @Autowired
    private AgentPurchaseOrderService agentPurchaseOrderService;

    @RequestMapping(value = "/delivery",method = RequestMethod.GET)
    public ModelAndView showDelivery(
            @AgtAuthenticationPrincipal Agent agent,
            @RequestParam(required = true) String pOrderId) throws Exception{
        ModelAndView model = new ModelAndView();
        AgentPurchaseOrder purchaseOrder = agentPurchaseOrderService.findByPOrderId(pOrderId);
        if(purchaseOrder.getAuthor().getParentAuthor() != null && purchaseOrder.getAuthor().getParentAuthor().getId().equals(agent.getId())){
            model.addObject("purchaseOrder",purchaseOrder);
        }else{
            throw new Exception("没有权限！");
        }
        model.setViewName("purchase/delivery");
        return model;
    }
}