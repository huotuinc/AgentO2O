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
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.config.Address;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.service.config.AddressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by WangJie on 2016/5/24.
 */
@Controller
@RequestMapping("/config")
@PreAuthorize("hasAnyRole('BASE_DATA','SHOP','AGENT')")
public class AddressController {

    @Autowired
    private AddressService addressService;

    @RequestMapping("/addressList")
    public String showAddressList(@AgtAuthenticationPrincipal Author author, Model model) {
        List<Address> addressList = addressService.findAddressByAuthorId(author.getId());
        model.addAttribute("addressList", addressList);
        return "config/addressList";
    }

    @RequestMapping(value = "/saveAddress", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addOrSaveAddress(@AgtAuthenticationPrincipal Author author, Model model, Address requestAddress) {
        return addressService.addOrUpdate(requestAddress.getId(), author.getId(), requestAddress);
    }

    @RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteAddress(@AgtAuthenticationPrincipal Author author, Model model, Integer addressId) {
        return addressService.deleteAddress(addressId, author.getId());
    }

    @RequestMapping(value = "/configDefault", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult configDefault(@AgtAuthenticationPrincipal Author author, Model model, Integer addressId) {
        return addressService.configDefault(addressId, author.getId());
    }

    /**
     * 根据id获取收货地址
     *
     * @param addressId
     * @return
     */
    @RequestMapping(value = "/showAddress", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult showAddress(Integer addressId) {
        Address address = addressService.findById(addressId);
        if (address == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, address);
    }
}