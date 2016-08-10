/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.model.order;

import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.entity.order.MallDelivery;
import com.huotu.agento2o.service.entity.order.MallOrderItem;
import com.huotu.agento2o.service.entity.order.MallPayments;
import com.huotu.agento2o.service.entity.order.MallRefunds;
import lombok.Data;

import javax.persistence.Column;
import java.util.ArrayList;
import java.util.List;

/**
 * 订单详情
 * Created by Administrator on 2015/11/13.
 */
@Data
public class OrderDetailModel {

    /**
     * 订单编号
     */
    private String orderId;

    /**
     * 发货单列表
     */
    private List<MallDelivery> deliveryList;
    /**
     * 退货单列表
     */
    private List<MallDelivery> refundsList;
    /**
     * 付款列表
     */
    private List<MallPayments> paymentsList;
    /**
     * 退款列表
     */
    private List<MallRefunds> refundsMoneyList;

//    /**
//     * 物流公司名字
//     */
//    private String logisticsName;
//
//    /**
//     * 物流单号
//     */
//    private String logisticsNo;

    /**
     * 收货人
     */
    private String shipName;

    /**
     * 收货人电话
     */
    private String shipTel;

    /**
     * 收货人手机号
     */
    private String shipMobile;

    /**
     * 收货人地区
     */
    private String shipArea;

    /**
     * 收货人地址
     */
    private String shipAddr;

    /**
     * 实付金额（含运费）
     */
    private double finalAmount;

    /**
     * 运费
     */
    private double costFreight;
    /**
     * 成本价
     */
    private double costPrice;
    /**
     * 订单优惠金额
     */
    private double pmtAmount;

    /**
     * 下单时间
     */
    private String createTime;

    /**
     * 支付时间
     */
    private String payTime;

    /**
     * 支付方式名称
     */
    private String payTypeName;
    /**
     * 订单来源
     */
    private OrderEnum.OrderSourceType orderSourceType;

    /**
     * 商品列表
     */
    private List<MallOrderItem> supOrderItemList = new ArrayList<>();
    /**
     * 订单备注
     */
    private String remark;
    /**
     * 用户附言
     */
    private String memo;
    /**
     * 代理商备注
     */
    private String agentRemark;
}
