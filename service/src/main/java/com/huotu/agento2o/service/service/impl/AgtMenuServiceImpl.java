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

import com.huotu.agento2o.service.service.AgtMenuService;
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
        //01
        initOrderMenu();
        //02
        initPurchaseMenu();
        //03
        initShopMenu();
        //04
        initConfigMenu();
    }

    /**
     * 初始化订单模块，订单模块以 01 开头
     */
    private void initOrderMenu(){
        //判断是否存在订单模块
        if(menuRepository.findOne("01") == null){
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

            //单据管理
            AgtMenu djgl = new AgtMenu(1, "", "单据管理", 1, orderMenu, "ORDER", 0, "0102");
            djgl = menuRepository.save(djgl);
            subMenu2.clear();
            AgtMenu fhd = new AgtMenu(2, "/order/deliveries", "发货单", 0, djgl, "ORDER", 0, "010201");
            fhd = menuRepository.save(fhd);
            subMenu2.add(fhd);
            AgtMenu thd = new AgtMenu(2, "/order/deliveries?type=return", "退货单", 1, djgl, "ORDER", 0, "010202");
            thd = menuRepository.save(thd);
            subMenu2.add(thd);
            djgl.setChildren(subMenu2);
            djgl = menuRepository.save(djgl);
            subMenu1.add(djgl);

            orderMenu.setChildren(subMenu1);
            menuRepository.save(orderMenu);
        }
    }

    /**
     * 初始化采购管理模块，采购管理模块以 02 开头
     */
    private void initPurchaseMenu(){
        if(menuRepository.findOne("02") == null){
            //一级子菜单列表
            List<AgtMenu> subMenu1 = new ArrayList<>();
            //二级子菜单列表
            List<AgtMenu> subMenu2 = new ArrayList<>();
            //商品采购
            AgtMenu purchaseMenu = new AgtMenu(0,"","商品采购",1,null,"PURCHASE",0,"02");
            purchaseMenu = menuRepository.save(purchaseMenu);
            subMenu1.clear();
            //一级菜单 商品采购
            AgtMenu cggl = new AgtMenu(1,"","商品采购",0,purchaseMenu,"PURCHASE",0,"0201");
            cggl = menuRepository.save(cggl);
            subMenu2.clear();
            //二级菜单 采购管理
            AgtMenu spcg = new AgtMenu(2,"/purchase","商品采购",0,cggl,"PURCHASE",0,"020101");
            spcg = menuRepository.save(spcg);
            subMenu2.add(spcg);
            AgtMenu gwc = new AgtMenu(2,"/purchase","购物车",1,cggl,"PURCHASE",0,"020102");
            gwc = menuRepository.save(gwc);
            subMenu2.add(gwc);
            cggl.setChildren(subMenu2);
            cggl = menuRepository.save(cggl);
            subMenu1.add(cggl);

            //一级菜单 我的采购管理
            AgtMenu wdcgd = new AgtMenu(1,"","我的采购管理",1,purchaseMenu,"PURCHASE",0,"0202");
            wdcgd = menuRepository.save(wdcgd);
            subMenu2.clear();
            //二级菜单 我的采购单
            AgtMenu cgd = new AgtMenu(2,"/purchase","我的采购单",0,wdcgd,"PURCHASE",0,"020201");
            cgd = menuRepository.save(cgd);
            subMenu2.add(cgd);
            AgtMenu thsq = new AgtMenu(2,"/purchase","退货申请",1,wdcgd,"PURCHASE",0,"020202");
            thsq = menuRepository.save(thsq);
            subMenu2.add(thsq);
            AgtMenu thd = new AgtMenu(2,"/purchase","我的退货单",2,wdcgd,"PURCHASE",0,"020203");
            subMenu2.add(thd);
            wdcgd.setChildren(subMenu2);
            wdcgd = menuRepository.save(wdcgd);
            subMenu1.add(wdcgd);

            //一级订单 下级采购管理
            AgtMenu xjcggl = new AgtMenu(1,"","下级采购管理",2,purchaseMenu,"PURCHASE",0,"0203");
            xjcggl = menuRepository.save(xjcggl);
            subMenu2.clear();
            //二级菜单 下级采购单
            AgtMenu xjcgd = new AgtMenu(2,"/purchase","下级采购单",0,xjcggl,"PURCHASE",0,"020301");
            xjcgd = menuRepository.save(xjcgd);
            subMenu2.add(xjcgd);
            //二级菜单 下级退货单
            AgtMenu xjthd = new AgtMenu(2,"/purchase","下级退货单",1,xjcggl,"PURCHASE",0,"020302");
            xjthd = menuRepository.save(xjthd);
            subMenu2.add(xjthd);
            xjcggl.setChildren(subMenu2);
            xjcggl = menuRepository.save(xjcggl);
            subMenu1.add(xjcggl);

            purchaseMenu.setChildren(subMenu1);
            purchaseMenu = menuRepository.save(purchaseMenu);
        }
    }

    /**
     * 初始化门店管理模块， 门店管理模块以 03 开头
     */
    private void initShopMenu(){
        // TODO: 2016/5/12
        //判断是否存在订单模块
        if(menuRepository.findOne("03") == null) {
            //一级子菜单列表
            List<AgtMenu> subMenu1 = new ArrayList<>();
            //二级子菜单列表
            List<AgtMenu> subMenu2 = new ArrayList<>();

            //门店管理
            AgtMenu shopMenu = new AgtMenu(0, "", "门店管理", 0, null, "SHOP", 0, "03");
            shopMenu = menuRepository.save(shopMenu);
            subMenu1.clear();

            //门店管理
            AgtMenu mdgl = new AgtMenu(1, "", "门店管理", 0, shopMenu, "SHOP", 0, "0301");
            mdgl = menuRepository.save(mdgl);
            subMenu2.clear();

            //新增门店
            AgtMenu xzmd = new AgtMenu(2, "/shop/addShopPage", "新增门店", 0, mdgl, "SHOP", 0, "030101");
            xzmd = menuRepository.save(xzmd);
            subMenu2.add(xzmd);

            //门店列表
            AgtMenu shlb = new AgtMenu(2, "/shop/shopList", "门店列表", 1, mdgl, "SHOP", 0, "030102");
            shlb = menuRepository.save(shlb);
            subMenu2.add(shlb);

            mdgl.setChildren(subMenu2);
            mdgl = menuRepository.save(mdgl);
            subMenu1.add(mdgl);

            shopMenu.setChildren(subMenu1);
            menuRepository.save(shopMenu);
        }
    }

    /**
     * 初始化基本设置模块，基本设置模块以 04 开头
     */
    private void initConfigMenu(){
        // TODO: 2016/5/12
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
