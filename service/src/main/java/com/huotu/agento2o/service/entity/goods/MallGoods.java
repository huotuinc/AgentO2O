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

import com.huotu.agento2o.service.entity.author.Shop;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
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
    private Integer goodId;
    /**
     * 商品简介
     */
    @Column(name = "Brief")
    private String brief;
    @OneToMany(mappedBy = "good")
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

    @Column(name = "Customer_Id")
    private Integer customerId;

    @JoinColumn(name = "Agent_Id")
    @ManyToOne
    private Shop shop;

    @Column(name = "Name")
    private String name;
    @Column(name = "Disabled")
    private boolean disabled;
    // TODO: 3/17/16
}
