/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.controller.purchase;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallGoodsType;
import com.huotu.agento2o.service.entity.purchase.AgentGoods;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.entity.purchase.FreightTemplate;
import com.huotu.agento2o.service.repository.purchase.FreightTemplateRepository;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.goods.MallGoodsTypeService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 商品管理
 * Created by elvis on 2016/5/16.
 */
@Controller
@RequestMapping("/goods")
@PreAuthorize("hasAnyRole('AGENT','SHOP') or hasAnyAuthority('PURCHASE')")
public class GoodsController {

    @Autowired
    private AgentProductService agentProductService;
    @Autowired
    private MallGoodsService goodsService;
    @Autowired
    private FreightTemplateRepository freightTemplateRepository;
    @Autowired
    private StaticResourceService resourceService;
    @Autowired
    private MallGoodsTypeService goodsTypeService;


    /**
     * 显示库存信息
     *
     * @param author
     * @return
     * @throws Exception
     */
    @RequestMapping("/managerUI")
    public ModelAndView managerUI(
            @AgtAuthenticationPrincipal Author author) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/goods/manager");
        List<AgentProduct> agentProduct = agentProductService.findByAgentId(author);
        model.addObject("agentProduct", agentProduct);
        return model;
    }

    /**
     * 修改库存预警数量
     *
     * @param author
     * @param agentProductId
     * @param warning
     * @return
     */
    @RequestMapping("/save")
    @ResponseBody
    public ApiResult saveInfo(@AgtAuthenticationPrincipal Author author,
                              @RequestParam(required = true, defaultValue = "0") Integer agentProductId,
                              @RequestParam(required = true, defaultValue = "0") Integer warning) {
        ApiResult result;
        result = agentProductService.updateWarning(author, agentProductId, warning);
        return result;
    }

    /**
     * 显示门店/代理商商品列表
     *
     * @param author
     * @return
     * @throws Exception
     */
    @RequestMapping("/goodsList")
    public ModelAndView goodsList(
            @AgtAuthenticationPrincipal Author author,
            GoodsSearcher goodsSearcher
    ) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/goods/goodsList");
        Page<MallGoods> goodsPage = goodsService.findByAuthorId(author, goodsSearcher);
        List<MallGoods> goodsList = goodsPage.getContent();
        resourceService.setListUri(goodsList, "thumbnailPic", "picUri");
        //获取标准类目列表
        List<MallGoodsType> typeList = goodsTypeService.getAllParentTypeList(goodsSearcher.getStandardTypeId());
        //获取自定义类型列表
        List<MallGoodsType> customerTypeList = goodsTypeService.getCustomerTypeList(author.getCustomer().getCustomerId());
        if (customerTypeList != null && customerTypeList.size() > 0) {
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
     * 显示商品详情
     *
     * @param author
     * @param goodsId
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/showGoodsDetail")
    public ModelAndView showGoodsDetail(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(value = "goodsId", required = true) Integer goodsId) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("purchase/goods/showGoodsDetail");
        MallGoods mallGoods = null;
        mallGoods = goodsService.findByGoodsIdAndAuthor(goodsId, author);
        model.addObject("freightTemplateList", freightTemplateRepository.findByCustomerId(author.getId()));
        model.addObject("mallGoods", mallGoods);
        model.addObject("author", author);
        return model;
    }

    /**
     * 门店修改商品信息
     * 暂时只修改运费模板
     *
     * @param author
     * @param agentGoods
     * @return
     */
    @RequestMapping("/updateAgentGoods")
    @ResponseBody
    @PreAuthorize("hasAnyRole('SHOP') or hasAnyAuthority('PURCHASE')")
    public ApiResult updateAgentGoods(@AgtAuthenticationPrincipal Author author, AgentGoods agentGoods, @RequestParam(value = "freightTemplateId", required = true, defaultValue = "0") Long freightTemplateId) {
        FreightTemplate freightTemplate = freightTemplateRepository.findOne(freightTemplateId);
        return goodsService.updateAgentGoods(agentGoods, author, freightTemplate);
    }
}
