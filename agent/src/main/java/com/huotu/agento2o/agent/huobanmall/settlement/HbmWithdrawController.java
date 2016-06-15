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
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.WithdrawEnum;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.settlement.AuthorAccount;
import com.huotu.agento2o.service.entity.settlement.WithdrawRecord;
import com.huotu.agento2o.service.model.settlement.WithdrawApplyInfo;
import com.huotu.agento2o.service.searchable.WithdrawRecordSearcher;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.settlement.AuthorAccountService;
import com.huotu.agento2o.service.service.settlement.WithdrawRecordService;
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
@RequestMapping("/huobanmall/withdraw")
public class HbmWithdrawController {
    private static final Log log = LogFactory.getLog(HbmWithdrawController.class);
    @Autowired
    private WithdrawRecordService withdrawRecordService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private AuthorAccountService authorAccountService;

    /**
     * 平台方显示需要审核（打款）的提现申请
     *
     * @param customerId
     * @param withdrawRecordSearcher
     * @return
     */
    @RequestMapping(value = "withdrawRecords")
    public ModelAndView showWithdrawRecordList(@RequestAttribute(value = "customerId") Integer customerId, WithdrawRecordSearcher withdrawRecordSearcher) {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("huobanmall/settlement/withdrawRecord");
        Page<WithdrawRecord> withdrawRecordPage = withdrawRecordService.getPageByCustomerId(customerId, withdrawRecordSearcher);
        List<Shop> shopList = shopService.findByCustomerId(customerId);
        modelAndView.addObject("withdrawRecordPage", withdrawRecordPage);
        modelAndView.addObject("shopList", shopList);
        modelAndView.addObject("totalRecords", withdrawRecordPage.getTotalElements());
        modelAndView.addObject("totalPages", withdrawRecordPage.getTotalPages());
        modelAndView.addObject("pageSize", withdrawRecordPage.getSize());
        return modelAndView;
    }

    /**
     * 分销商 查看
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "getWithdrawRecordInfo", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ApiResult getWithdrawRecordInfo(Integer id) {
        WithdrawApplyInfo withdrawApplyInfo = withdrawRecordService.findInfoById(id);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, withdrawApplyInfo);
    }

    /**
     * 分销商 查看批量合计
     *
     * @param choice
     * @return
     */
    @RequestMapping(value = "getWithdrawRecordsInfo", method = RequestMethod.GET, produces = "application/json")
    @ResponseBody
    public ApiResult getWithdrawRecordsInfo(String choice) {
        if (choice == null || choice.length() == 0) {
            return new ApiResult("不存在在需要操作的记录！", 500);
        }
        String[] str_id = choice.trim().split(",");
        //遍历判断是否存在不同供应商的记录
        Integer authorId = 0;
        double amount = 0;
        for (int i = 0; i < str_id.length; i++) {
            WithdrawRecord withdrawRecord = withdrawRecordService.findById(Integer.parseInt(str_id[i]));
            amount += withdrawRecord.getAmount();
            if (withdrawRecord.getStatus() != WithdrawEnum.WithdrawEnumStatus.APPLYING) {
                return new ApiResult("存在已审核或审核不通过的记录，请按审核状态查询后进行批量操作", 500);
            }
            if (authorId == 0) {
                authorId = withdrawRecord.getShopAccount().getAuthor().getId();
            } else if (!authorId.equals(withdrawRecord.getShopAccount().getAuthor().getId())) {
                return new ApiResult("存在不同供应商的记录，请按供应商查询后进行批量操作", 500);
            }
        }
        WithdrawApplyInfo withdrawApplyInfo = withdrawRecordService.findInfoById(Integer.parseInt(str_id[0]));
        withdrawApplyInfo.setBalance(amount);
        if (str_id.length != 1) {
            withdrawApplyInfo.setWithdrawNo("");
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, withdrawApplyInfo);
    }

