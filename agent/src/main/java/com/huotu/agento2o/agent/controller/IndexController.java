package com.huotu.agento2o.agent.controller;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.entity.AgtMenu;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.model.statistics.IndexStatistics;
import com.huotu.agento2o.service.service.AgtMenuService;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.statistics.IndexStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Controller
public class IndexController {

    @Autowired
    private IndexStatisticsService indexStatisticsService;
    @Autowired
    private AgtMenuService menuService;
    @Autowired
    private AuthorService authorService;
    @Autowired
    private MallCustomerService mallCustomerService;

    @RequestMapping(value = {"", "/", "/login"})
    public String index() {
        return "login";
    }

    @RequestMapping(value = "loginFailed")
    public String loginFailed() {
        return "redirect:login";
    }

    @RequestMapping(value = "index")
    public String loginSuccess(@AgtAuthenticationPrincipal Author author,
                               String activeMenuId,
                               Model model) {
        model.addAttribute("authorId", author.getId());
        List<AgtMenu> menus = menuService.findPrimary();
        model.addAttribute("menus", menus);
        model.addAttribute("activeMenuId", activeMenuId);
        return "home";
    }

    @RequestMapping(value = "home")
    public String home(@AgtAuthenticationPrincipal Author author, Model model) {
        IndexStatistics indexStatistics = indexStatisticsService.orderStatistics(author);
        model.addAttribute("indexStatistics", indexStatistics);
        return "index";
    }

    @RequestMapping("/leftMenu")
    public String leftMenu(
            @AgtAuthenticationPrincipal Author author,
            String parentId,
            String activeMenuId,
            Model model
    ) {
        if (!StringUtils.isEmpty(parentId)) {
            List<AgtMenu> menus = menuService.findByParent(parentId, 0);
            /*List<AgtMenu> isAuthorMenus = new ArrayList<>();
            for (AgtMenu menu : menus) {
                if (menu.isAuthor()) {
                    isAuthorMenus.add(menu);
                }
            }*/
            List<AgtMenu> isAuthorMenus = getIsAuthorMenus(menus);
            model.addAttribute("menus", isAuthorMenus);
            AgtMenu activeMenu;
            if (StringUtils.isEmpty(activeMenuId)) {
                activeMenu = isAuthorMenus.get(0).getChildren().get(0);
            } else {
                activeMenu = menuService.findById(activeMenuId);
            }
            model.addAttribute("activeMenu", activeMenu);
        }
        model.addAttribute("parentId", parentId);
        return "left_menu";
    }

    /**
     * 获取有权限的菜单
     *
     * @param menus
     * @return
     */
    private List<AgtMenu> getIsAuthorMenus(List<AgtMenu> menus) {
        if (menus == null || menus.size() < 1) {
            return null;
        }
        List<AgtMenu> isAuthorMenus = new ArrayList<>();
        for (AgtMenu menu : menus) {
            if (menu.isAuthor()) {
                menu.setChildren(getIsAuthorMenus(menu.getChildren()));
                isAuthorMenus.add(menu);
            }
        }
        return isAuthorMenus;
    }

    /**
     * 放开权限，所有角色均可修改自己的登录密码
     *
     * @return
     */
    @RequestMapping(value = "showModifyPwd")
    public String showModifyPwd() {
        return "modifyPwd";
    }

    /**
     * @param oldPwd
     * @param password
     * @return
     * @throws Exception
     */
    @RequestMapping(value = "/modifyPwd", produces = MediaType.APPLICATION_JSON_VALUE, method = RequestMethod.POST)
    @ResponseBody
    public ApiResult modifyPwd(@AgtAuthenticationPrincipal UserDetails user, String oldPwd, String password) throws Exception {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(oldPwd)) {
            return ApiResult.resultWith(ResultCodeEnum.PASSWORD_NULL);
        }
        //校验旧密码
        if (user instanceof Author) {
            if (!authorService.checkPwd((Author) user, oldPwd)) {
                return new ApiResult("原始密码错误!");
            }
        }
        //修改新密码
        if (user instanceof Author) {
            //修改代理商/门店密码
            int result = mallCustomerService.resetPassword(((Author) user).getId(), password);
            return result > 0 ? ApiResult.resultWith(ResultCodeEnum.SUCCESS) : ApiResult.resultWith(ResultCodeEnum.CONFIG_SAVE_FAILURE);
        }
        return ApiResult.resultWith(ResultCodeEnum.CONFIG_SAVE_FAILURE);
    }
}
