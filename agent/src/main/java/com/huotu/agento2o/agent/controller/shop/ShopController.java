package com.huotu.agento2o.agent.controller.shop;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.AuthorService;
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
@PreAuthorize("hasAnyRole('AGENT','SHOP')")
public class ShopController {

    @Autowired
    private ShopService shopService ;

    /**
     * 门店新增页面
     * @param customer
     * @param shop
     * @param model
     * @return
     */
    @RequestMapping("/addShopPage")
    public String toAddShopPage(@AuthenticationPrincipal Agent customer, Shop shop, Model model) {
        model.addAttribute("agent",customer);
        if(!"".equals(shop.getId()) && shop.getId()!=null){//编辑
            shop = shopService.findById(shop.getId());
            model.addAttribute("shop",shop);
        }
        return "shop/addShop";
    }

    /**
     *保存门店
     * @param customer
     * @param shop
     * @return
     */
    @RequestMapping(value = "/addShop")
    @ResponseBody
    public ApiResult addShop(@AuthenticationPrincipal Agent customer, Shop shop) {
        shop.setParentAuthor(customer);
        shop = shopService.addShop(shop);
        ApiResult apiResult ;
        if(shop!=null){
            apiResult = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        }else {
            apiResult = ApiResult.resultWith(ResultCodeEnum.LOGINNAME_NOT_AVAILABLE);
        }
        return apiResult;
    }

    /**
     * 门店列表
     * @param customer
     * @param model
     * @return
     */
    @RequestMapping("/shopList")
    public String showShopList(@AuthenticationPrincipal Agent customer,
                               Model model ,
                               ShopSearchCondition searchCondition,
                               @RequestParam(required = false, defaultValue = "1")int pageIndex) {
        searchCondition.setParentAuthor(customer);
        Page<Shop> shopsList = shopService.findAll(pageIndex, Constant.PAGESIZE, searchCondition);
        int totalPages = shopsList.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalRecords", shopsList.getTotalElements());
        model.addAttribute("pageSize", shopsList.getSize());
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("shopList",shopsList);
        return "shop/shopList";
    }

    /**
     * 更改门店审核状态
     * @param id
     * @param type
     * @return
     */
    @RequestMapping("/changeStatus")
    @ResponseBody
    public ApiResult changeStatus(int id,String type){
        AgentStatusEnum statusEnum = AgentStatusEnum.NOT_CHECK;
        if("ToFactory".equals(type)){
            statusEnum = AgentStatusEnum.CHECKING ;
        }
        shopService.updateStatus(statusEnum,id);
        ApiResult res = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return res;
    }

    /**
     * 删除门店
     * @param id
     * @return
     */
    @RequestMapping("/delete")
    @ResponseBody
    public ApiResult deleteById(int id){
        shopService.deleteById(id);
        ApiResult res = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return res;
    }

    /**
     * 冻结解冻
     * @param id
     * @return
     */
    @RequestMapping("/changeIsDisabled")
    @ResponseBody
    public ApiResult changeIsDisabled(int id){
        Shop shop = shopService.findById(id);
        shopService.updateIsDisabledById(!shop.isDisabled(),id);
        ApiResult res = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return res;
    }

    /**
     * 导出Excel
     *
     */
    @RequestMapping("exportExcel")
    public void exportExcel(@AuthenticationPrincipal Agent customer,
                            ShopSearchCondition searchCondition,
                            int txtBeginPage, int txtEndPage,
                            HttpSession session,
                            HttpServletResponse response) {
        searchCondition.setParentAuthor(customer);
        int pageSize =  Constant.PAGESIZE * (txtEndPage - txtBeginPage + 1);
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
