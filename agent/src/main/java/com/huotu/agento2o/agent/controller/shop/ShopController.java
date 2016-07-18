package com.huotu.agento2o.agent.controller.shop;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.AgentShopService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/shop")
@PreAuthorize("hasAnyRole('AGENT') or hasAnyAuthority('SHOP_MANAGER')")
public class ShopController {

    @Autowired
    private AgentShopService agentShopService;

    /**
     * 代理商登陆 新增/编辑门店页面
     *
     * @param curAgent
     * @param shop
     * @param model
     * @return
     */
    @RequestMapping("/addShopPage")
    public String toAddShopPage(@AgtAuthenticationPrincipal(type = Agent.class) Agent curAgent, ShopAuthor shop, Model model, boolean ifShow) throws Exception {
        model.addAttribute("agent", curAgent);
        if (!"".equals(shop.getId()) && shop.getId() != null) {//编辑
            shop = agentShopService.findByIdAndParentAuthor(shop.getId(), curAgent);
            model.addAttribute("shop", shop);
        }
        return ifShow ? "shop/showShop" : "shop/addShop";
    }

    /**
     * 代理商登陆 保存更新门店
     *
     * @param curAgent
     * @param shop
     * @return
     */
    @RequestMapping(value = "/addShop", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult saveShop(@AgtAuthenticationPrincipal(type = Agent.class) Agent curAgent, ShopAuthor shop, String hotUserName, Integer statusVal) {
        if (StringUtil.isEmptyStr(shop.getUsername())) {
            return new ApiResult("请输入用户名");
        }
        if (shop.getId() == null && StringUtil.isEmptyStr(shop.getPassword())) {
            return new ApiResult("请输入密码");
        }
        if (StringUtil.isEmptyStr(shop.getProvinceCode()) || StringUtil.isEmptyStr(shop.getCityCode()) || StringUtil.isEmptyStr(shop.getAddress_Area())) {
            return new ApiResult("请选择区域");
        }
        if (StringUtil.isEmptyStr(shop.getName())) {
            return new ApiResult("请输入代理商名称");
        }
        if (StringUtil.isEmptyStr(shop.getContact())) {
            return new ApiResult("请输入联系人");
        }
        if (StringUtil.isEmptyStr(shop.getMobile())) {
            return new ApiResult("请输入手机号码");
        }
        if (StringUtil.isEmptyStr(shop.getEmail())) {
            return new ApiResult("请输入E-mail");
        }
        if (StringUtil.isEmptyStr(shop.getAddress())) {
            return new ApiResult("请输入详细地址");
        }
        if (shop.getLan() == null || shop.getLat() == null || shop.getLan() == 0 || shop.getLat() == 0) {
            return new ApiResult("请输入经纬度");
        }
        if(statusVal == null || statusVal == 0){
            shop.setStatus(AgentStatusEnum.NOT_CHECK);
        }else if(statusVal == 1){
            shop.setStatus(AgentStatusEnum.CHECKING);
        }
        return agentShopService.saveOrUpdateShop(shop, hotUserName, curAgent);
    }

    /**
     * 门店列表
     *
     * @param curAgent
     * @param model
     * @return
     */
    @RequestMapping("/shopList")
    public String showShopList(@AgtAuthenticationPrincipal(type = Agent.class) Agent curAgent,
                               Model model,
                               ShopSearchCondition searchCondition,
                               @RequestParam(required = false, defaultValue = "1") int pageIndex) throws Exception {
        searchCondition.setMallCustomer(curAgent.getCustomer());
        searchCondition.setParentAuthor(curAgent);
        Page<ShopAuthor> shopsList = agentShopService.findAll(pageIndex, Constant.PAGESIZE, searchCondition);
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
     * 提交审核状态
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/changeStatus", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult changeStatus(@AgtAuthenticationPrincipal(type = Agent.class) Agent curAgent, int id) {
        return agentShopService.updateStatus(AgentStatusEnum.CHECKING, id, curAgent);
    }

    /**
     * 删除门店
     *
     * @param id
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteById(@AgtAuthenticationPrincipal(type = Agent.class) Agent curAgent, int id) {
        return agentShopService.deleteById(id, curAgent);
    }

    /**
     * 重置密码
     *
     * @param shop
     * @return
     */
    @RequestMapping(value = "/resetpassword", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult resetPassword(@AgtAuthenticationPrincipal(type = Agent.class) Agent curAgent, ShopAuthor shop) {
        if (shop == null || shop.getId() == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        ShopAuthor oldShop = agentShopService.findByIdAndParentAuthor(shop.getId(), curAgent);
        if (oldShop == null) {
            return ApiResult.resultWith(ResultCodeEnum.CONFIG_SAVE_FAILURE);
        }
        return agentShopService.updatePasswordById(shop.getPassword(), shop.getId());
    }

    /**
     * 导出Excel
     */
    @RequestMapping("/exportExcel")
    public void exportExcel(@AgtAuthenticationPrincipal(type = Agent.class) Agent customer,
                            ShopSearchCondition searchCondition,
                            int txtBeginPage, int txtEndPage,
                            HttpSession session,
                            HttpServletResponse response) {
        searchCondition.setParentAuthor(customer);
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
            String excelName = "shop-" + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
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

    /**
     * 获取小伙伴集合
     *
     * @param agent
     * @param hotUserName
     * @return
     */
    @RequestMapping(value = "/getUserNames", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult getUserNames(@AgtAuthenticationPrincipal(type = Agent.class) Agent agent, String hotUserName) {
        int customerId = agent.getCustomer().getCustomerId();
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, agentShopService.getHotUserNames(customerId, hotUserName));
    }
}
