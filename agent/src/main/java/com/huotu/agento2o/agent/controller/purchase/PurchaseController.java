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

import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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


    @RequestMapping(value = "/showGoodsList")
    public ModelAndView showGoodsList(
            @AuthenticationPrincipal Author author,
            GoodsSearcher goodsSearcher) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("/purchase/goods_list");
        Page<MallGoods> goodsList;
        //如果上级直系代理商为空，则读取平台方代理商品(Agent_Id=0)；否则读取 上级直系代理商商品
        if (author.getParentAuthor() == null) {
            goodsList = goodsService.findByCustomerIdAndAgentId(author.getCustomer().getCustomerId(), 0, goodsSearcher);
        } else {
            goodsList = goodsService.findByAgentId(author.getParentAuthor().getId(), goodsSearcher);
        }
        model.addObject("goodsList", goodsList);
        return model;
    }


}
