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
import java.util.Date;

/**
 * Created by helloztt on 2016/5/13.
 */
@Entity
@Table(name = "Mall_Refunds")
@Cacheable
@Getter
@Setter
public class MallRefunds {
    @Id
    @Column(name = "Refund_Id")
    private String refundId;

    @Column(name = "Order_Id")
    private String orderId;

    /**
     * 单据日期
     */
    @Column(name = "T_Ready")
    private Date readyTime;

    /**
     * 退款金额
     */
    @Column(name = "Money")
    private double money;

    /**
     * 退款方式
     */
    @Column(name = "Paymethod")
    private String payMethod;

}