    /**
     * 分销商审核提现，并打款
     * 修改提现记录的提现状态，打款时间，修改结算账户（供应商账户）的相关金额
     *
     * @param id
     * @param status
     * @return
     */
    @RequestMapping(value = "updateWithdrawRecord", produces = "application/json")
    @ResponseBody
    public ApiResult updateWithdrawRecord(Integer id, int status) {
        ApiResult apiResult = new ApiResult();
        WithdrawRecord withdrawRecord = withdrawRecordService.findById(id);
        if (withdrawRecord == null) {
            log.error("提现ID为空");
            return new ApiResult("提现编号错误！");
        }
        //判断该提现记录的状态是否为申请中
        if (withdrawRecord.getStatus() != WithdrawEnum.WithdrawEnumStatus.APPLYING) {
            log.error("提现记录已处理");
            return new ApiResult("该提现已处理，请勿重复操作！");
        }
        try {
            apiResult = authorAccountService.changeWithdraw(id, status);
        } catch (Exception e) {
            log.error("提现打款失败", e);
            apiResult.setCode(500);
            apiResult.setMsg("操作失败，请联系管理员！");
        }
        return apiResult;
    }

    /**
     * @param choice
     * @param status
     * @return
     */
    @RequestMapping(value = "updateWithdrawRecords", produces = "application/json")
    @ResponseBody
    public ApiResult updateWithdrawRecords(int status, String... choice) {
        ApiResult apiResult = new ApiResult();
        if (choice == null || choice.length == 0) {
            return new ApiResult("不存在在需要操作的记录！");
        }
        for (String str_id : choice) {
            if (str_id.length() <= 0) {
                continue;
            }
            WithdrawRecord withdrawRecord = withdrawRecordService.findById(Integer.parseInt(str_id));
            if (withdrawRecord == null) {
                log.error("提现ID为空");
                apiResult.setCode(500);
                apiResult.setMsg("批量请求失败，有提现记录不存在！");
            }
            if (withdrawRecord.getStatus() != WithdrawEnum.WithdrawEnumStatus.APPLYING) {
                log.error("提现记录已处理");
                apiResult.setCode(500);
                apiResult.setMsg("批量请求失败，有已处理的提现记录！");
            }
            try {
                apiResult = authorAccountService.changeWithdraw(Integer.parseInt(str_id), status);
            } catch (Exception e) {
                log.error("提现打款失败", e);
                apiResult.setCode(500);
                apiResult.setMsg("批量请求失败，请联系管理员！");
            }
        }
        return apiResult;
    }

