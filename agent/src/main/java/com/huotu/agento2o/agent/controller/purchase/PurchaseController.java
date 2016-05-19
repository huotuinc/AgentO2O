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
import com.huotu.agento2o.common.ienum.ICommonEnum;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentPurchaseOrder;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.goods.MallProductService;
import com.huotu.agento2o.service.service.purchase.AgentPurchaseOrderService;
import com.huotu.agento2o.service.service.purchase.ShoppingCartService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/12.
 */
@Controller
@PreAuthorize("hasAnyRole('PURCHASE')")
@RequestMapping("/purchase")
public class PurchaseController {
    @Autowired
    private MallGoodsService goodsService;
    @Autowired
    private MallProductService productService;
    @Autowired
    private ShoppingCartService shoppingCartService;
    @Autowired
    private StaticResourceService resourceService;
    @Autowired
    private AgentPurchaseOrderService purchaseOrderService;

    /**
     * 显示商品采购列表
     * 如果上级直系代理商为空，则读取平台方代理商品(Agent_Id=0)；否则读取 上级直系代理商商品
     *
     * @param author
     * @param goodsSearcher
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showGoodsList")
    public ModelAndView showGoodsList(
            @AgtAuthenticationPrincipal Author author,
            GoodsSearcher goodsSearcher) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/goods_list");
        Page<MallGoods> goodsPage;
        //如果上级直系代理商为空，则读取平台方代理商品(Agent_Id=0)；否则读取 上级直系代理商商品
        // TODO: 2016/5/17 代理商/门店进货价读取
        if (author.getParentAuthor() == null) {
            goodsPage = goodsService.findByCustomerIdAndAgentId(author.getCustomer().getCustomerId(), 0, goodsSearcher);
        } else {
            goodsPage = goodsService.findByAgentId(author.getParentAuthor().getId(), goodsSearcher);
        }
        List<MallGoods> goodsList = goodsPage.getContent();
        resourceService.setListUri(goodsList,"thumbnailPic","picUri");
        model.addObject("goodsList", goodsList);
        model.addObject("pageSize", Constant.PAGESIZE);
        model.addObject("pageNo", goodsSearcher.getPageNo());
        model.addObject("totalPages", goodsPage.getTotalPages());
        model.addObject("totalRecords", goodsPage.getTotalElements());
        return model;
    }

    /**
     * 将商品/货品添加到购物车
     *
     * @param author
     * @param goodsId   商品ID
     * @param productId 货品ID
     * @param num       订购数量
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addShopping", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addShopping(
            @AgtAuthenticationPrincipal Author author,
            Integer goodsId,
            Integer productId,
            @RequestParam(required = true) Integer num) throws Exception {
        ApiResult result = ApiResult.resultWith(ResultCodeEnum.SAVE_DATA_ERROR);
        //校验数量
        if (num == null || num == 0) {
            return new ApiResult("请输入要订购的数量");
        }
        //校验商品
        MallGoods goods = null;
        if (goodsId != null && goodsId != 0) {
            goods = goodsService.findByGoodsId(goodsId);
            if (goods == null) {
                return new ApiResult("请选择要订购的商品！");
            }
        }
        //校验货品
        MallProduct product = null;
        //货品未空时，如果商品只有一件货品，则取第一个货品；否则提示请选择货品
        if (productId == null || productId == 0) {
            if (goods != null && goods.getProducts() != null && goods.getProducts().size() == 1) {
                product = goods.getProducts().get(0);
            } else {
                return new ApiResult("请选择要订购的货品！");
            }
        } else {
            product = productService.findByProductId(productId);
        }
        if (product == null) {
            return new ApiResult("请选择要订购的商品！");
        }
        //增加购物车记录
        ShoppingCart cart = new ShoppingCart();
        cart.setAuthor(author);
        cart.setProduct(product);
        cart.setNum(num);
        cart.setCreateTime(new Date());
        cart = shoppingCartService.createShoppingCart(cart);
        if (cart != null) {
            result = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }
        return result;
    }


    @RequestMapping(value = "/showProductList")
    public ModelAndView showProductList(
            @AgtAuthenticationPrincipal Author author,
            Integer goodsId) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/product_list");
        List<MallProduct> productList = null;
        if (goodsId != null && goodsId != 0) {
            productList = productService.findByGoodsId(goodsId);
        }
        model.addObject("productList", productList);
        return model;
    }

    /**
     * 采购下单
     *
     * @param author
     * @param agentPurchaseOrder
     * @param shoppingCartIds
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/addPurchase", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addPurchase(
            @AgtAuthenticationPrincipal Author author, HttpServletRequest request,
            AgentPurchaseOrder agentPurchaseOrder, String... shoppingCartIds) throws Exception {
        String sendModeCode = request.getParameter("sendModeCode");
        String taxTypeCode = request.getParameter("taxTypeCode");
        //采购信息校验
        if (agentPurchaseOrder == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (shoppingCartIds.length == 0) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (StringUtil.isEmptyStr(agentPurchaseOrder.getShipName()) || StringUtil.isEmptyStr(agentPurchaseOrder.getShipMobile())
                || StringUtil.isEmptyStr(agentPurchaseOrder.getShipAddr())) {
            return new ApiResult("请输入收货信息");
        }
        if (StringUtil.isEmptyStr(sendModeCode)) {
            return new ApiResult("请选择配送方式");
        }
        if (StringUtil.isEmptyStr(taxTypeCode)) {
            return new ApiResult("请选择发票类型");
        }
        if ("1".equals(taxTypeCode)) {
            if (StringUtil.isEmptyStr(agentPurchaseOrder.getTaxTitle()) || StringUtil.isEmptyStr(agentPurchaseOrder.getTaxContent())) {
                return new ApiResult("请输入普通发票信息");
            }
        } else if ("2".equals(taxTypeCode)) {
            if (StringUtil.isEmptyStr(agentPurchaseOrder.getTaxTitle()) || StringUtil.isEmptyStr(agentPurchaseOrder.getTaxContent())
                    || StringUtil.isEmptyStr(agentPurchaseOrder.getTaxpayerCode()) || StringUtil.isEmptyStr(agentPurchaseOrder.getBankName())
                    || StringUtil.isEmptyStr(agentPurchaseOrder.getAccountNo())) {
                return new ApiResult("请输入增值税发票信息");
            }
        }
        agentPurchaseOrder.setSendMode(EnumHelper.getEnumType(PurchaseEnum.SendmentStatus.class, Integer.parseInt(sendModeCode)));
        agentPurchaseOrder.setTaxType(EnumHelper.getEnumType(PurchaseEnum.TaxType.class, Integer.parseInt(taxTypeCode)));
        ApiResult result = purchaseOrderService.addPurchaseOrder(agentPurchaseOrder, author, shoppingCartIds);
        return result;
    }


}
