/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.order;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by helloztt on 2016/3/25.
 */
@Entity
@Table(name = "Mall_Delivery_item")
@Setter
@Getter
@Cacheable(value = false)
public class MallDeliveryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Item_Id")
    private Integer itemId;

    @ManyToOne
    @JoinColumn(name = "Delivery_Id")
    private MallDelivery delivery;

    @Column(name = "Item_Type")
    private String type;

    @Column(name = "Product_Id")
    private Integer productId;

    @Column(name = "Product_Bn")
    private String productBn;

    @Column(name = "Product_Name")
    private String productName;

    @Column(name = "Number")
    private int num;

}
