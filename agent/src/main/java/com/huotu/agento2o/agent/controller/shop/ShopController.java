package com.huotu.agento2o.agent.controller.shop;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.author.ShopService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by admin on 2016/5/11.
 */
@Controller
@RequestMapping("/shop")
//@PreAuthorize("hasAnyRole('Agent')")
public class ShopController {

    @Autowired
    private ShopService shopService ;

    @Autowired
    private AgentService AgentService ;

    @Autowired
    private AuthorService authorService ;

    /**
     * 跳转门店新增页面
     * @param customer
     * @param shop
     * @param model
     * @return
     */
    @RequestMapping("/addShopPage")
    public String toAddShopPage(@AuthenticationPrincipal Agent customer, Shop shop, Model model) {
        // TODO: 2016/5/13 等级名称
        model.addAttribute("agent",customer);
        if(!"".equals(shop.getId()) && shop.getId()!=null){//编辑
            shop = shopService.findById(shop.getId());
            model.addAttribute("shop",shop);
        }
        return "shop/addShop";
    }

    /**
     *新增门店
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
    public String showShopList(@AuthenticationPrincipal Agent customer, Model model , ShopSearchCondition searchCondition, @RequestParam(required = false, defaultValue = "1")int pageIndex) {
        Author author = authorService.findById(customer.getId());
        searchCondition.setAuthor(author);

        Page<Shop> shopsList = shopService.findAll(pageIndex, 20, searchCondition);
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

    @RequestMapping("/delete")
    @ResponseBody
    public ApiResult deleteById(int id){
        shopService.deleteById(id);
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

}
