/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.goods;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by allan on 3/17/16.
 */
@Entity
@Table(name = "Mall_Products")
@Setter
@Getter
@Cacheable(false)
public class MallProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Product_Id")
    private Integer productId;
    @ManyToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "Goods_Id")
    private MallGoods goods;
    /**
     * 货号
     */
    @Column(name = "Bn")
    private String bn;

    /**
     * 规格
     */
    @Column(name = "Pdt_Desc")
    private String standard;

    /**
     * 成本价
     */
    @Column(name = "Cost")
    private double cost;
    /**
     * 销售价
     */
    @Column(name = "Price")
    private double price;

    @Transient
    private double purchasePrice;

    @Column(name = "Name", nullable = false)
    private String name;
    /**
     * 单位
     */
    @Column(name = "Unit")
    private String unit;

    /**
     * 库存
     */
    @Column(name = "Store")
    private int store;
    /**
     * 预占库存
     */
    @Column(name = "Freez")
    private int freez;

    /**
     * 代理商进货价
     */
    @Column(name = "AgentPriceInfo")
    private String agentPriceInfo;
    /**
     * 代理商，门店默认进货价
     */
    @Column(name = "AgentPrice")
    private Double agentPrice;

    /**
     * 如果读取平台方库存,则可用库存为 this.[store - freez]
     * 如果读取上级代理商库存，则可用库存为 agentProdcut.[stroe-freez]
     * 可用库存
     */
    @Transient
    private int usableStore = 0;
    /**
     * 购物车中商品数量
     */
    @Transient
    private int shoppingStore=0;

    /**
     * 当前代理商/门店库存
     */
    @Transient
    private int authorStore = 0;

    @Column(name = "Marketable", nullable = false)
    private boolean marketable;

    @Column(name = "Is_Local_Stock", nullable = false)
    private boolean isLocalStock;

}
