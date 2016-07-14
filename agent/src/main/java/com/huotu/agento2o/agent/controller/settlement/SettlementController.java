/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.controller.settlement;
import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.settlement.Settlement;
import com.huotu.agento2o.service.entity.settlement.SettlementOrder;
import com.huotu.agento2o.service.model.order.OrderDetailModel;
import com.huotu.agento2o.service.searchable.SettlementSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.goods.MallProductService;
import com.huotu.agento2o.service.service.settlement.SettlementOrderService;
import com.huotu.agento2o.service.service.settlement.SettlementService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.apache.poi.hssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
 * 结算单
 * Created by liual on 2015-11-15.
 */
@Controller
@RequestMapping("/settlement")
@PreAuthorize("hasAnyRole('SHOP') or hasAnyAuthority('SETTLEMENT')")
public class SettlementController {
    private static final Log log = LogFactory.getLog(SettlementController.class);
    @Autowired
    private SettlementService settlementService;
    @Autowired
    private SettlementOrderService settlementOrderService;
    @Autowired
    private MallGoodsService goodService;
    @Autowired
    private MallProductService productService;
    @Autowired
    private StaticResourceService resourceService;

    /**
     * 结算单列表
     *
     * @param shop
     * @param settlementSearcher
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/settlements", method = RequestMethod.GET)
    public ModelAndView showSettlementList(@AgtAuthenticationPrincipal(type = Shop.class)Shop shop, SettlementSearcher settlementSearcher) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("settlement/settlements");
        settlementSearcher.setShopId(shop.getId());
        Page<Settlement> settlementPage = settlementService.getPage(settlementSearcher);
        modelAndView.addObject("settlements", settlementPage.getContent());
        modelAndView.addObject("totalRecords", settlementPage.getTotalElements());
        modelAndView.addObject("totalPages", settlementPage.getTotalPages());
        modelAndView.addObject("pageSize", settlementPage.getSize());
        return modelAndView;
    }

    /**
     * 门店结算单审核
     * @param shop
     * @param settlementNo
     * @param authorStatus
     * @param settlementComment
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/changeSettlementStatus", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiResult changeSettlementStatus(@AgtAuthenticationPrincipal(type = Shop.class)Shop shop,
                                            @RequestParam("settlementNo") String settlementNo,
                                            @RequestParam("authorStatus") Integer authorStatus,
                                            @RequestParam("settlementComment") String settlementComment) throws Exception {
        ApiResult apiResult;
        try {
            apiResult = settlementService.updateSettlementStatus(settlementNo, -1, authorStatus, settlementComment);
        } catch (Exception e) {
            log.error("结算单编号" + settlementNo + " 审核失败", e);
            return new ApiResult("结算单审核失败！");
        }
        if (apiResult == null) {
            apiResult = new ApiResult("结算单审核失败！");
        }
        return apiResult;
    }

    @RequestMapping(value = "/batchChangeSettlementStatus", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiResult batchChangeSettlementStatus(@AgtAuthenticationPrincipal(type = Shop.class)Shop shop,
                                                 @RequestParam("authorStatus") Integer authorStatus,
                                                 @RequestParam("settlementNo") String... settlementNo) throws Exception {
        if (settlementNo.length == 0) {
            return ApiResult.resultWith(ResultCodeEnum.SETTLEMENTNO_NULL);
        }
        for (String s : settlementNo) {
            try {
                ApiResult apiResult = settlementService.updateSettlementStatus(s, -1, authorStatus, null);
                if (apiResult.getCode() != 200) {
                    return apiResult;
                }
            } catch (Exception e) {
                log.error("结算单编号" + s + " 结算失败", e);
                return new ApiResult("结算单结算失败！");
            }
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 结算单详细
     *
     * @param settlementNo
     * @param pageNo
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/settlementDetail", method = RequestMethod.GET)
    public ModelAndView showSettlementOrderList(@AgtAuthenticationPrincipal(type = Shop.class)Shop shop,
                                                @RequestParam(value = "settlementNo") String settlementNo,
                                                @RequestParam(value = "pageNo") Integer pageNo) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        Settlement settlement = settlementService.findBySettlementNo(settlementNo);
        modelAndView.setViewName("settlement/settlements_detail");
        Page<SettlementOrder> settlementOrderPage = settlementOrderService.getPage(settlement, pageNo, Constant.PAGESIZE);
        modelAndView.addObject("settlement", settlement);
        modelAndView.addObject("settlementOrders", settlementOrderPage);
        modelAndView.addObject("totalPages", settlementOrderPage.getTotalPages());
        modelAndView.addObject("totalRecords", settlementOrderPage.getTotalElements());
        modelAndView.addObject("currentPageNo", pageNo);
        return modelAndView;
    }

    @RequestMapping(value = "/exportSettlementDetail", method = RequestMethod.GET)
    @SuppressWarnings("Duplicates")
    public void exportSettlementDetail(@AgtAuthenticationPrincipal(type = Shop.class)Shop shop,
                                       @RequestParam(value = "settlementNo") String settlementNo,
                                       @RequestParam(value = "beginPage") Integer beginPage,
                                       @RequestParam(value = "endPage") Integer endPage,
                                       HttpServletResponse response) throws Exception {
        int pageSize = 20 * (endPage - beginPage + 1);
        Settlement settlement = settlementService.findBySettlementNo(settlementNo);
        String createTime = StringUtil.DateFormat(settlement.getCreateDateTime(), StringUtil.DATE_PATTERN_WITH_NOSUP);
        String excelName = URLEncoder.encode(createTime + shop.getName() + "结算单明细", "UTF-8");
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
            row.createCell(1).setCellValue(StringUtil.DateFormat(settlementOrder.getOrderDateTime(), StringUtil.TIME_PATTERN));
            row.createCell(2).setCellValue(StringUtil.DateFormat(settlementOrder.getPayDateTime(), StringUtil.TIME_PATTERN));
            row.createCell(3).setCellValue(settlementOrder.getFinalAmount());
            row.createCell(4).setCellValue(settlementOrder.getFreight());
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
        ServletOutputStream stream = response.getOutputStream();
        workbook.write(stream);
        stream.close();
    }

    /**
     * 结算单中的订单详细
     *
     * @param orderId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showOrderDetail", method = RequestMethod.GET)
    public ModelAndView showOrderDetail(@RequestParam("orderId") String orderId) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("settlement/settlements_orderDetail");
        OrderDetailModel orderDetailModel = settlementService.findOrderDetail(orderId);
        resourceService.setListUri(orderDetailModel.getSupOrderItemList(),"thumbnailPic","picUri");
        modelAndView.addObject("orderDetail", orderDetailModel);
        return modelAndView;
    }


}
