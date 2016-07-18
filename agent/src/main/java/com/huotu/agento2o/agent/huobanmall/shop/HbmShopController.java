package com.huotu.agento2o.agent.huobanmall.shop;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentShopService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
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
@RequestMapping("/huobanmall/shop")
public class HbmShopController {

    @Autowired
    private AgentShopService agentShopService;

    @Autowired
    private AgentLevelService agentLevelService;

    @Autowired
    private MallCustomerService mallCustomerService;

    /**
     * 查看门店
     *
     * @param shop
     * @param model
     * @return
     */
    @RequestMapping("/showShopPage")
    public String toAddShopPage(@RequestAttribute(value = "customerId") Integer customerId, ShopAuthor shop, Model model) throws Exception {
        shop = agentShopService.findByIdAndCustomer_Id(shop.getId(), customerId);
        if (shop == null) {
            throw new Exception("没有权限");
        }
        model.addAttribute("shop", shop);
        return "huobanmall/shop/hb_showShop";
    }

    /**
     * 门店列表
     *
     * @param model
     * @return
     */
    @RequestMapping(value = "/shopList")
    public String showShopList(@RequestAttribute(value = "customerId") Integer customerId,
                               Model model,
                               ShopSearchCondition searchCondition,
                               @RequestParam(required = false, defaultValue = "1") int pageIndex) throws Exception {
        List<AgentLevel> agentLevels = agentLevelService.findByCustomertId(customerId);
        CustomerAuthor mallCustomer = mallCustomerService.findByCustomerId(customerId);
        searchCondition.setMallCustomer(mallCustomer);
        Page<ShopAuthor> shopsList = agentShopService.findAll(pageIndex, Constant.PAGESIZE, searchCondition);
        int totalPages = shopsList.getTotalPages();
        model.addAttribute("type", searchCondition.getType());
        model.addAttribute("agentLevels", agentLevels);
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalRecords", shopsList.getTotalElements());
        model.addAttribute("pageSize", shopsList.getSize());
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("shopList", shopsList);
        return "huobanmall/shop/hb_shopList";
    }

    /**
     * 冻结解冻
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/changeIsDisabled", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult changeIsDisabled(@RequestAttribute(value = "customerId") Integer customerId, int id) {
        ShopAuthor shop = agentShopService.findByIdAndCustomer_Id(id, customerId);
        if (shop == null) {
            return ApiResult.resultWith(ResultCodeEnum.CONFIG_SAVE_FAILURE);
        }
        return agentShopService.updateIsDisabledById(id);
    }

    /**
     * 审核
     *
     * @param shop
     * @return
     */
    @RequestMapping(value = "/audit", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult toAudit(@RequestAttribute(value = "customerId") Integer customerId, ShopAuthor shop) {
        if (shop == null || shop.getId() == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        ShopAuthor oldShop = agentShopService.findByIdAndCustomer_Id(shop.getId(), customerId);
        if (oldShop == null) {
            return ApiResult.resultWith(ResultCodeEnum.CONFIG_SAVE_FAILURE);
        }
        return agentShopService.updateStatusAndAuditComment(shop.getStatus(), shop.getAuditComment(), shop.getId());
    }

    /**
     * 重置密码
     *
     * @param shop
     * @return
     */
    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult resetPassword(@RequestAttribute(value = "customerId") Integer customerId, ShopAuthor shop) {
        ShopAuthor oldShop = agentShopService.findByIdAndCustomer_Id(shop.getId(), customerId);
        if (oldShop == null) {
            return ApiResult.resultWith(ResultCodeEnum.CONFIG_SAVE_FAILURE);
        }
        if (shop == null || shop.getId() == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        return agentShopService.updatePasswordById(shop.getPassword(), shop.getId());
    }

    /**
     * 导出Excel
     */
    @RequestMapping("exportExcel")
    public void exportExcel(ShopSearchCondition searchCondition, int txtBeginPage, int txtEndPage,
                            HttpSession session, HttpServletResponse response) {
        int pageSize = Constant.PAGESIZE * (txtEndPage - txtBeginPage + 1);
        Page<ShopAuthor> pageInfo = agentShopService.findAll(txtBeginPage, pageSize, searchCondition);
        List<ShopAuthor> shopList = pageInfo.getContent();
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        String codedFileName = null;
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = "shops-" + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = agentShopService.createWorkBook(shopList);
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
