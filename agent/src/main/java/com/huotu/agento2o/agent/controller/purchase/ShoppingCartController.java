/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.InvoiceEnum;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.config.Address;
import com.huotu.agento2o.service.entity.config.InvoiceConfig;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.service.config.AddressService;
import com.huotu.agento2o.service.service.config.InvoiceService;
import com.huotu.agento2o.service.service.purchase.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/16.
 */
@Controller
@PreAuthorize("hasAnyRole('AGENT','SHOP') or hasAnyAuthority('PURCHASE')")
@RequestMapping("/shoppingCart")
public class ShoppingCartController {
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private StaticResourceService resourceService;
    @Autowired
    private InvoiceService invoiceService;
    @Autowired
    private AddressService addressService;

    /**
     * 显示购物车列表
     *
     * @param author
     * @return
     */
    @RequestMapping("/showShoppingCart")
    public ModelAndView showShoppingCart(
            @AgtAuthenticationPrincipal Author author) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/purchase/shopping_cart");
        List<ShoppingCart> shoppingCarts = shoppingCartService.findByAgentId(author);
        getPicUri(shoppingCarts);
        model.addObject("shoppingCartList", shoppingCarts);
        return model;
    }

    private void getPicUri(List<ShoppingCart> shoppingCartList) {
        shoppingCartList.forEach(p -> {
            if (p.getProduct() != null && p.getProduct().getGoods() != null && !StringUtil.isEmptyStr(p.getProduct().getGoods().getThumbnailPic())) {
                try {
                    URI picUri = resourceService.getResource(p.getProduct().getGoods().getThumbnailPic());
                    p.getProduct().getGoods().setPicUri(picUri);
                } catch (URISyntaxException e) {
                }
            }
        });
    }

    /**
     * 删除购物车中的货品
     *
     * @param author
     * @param id
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult delete(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true, name = "shoppingCartId") Integer id) throws Exception {
        ShoppingCart shoppingCart = shoppingCartService.findById(id, author);
        if (shoppingCart == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        shoppingCartService.deleteShoppingCart(shoppingCart);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @RequestMapping("/editNum")
    @ResponseBody
    public ApiResult editNum(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(required = true, name = "shoppingCartId") Integer id,
            @RequestParam(required = true, name = "num") Integer num) throws Exception {
        if (id == null || id.equals(0)) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (num.equals(0)) {
            return new ApiResult("订购数量必须大于0！");
        }
        ApiResult result = shoppingCartService.editShoppingCart(author, id, num);
        return result;
    }

    /**
     * 清空购物车
     *
     * @param author
     * @return
     * @throws Exception
     */
    @RequestMapping("/deleteAll")
    @ResponseBody
    public ApiResult deleteAll(@AgtAuthenticationPrincipal Author author) throws Exception {
        shoppingCartService.deleteAllShoppingCartByAgentId(author.getId());
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 填写采购单
     *
     * @param author
     * @param shoppingCartId
     * @return
     * @throws Exception
     */
    @RequestMapping("/addPurchase")
    public ModelAndView addPurchase(
            @AgtAuthenticationPrincipal Author author,
            String... shoppingCartId) throws Exception {
        ModelAndView model = new ModelAndView();
        if (shoppingCartId == null || shoppingCartId.length == 0) {
            //如果没有选中的购物车货品ID，则跳转到购物车
            return new ModelAndView("redirect:/shoppingCart/showShoppingCart");
        }
        model.setViewName("/purchase/purchase/add_purchase");
        List<Integer> shoppingCartIds = new ArrayList<>();
        for (String shoppingCart : shoppingCartId) {
            shoppingCartIds.add(Integer.valueOf(shoppingCart));
        }
        List<ShoppingCart> shoppingCartList = shoppingCartService.findById(shoppingCartIds, author);
        if (shoppingCartList != null && shoppingCartList.size() > 0) {
            getPicUri(shoppingCartList);
        } else {
            //如果没有可采购的购物车货品ID，则跳转到购物车
            return new ModelAndView("redirect:/shoppingCart/showShoppingCart");
        }
        AgentPurchaseOrder agentPurchaseOrder = new AgentPurchaseOrder();
        //获取默认收货地址
        // TODO: 2016/7/13
        Address defaultAddress = addressService.findDefaultByAuthorId(author);
        if(defaultAddress != null){
//            agentPurchaseOrder.setShipAddr(defaultAddress.getProvince() + defaultAddress.getCity() + defaultAddress.getDistrict() + defaultAddress.getAddress());
            agentPurchaseOrder.setShipAddr(defaultAddress.getAddress());
            agentPurchaseOrder.setShipMobile(defaultAddress.getTelephone());
            agentPurchaseOrder.setShipName(defaultAddress.getReceiver());
        }
        //获取默认发票类型及基本信息
        int invoiceType = 0;
        InvoiceConfig defaultConfig = invoiceService.findDefaultByAuthor(author);
        if (defaultConfig != null && defaultConfig.getType() != null && InvoiceEnum.InvoiceTypeStatus.NORMALINVOICE.equals(defaultConfig.getType())) {
            invoiceType = 1;
            agentPurchaseOrder.setTaxTitle(defaultConfig.getTaxTitle());
            agentPurchaseOrder.setTaxContent(defaultConfig.getTaxContent());
        } else if (defaultConfig != null && defaultConfig.getType() != null && InvoiceEnum.InvoiceTypeStatus.TAXINVOICE.equals(defaultConfig.getType())) {
            invoiceType = 2;
            agentPurchaseOrder.setTaxTitle(defaultConfig.getTaxTitle());
            agentPurchaseOrder.setTaxContent(defaultConfig.getTaxContent());
            agentPurchaseOrder.setTaxpayerCode(defaultConfig.getTaxpayerCode());
            agentPurchaseOrder.setBankName(defaultConfig.getBankName());
            agentPurchaseOrder.setAccountNo(defaultConfig.getAccountNo());
        }
        agentPurchaseOrder.setTaxType(EnumHelper.getEnumType(PurchaseEnum.TaxType.class, invoiceType));
        model.addObject("agentPurchaseOrder", agentPurchaseOrder);
        model.addObject("shoppingCartList", shoppingCartList);
        model.addObject("sendmentEnum", PurchaseEnum.SendmentStatus.values());
        model.addObject("taxTypeEnum", PurchaseEnum.TaxType.values());
        return model;

    }
}
