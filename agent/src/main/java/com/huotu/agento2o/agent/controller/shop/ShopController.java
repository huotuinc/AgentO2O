package com.huotu.agento2o.agent.controller.shop;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.purchase.ShoppingCart;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.ShopService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.List;

/**
 * Created by admin on 2016/5/11.
 */
@Controller
@RequestMapping("/shop")
@PreAuthorize("hasAnyRole('SHOP','BASE_DATA','BASE_SHOP')")
public class ShopController {

    @Autowired
    private ShopService shopService;

    /**
     * 代理商新增门店页面
     *
     * @param curAgent
     * @param shop
     * @param model
     * @return
     */
    @RequestMapping("/addShopPage")
    public String toAddShopPage(@AuthenticationPrincipal Agent curAgent, Shop shop, Model model) {
        model.addAttribute("agent", curAgent);
        if (!"".equals(shop.getId()) && shop.getId() != null) {//编辑
            shop = shopService.findById(shop.getId());
            model.addAttribute("shop", shop);
        }
        return "shop/addShop";
    }

    /**
     * 代理商保存更新门店
     *
     * @param curAgent
     * @param shop
     * @return
     */
    @RequestMapping(value = "/addShop")
    @ResponseBody
    public ApiResult saveShop(@AuthenticationPrincipal Agent curAgent, Shop shop, String hotUserName) {
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (curAgent == null) {
            return new ApiResult("没有权限");
        }
        //新增后上级代理商和平台就不可修改
        shop.setParentAuthor(curAgent);
        shop.setCustomer(curAgent.getCustomer());

        ApiResult apiResult = shopService.addShop(shop, hotUserName);
        return apiResult;
    }

    /**
     * 门店基本资料设置頁面
     *
     * @return
     */
    @RequestMapping("/baseConfig")
    public String baseConfig(@AuthenticationPrincipal Shop curShop, Model model) {
        Shop shop = shopService.findById(curShop.getId());
        model.addAttribute("shop", shop);
        model.addAttribute("agent", shop.getParentAuthor());
        return "shop/BaseConfigShop";
    }

    /**
     * 门店基本资料更新
     *
     * @param shop
     * @return
     */
    @RequestMapping(value = "/updateShop")
    @ResponseBody
    public ApiResult updateShop(@AuthenticationPrincipal Shop curShop, Shop shop, String hotUserName) {
        if (shop == null || shop.getId() == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (curShop == null || !curShop.getId().equals(shop.getId())) {
            return new ApiResult("没有权限");
        }
        ApiResult apiResult = shopService.addShop(shop, hotUserName);
        return apiResult;
    }


    /**
     * 门店列表
     *
     * @param customer
     * @param model
     * @return
     */
    @RequestMapping("/shopList")
    public String showShopList(@AuthenticationPrincipal Agent customer,
                               Model model,
                               ShopSearchCondition searchCondition,
                               @RequestParam(required = false, defaultValue = "1") int pageIndex) {
        searchCondition.setParentAuthor(customer);
        Page<Shop> shopsList = shopService.findAll(pageIndex, Constant.PAGESIZE, searchCondition);
        int totalPages = shopsList.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalRecords", shopsList.getTotalElements());
        model.addAttribute("pageSize", shopsList.getSize());
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("shopList", shopsList);
        return "shop/shopList";
    }

    /**
     * 更改门店审核状态
     *
     * @param id
     * @param type
     * @return
     */
    @RequestMapping("/changeStatus")
    @ResponseBody
    public ApiResult changeStatus(@AuthenticationPrincipal Agent curAgent, int id, String type) {
        Shop shop = shopService.findById(id);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (shop.isDeleted()) {
            return new ApiResult("该门店已被删除");
        }
        if (shop.isDisabled()) {
            return new ApiResult("该门店已被冻结");
        }
        if (curAgent == null || !curAgent.getId().equals(shop.getParentAuthor().getId())) {
            return new ApiResult("没有权限");
        }
        AgentStatusEnum statusEnum = AgentStatusEnum.NOT_CHECK;
        if ("ToFactory".equals(type)) {
            statusEnum = AgentStatusEnum.CHECKING;
        }
        shopService.updateStatus(statusEnum, id);
        ApiResult res = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return res;
    }

    /**
     * 删除门店
     *
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ApiResult deleteById(@AuthenticationPrincipal Agent curAgent, int id) {
        Shop shop = shopService.findById(id);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (curAgent == null || !curAgent.getId().equals(shop.getParentAuthor().getId())) {
            return new ApiResult("没有权限");
        }
        shopService.deleteById(id);
        ApiResult res = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return res;
    }

    /**
     * 导出Excel
     */
    @RequestMapping("/exportExcel")
    public void exportExcel(@AuthenticationPrincipal Agent customer,
                            ShopSearchCondition searchCondition,
                            int txtBeginPage, int txtEndPage,
                            HttpSession session,
                            HttpServletResponse response) {
        searchCondition.setParentAuthor(customer);
        int pageSize = Constant.PAGESIZE * (txtEndPage - txtBeginPage + 1);
        Page<Shop> pageInfo = shopService.findAll(txtBeginPage, pageSize, searchCondition);
        List<Shop> shopList = pageInfo.getContent();
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        String codedFileName = null;
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = "shop-" + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = shopService.createWorkBook(shopList);
            fOut = response.getOutputStream();
            workbook.write(fOut);
        } catch (Exception ignored) {
        } finally {
            try {
                assert fOut != null;
                fOut.flush();
                fOut.close();
            } catch (IOException ignored) {
            }
            session.setAttribute("state", "open");
        }
    }
}
