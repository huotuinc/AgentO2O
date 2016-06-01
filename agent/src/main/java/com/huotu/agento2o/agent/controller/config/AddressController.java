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
@PreAuthorize("hasAnyRole('AGENT','SHOP') or hasAnyAuthority('BASE_DATA')")
public class AddressController {

    @Autowired
    private AddressService addressService;

    /**
     * 展示收货地址列表
     *
     * @param author
     * @param model
     * @param target 返回页面 如果为“choice”则返回addressChoice；否则返回addressList
     * @return
     */
    @RequestMapping("/addressList")
    public String showAddressList(@AgtAuthenticationPrincipal Author author, Model model,String target) {
        List<Address> addressList = addressService.findAddressByAuthorId(author.getId());
        model.addAttribute("addressList", addressList);
        if(target != null && "choice".equals(target)){
            return "config/addressChoice";
        }else{
            return "config/addressList";
        }
    }

    /**
     * 保存或修改收货地址
     *
     * @param author
     * @param model
     * @param requestAddress
     * @return
     */
    @RequestMapping(value = "/saveAddress", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addOrSaveAddress(@AgtAuthenticationPrincipal Author author, Model model, Address requestAddress) {
        return addressService.addOrUpdate(requestAddress.getId(), author.getId(), requestAddress);
    }

    /**
     * 删除收货地址
     *
     * @param author
     * @param model
     * @param addressId
     * @return
     */
    @RequestMapping(value = "/deleteAddress", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteAddress(@AgtAuthenticationPrincipal Author author, Model model, Integer addressId) {
        return addressService.deleteAddress(addressId, author.getId());
    }

    /**
     * 设置默认地址
     *
     * @param author
     * @param model
     * @param addressId
     * @return
     */
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
    public ApiResult showAddress(@AgtAuthenticationPrincipal Author author,Integer addressId) {
        Address address = addressService.findById(addressId,author.getId());
        if (address == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, address);
    }
}
