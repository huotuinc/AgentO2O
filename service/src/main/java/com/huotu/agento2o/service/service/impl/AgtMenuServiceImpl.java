/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.impl;

import com.huotu.agento2o.service.AgtMenuService;
import com.huotu.agento2o.service.entity.AgtMenu;
import com.huotu.agento2o.service.repository.AgtMenuRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.PostConstruct;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by helloztt on 2016/5/10.
 */
@Service
public class AgtMenuServiceImpl implements AgtMenuService {

    @Autowired
    private AgtMenuRepository menuRepository;

    @PostConstruct
    public void init(){
        long count = menuRepository.count();
        if(count == 0){
            //一级子菜单列表
            List<AgtMenu> subMenu1 = new ArrayList<>();
            //二级子菜单列表
            List<AgtMenu> subMenu2 = new ArrayList<>();
            //订单管理
            AgtMenu orderMenu = new AgtMenu(0, "", "订单管理", 0, null, "ORDER", 0, "01");
            orderMenu = menuRepository.save(orderMenu);
            subMenu1.clear();
            //订单管理
            AgtMenu ddgl = new AgtMenu(1, "", "订单管理", 0, orderMenu, "ORDER", 0, "0101");
            ddgl = menuRepository.save(ddgl);
            subMenu2.clear();
            //订单列表
            AgtMenu ddlb = new AgtMenu(2, "/order/getOrdersPage", "订单列表", 0, ddgl, "ORDER", 0, "010101");
            ddlb = menuRepository.save(ddlb);
            subMenu2.add(ddlb);
            //售后列表
            AgtMenu shlb = new AgtMenu(2, "/supplier/afterSale/afterSaleList", "售后列表", 1, ddgl, "ORDER", 0, "010102");
            shlb = menuRepository.save(shlb);
            subMenu2.add(shlb);
            ddgl.setChildren(subMenu2);
            ddgl = menuRepository.save(ddgl);
            subMenu1.add(ddgl);
        }
    }

    @Override
    @Transactional
    public AgtMenu save(AgtMenu menu) {
        return menuRepository.save(menu);
    }

    @Override
    public List<AgtMenu> findAll() {
        List<AgtMenu> menus = new ArrayList<>();
        List<AgtMenu> primaryMenus = menuRepository.findAllPrimary();
        for (AgtMenu menu : primaryMenus) {
            menus.add(menu);
            if (menu.getChildren() != null) {
                for (AgtMenu child : menu.getChildren()) {
                    menus.add(child);
                    findChild(menus, child);
                }
            }
        }
        return menus;
    }

    private void findChild(List<AgtMenu> menus, AgtMenu parentMenu) {
        if (parentMenu.getChildren() != null) {
            for (AgtMenu child : parentMenu.getChildren()) {
                menus.add(child);
                findChild(menus, child);
            }
        }
    }

    @Override
    public List<AgtMenu> findPrimary() {
        return menuRepository.findPrimary();
    }

    @Override
    public AgtMenu findById(String id) {
        return menuRepository.findOne(id);
    }

    @Override
    public List<AgtMenu> findByParent(String parentId, int isDisabled) {
        return menuRepository.findByParent_MenuIdAndIsDisabled(parentId, isDisabled);
    }

    @Override
    @Transactional
    public void delete(String id) {
        menuRepository.delete(id);
    }

    @Override
    @Transactional
    public void delete(AgtMenu menu) {
        menuRepository.delete(menu);
    }
}
