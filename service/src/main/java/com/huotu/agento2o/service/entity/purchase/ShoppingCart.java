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

import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 采购购物车
 * Created by helloztt on 2016/5/12.
 */
@Entity
@Table(name = "Agt_ShoppingCart")
@Cacheable(value = false)
@Getter
@Setter
public class ShoppingCart {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    /**
     * 代理商
     */
    @JoinColumn(name = "Agent_Id")
    @ManyToOne
    private Agent agent;

    /**
     * 门店
     */
    @JoinColumn(name = "Shop_Id")
    @ManyToOne
    private ShopAuthor shop;

    @JoinColumn(name = "Product_Id")
    @ManyToOne
    private MallProduct product;

    @Column(name = "Num")
    private Integer num;

    @Column(name = "Createtime")
    private Date createTime;

    public Agent getParentAgent(){
        if(agent != null){
            return agent.getParentAgent();
        }else if(shop != null){
            return shop.getParentAgent();
        }
        return null;
    }
}
