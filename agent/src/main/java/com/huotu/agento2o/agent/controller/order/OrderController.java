package com.huotu.agento2o.agent.controller.order;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.searchable.OrderSearchCondition;
import com.huotu.agento2o.service.service.order.MallOrderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

/**
 * Created by AiWelv on 2016/5/11.
 */
@Controller
@RequestMapping("/order")
@PreAuthorize("hasAnyRole('AGENT','ORDER')")
public class OrderController {

    private static final Log log = LogFactory.getLog(OrderController.class);

    @Autowired
    private MallOrderService orderService;

    /**
     * 获取订单全部数据 query 查询分页
     * Modified By cwb
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/getOrdersPage", method = RequestMethod.GET)
    public String getOrdersAll(
            @AgtAuthenticationPrincipal Author author,
            Model model,
            OrderSearchCondition searchCondition,
            @RequestParam(required = false, defaultValue = "1") int pageIndex
    ) {
        searchCondition.setAgentId(author.getId());
        Page<MallOrder> ordersList  = orderService.findAll(pageIndex, Constant.PAGESIZE, searchCondition);
        int totalPages = ordersList.getTotalPages();
        model.addAttribute("payStatusEnums", OrderEnum.PayStatus.values());
        model.addAttribute("shipStatusEnums",OrderEnum.ShipStatus.values());
        model.addAttribute("ordersList", ordersList.getContent());
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("agentId", author.getId());
        model.addAttribute("totalRecords", ordersList.getTotalElements());
        model.addAttribute("pageSize", ordersList.getSize());
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("pageIndex", pageIndex);
        return "order/order_list";
    }



}
