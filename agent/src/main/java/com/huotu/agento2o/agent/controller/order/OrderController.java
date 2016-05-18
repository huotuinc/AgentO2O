package com.huotu.agento2o.agent.controller.order;

import com.alibaba.fastjson.JSON;
import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.common.util.*;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.order.MallOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.model.order.OrderExportModel;
import com.huotu.agento2o.service.model.order.OrderForDelivery;
import com.huotu.agento2o.service.searchable.OrderSearchCondition;
import com.huotu.agento2o.service.service.order.MallDeliveryService;
import com.huotu.agento2o.service.service.order.MallOrderService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.*;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

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

    @Autowired
    private MallDeliveryService deliveryService;

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
        Page<MallOrder> ordersList  = orderService.findAll(pageIndex, author, Constant.PAGESIZE, searchCondition);
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
        model.addAttribute("authorType", author.getClass().getSimpleName());
        return "order/order_list";
    }

    @RequestMapping(value = "/showOrderDetail", method = RequestMethod.GET)
    public ModelAndView showOrderDetail(@RequestParam("orderId") String orderId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("order/order_detail");
        OrderDetailModel orderDetailModel = orderService.findOrderDetail(orderId);
        modelAndView.addObject("orderDetail", orderDetailModel);
        return modelAndView;
    }

    /**
     * 显示门店备注及备注等级
     */
    @RequestMapping("/showRemark")
    public String showRemark(
            @AgtAuthenticationPrincipal Shop shop,
            Model model,
            @RequestParam(name = "orderId",required = true) String orderId){
        MallOrder order = orderService.findByShopAndOrderId(shop,orderId);
        if(order != null){
            model.addAttribute("orderId",order.getOrderId());
            model.addAttribute("agentMarkType",order.getAgentMarkType());
            model.addAttribute("agentMarkText",order.getAgentMarkText());
        }
        return "order/remark";
    }

    /**
     * 修改门店备注及备注等级
     */
    @RequestMapping("/remark")
    @ResponseBody
    public ApiResult editRemark(
            @AgtAuthenticationPrincipal Shop shop,
            @RequestParam(name = "orderId",required = true) String orderId,
            String agentMarkType,
            String agentMarkText){
        if(orderId != ""){
            return orderService.updateRemark(shop,orderId,agentMarkType,agentMarkText);
        }else{
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
    }

    /**
     * Excel导出页面
     *
     * @param orderExportModel OrderExportModel
     * @return "orderExportV2.html";
     */
    @RequestMapping("/orderExport")
    public String OrderExportV2(OrderExportModel orderExportModel, Model model) {
        model.addAttribute("orderExportModel", orderExportModel);
        return "order/orderExportV2";
    }


    /**
     * 导出Excel 订单
     *
     * @param agentId
     */
    @RequestMapping("exportExcel")
    public void exportExcel(OrderSearchCondition searchCondition,
                            @AgtAuthenticationPrincipal Author author,
                            int txtBeginPage,
                            int txtEndPage,
                            Integer agentId,
                            HttpSession session,
                            HttpServletResponse response) {
        searchCondition.setAgentId(agentId);
        int pageSize = 20 * (txtEndPage - txtBeginPage + 1);
        Page<MallOrder> pageInfo = orderService.findAll(txtBeginPage,author ,pageSize, searchCondition);
        List<MallOrder> orderList = pageInfo.getContent();
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = "order-" + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = orderService.createWorkBook(orderList);
            fOut = response.getOutputStream();
            workbook.write(fOut);
        } catch (Exception ignored) {
        } finally {
            try {
                assert fOut != null;
                fOut.flush();
                fOut.close();
            } catch (IOException ignored) {
            }
            session.setAttribute("state", "open");
        }
    }

    @RequestMapping(value = "/batchDeliver", method = RequestMethod.GET)
    public String batchDeliver() {
        return "order/batchDeliver";
    }

    @RequestMapping(value = "/batchDeliver", method = RequestMethod.POST)
    public String batchDeliver(
            @AgtAuthenticationPrincipal Shop shop,
            HttpServletRequest request,
            RedirectAttributes redirectAttributes
    ) throws IOException {
        MultipartHttpServletRequest multipartHttpServletRequest = (MultipartHttpServletRequest) request;
        MultipartFile file = multipartHttpServletRequest.getFile("FileData");
        ApiResult apiResult;
        if (file == null) {
            apiResult = ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        } else {
            HSSFWorkbook workbook = new HSSFWorkbook(file.getInputStream());
            List<List<ExcelHelper.CellDesc>> deliveryInfo = ExcelHelper.readWorkbook(workbook, 1, 6, 0);
            List<OrderForDelivery> orderForDeliveries = new ArrayList<>();
            deliveryInfo.forEach(itemInfo -> {
                OrderForDelivery orderForDelivery = new OrderForDelivery();
                orderForDelivery.setOrderNo((String) itemInfo.get(0).getValue());
                orderForDelivery.setLogiName((String) itemInfo.get(1).getValue());
                orderForDelivery.setLogiNo((String) itemInfo.get(2).getValue());
                orderForDelivery.setLogiCode((String) itemInfo.get(3).getValue());
                String logiMoneyStr = (String) itemInfo.get(4).getValue();
                double logiMoney = 0;
                if (!StringUtils.isEmpty(logiMoneyStr)) logiMoney = Double.parseDouble(logiMoneyStr);
                orderForDelivery.setLogiMoney(logiMoney);
                orderForDelivery.setRemark((String) itemInfo.get(5).getValue());
                orderForDeliveries.add(orderForDelivery);
            });
            apiResult = deliveryService.pushBatchDelivery(orderForDeliveries, shop.getId());
        }
        redirectAttributes.addFlashAttribute("deliverResult", JSON.toJSONString(apiResult));
        return "redirect: batchDeliver";
    }

    /**
     * Excel文件模板下载
     * upLoadExcel
     */
    @RequestMapping("/upLoadExcel")
    public void download(HttpServletRequest request,
                         HttpServletResponse response) throws Exception {
        response.setContentType("text/html;charset=UTF-8");
        BufferedInputStream in = null;
        BufferedOutputStream out = null;
        request.setCharacterEncoding("UTF-8");
        String rootpath = request.getSession().getServletContext().getRealPath("/");
        String fileName = "resource/tmpl_delivery_orders2.xls";
        try {
            File f = new File(rootpath + fileName);
            response.setContentType("application/x-excel");
            response.setCharacterEncoding("UTF-8");
            response.setHeader("Content-Disposition", "attachment; filename=" + fileName);
            response.setHeader("Content-Length", String.valueOf(f.length()));
            in = new BufferedInputStream(new FileInputStream(f));
            out = new BufferedOutputStream(response.getOutputStream());
            byte[] data = new byte[1024];
            int len = 0;
            while (-1 != (len = in.read(data, 0, data.length))) {
                out.write(data, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (in != null) {
                in.close();
            }
            if (out != null) {
                out.close();
            }
        }
    }
}
