/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.controller.menu;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.service.AgtMenuService;
import com.huotu.agento2o.service.entity.AgtMenu;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by helloztt on 2016/5/11.
 */
@Controller
@RequestMapping("/agent/index")
public class MenuController {
    @Autowired
    private AgtMenuService menuService;

    @RequestMapping("/manager/agtMenu/config/12321")
    public String menuConfig(Model model) {
        List<AgtMenu> agtMenus = menuService.findAll();
        model.addAttribute("menus", agtMenus);
        return "menu_config";
    }

    @RequestMapping(value = "/manager/agtMenu/config/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult saveMenu(
            String menuName,
            String linkUrl,
            String menuId,
            @RequestParam(required = false, defaultValue = "0") int sortNum,
            @RequestParam(required = false, defaultValue = "") String author,
            @RequestParam(required = false, defaultValue = "0") int isDisabled,
            String parentId
    ) {
        try {
            AgtMenu agtMenu = menuService.findById(menuId);
            if (agtMenu == null) {
                agtMenu = new AgtMenu();
            }
            agtMenu.setMenuName(menuName);
            agtMenu.setLinkUrl(linkUrl);
            agtMenu.setSortNum(sortNum);
            agtMenu.setAuthor(author);
            agtMenu.setIsDisabled(isDisabled);
            agtMenu.setMenuId(menuId);
            AgtMenu parentMenu;
            if (parentId != null && !"null".equals(parentId)) {
                parentMenu = menuService.findById(parentId);
                agtMenu.setLength(parentMenu.getLength() + 1);
                agtMenu.setParent(parentMenu);
                parentMenu.getChildren().add(agtMenu);
                menuService.save(parentMenu);
            } else {
                menuService.save(agtMenu);
            }

            return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
        } catch (Exception e) {
            return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, e.getMessage());
        }
    }

    @RequestMapping("/manager/agtMenu/config/menu")
    @ResponseBody
    public ApiResult menu(String menuId) {
        AgtMenu agtMenu = menuService.findById(menuId);
        agtMenu.setChildren(null);
        agtMenu.setParent(null);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, agtMenu);
    }

    @RequestMapping(value = "/manager/agtMenu/config/delete", method = RequestMethod.POST)
    @ResponseBody
    @Transactional(value = "hbmTransactionManager")
    public ApiResult deleteMenu(String menuId) {
        AgtMenu menu = menuService.findById(menuId);
        if (menu != null) {
            menu.getChildren().clear();
            if (menu.getParent() == null) {
                menuService.delete(menu);
            } else {
                menu.getParent().getChildren().remove(menu);
            }
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
