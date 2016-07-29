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
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * 代理商商品
 * Created by helloztt on 2016/7/29.
 */
@Entity
@Table(name = "Agt_Goods")
@Cacheable(false)
@Getter
@Setter
public class AgentGoods {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Agt_Goods_Id")
    private Integer id;

    /**
     * 商品ID
     */
    @Column(name = "Goods_Id")
    private Integer goodsId;

    /**
     * 门店
     */
    @JoinColumn(name = "Shop_Id")
    @ManyToOne
    private Shop shop;

    /**
     * 运费模板ID
     */
    @JoinColumn(name = "Freight_Template_Id")
    @ManyToOne
    private FreightTemplate freightTemplate;

    /**
     * 是否可用
     */
    @Column(name = "Disabled")
    private boolean disabled;
}
