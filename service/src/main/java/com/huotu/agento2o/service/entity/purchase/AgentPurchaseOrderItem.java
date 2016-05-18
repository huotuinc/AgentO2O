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

import com.huotu.agento2o.service.entity.goods.MallProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.net.URI;

/**
 * Created by helloztt on 2016/5/12.
 */
@Entity
@Table(name = "Agt_Purchase_Order_Item")
@Getter
@Setter
public class AgentPurchaseOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "P_Item_Id")
    private Integer id;

    /**
     * 采购单
     */
    @JoinColumn(name = "P_Order_Id")
    @ManyToOne
    private AgentPurchaseOrder purchaseOrder;
    /**
     * 货品
     */
    @JoinColumn(name = "Product_Id")
    @ManyToOne
    private MallProduct product;
    /**
     * 数量
     */
    @Column(name = "Num")
    private Integer num;
    /**
     * 冗余 货号
     */
    @Column(name = "Bn")
    private String  bn;
    /**
     * 冗余 商品名称
     */
    @Column(name = "Name")
    private String name;
    /**
     * 冗余 货品规格
     */
    @Column(name = "Pdt_Desc")
    private String pdtDesc;
    /**
     * 冗余 单位
     */
    @Column(name = "Unit",length = 10)
    private String unit;
    /**
     * 冗余 销售价
     */
    @Column(name = "Price",precision = 2)
    private double price;
    /**
     * 冗余 缩略图
     */
    @Column(name = "Thumbnail_Pic")
    private String thumbnailPic;

    @Transient
    private URI picUri;
}
