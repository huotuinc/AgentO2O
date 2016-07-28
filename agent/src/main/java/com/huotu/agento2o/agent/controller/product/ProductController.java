package com.huotu.agento2o.agent.controller.product;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallGoodsType;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.goods.MallGoodsTypeService;
import com.huotu.agento2o.service.service.goods.MallProductService;
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
@RequestMapping("/product")
@PreAuthorize("hasAnyRole('AGENT','SHOP')")
public class ProductController {

    @Autowired
    private AgentProductService agentProductService;
    @Autowired
    private MallGoodsService goodsService;
    @Autowired
    private MallProductService productService;
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
    @RequestMapping("managerUI")
    public ModelAndView managerUI(
            @AgtAuthenticationPrincipal Author author) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("product/manager");
        List<AgentProduct> agentProduct = agentProductService.findByAgentId(author);
        model.addObject("agentProduct", agentProduct);
        return model;
    }

    /**
     * 修改库存预警数量
     * @param author
     * @param agentProductId
     * @param warning
     * @return
     */
    @RequestMapping("save")
    public @ResponseBody
    ApiResult saveInfo(@AgtAuthenticationPrincipal Author author,
                       @RequestParam(required = true,defaultValue = "0") Integer agentProductId,
                       @RequestParam(required = true,defaultValue = "0") Integer warning) {
        ApiResult result;
        result = agentProductService.updateWarning(author,agentProductId,warning);
        return result;
    }

    /**
     * 显示门店/代理商商品列表
     *
     * @param author
     * @return
     * @throws Exception
     */
    @RequestMapping("goodsList")
    public ModelAndView gooodsList(
            @AgtAuthenticationPrincipal Author author,
            GoodsSearcher goodsSearcher
    ) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName("product/goodsList");
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
}