    /**
     * 导出提现记录
     *
     * @param customerIdStr
     * @param beginPage
     * @param endPage
     * @param response
     * @throws Exception
     */
    @RequestMapping(value = "/exportWithdrawRecords", method = RequestMethod.GET)
    public void exportSettlementDetail(@RequestAttribute(value = "customerId") String customerIdStr,
                                       @RequestParam(value = "beginPage") Integer beginPage,
                                       @RequestParam(value = "endPage") Integer endPage,
                                       HttpServletResponse response) throws Exception {
        int pageSize = 20 * (endPage - beginPage + 1);
        int customerId = Integer.parseInt(customerIdStr);
        WithdrawRecordSearcher withdrawRecordSearcher = new WithdrawRecordSearcher();
        withdrawRecordSearcher.setPageNo(beginPage);
        withdrawRecordSearcher.setPageSize(pageSize);
        Page<WithdrawRecord> withdrawRecordPage = withdrawRecordService.getPageByCustomerId(customerId, withdrawRecordSearcher);
        String excelName = customerIdStr;
        if (withdrawRecordPage != null && withdrawRecordPage.getContent().size() > 0) {
            //分销商昵称
            excelName = withdrawRecordPage.getContent().get(0).getShopAccount().getAuthor().getCustomer().getNickName();
        }
        excelName = URLEncoder.encode(excelName + "提现记录", "UTF-8");
        List<WithdrawRecord> withdrawRecords = withdrawRecordPage.getContent();
        // 产生工作簿对象
        HSSFWorkbook workbook = new HSSFWorkbook();
        //产生工作表对象
        HSSFSheet sheet = workbook.createSheet("提现明细");
        sheet.setColumnWidth(0, 6000);
        sheet.setColumnWidth(1, 6000);
        sheet.setColumnWidth(2, 6000);
        sheet.setColumnWidth(3, 2000);
        sheet.setColumnWidth(4, 2000);
        sheet.setColumnWidth(5, 2000);
        sheet.setColumnWidth(6, 2000);
        sheet.setColumnWidth(7, 2000);
        sheet.setColumnWidth(8, 2000);

        //创建第一行作为表头
        HSSFRow row = sheet.createRow(0);
        //创建单元格，并设置值表头 设置表头居中
        HSSFCellStyle style = workbook.createCellStyle();
        style.setAlignment(HSSFCellStyle.ALIGN_CENTER); // 创建一个居中格式

        HSSFCell cell = row.createCell(0);
        cell.setCellValue("提款编号");
        cell.setCellStyle(style);

        cell = row.createCell(1);
        cell.setCellValue("供应商");
        cell.setCellStyle(style);

        cell = row.createCell(2);
        cell.setCellValue("提现金额");
        cell.setCellStyle(style);

        cell = row.createCell(3);
        cell.setCellValue("开户行");
        cell.setCellStyle(style);

        cell = row.createCell(4);
        cell.setCellValue("户名");
        cell.setCellStyle(style);

        cell = row.createCell(5);
        cell.setCellValue("提现账户");
        cell.setCellStyle(style);

        cell = row.createCell(6);
        cell.setCellValue("状态");
        cell.setCellStyle(style);

        cell = row.createCell(7);
        cell.setCellValue("申请日期");
        cell.setCellStyle(style);

        cell = row.createCell(8);
        cell.setCellValue("打款日期");
        cell.setCellStyle(style);

        for (int i = 0; i < withdrawRecords.size(); i++) {
            row = sheet.createRow(i + 1);
            WithdrawRecord withdrawRecord = withdrawRecords.get(i);
            row.createCell(0).setCellValue(withdrawRecord.getWithdrawNo());
            row.createCell(1).setCellValue(withdrawRecord.getShopAccount().getAuthor().getCustomer().getNickName());
            row.createCell(2).setCellValue(withdrawRecord.getAmount());
            row.createCell(3).setCellValue(withdrawRecord.getShopAccount().getAuthor().getBankName());
            row.createCell(4).setCellValue(withdrawRecord.getShopAccount().getAuthor().getAccountName());
            row.createCell(5).setCellValue(withdrawRecord.getShopAccount().getAuthor().getAccountNo());
            switch (withdrawRecord.getStatus()) {
                case APPLYING:
                    row.createCell(6).setCellValue("申请中");
                    break;
                case PAYED:
                    row.createCell(6).setCellValue("已打款");
                    break;
                default:
                    row.createCell(6).setCellValue(withdrawRecord.getStatus().getValue());
            }
            row.createCell(7).setCellValue(StringUtil.DateFormat(withdrawRecord.getApplyTime(), StringUtil.TIME_PATTERN));
            if (withdrawRecord.getRemitTime() != null) {
                row.createCell(8).setCellValue(StringUtil.DateFormat(withdrawRecord.getRemitTime(), StringUtil.TIME_PATTERN));
            }
        }
        response.setContentType("application/vnd.ms-excel;charset=utf-8");
        response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
        ServletOutputStream stream = response.getOutputStream();
        workbook.write(stream);
        stream.close();
    }

    /**
     * 根据分销商编号和门店编号获取结算账号
     *
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showWithdrawCount", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult showWithdrawCount(@RequestAttribute(value = "customerId") Integer customerId,
                                       @RequestParam(name = "authorId", required = true) Integer authorId) throws Exception {
        AuthorAccount authorAccount = authorAccountService.findByAuthorAndCustomer(authorId, customerId);
        if (authorAccount != null) {
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS, authorAccount.getWithdrawCount());
        } else {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
    }

    @RequestMapping(value = "/updateWithdrawCount", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult updateWithdrawCount(@RequestAttribute(value = "customerId") Integer customerId, Integer authorId, Integer withdrawCount) throws Exception {
        AuthorAccount authorAccount = null;
        authorAccount = authorAccountService.findByAuthorAndCustomer(authorId, customerId);
        if (authorAccount == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        authorAccount.setWithdrawCount(withdrawCount);
        authorAccountService.save(authorAccount);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
