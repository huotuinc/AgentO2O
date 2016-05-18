package com.huotu.agento2o.agent.controller.order;

import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallAfterSalesItem;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import com.huotu.agento2o.service.searchable.AfterSaleSearch;
import com.huotu.agento2o.service.service.order.MallAfterSalesItemService;
import com.huotu.agento2o.service.service.order.MallAfterSalesService;
import com.huotu.agento2o.service.service.order.MallOrderService;
import com.huotu.agento2o.service.service.user.UserBaseInfoService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by AiWelv on 2016/5/11.
 */
@Controller
@RequestMapping("/agent/afterSale")
@PreAuthorize("hasAnyRole('AGENT','ORDER')")
public class AfterSaleController {

    private static final Log log = LogFactory.getLog(AfterSaleController.class);

    @Autowired
    private MallAfterSalesService afterSalesService;
    @Autowired
    private MallAfterSalesItemService afterSalesItemService;
    @Autowired
    private UserBaseInfoService userBaseInfoService;
    @Autowired
    private MallOrderService orderService;

    @RequestMapping("/afterSaleList")
    public ModelAndView afterSaleList(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true, defaultValue = "1") int pageIndex,
            AfterSaleSearch afterSaleSearch
    ){
        ModelAndView modelAndView = new ModelAndView();
        afterSaleSearch.setAgentId(author.getId());
        if (author.getId() > 0) {
            Page<MallAfterSales> afterSales = afterSalesService.findAll(pageIndex, author, Constant.PAGESIZE, author.getId(), afterSaleSearch);
            modelAndView.addObject("afterSales", afterSales.getContent());
            modelAndView.addObject("pageSize", Constant.PAGESIZE);
            modelAndView.addObject("totalRecords", afterSales.getTotalElements());
            modelAndView.addObject("totalPages", afterSales.getTotalPages());
            modelAndView.addObject("afterSaleSearch", afterSaleSearch);
            modelAndView.addObject("pageIndex", pageIndex);
            modelAndView.addObject("afterSaleStatusList", AfterSaleEnum.AfterSaleStatus.values());
        }
        modelAndView.setViewName("order/afterSales/after_sales_list");
        return modelAndView;
    }

    @RequestMapping("/afterSalesDetail")
    @PreAuthorize("hasAnyRole('ORDER','AGENT')")
    public String afterSaleDetail(
            @AgtAuthenticationPrincipal Author author,
            String afterId,
            Model model
    ) {
        MallAfterSales afterSales = afterSalesService.findByAfterId(afterId);
        List<MallAfterSalesItem> afterSalesItems = afterSalesItemService.findByAfterId(afterId);
        UserBaseInfo userBaseInfo = userBaseInfoService.findById(afterSales.getMemberId());
        MallOrder order = orderService.findByOrderId(afterSales.getOrderId());

        model.addAttribute("afterSales", afterSales);
        model.addAttribute("afterSalesItems", afterSalesItems);
        model.addAttribute("hbResourceHost", SysConstant.HUOBANMALL_RESOURCE_HOST);
        model.addAttribute("userInfo", userBaseInfo);
        model.addAttribute("order", order);

        model.addAttribute("authorType", author.getClass().getSimpleName());
        return "order/afterSales/after_sales_detail";
    }

}
