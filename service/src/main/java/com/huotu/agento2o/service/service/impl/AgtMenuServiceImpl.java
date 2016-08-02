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

import com.huotu.agento2o.service.entity.AgtMenu;
import com.huotu.agento2o.service.repository.AgtMenuRepository;
import com.huotu.agento2o.service.service.AgtMenuService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
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
    private static final Log log = LogFactory.getLog(AgtMenuServiceImpl.class);


    @Autowired
    private AgtMenuRepository menuRepository;

    @PostConstruct
    @Transactional
    public void init() {
        log.info("start check...");
        //01
        initOrderMenu();
        //02
        initPurchaseMenu();
        //03
        initShopMenu();
        //04
        initConfigMenu();
        //05
        initSettlementMenu();
    }

    /**
     * 初始化订单模块，订单模块以 01 开头
     */
    private void initOrderMenu() {
        //判断是否存在订单模块
        if (menuRepository.findOne("01") == null) {
            //订单管理
            AgtMenu orderMenu = new AgtMenu(0, "", "订单管理", 0, null, "AGENT,SHOP,ORDER", 0, "01");
            //一级子菜单 订单管理
            AgtMenu ddgl = new AgtMenu(1, "", "订单管理", 0, orderMenu, "AGENT,SHOP,ORDER", 0, "0101");
            //二级子菜单 订单列表
            AgtMenu ddlb = new AgtMenu(2, "/order/getOrdersPage", "订单列表", 0, ddgl, "AGENT,SHOP,ORDER", 0, "010101");
            menuRepository.save(ddlb);

            //二级子菜单 售后列表
            AgtMenu shlb = new AgtMenu(2, "/agent/afterSale/afterSaleList", "售后列表", 1, ddgl, "SHOP,ORDER", 0, "010102");
            menuRepository.save(shlb);

            //一级子菜单 单据管理
            AgtMenu djgl = new AgtMenu(1, "", "单据管理", 1, orderMenu, "SHOP,ORDER", 0, "0102");
            AgtMenu fhd = new AgtMenu(2, "/order/deliveries", "发货单", 0, djgl, "SHOP,ORDER", 0, "010201");
            menuRepository.save(fhd);
            AgtMenu thd = new AgtMenu(2, "/order/deliveries?type=return", "退货单", 1, djgl, "SHOP,ORDER", 0, "010202");
            menuRepository.save(thd);
        }
    }

    /**
     * 初始化采购管理模块，采购管理模块以 02 开头
     */
    private void initPurchaseMenu() {
        if (menuRepository.findOne("02") == null) {
            //商品采购
            AgtMenu purchaseMenu = new AgtMenu(0, "", "采购管理", 1, null, "AGENT,SHOP,PURCHASE", 0, "02");
            //一级菜单 商品采购
            AgtMenu cggl = new AgtMenu(1, "", "商品采购", 0, purchaseMenu, "AGENT,SHOP,PURCHASE", 0, "0201");
            //二级菜单 采购管理
            AgtMenu spcg = new AgtMenu(2, "/purchase/showGoodsList", "商品采购", 0, cggl, "AGENT,SHOP,PURCHASE", 0, "020101");
            menuRepository.save(spcg);
            AgtMenu gwc = new AgtMenu(2, "/shoppingCart/showShoppingCart", "购物车", 1, cggl, "AGENT,SHOP,PURCHASE", 0, "020102");
            menuRepository.save(gwc);

            //一级菜单 我的采购管理
            AgtMenu wdcgd = new AgtMenu(1, "", "我的采购管理", 1, purchaseMenu, "AGENT,SHOP,PURCHASE", 0, "0202");
            //二级菜单 我的采购单
            AgtMenu cgd = new AgtMenu(2, "/purchaseOrder/showPurchaseOrderList", "我的采购单", 0, wdcgd, "AGENT,SHOP,PURCHASE", 0, "020201");
            menuRepository.save(cgd);
            AgtMenu thsq = new AgtMenu(2, "/returnedOrder/showPurchasedProductList", "采购退货申请", 1, wdcgd, "AGENT,SHOP,PURCHASE", 0, "020202");
            menuRepository.save(thsq);
            AgtMenu thd = new AgtMenu(2, "/returnedOrder/showReturnedOrderList", "我的采购退货单", 2, wdcgd, "AGENT,SHOP,PURCHASE", 0, "020203");
            menuRepository.save(thd);

            //一级菜单 商品管理
            AgtMenu kcgl = new AgtMenu(1, "", "商品管理", 2, purchaseMenu, "AGENT,SHOP,PURCHASE", 0, "0203");
            //二级采购单 商品列表
            AgtMenu splb = new AgtMenu(2, "/goods/goodsList", "我的商品", 0, kcgl, "AGENT,SHOP,PURCHASE", 0, "020301");
            menuRepository.save(splb);
            //二级采购单 库存预警
            AgtMenu kcyj = new AgtMenu(2, "/goods/managerUI", "库存预警", 1, kcgl, "AGENT,SHOP,PURCHASE", 0, "020302");
            menuRepository.save(kcyj);

            //一级订单 下级采购管理
            AgtMenu xjcggl = new AgtMenu(1, "", "下级采购管理", 3, purchaseMenu, "AGENT,AGENT_PURCHASE", 0, "0204");
            //二级菜单 下级采购单
            AgtMenu xjcgd = new AgtMenu(2, "/purchaseOrder/showAgentPurchaseOrderList", "下级采购单", 0, xjcggl, "AGENT,AGENT_PURCHASE", 0, "020401");
            menuRepository.save(xjcgd);
            //二级菜单 下级退货单
            AgtMenu xjthd = new AgtMenu(2, "/returnedOrder/showAgentReturnedOrderList", "下级退货单", 1, xjcggl, "AGENT,AGENT_PURCHASE", 0, "020402");
            menuRepository.save(xjthd);

            //一级菜单 下级采购单据管理
            AgtMenu cgdjgl = new AgtMenu(1,"","下级采购单据管理",2,purchaseMenu,"AGENT,AGENT_PURCHASE",0,"0205");
            //二级菜单，采购单物流信息列表
            AgtMenu cgdwl = new AgtMenu(2,"/purchaseOrder/delivery/showPurchaseDeliveryList","采购单物流表",0,cgdjgl,"AGENT,AGENT_PURCHASE",0,"020501");
            menuRepository.save(cgdwl);
            //二级菜单 退货单物流信息列表
            AgtMenu thdwl = new AgtMenu(2,"/purchaseOrder/delivery/showReturnDeliveryList","退货单物流表",0,cgdjgl,"AGENT,AGENT_PURCHASE",0,"020502");
            menuRepository.save(thdwl);
        }
    }

    /**
     * 初始化门店管理模块， 门店管理模块以 03 开头
     */
    private void initShopMenu() {
        //判断是否存在订单模块
        if (menuRepository.findOne("03") == null) {
            //门店管理
            AgtMenu shopMenu = new AgtMenu(0, "", "门店管理", 2, null, "AGENT,SHOP_MANAGER", 0, "03");

            //一级子菜单 门店管理
            AgtMenu mdgl = new AgtMenu(1, "", "门店管理", 0, shopMenu, "AGENT,SHOP_MANAGER", 0, "0301");

            //二级子菜单 门店列表
            AgtMenu shlb = new AgtMenu(2, "/shop/shopList", "门店列表", 0, mdgl, "AGENT,SHOP_MANAGER", 0, "030102");
            menuRepository.save(shlb);
            //二级子菜单 新增门店
            AgtMenu xzmd = new AgtMenu(2, "/shop/addShopPage", "新增门店", 1, mdgl, "AGENT,SHOP_MANAGER", 0, "030101");
            menuRepository.save(xzmd);

        }
    }

    /**
     * 初始化基本设置模块，基本设置模块以 04 开头
     */
    private void initConfigMenu() {
        //判断是否存在基本设置
        if (menuRepository.findOne("04") == null) {
            //基本设置
            AgtMenu baseMenu = new AgtMenu(0, "", "基本设置", 3, null, "AGENT,SHOP,BASE_DATA", 0, "04");

            //一级子菜单 资料设置
            AgtMenu jbse = new AgtMenu(1, "", "资料设置", 0, baseMenu, "AGENT,SHOP,BASE_DATA", 0, "0401");
            //二级子菜单 基本信息配置
            AgtMenu mdjbsz = new AgtMenu(2, "/config/baseConfig", "基本信息配置", 0, jbse, "AGENT,SHOP,BASE_DATA", 0, "040101");
            menuRepository.save(mdjbsz);

            //一级子菜单 票据管理
            AgtMenu pjgl = new AgtMenu(1, "", "票据管理", 1, baseMenu, "AGENT,SHOP,BASE_DATA", 0, "0402");
            //二级子菜单 票据设置
            AgtMenu pjsz = new AgtMenu(2, "/config/invoiceConfig", "票据设置", 0, pjgl, "AGENT,SHOP,BASE_DATA", 0, "040201");
            menuRepository.save(pjsz);

            //一级菜单 收货地址管理
            AgtMenu shdzgl = new AgtMenu(1, "", "收货地址管理", 2, baseMenu, "AGENT,SHOP,BASE_DATA", 0, "0403");
            //二级菜单 收货地址列表
            AgtMenu shdzlb = new AgtMenu(2, "/config/addressList", "收货地址列表", 0, shdzgl, "AGENT,SHOP,BASE_DATA", 0, "040301");
            menuRepository.save(shdzlb);
        }
    }

    /**
     * 初始化结算模块，结算模块以 05 开头
     */
    private void initSettlementMenu(){
        if(menuRepository.findOne("05") == null){
            //结算管理
            AgtMenu settlementMenu = new AgtMenu(0,"","结算管理",4,null,"SHOP,SETTLEMENT",0,"05");

            //一级子菜单 结算单管理
            AgtMenu jsdgl = new AgtMenu(1,"","结算单管理",0,settlementMenu,"SHOP,SETTLEMENT",0,"0501");
            //二级子菜单 结算单列表
            AgtMenu jsdlb = new AgtMenu(2,"/settlement/settlements","结算单列表",0,jsdgl,"SHOP,SETTLEMENT",0,"050101");
            menuRepository.save(jsdlb);

            //一级子菜单 结算账户
            AgtMenu jszh = new AgtMenu(1,"","结算账户",1,settlementMenu,"SHOP,SETTLEMENT",0,"0502");
            //二级子菜单 我的账户
            AgtMenu wdzh = new AgtMenu(2,"/withdraw/withdrawRecords","我的账户",0,jszh,"SHOP,SETTLEMENT",0,"050201");
            menuRepository.save(wdzh);
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
