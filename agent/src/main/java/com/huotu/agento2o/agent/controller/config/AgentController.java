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
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.author.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import java.net.URI;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by WangJie on 2016/5/23.
 */
@Controller
@RequestMapping("/config")
@PreAuthorize("hasAnyRole('AGENT','SHOP') or hasAnyAuthority('BASE_DATA')")
public class AgentController {

    @Autowired
    private AgentService agentService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private ShopService shopService;
    @Autowired
    private StaticResourceService resourceServer;

    /**
     * 基本资料设置
     *
     * @return
     */
    @RequestMapping("/baseConfig")
    public String baseConfig(@AgtAuthenticationPrincipal Author author, Model model) throws Exception {
        //获取最新数据
        author = authorService.findById(author);
        if(author == null){
            throw new Exception("没有权限");
        }
        if(Agent.class == author.getType()){
            //代理商
            Agent agent = author.getAuthorAgent();
            agent.setUsername(author.getUsername());
            model.addAttribute("agent", agent);
            return "config/agentConfig";
        }else if(Shop.class == author.getType()){
            Shop shop = author.getAuthorShop();
            URI logo_uri = null;
            if (!StringUtils.isEmpty(shop.getLogo())) {
                logo_uri = resourceServer.getResource(shop.getLogo());
            }
            //门店
            model.addAttribute("shop", shop);
            model.addAttribute("logo_uri", logo_uri);

            return "config/shopConfig";
        }
        throw new Exception("没有权限");
    }

