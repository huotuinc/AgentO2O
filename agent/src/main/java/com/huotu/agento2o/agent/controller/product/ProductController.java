package com.huotu.agento2o.agent.controller.product;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallGoods;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.repository.purchase.AgentProductRepository;
import com.huotu.agento2o.service.searchable.GoodsSearcher;
import com.huotu.agento2o.service.service.goods.MallGoodsService;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;
import java.util.Map;

/**
 * 库存管理
 * Created by elvis on 2016/5/16.
 */
@Controller
@RequestMapping("/product")
@PreAuthorize("hasAnyRole('AGENT','ORDER')")
public class ProductController {

    private static String PRODUCT_MANAGER_URL = "product/manager";

    @Autowired
    protected AgentProductService agentProductService;


    @RequestMapping("managerUI")
    public ModelAndView managerUI(
            @AuthenticationPrincipal Author author,
            @RequestParam(required = true, defaultValue = "1") int pageIndex) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName(PRODUCT_MANAGER_URL);
        Page<AgentProduct> agentProduct = agentProductService.findByAgentId(pageIndex, Constant.PAGESIZE, author.getId());
        model.addObject("agentProduct", agentProduct);
        return model;
    }

    @SuppressWarnings("unchecked")
    @RequestMapping("save")
    public
    @ResponseBody
    ApiResult saveInfo(@RequestBody JSONObject products) {

        boolean success = false;
        List<String> l = (List) products.get("info");
        for (String str : l) {
            String _item = str.substring(1, str.length() - 1);
            String[] item = _item.split(",");
            if (item.length == 3) {
                //更新
                success = agentProductService.updateWaring(Integer.parseInt(item[0].trim()),
                        Integer.parseInt(item[1].trim()),
                        Integer.parseInt(item[2].trim()));
            }
        }
        return success ? ApiResult.resultWith(ResultCodeEnum.SUCCESS):ApiResult.resultWith(ResultCodeEnum.CONFIG_SAVE_FAILURE);
    }


}
