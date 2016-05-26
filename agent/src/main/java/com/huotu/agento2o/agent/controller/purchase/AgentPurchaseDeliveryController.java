package com.huotu.agento2o.agent.controller.purchase;

import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by wuxiongliu on 2016/5/26.
 */

@Controller
@PreAuthorize("hasAnyRole('PURCHASE')")
@RequestMapping("/purchase")
public class AgentPurchaseDeliveryController {

    @RequestMapping(value = "/showDeliveryList")
    public ModelAndView showDeliveryList(){
        return null;
    }


}
