package com.huotu.agento2o.agent.controller;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.AgtMenuService;
import com.huotu.agento2o.service.entity.AgtMenu;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.model.statistics.IndexStatistics;
import com.huotu.agento2o.service.service.author.AuthorService;
import com.huotu.agento2o.service.service.statistics.IndexStatisticsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import javax.servlet.http.HttpServletRequest;
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
    private PasswordEncoder passwordEncoder;
    @Autowired
    private AuthorService authorService;

    @RequestMapping(value = {"", "/", "/login"})
    public String index() {
        return "login";
    }

    @RequestMapping(value = "loginFailed")
    public String loginFailed(HttpServletRequest request, RedirectAttributes attributes) {
        attributes.addFlashAttribute("errMsg", request.getSession().getAttribute("SPRING_SECURITY_LAST_EXCEPTION"));
        attributes.addFlashAttribute("loginFailed", true);
        return "redirect:login";
    }

    @RequestMapping(value = "index")
    public String loginSuccess(@AuthenticationPrincipal Author author,
                               String activeMenuId,
                               Model model) {
        model.addAttribute("authorId", author.getId());
        List<AgtMenu> menus = menuService.findPrimary();
        model.addAttribute("menus", menus);
        model.addAttribute("activeMenuId", activeMenuId);
        return "home";
    }

    @RequestMapping(value = "home")
    public String home(@AuthenticationPrincipal Author author, Model model) {
        IndexStatistics indexStatistics = indexStatisticsService.orderStatistics(author.getId());
        model.addAttribute("indexStatistics", indexStatistics);
        return "index";
    }

    @RequestMapping("/leftMenu")
    public String leftMenu(
            @AuthenticationPrincipal Author author,
            String parentId,
            String activeMenuId,
            Model model
    ) {
        if (!StringUtils.isEmpty(parentId)) {
            List<AgtMenu> menus = menuService.findByParent(parentId, 0);

            model.addAttribute("menus", menus);
            AgtMenu activeMenu;
            if (StringUtils.isEmpty(activeMenuId)) {
                activeMenu = menus.get(0).getChildren().get(0);
            } else {
                activeMenu = menuService.findById(activeMenuId);
            }
            model.addAttribute("activeMenu", activeMenu);
        }
        model.addAttribute("parentId", parentId);
        return "left_menu";
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
    public ApiResult modifyPwd(@AuthenticationPrincipal UserDetails user, String oldPwd, String password) throws Exception {
        if (StringUtils.isEmpty(password) || StringUtils.isEmpty(oldPwd)) {
            return ApiResult.resultWith(ResultCodeEnum.PASSWORD_NULL);
        }
        //校验旧密码
        if (user instanceof Author) {
            if (!authorService.checkPwd(((Author) user).getId(), oldPwd)) {
                return new ApiResult("原始密码错误!");
            }
        }
        //修改新密码
        if (user instanceof Author) {
            //修改代理商/门店密码
            authorService.updatePwd(((Author) user).getId(), password);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
