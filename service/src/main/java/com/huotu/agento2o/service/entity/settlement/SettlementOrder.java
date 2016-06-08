/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.settlement;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 结算订单
 * Created by WenbinChen on 2015/10/26 11:32.
 */
@Entity
@Table(name="Agt_SettlementOrder")
@Cacheable(false)
@Getter
@Setter
public class SettlementOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "Settlement_Id")
    private Settlement settlement;

    /**
     * 订单编号
     */
    @Column(name = "Order_Id")
    private String orderId;

    /**
     * 下单时间
     */
    @Column(name = "OrderDateTime")
    private Date orderDateTime;


    /**
     * 支付时间
     */
    @Column(name = "PayDateTime")
    private Date payDateTime;


    /**
     * 邮费
     */
    @Column(name = "Freight",precision = 2)
    private double freight;

    /**
     * 实付金额
     */
    @Column(name = "FinalAmount",precision = 2)
    private double finalAmount;
}
