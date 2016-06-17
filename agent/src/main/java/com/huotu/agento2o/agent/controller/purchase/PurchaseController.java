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
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallGoodsType;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.goods.MallGoodsTypeService;
import com.huotu.agento2o.service.service.goods.MallProductService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
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
 * 商品采购/货品采购
 * Created by helloztt on 2016/5/12.
 */
@Controller
@PreAuthorize("hasAnyRole('AGENT','SHOP') or hasAnyAuthority('PURCHASE')")
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
    @Autowired
    private MallGoodsTypeService goodsTypeService;
    @Autowired
    private AgentProductService agentProductService;

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
        model.setViewName("/purchase/purchase/goods_list");
        Page<MallGoods> goodsPage;
        //如果上级直系代理商为空，则读取平台方代理商品(Agent_Id=0)；否则读取 上级直系代理商商品
        // TODO: 2016/5/17 代理商/门店进货价读取
        if (author.getParentAuthor() == null) {
            goodsPage = goodsService.findByCustomerIdAndAgentId(author.getCustomer().getCustomerId(), author, goodsSearcher);
        } else {
            goodsPage = goodsService.findByAgentId(author, goodsSearcher);
        }
        List<MallGoods> goodsList = goodsPage.getContent();
        resourceService.setListUri(goodsList, "thumbnailPic", "picUri");

        //获取标准类目列表
        List<MallGoodsType> typeList = goodsTypeService.getAllParentTypeList(goodsSearcher.getStandardTypeId());
        //获取自定义类型列表
        List<MallGoodsType> customerTypeList = goodsTypeService.getCustomerTypeList(author.getCustomer().getCustomerId());
        if(customerTypeList != null && customerTypeList.size() > 0){
            model.addObject("typeList", typeList);
        }
        model.addObject("customerTypeList", customerTypeList);
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
        if (num == null || num.equals(0)) {
            return new ApiResult("订购数量必须大于0！");
        }
        //校验商品
        MallGoods goods = null;
        if (goodsId != null && !goodsId.equals(0)) {
            goods = goodsService.findByGoodsId(goodsId);
            //判断商品是否为空 或者 是否为平台代理商品
            if (goods == null || !author.getCustomer().getCustomerId().equals(goods.getCustomerId())) {
                return new ApiResult("请选择要订购的商品！");
            }
        }
        //校验货品
        MallProduct product = null;
        //货品为空时，如果商品只有一件货品，则取第一个货品；否则提示请选择货品
        if (productId == null || productId.equals(0)) {
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
        //校验库存
        if(author.getParentAuthor() == null){
            //上级为平台方
            if (num > product.getStore() - product.getFreez()) {
                return new ApiResult("库存不足！");
            }
        }else{
            AgentProduct agentProduct = agentProductService.findAgentProduct(author.getParentAuthor(),product);
            if(agentProduct != null && num > agentProduct.getStore() - agentProduct.getFreez()){
                return new ApiResult("库存不足！");
            }
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


    /**
     * 显示商品的货品列表
     * @param author
     * @param goodsId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showProductList")
    public ModelAndView showProductList(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(value = "goodsId", required = true) Integer goodsId) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/purchase/product_list");
        List<MallProduct> productList = null;
        if (goodsId != null && !goodsId.equals(0)) {
            productList = productService.findByGoodsId(author, goodsId);
        }
        model.addObject("productList", productList);
        return model;
    }

    /**
     * 根据标准类目ID，获取其子类目LIST
     *
     * @param standardTypeId 父类目ID
     * @return
     * @throws Exception
     */
    @RequestMapping("/getType")
    @ResponseBody
    public ApiResult getStandardType(
            @AgtAuthenticationPrincipal Author author,
            String standardTypeId) throws Exception{
        if (standardTypeId == null || standardTypeId.length() == 0) {
            standardTypeId = "0";
        }
        List<MallGoodsType> typeList = goodsTypeService.getGoodsTypeByParentId(standardTypeId);
        if (typeList != null && typeList.size() > 0) {
            return ApiResult.resultWith(ResultCodeEnum.SUCCESS, typeList);
        } else {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
    }




}
