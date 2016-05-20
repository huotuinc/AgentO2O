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

import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 代理商/门店库存
 * Created by helloztt on 2016/5/11.
 */
@Entity
@Table(name = "Agt_Product")
@Cacheable(value = false)
@Getter
@Setter
public class AgentProduct {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Agent_Product_Id")
    private Integer id;

    /**
     * 代理商/门店
     */
    @JoinColumn(name = "Agent_Id")
    @ManyToOne
    private Author author;
    /**
     * 商品ID
     */
    @Column(name = "Goods_Id")
    private Integer goodsId;

    /**
     * 货品
     */
    @JoinColumn(name = "Product_Id")
    @ManyToOne
    private MallProduct product;

    /**
     * 库存
     */
    @Column(name = "Store")
    private Integer store;

    /**
     * 预占库存
     */
    @Column(name = "Freez")
    private Integer freez;

    /**
     * 库存预警
     */
    @Column(name = "Warning")
    private Integer warning;

    /**
     * 是否可用
     */
    @Column(name = "Disabled")
    private boolean disabled;

}
