package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.purchase.AgentDeliveryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by wuxiongliu on 2016/5/26.
 */

@Controller
@PreAuthorize("hasAnyRole('PURCHASE')")
// TODO: 2016/5/31
@RequestMapping("/purchase")
public class AgentPurchaseDeliveryController {

    @Autowired
    private AuthorService authorService;

    @Autowired
    private AgentDeliveryService agentDeliveryService;

    @RequestMapping(value = "/showPurchaseDeliveryList")
    public ModelAndView showPurchaseDeliveryList(@AgtAuthenticationPrincipal(type = Agent.class) Agent agent, DeliverySearcher deliverySearcher){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/purchase/agent_purchase_delivery_list");

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

    @RequestMapping(value = "/showReturnDeliveryList")
    public ModelAndView showReturnDeliveryList(@AgtAuthenticationPrincipal(type = Agent.class) Agent agent, DeliverySearcher deliverySearcher){
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("/purchase/agent_return_delivery_list");

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
