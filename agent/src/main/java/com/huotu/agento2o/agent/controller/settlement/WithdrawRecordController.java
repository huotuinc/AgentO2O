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
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.settlement.Account;
import com.huotu.agento2o.service.entity.settlement.WithdrawRecord;
import com.huotu.agento2o.service.model.settlement.WithdrawApplyInfo;
import com.huotu.agento2o.service.searchable.WithdrawRecordSearcher;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.settlement.AccountService;
import com.huotu.agento2o.service.service.settlement.WithdrawRecordService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.time.LocalDateTime;
import java.time.temporal.TemporalAdjusters;

/**
 * Created by helloztt on 2016/6/14.
 */
@Controller
@RequestMapping("/withdraw")
@PreAuthorize("hasAnyRole('SHOP') or hasAnyAuthority('SETTLEMENT')")
public class WithdrawRecordController {
    @Autowired
    private WithdrawRecordService withdrawRecordService;
    @Autowired
    private AccountService accountService;
    @Autowired
    private ShopService shopService;

    @RequestMapping("withdrawRecords")
    public ModelAndView showWithdrawRecords(@AgtAuthenticationPrincipal(type = ShopAuthor.class) ShopAuthor shop, WithdrawRecordSearcher withdrawRecordSearcher) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        Account account = accountService.findByAuthor(shop);
        Page<WithdrawRecord> withdrawRecordPage = withdrawRecordService.getPage(account, withdrawRecordSearcher);
        modelAndView.setViewName("settlement/withdrawRecords");
        modelAndView.addObject("authorAccount", account);
        modelAndView.addObject("withdrawRecordPage", withdrawRecordPage);
        modelAndView.addObject("totalRecords", withdrawRecordPage.getTotalElements());
        modelAndView.addObject("totalPages", withdrawRecordPage.getTotalPages());
        modelAndView.addObject("pageSize", withdrawRecordPage.getSize());
        return modelAndView;
    }

    @RequestMapping("saveAuthorAccountInfo")
    @ResponseBody
    public ApiResult saveAuthorAccountInfo(@AgtAuthenticationPrincipal(type = ShopAuthor.class) ShopAuthor shop,
                                           @RequestParam(value = "bankName") String bankName,
                                           @RequestParam(value = "accountName") String accountName,
                                           @RequestParam(value = "accountNo") String accountNo) throws Exception{
        if (StringUtils.isEmpty(bankName)) {
            return new ApiResult("开户银行名称不能为空");
        }
        if (StringUtils.isEmpty(accountName)) {
            return new ApiResult("账户名不能为空");
        }
        if (StringUtils.isEmpty(accountNo)) {
            return new ApiResult("银行账号不能为空");
        }
        ApiResult result = shopService.updateAccountInfo(shop.getId(), bankName, accountName, accountNo);
        return result;
    }

    @RequestMapping(value = "/checkWithdrawNum",method = RequestMethod.GET,produces = "application/json")
    @ResponseBody
    public ApiResult checkWithdrawNum(@AgtAuthenticationPrincipal(type = ShopAuthor.class) ShopAuthor shop, @RequestParam("customerNo") Integer customerId) throws Exception {
        if(!shop.getCustomer().getCustomerId().equals(customerId)){
            return new ApiResult("没有权限！");
        }
        Account supplierAccount = accountService.findByAuthor(shop);
        LocalDateTime now = LocalDateTime.now();
        //获取申请日期当月的第一天
        LocalDateTime firstDayOfMonth  = now.with(TemporalAdjusters.firstDayOfMonth()).toLocalDate().atStartOfDay();
        //获取申请日期当月最后一天
        LocalDateTime lastDayOfMonth = now.with(TemporalAdjusters.lastDayOfMonth()).toLocalDate().atTime(23,59,59);
        long withdrawRecordCount = accountService.getRecords(supplierAccount, firstDayOfMonth, lastDayOfMonth);
        if( withdrawRecordCount >= supplierAccount.getWithdrawCount()) {
            return new ApiResult("提现次数已用完，无法提现！");
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @RequestMapping(value = "/addWithdrawRecord", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiResult applyWithdraw(@RequestParam("supplierAccountId") Integer supplierAccountId, WithdrawApplyInfo withdrawApplyInfo) throws Exception {
        ApiResult apiResult = new ApiResult();
        if (withdrawApplyInfo.getBalance() == 0) {
            apiResult.setMsg("提现金额不能为0");
            return apiResult;
        }
        if (StringUtils.isEmpty(withdrawApplyInfo.getAccountName())) {
            apiResult.setMsg("账户名不能为空");
            return apiResult;
        }
        if (StringUtils.isEmpty(withdrawApplyInfo.getBankName())) {
            apiResult.setMsg("开户银行名称不能为空");
            return apiResult;
        }
        if (StringUtils.isEmpty(withdrawApplyInfo.getAccountNo())) {
            apiResult.setMsg("银行账号不能为空");
            return apiResult;
        }
        apiResult = accountService.applyWithdraw(supplierAccountId, withdrawApplyInfo);
        return apiResult;
    }
}
