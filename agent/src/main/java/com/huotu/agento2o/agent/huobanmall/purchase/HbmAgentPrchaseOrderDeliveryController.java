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

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.model.order.DeliveryInfo;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by helloztt on 2016/5/19.
 */
@Controller
@RequestMapping("/huobanmall/purchaseOrder")
public class HbmAgentPrchaseOrderDeliveryController {
    @Autowired
    private AgentPurchaseOrderService agentPurchaseOrderService;
    @Autowired
    private AgentDeliveryService agentDeliveryService;

    @RequestMapping(value = "/showDelivery",method = RequestMethod.GET)
    public ModelAndView showDelivery(
            @RequestAttribute(value = "customerId") Integer customerId,
            @RequestParam(required = true) String pOrderId) throws Exception{
        ModelAndView model = new ModelAndView();
        AgentPurchaseOrder purchaseOrder = agentPurchaseOrderService.findByPOrderId(pOrderId);
        if(purchaseOrder.getAuthor().getParentAuthor() == null && purchaseOrder.getAuthor().getCustomer().getCustomerId().equals(customerId)){
            model.addObject("purchaseOrder",purchaseOrder);
        }else{
            throw  new Exception("没有权限！");
        }
        model.setViewName("huobanmall/purchase/delivery");
        return model;
    }

    @RequestMapping(value = "/delivery")
    @ResponseBody
    public ApiResult delivery(
            @RequestAttribute(value = "customerId") Integer customerId,
            DeliveryInfo deliveryInfo) throws Exception{
        return agentDeliveryService.pushDelivery(deliveryInfo,customerId,null);
    }
}
