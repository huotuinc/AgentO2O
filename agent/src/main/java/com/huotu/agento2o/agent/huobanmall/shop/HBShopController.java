package com.huotu.agento2o.agent.huobanmall.shop;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.author.ShopService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
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
@RequestMapping("/huobanmall/shop")
public class HBShopController {

    @Autowired
    private ShopService shopService;

    @Autowired
    private AgentLevelService agentLevelService;

    /**
     * 跳转门店新增页面
     * @param shop
     * @param model
     * @return
     */
    @RequestMapping("/addShopPage")
    public String toAddShopPage(Shop shop, Model model) {
        // TODO: 2016/5/13 等级名称
        shop = shopService.findById(shop.getId());
        model.addAttribute("shop",shop);
        return "shop/huobanmall/hb_viewShop";
    }

    /**
     * 门店列表
     * @param model
     * @return
     */
    @RequestMapping("/shopList")
    public String showShopList(@RequestAttribute(value = "customerId") String customerIdStr, Model model , ShopSearchCondition searchCondition, @RequestParam(required = false, defaultValue = "1")int pageIndex, String type) {
        if("list".equals(type)){//门店列表 为非未审核的门店 ?type=list  //1,2,3
        }else{//门店审核 显示所有状态为待审核且Disabled=0的门店信息 ?type=audit

        }
        int customerId = Integer.parseInt(customerIdStr);
        List<AgentLevel> agentLevels =agentLevelService.findByCustomertId(customerId);
        model.addAttribute("agentLevels", agentLevels);
        Page<Shop> shopsList = shopService.findAll(pageIndex, Constant.PAGESIZE, searchCondition);
        int totalPages = shopsList.getTotalPages();
        model.addAttribute("totalPages", totalPages);
        model.addAttribute("totalRecords", shopsList.getTotalElements());
        model.addAttribute("pageSize", shopsList.getSize());
        model.addAttribute("searchCondition", searchCondition);
        model.addAttribute("pageIndex", pageIndex);
        model.addAttribute("shopList",shopsList);
        return "shop/huobanmall/hb_shopList";
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

    @RequestMapping("/changeIsDisabled")
    @ResponseBody
    public ApiResult changeIsDisabled(int id){
        Shop shop = shopService.findById(id);
        shopService.updateIsDisabledById(!shop.isDisabled(),id);
        ApiResult res = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return res;
    }

    /**
     * 导出Excel 订单
     *
     */
    @RequestMapping("exportExcel")
    public void exportExcel(ShopSearchCondition searchCondition, int txtBeginPage, int txtEndPage,
                            HttpSession session, HttpServletResponse response) {
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

    @RequestMapping("/audit")
    @ResponseBody
    public ApiResult toAudit(Shop shop) {
        shopService.updateStatusAndComment(shop.getStatus(),shop.getComment(),shop.getId());
        ApiResult res = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return res;
    }

    @RequestMapping("/resetpassword")
    @ResponseBody
    public ApiResult resetPassword(Shop shop) {
        shopService.updatePasswordById(shop.getPassword(),shop.getId());
        ApiResult res = ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        return res;
    }
}
