/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.huobanmall.settlement;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.settlement.Settlement;
import com.huotu.agento2o.service.entity.settlement.SettlementOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.searchable.SettlementSearcher;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.settlement.SettlementOrderService;
import com.huotu.agento2o.service.service.settlement.SettlementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.net.URLEncoder;
import java.util.List;

/**
 * Created by helloztt on 2016/6/15.
 */
@Controller
@RequestMapping("/huobanmall/settlement")
public class HbmSettlementController {
    private static final Log log = LogFactory.getLog(HbmSettlementController.class);

    @Autowired
    private SettlementService settlementService;
    @Autowired
    private SettlementOrderService settlementOrderService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private MallCustomerService customerService;


    /**
     * 分销商后台显示 T+1 结算对账
     * 根据 customerId  获取 shop 列表
     *
     * @param settlementSearcher
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/settlements", method = RequestMethod.GET)
    public ModelAndView showSettlementList(@RequestAttribute(value = "customerId") Integer customerId, SettlementSearcher settlementSearcher) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("huobanmall/settlement/settlements");
        Page<Settlement> settlementPage = settlementService.getCustomerPage(customerId, settlementSearcher);
        List<ShopAuthor> shopList = shopService.findByCustomerId(customerId);
        modelAndView.addObject("settlementPage", settlementPage);
        modelAndView.addObject("shopList", shopList);
        modelAndView.addObject("totalRecords", settlementPage.getTotalElements());
        modelAndView.addObject("totalPages", settlementPage.getTotalPages());
        modelAndView.addObject("pageSize", settlementPage.getSize());
        return modelAndView;
    }

    /**
     * 分销商审核结算单
     *
     * @param settlementNo 结算单编号
     * @param status       分销商审核状态：1表示审核通过，2表示审核不通过
     * @return
     */
    @RequestMapping(value = "/updateStatusSettlement", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiResult updateSettlement(@RequestAttribute(value = "customerId") Integer customerId,String settlementNo, int status, String settlementComment) throws Exception {
        ApiResult apiResult = null;
        try {
            apiResult = settlementService.updateSettlementStatus(settlementNo, status, -1, settlementComment);
        } catch (Exception e) {
            log.error("结算单编号" + settlementNo + " 结算失败", e);
            return new ApiResult("结算单结算失败！");
        }
        if (apiResult == null) {
            apiResult = new ApiResult("结算单结算失败！");
        }
        return apiResult;
    }

    /**
     * 分销商批量审核结算单
     *
     * @param choice 结算单编号 以逗号分隔
     * @param status 分销商审核状态：1表示审核通过，2表示审核不通过
     * @return
     */
    @RequestMapping(value = "/updateStatusSettlements", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiResult updateSettlements(@RequestAttribute(value = "customerId") Integer customerId,int status, String... choice) throws Exception {
        ApiResult result = new ApiResult();
        if (choice == null || choice.length == 0) {
            return new ApiResult("不存在需要请求的结算单");
        } else {
            for (String settlementNo : choice) {
                if(settlementNo.length() <= 0){
                    continue;
                }
                try {
                    settlementService.updateSettlementStatus(settlementNo, status, -1, null);
                } catch (Exception e) {
                    log.error("结算单编号" + settlementNo + " 结算失败", e);
                    return new ApiResult("结算单结算失败！");
                }
            }
            if (result.getCode() == 0) {
                result.setCode(200);
                result.setMsg("批量请求成功！");
            }
        }
        return result;
    }

    /**
     * 显示结算单明细
     *
     * @param settlementNo
     * @param pageNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/settlementDetail", method = RequestMethod.GET)
    public ModelAndView showSettlementOrderList(@RequestAttribute(value = "customerId") Integer customerId,
                                                @RequestParam(value = "settlementNo") String settlementNo,
                                                @RequestParam(value = "pageNo") Integer pageNo) throws Exception {
        ModelAndView modelAndView = new ModelAndView();

        modelAndView.setViewName("huobanmall/settlement/settlements_detail");
        Settlement settlement = settlementService.findBySettlementNo(settlementNo);
        Page<SettlementOrder> settlementOrderPage = settlementOrderService.getPage(settlement, pageNo, Constant.PAGESIZE);
        modelAndView.addObject("settlement", settlement);
        modelAndView.addObject("settlementOrders", settlementOrderPage);
        modelAndView.addObject("totalPages", settlementOrderPage.getTotalPages());
        modelAndView.addObject("totalRecords", settlementOrderPage.getTotalElements());
        modelAndView.addObject("currentPageNo", pageNo);
        return modelAndView;
    }

    @SuppressWarnings("Duplicates")
    @RequestMapping(value = "/showOrderDetail", method = RequestMethod.GET)
    public ModelAndView showOrderDetail(@RequestAttribute(value = "customerId") Integer customerId,
                                        @RequestParam("orderId") String orderId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("huobanmall/settlement/settlements_orderDetail");
        OrderDetailModel orderDetailModel = settlementService.findOrderDetail(orderId);
        modelAndView.addObject("orderDetail", orderDetailModel);
        return modelAndView;
    }

    /**
     * 导出结算单明细
     *
     * @param settlementNo 结算编号
     * @param beginPage
     * @param endPage
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportSettlementDetail", method = RequestMethod.GET)
    @SuppressWarnings("Duplicates")
    public void exportSettlementDetail(@RequestAttribute(value = "customerId") Integer customerId,
                                       @RequestParam(value = "settlementNo") String settlementNo,
                                       @RequestParam(value = "beginPage") Integer beginPage,
                                       @RequestParam(value = "endPage") Integer endPage,
                                       HttpServletResponse response) throws Exception {
        int pageSize = 20 * (endPage - beginPage + 1);
        Settlement settlement = settlementService.findBySettlementNo(settlementNo);
        CustomerAuthor customer = customerService.findByCustomerId(customerId);
        String createTime = StringUtil.DateFormat(settlement.getCreateDateTime(),StringUtil.DATE_PATTERN);
        String excelName = URLEncoder.encode(createTime + customer.getNickName() + "结算单明细", "UTF-8");
        Page<SettlementOrder> settlementOrderPage = settlementOrderService.getPage(settlement, beginPage, pageSize);
        List<SettlementOrder> settlementOrderList = settlementOrderPage.getContent();
        // 产生工作簿对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        //产生工作表对象
        HSSFSheet sheet = workbook.createSheet("结算单明细");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 2000);
        sheet.setColumnWidth(4, 2000);
        //创建第一行作为表头
        HSSFRow row = sheet.createRow(0);
        //创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("订单编号");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("下单时间");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("支付时间");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("总货款");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("邮费");
        cell.setCellStyle(style);

        for (int i = 0; i < settlementOrderList.size(); i++) {
            SettlementOrder settlementOrder = settlementOrderList.get(i);
            row = sheet.createRow(i + 1);
            row.createCell(0).setCellValue(settlementOrder.getOrderId());
            row.createCell(1).setCellValue(StringUtil.DateFormat(settlementOrder.getOrderDateTime(),StringUtil.TIME_PATTERN));
            row.createCell(2).setCellValue(StringUtil.DateFormat(settlementOrder.getPayDateTime(),StringUtil.TIME_PATTERN));
            row.createCell(3).setCellValue(settlementOrder.getFinalAmount());
            row.createCell(4).setCellValue(settlementOrder.getFreight());
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
        ServletOutputStream stream = response.getOutputStream();
        workbook.write(stream);
        stream.close();
    }
}
