package com.huotu.agento2o.agent.controller.product;

import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.config.annotataion.SystemControllerLog;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import com.huotu.agento2o.service.service.purchase.AgentProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;

import java.util.List;

/**
 * 库存管理
 * Created by elvis on 2016/5/16.
 */
@Controller
@RequestMapping("/product")
@PreAuthorize("hasAnyRole('AGENT','SHOP')")
public class ProductController {

    private static String PRODUCT_MANAGER_URL = "product/manager";

    @Autowired
    protected AgentProductService agentProductService;


    @RequestMapping("managerUI")
    public ModelAndView managerUI(
            @AgtAuthenticationPrincipal Author author) throws Exception {
        ModelAndView model = new ModelAndView();
        model.setViewName(PRODUCT_MANAGER_URL);
        List<AgentProduct> agentProduct = agentProductService.findByAgentId(author.getId());
        model.addObject("agentProduct", agentProduct);
        return model;
    }

    @RequestMapping("save")
    @SystemControllerLog(value = "库存数量修改")
    public @ResponseBody
    ApiResult saveInfo(@AgtAuthenticationPrincipal Author author,
                       @RequestParam(required = true,defaultValue = "0") Integer agentProductId,
                       @RequestParam(required = true,defaultValue = "0") Integer warning) {
        ApiResult result;
        if(agentProductId == 0 || warning == 0){
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        result = agentProductService.updateWarning(author,agentProductId,warning);
        return result;
    }
}
