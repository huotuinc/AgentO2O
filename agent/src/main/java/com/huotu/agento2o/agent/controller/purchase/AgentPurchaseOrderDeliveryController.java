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
import com.huotu.agento2o.agent.config.annotataion.SystemControllerLog;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.model.order.DeliveryInfo;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by helloztt on 2016/5/19.
 */
@Controller
@PreAuthorize("hasAnyRole('AGENT') or hasAnyAuthority('PURCHASE')")
@RequestMapping("/purchaseOrder/delivery")
public class AgentPurchaseOrderDeliveryController {

    @Autowired
    private AgentPurchaseOrderService agentPurchaseOrderService;
    @Autowired
    private AgentDeliveryService agentDeliveryService;
    @Autowired
    private AuthorService authorService;

    /**
     * 显示发货单
     * @param author
     * @param pOrderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showDelivery",method = RequestMethod.GET)
    public ModelAndView showDelivery(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true) String pOrderId) throws Exception{
        ModelAndView model = new ModelAndView();
        AgentPurchaseOrder purchaseOrder = agentPurchaseOrderService.findByPOrderId(pOrderId);
        //判断采购单是否是所属 agent
        if(purchaseOrder.getAuthor().getParentAuthor() != null && purchaseOrder.getAuthor().getParentAuthor().getId().equals(author.getId())){
            model.addObject("purchaseOrder",purchaseOrder);
        }else{
            throw new Exception("没有权限！");
        }
        model.setViewName("purchase/delivery/delivery");
        return model;
    }

    /**
     * 发货
     * @param agent
     * @param deliveryInfo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delivery")
    @ResponseBody
    @SystemControllerLog(value = "采购单发货")
    public ApiResult delivery(
            @AgtAuthenticationPrincipal(type = Agent.class) Agent agent,
            DeliveryInfo deliveryInfo) throws Exception{
        return agentDeliveryService.pushDelivery(deliveryInfo,null,agent.getId());
    }

    /**
     * 发货物流列表
     * @param agent
     * @param deliverySearcher
     * @return
     */
    @RequestMapping(value = "/showPurchaseDeliveryList")
    public ModelAndView showPurchaseDeliveryList(@AgtAuthenticationPrincipal(type = Agent.class) Agent agent, DeliverySearcher deliverySearcher){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/purchase/delivery/agent_purchase_delivery_list");

        deliverySearcher.setAgentId(deliverySearcher.getAgentId());
        deliverySearcher.setParentAgentId(agent.getId());
        Page page = agentDeliveryService.showPurchaseDeliveryList(deliverySearcher);
        int totalPages = page.getTotalPages();
        List<Author> authorList = authorService.findByParentAgentId(agent);

        modelAndView.addObject("purchaseDeliveryList",page.getContent());
        modelAndView.addObject("totalPages",totalPages);
        modelAndView.addObject("totalRecords",page.getTotalElements());
        modelAndView.addObject("pageSize",page.getSize());
        modelAndView.addObject("deliverySearcher",deliverySearcher);
        modelAndView.addObject("pageIndex",deliverySearcher.getPageIndex());
        modelAndView.addObject("authorType", agent.getClass().getSimpleName());
        modelAndView.addObject("authorList",authorList);

        return modelAndView;
    }

    /**
     * 退货物流列表
     * @param agent
     * @param deliverySearcher
     * @return
     */
    @RequestMapping(value = "/showReturnDeliveryList")
    public ModelAndView showReturnDeliveryList(@AgtAuthenticationPrincipal(type = Agent.class) Agent agent, DeliverySearcher deliverySearcher){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/purchase/delivery/agent_return_delivery_list");

        deliverySearcher.setAgentId(deliverySearcher.getAgentId());
        deliverySearcher.setParentAgentId(agent.getId());
        Page page = agentDeliveryService.showReturnDeliveryList(deliverySearcher);
        int totalPages = page.getTotalPages();
        List<Author> authorList = authorService.findByParentAgentId(agent);

        modelAndView.addObject("purchaseDeliveryList",page.getContent());
        modelAndView.addObject("totalPages",totalPages);
        modelAndView.addObject("totalRecords",page.getTotalElements());
        modelAndView.addObject("pageSize",page.getSize());
        modelAndView.addObject("deliverySearcher",deliverySearcher);
        modelAndView.addObject("pageIndex",deliverySearcher.getPageIndex());
        modelAndView.addObject("authorType", agent.getClass().getSimpleName());
        modelAndView.addObject("authorList",authorList);

        return modelAndView;
    }
}
