package com.huotu.agento2o.agent.controller.order;

import com.huotu.agento2o.agent.config.annotataion.AuthenticationPrincipal;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.order.MallDelivery;
import com.huotu.agento2o.service.searchable.DeliverySearcher;
import com.huotu.agento2o.service.service.order.MallDeliveryService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

/**
 * Created by AiWelv on 2016/5/11.
 */

@Controller
@RequestMapping("/order")
@PreAuthorize("hasAnyRole('AGENT','ORDER')")
public class DeliveryController {
    private static final Log log = LogFactory.getLog(DeliveryController.class);

    @Autowired
    private MallDeliveryService deliveryService;

    /**
     * 根据供应商和筛选条件查找发货单列表
     *
     * @param author
     * @return view:
     * "deliveryList"
     * model:
     * "deliveryList":List<OrdersDelivery>
     * "totalRecords":int
     * "totalPages":int
     * "pageSize":int
     * @throws Exception
     */
    @RequestMapping(value = "/deliveries", method = RequestMethod.GET)
    public ModelAndView showDeliveryList(
            @AuthenticationPrincipal Author author,
            @RequestParam(required = false, defaultValue = "1") int pageIndex,
            @RequestParam(required = false, defaultValue = "delivery") String type,
            DeliverySearcher deliverySearcher
    ) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        deliverySearcher.setAgentId(author.getId());
        if (type.equals("delivery"))
            modelAndView.setViewName("order/delivery_list");
        else
            modelAndView.setViewName("order/return_list");

        Page<MallDelivery> ordersDeliveryPage = deliveryService.getPage(pageIndex,author, Constant.PAGESIZE,  deliverySearcher, type);
        modelAndView.addObject("deliveryList", ordersDeliveryPage.getContent());
        modelAndView.addObject("totalRecords", ordersDeliveryPage.getTotalElements());
        modelAndView.addObject("totalPages", ordersDeliveryPage.getTotalPages());
        modelAndView.addObject("pageSize", ordersDeliveryPage.getSize());
        modelAndView.addObject("pageIndex", pageIndex);
        modelAndView.addObject("deliverySearcher", deliverySearcher);
        return modelAndView;
    }


}
