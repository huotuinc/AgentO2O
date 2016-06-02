/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.controller.config;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.common.InvoiceEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.config.InvoiceConfig;
import com.huotu.agento2o.service.service.config.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * Created by AiWelv on 2016/5/20.
 */
@Controller
@PreAuthorize("hasAnyRole('BASE_DATA','SHOP','AGENT')")
@RequestMapping("/config")
public class InvoiceController {

    @Autowired
    private InvoiceService invoiceService;

    @RequestMapping(value = "/invoiceConfig")
    public ModelAndView invoiceConfig(@AgtAuthenticationPrincipal Author author, Model model) throws Exception {
        ModelAndView modelAndView = new ModelAndView();
        modelAndView.setViewName("config/invoiceConfig");

        InvoiceConfig normalInvoiceConfig = null;
        InvoiceConfig taxInvoiceConfig = null;
        int configType = 1;

        List<InvoiceConfig> invoiceConfigsList = invoiceService.findByAuthor(author);
        if (invoiceConfigsList != null){
            for (InvoiceConfig invoiceConfig : invoiceConfigsList){
                switch(invoiceConfig.getType()){
                    case TAXINVOICE:
                        taxInvoiceConfig = invoiceConfig;
                        break;
                    case NORMALINVOICE:
                        normalInvoiceConfig = invoiceConfig;
                        if (invoiceConfig.getDefaultType() == 1){
                            configType = 0;
                        }
                        break;
                }
            }
        }

        modelAndView.addObject("normalConfig", normalInvoiceConfig);
        modelAndView.addObject("taxConfig", taxInvoiceConfig);
        modelAndView.addObject("configType", configType);

        return modelAndView;
    }

    @RequestMapping(value = "/updateInvoiceConfig", method = RequestMethod.POST, produces = "application/json")
    @ResponseBody
    public ApiResult updateInvoiceConfig(
            @AgtAuthenticationPrincipal Author author,
            InvoiceConfig invoiceConfig,
            @RequestParam(value = "invoiceType") int invoiceType
    ) {
        InvoiceEnum.InvoiceTypeStatus typeStatus = EnumHelper.getEnumType(InvoiceEnum.InvoiceTypeStatus.class, invoiceType);
        if (invoiceConfig != null) {
            invoiceConfig.setType(typeStatus);
        }
        boolean isSuccess = invoiceService.updateInvoice(author, invoiceConfig);
        if (isSuccess) {
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        return ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
    }

}
