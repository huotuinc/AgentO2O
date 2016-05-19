/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.purchase;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by helloztt on 2016/5/19.
 */
@Entity
@Table(name = "Agt_Delivery_Item")
@Getter
@Setter
@Cacheable(value = false)
public class AgentDeliveryItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Item_Id")
    private Integer itemId;

    @ManyToOne
    @JoinColumn(name = "Delivery_Id")
    private AgentDelivery delivery;

    @Column(name = "Product_Id")
    private Integer productId;

    @Column(name = "Agent_Product_Id")
    private Integer agentProductId;

    @Column(name = "Product_Bn")
    private String productBn;

    @Column(name = "Product_Name")
    private String productName;

    @Column(name = "Number")
    private int num;
}
