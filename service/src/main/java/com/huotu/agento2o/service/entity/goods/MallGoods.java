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

import com.huotu.agento2o.service.entity.purchase.FreightTemplate;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.net.URI;
import java.util.List;

/**
 * Created by allan on 3/17/16.
 */
@Entity
@Table(name = "Mall_Goods")
@Setter
@Getter
@Cacheable(false)
public class MallGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Goods_Id")
    private Integer goodsId;
    /**
     * 商品简介
     */
    @Column(name = "Brief")
    private String brief;
    @OneToMany(mappedBy = "goods",cascade = CascadeType.ALL)
    private List<MallProduct> products;
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
    /**
     * 如果只有一个货品，显示这个货品的进货价；如果有多个货品，则显示区间值
     */
    @Transient
    private String purchasePrice;
    /**
     * 进货价
     */
    @Column(name = "AgentPrice")
    private Double agentPrice;

    @Column(name = "Customer_Id")
    private Integer customerId;

    /**
     * 代理商ID，值为0表示平台方1代理商品
     */
    @Column(name = "Is_Agent")
    private boolean isAgent;

    @Column(name = "Name")
    private String name;
    @Column(name = "Disabled")
    private boolean disabled;
    /**
     * 缩略图
     */
    @Column(name = "Thumbnail_Pic")
    private String thumbnailPic;
    @Transient
    private URI picUri;
    /**
     * 库存数量
     */
    @Column(name = "Store")
    private int store;
    /**
     * 单位
     */
    @Column(name = "Unit")
    private String unit;
    /**
     * 销量
     */
    @Column(name = "SalesCount")
    private Integer salesCount;
    /**
     * 商品类型
     */
    @Column(name = "Type_Id")
    private Integer typeId;
    /**
     * 运费模板ID
     */
    @JoinColumn(name = "Freight_Template_Id")
    @ManyToOne
    private FreightTemplate freightTemplate;
}
