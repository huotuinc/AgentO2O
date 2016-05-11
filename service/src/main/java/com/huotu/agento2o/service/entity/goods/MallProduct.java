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
    @ManyToOne
    @JoinColumn(name = "Goods_Id")
    private MallGoods goods;

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
    @Column(name = "Name")
    private String name;
    // TODO: 3/17/16
}
