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
    @Transactional
    public void init() {
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
    private void initOrderMenu() {
        //判断是否存在订单模块
        if (menuRepository.findOne("01") == null) {
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
    private void initPurchaseMenu() {
        if (menuRepository.findOne("02") == null) {
            //一级子菜单列表
            List<AgtMenu> subMenu1 = new ArrayList<>();
            //二级子菜单列表
            List<AgtMenu> subMenu2 = new ArrayList<>();
            //商品采购
            AgtMenu purchaseMenu = new AgtMenu(0, "", "商品采购", 1, null, "PURCHASE", 0, "02");
            purchaseMenu = menuRepository.save(purchaseMenu);
            subMenu1.clear();
            //一级菜单 商品采购
            AgtMenu cggl = new AgtMenu(1, "", "商品采购", 0, purchaseMenu, "PURCHASE", 0, "0201");
            cggl = menuRepository.save(cggl);
            subMenu2.clear();
            //二级菜单 采购管理
            AgtMenu spcg = new AgtMenu(2, "/purchase/showGoodsList", "商品采购", 0, cggl, "PURCHASE", 0, "020101");
            spcg = menuRepository.save(spcg);
            subMenu2.add(spcg);
            AgtMenu gwc = new AgtMenu(2, "/shoppingCart/showShoppingCart", "购物车", 1, cggl, "PURCHASE", 0, "020102");
            gwc = menuRepository.save(gwc);
            subMenu2.add(gwc);
            cggl.setChildren(subMenu2);
            cggl = menuRepository.save(cggl);
            subMenu1.add(cggl);

            //一级菜单 我的采购管理
            AgtMenu wdcgd = new AgtMenu(1, "", "我的采购管理", 1, purchaseMenu, "PURCHASE", 0, "0202");
            wdcgd = menuRepository.save(wdcgd);
            subMenu2.clear();
            //二级菜单 我的采购单
            AgtMenu cgd = new AgtMenu(2, "/purchaseOrder/showPurchaseOrderList", "我的采购单", 0, wdcgd, "PURCHASE", 0, "020201");
            cgd = menuRepository.save(cgd);
            subMenu2.add(cgd);
            AgtMenu thsq = new AgtMenu(2, "/purchase/showPurchasedProductList", "采购退货申请", 1, wdcgd, "PURCHASE", 0, "020202");
            thsq = menuRepository.save(thsq);
            subMenu2.add(thsq);
            AgtMenu thd = new AgtMenu(2, "/purchase/showReturnedOrderList", "我的采购退货单", 2, wdcgd, "PURCHASE", 0, "020203");
            subMenu2.add(thd);
            wdcgd.setChildren(subMenu2);
            wdcgd = menuRepository.save(wdcgd);
            subMenu1.add(wdcgd);

            //一级菜单 采购单据管理
            AgtMenu cgdjgl = new AgtMenu(1,"","我的采购单据管理",2,purchaseMenu,"PURCHASE",0,"0203");
            cgdjgl = menuRepository.save(cgdjgl);
            subMenu2.clear();
            //二级菜单，采购单物流信息列表
            AgtMenu cgdwl = new AgtMenu(2,"/purchase/showPurchaseDeliveryList","采购单物流表",0,cgdjgl,"PURCHASE",0,"020301");
            cgdwl = menuRepository.save(cgdwl);
            subMenu2.add(cgdwl);
            //二级菜单 退货单物流信息列表
            AgtMenu thdwl = new AgtMenu(2,"/purchase/showReturnDeliveryList","退货单物流表",0,cgdjgl,"PURCHASE",0,"020302");
            thdwl = menuRepository.save(thdwl);
            subMenu2.add(thdwl);
            cgdjgl.setChildren(subMenu2);
            cgdjgl = menuRepository.save(cgdjgl);
            subMenu1.add(cgdjgl);

            //一级订单 下级采购管理
            AgtMenu xjcggl = new AgtMenu(1, "", "下级采购管理", 3, purchaseMenu, "AGENT_PURCHASE", 0, "0204");
            xjcggl = menuRepository.save(xjcggl);
            subMenu2.clear();
            //二级菜单 下级采购单
            AgtMenu xjcgd = new AgtMenu(2, "/purchaseOrder/showAgentPurchaseOrderList", "下级采购单", 0, xjcggl, "AGENT_PURCHASE", 0, "020401");
            xjcgd = menuRepository.save(xjcgd);
            subMenu2.add(xjcgd);
            //二级菜单 下级退货单
            AgtMenu xjthd = new AgtMenu(2, "/purchase/showAgentReturnedOrderList", "下级退货单", 1, xjcggl, "AGENT_PURCHASE", 0, "020402");
            xjthd = menuRepository.save(xjthd);
            subMenu2.add(xjthd);
            xjcggl.setChildren(subMenu2);
            xjcggl = menuRepository.save(xjcggl);
            subMenu1.add(xjcggl);


            //一级菜单 库存管理
            AgtMenu kcgl = new AgtMenu(1, "", "库存管理", 4, purchaseMenu, "PURCHASE", 0, "0205");
            kcgl = menuRepository.save(kcgl);
            subMenu2.clear();
            //二级采购单 库存预警
            AgtMenu kcyj = new AgtMenu(2, "/product/managerUI", "库存预警", 0, kcgl, "PURCHASE", 0, "020501");
            kcyj = menuRepository.save(kcyj);
            subMenu2.add(kcyj);
            kcgl.setChildren(subMenu2);
            kcgl = menuRepository.save(kcgl);
            subMenu1.add(kcgl);

            purchaseMenu.setChildren(subMenu1);
            purchaseMenu = menuRepository.save(purchaseMenu);
        }
    }

    /**
     * 初始化门店管理模块， 门店管理模块以 03 开头
     */
    private void initShopMenu() {
        //判断是否存在订单模块
        if (menuRepository.findOne("03") == null) {
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
    private void initConfigMenu() {
        //判断是否存在基本设置
        if (menuRepository.findOne("04") == null) {
            //一级子菜单列表
            List<AgtMenu> subMenu1 = new ArrayList<>();
            //二级子菜单列表
            List<AgtMenu> subMenu2 = new ArrayList<>();

            //基本设置
            AgtMenu baseMenu = new AgtMenu(0, "", "基本设置", 0, null, "BASE_DATA", 0, "04");
            baseMenu = menuRepository.save(baseMenu);
            subMenu1.clear();

            //资料设置
            AgtMenu jbse = new AgtMenu(1, "", "资料设置", 0, baseMenu, "BASE_SHOP", 0, "0401");
            jbse = menuRepository.save(jbse);
            subMenu2.clear();

            //基本信息配置
            AgtMenu mdjbsz = new AgtMenu(2, "/shop/baseConfig", "基本信息配置", 0, jbse, "BASE_SHOP", 0, "040101");
            mdjbsz = menuRepository.save(mdjbsz);
            subMenu2.add(mdjbsz);

            //票据管理
            AgtMenu pjgl = new AgtMenu(1, "", "票据管理", 1, baseMenu, "BASE_DATA", 0, "0402");
            pjgl = menuRepository.save(pjgl);
            subMenu2.clear();

            //票据设置
            AgtMenu pjsz = new AgtMenu(2, "/config/invoiceConfig", "票据设置", 0, pjgl, "BASE_DATA", 0, "040201");
            pjsz = menuRepository.save(pjsz);
            subMenu2.add(pjsz);

            jbse.setChildren(subMenu2);
            jbse = menuRepository.save(jbse);
            subMenu1.add(jbse);

            //一级菜单 代理商基本设置
            AgtMenu zlsz = new AgtMenu(1, "", "资料设置", 2, baseMenu, "BASE_AGENT", 0, "0403");
            zlsz = menuRepository.save(zlsz);
            subMenu2.clear();

            //二级菜单 代理商基本设置
            AgtMenu jbxxpz = new AgtMenu(2, "/config/agentConfig", "基本信息配置", 0, zlsz, "BASE_AGENT", 0, "040301");
            jbxxpz = menuRepository.save(jbxxpz);
            subMenu2.add(jbxxpz);

            zlsz.setChildren(subMenu2);
            zlsz = menuRepository.save(zlsz);
            subMenu1.add(zlsz);

            //一级菜单 收货地址管理
            AgtMenu shdzgl = new AgtMenu(1, "", "收货地址管理", 3, baseMenu, "BASE_DATA", 0, "0404");
            shdzgl = menuRepository.save(shdzgl);
            subMenu2.clear();

            //二级菜单 收货地址列表
            AgtMenu shdzlb = new AgtMenu(2, "/config/addressList", "收货地址列表", 0, shdzgl, "BASE_DATA", 0, "040401");
            shdzlb = menuRepository.save(shdzlb);
            subMenu2.add(shdzlb);

            shdzgl.setChildren(subMenu2);
            shdzgl = menuRepository.save(shdzgl);
            subMenu1.add(shdzgl);

            baseMenu.setChildren(subMenu1);
            menuRepository.save(baseMenu);
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