    /**
     * 保存代理商基本信息
     *
     * @param agent
     * @param requestAgent
     * @param hotUserName
     * @return
     */
    @RequestMapping(value = "/saveAgentConfig", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAnyRole('AGENT') or hasAnyAuthority('BASE_DATA')")
    public ApiResult saveAgentConfig(@AgtAuthenticationPrincipal(type = Agent.class) Agent agent, Agent requestAgent, String hotUserName) {
        if (!agent.getId().equals(requestAgent.getId())) {
            return new ApiResult("没有权限");
        }
        if (StringUtil.isEmptyStr(requestAgent.getProvinceCode()) || StringUtil.isEmptyStr(requestAgent.getCityCode()) || StringUtil.isEmptyStr(requestAgent.getAddressArea())) {
            return new ApiResult("请选择区域");
        }
        if(StringUtil.isEmptyStr(requestAgent.getName())){
            return new ApiResult("请输入代理商名称");
        }
        if(StringUtil.isEmptyStr(requestAgent.getContact())){
            return new ApiResult("请输入联系人");
        }
        if(StringUtil.isEmptyStr(requestAgent.getMobile())){
            return new ApiResult("请输入手机号码");
        }
        if(StringUtil.isEmptyStr(requestAgent.getEmail())){
            return new ApiResult("请输入E-mail");
        }
        if(StringUtil.isEmptyStr(requestAgent.getAddress())){
            return new ApiResult("请输入详细地址");
        }
        if(StringUtil.isEmptyStr(requestAgent.getAccountName())){
            return new ApiResult("请输入开户名称");
        }
        if(StringUtil.isEmptyStr(requestAgent.getBankName())){
            return new ApiResult("请输入开户银行");
        }
        if(StringUtil.isEmptyStr(requestAgent.getAccountNo())){
            return new ApiResult("请输入银行卡号");
        }
        return agentService.saveAgentConfig(agent.getId(), requestAgent, hotUserName);
    }

    /**
     * 代理商获取可绑定的小伙伴用户名集合
     *
     * @param mallCustomer
     * @param hotUserName
     * @return
     */
    @RequestMapping(value = "/getUserNames", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAnyRole('AGENT','SHOP') or hasAnyAuthority('BASE_DATA')")
    public ApiResult getUserNames(@AgtAuthenticationPrincipal(type = MallCustomer.class) MallCustomer mallCustomer, String hotUserName) {
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, agentService.getHotUserNames(mallCustomer.getCustomer().getCustomerId(), hotUserName));
    }

    /**
     * 门店获取可绑定的小伙伴用户名集合
     *
     * @param curShop
     * @param hotUserName
     * @return
     */
    /*@RequestMapping(value = "/getShopUserNames", method = RequestMethod.POST)
    @ResponseBody
    @PreAuthorize("hasAnyRole('SHOP') or hasAnyAuthority('BASE_DATA')")
    public ApiResult getUserNames(@AgtAuthenticationPrincipal(type = Shop.class) Shop curShop, String hotUserName) {
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, shopService.getHotUserNames(curShop.getCustomer().getCustomerId(), hotUserName));
    }*/


    /**
     * 门店登陆 更新门店基本资料
     *
     * @param shop
     * @return
     */
    @RequestMapping(value = "/updateShop")
    @ResponseBody
    @PreAuthorize("hasAnyRole('SHOP') or hasAnyAuthority('BASE_DATA')")
    public ApiResult updateShop(@AgtAuthenticationPrincipal(type = Shop.class) Shop curShop, Shop shop, String hotUserName) {
        if (!curShop.getId().equals(shop.getId())) {
            return new ApiResult("没有权限");
        }
        if (StringUtil.isEmptyStr(shop.getProvinceCode()) || StringUtil.isEmptyStr(shop.getCityCode()) || StringUtil.isEmptyStr(shop.getAddressArea())) {
            return new ApiResult("请选择区域");
        }
        if(StringUtil.isEmptyStr(shop.getName())){
            return new ApiResult("请输入门店名称");
        }
        if(StringUtil.isEmptyStr(shop.getContact())){
            return new ApiResult("请输入联系人");
        }
        if(StringUtil.isEmptyStr(shop.getMobile())){
            return new ApiResult("请输入手机号码");
        }
        if(StringUtil.isEmptyStr(shop.getEmail())){
            return new ApiResult("请输入E-mail");
        }
        if(StringUtil.isEmptyStr(shop.getAddress())){
            return new ApiResult("请输入详细地址");
        }
        if(shop.getLat() == 0 || shop.getLan() == 0){
            return new ApiResult("请输入经纬度");
        }
        if(StringUtil.isEmptyStr(shop.getServeiceTel())){
            return new ApiResult("请输入客服电话");
        }
        if (StringUtil.isEmptyStr(shop.getLogo())) {
            return new ApiResult("请上传LOGO");
        }
        if(StringUtil.isEmptyStr(shop.getAfterSalTel())){
            return new ApiResult("请输入售后电话");
        }
        if(StringUtil.isEmptyStr(shop.getAfterSalQQ())){
            return new ApiResult("请输入售后QQ");
        }
        if(StringUtil.isEmptyStr(shop.getAccountName())){
            return new ApiResult("请输入开户名称");
        }
        if(StringUtil.isEmptyStr(shop.getBankName())){
            return new ApiResult("请输入开户银行");
        }
        if(StringUtil.isEmptyStr(shop.getAccountNo())){
            return new ApiResult("请输入银行卡号");
        }
        return shopService.saveShopConfig(shop, hotUserName);
    }

    @RequestMapping(value = "/uploadShopLogo", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> upLoadShop(
            @AgtAuthenticationPrincipal(type = Shop.class) Shop curShop,
            @RequestParam(value = "btnFile", required = false) MultipartFile files) {
        int result = 0;
        Map<Object, Object> responseData = new HashMap<Object, Object>();
        try {
            Date now = new Date();
            String fileName = files.getOriginalFilename();
            String prefix = fileName.substring(fileName.lastIndexOf(".") + 1);
            String path = StaticResourceService.IMG + curShop.getId() + "/" + StringUtil.DateFormat(now, "yyyyMMdd") + "/"
                    + StringUtil.DateFormat(now, "yyyyMMddHHmmSS") + "." + prefix;
            URI uri = resourceServer.uploadResource(path, files.getInputStream());
            responseData.put("fileUrl", uri);
            responseData.put("fileUri", path);
            result = 1;
        } catch (Exception e) {
            responseData.put("msg", e.getMessage());
        }
        responseData.put("result", result);

        return responseData;
    }

}
