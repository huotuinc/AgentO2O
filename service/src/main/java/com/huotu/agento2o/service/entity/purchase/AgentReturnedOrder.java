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

import com.huotu.agento2o.service.common.PurchaseEnum;
import com.huotu.agento2o.service.entity.author.Author;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 退货单
 * Created by helloztt on 2016/5/12.
 */
@Entity
@Table(name = "Agt_Returned_Order")
@Getter
@Setter
@Cacheable(false)
public class AgentReturnedOrder {
    @Id
    @Column(name = "R_Order_Id")
    private String rOrderId;

    /**
     * 代理商/门店
     */
    @JoinColumn(name = "Agent_Id")
    @ManyToOne
    private Author author;

    /**
     * 总金额
     */
    @Column(name = "Final_Amount",precision = 2)
    private double finalAmount;
    /**
     * 邮费
     */
    @Column(name = "Cost_Freight",precision = 2)
    private double costFreight;

    /*以下为收货人信息*/
    /**
     * 收货人
     */
    @Column(name = "Ship_Name")
    private String shipName;
    /**
     * 联系方式
     */
    @Column(name = "Ship_Mobile")
    private String shipMobile;
    /**
     * 收货人地址
     */
    @Column(name = "Ship_Addr")
    private String shipAddr;

    /**
     * 配送方式
     */
    @Column(name = "Sendment")
    private PurchaseEnum.SendmentStatus sendmentStatus;

    /**
     * 审核状态
     */
    @Column(name = "Status")
    private PurchaseEnum.OrderStatus status;
    /**
     * 发货状态
     */
    @Column(name = "Ship_Status")
    private PurchaseEnum.ShipStatus shipStatus;
    /**
     * 退款状态
     */
    @Column(name = "Pay_Status")
    private PurchaseEnum.PayStatus payStatus;

    /**
     * 生成时间
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "Createtime")
    private Date createTime;

    /**
     * 退款时间
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "Paytime")
    private Date payTime;
    /**
     * 最近修改时间
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "Last_update_time")
    private Date lastUpdateTime;
    /**
     * 直系代理商/平台方备注
     */
    @Column(name = "Parent_Comment")
    private String parentComment;
    /**
     * 代理商/门店备注
     */
    @Column(name = "Agent_Comment")
    private String authorComment;
    /**
     * 退货单状态
     */
    @Column(name = "Disabled")
    private boolean disabled;

    /**
     * 确认收货时间
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "Received_Time")
    private Date receivedTime;


    //采购状态为 待审核 或 审核不通过
    //可删除
    public boolean deletable() {
        return status == PurchaseEnum.OrderStatus.CHECKING || status == PurchaseEnum.OrderStatus.RETURNED;
    }

    //采购状态为 待审核
    //可审核
    public boolean checkable() {
        return status == PurchaseEnum.OrderStatus.CHECKING;
    }

    //采购状态为已审核,发货状态为已发货，且 支付状态 为空或 未支付
    //可支付
    public boolean payabled() {
        return status == PurchaseEnum.OrderStatus.CHECKED
                && shipStatus == PurchaseEnum.ShipStatus.DELIVERED
                && (payStatus == null || payStatus == PurchaseEnum.PayStatus.NOT_PAYED)
                && receivedTime != null;
    }

    //采购状态为已审核 且发货状态 为空 或未发货
    //可发货
    public boolean deliverable() {
        return status == PurchaseEnum.OrderStatus.CHECKED
                && (shipStatus == null || shipStatus == PurchaseEnum.ShipStatus.NOT_DELIVER);
    }

    //采购状态为已审核 且发货状态为已发货
    //可确认收货
    public boolean receivable(){
        return status == PurchaseEnum.OrderStatus.CHECKED
                && shipStatus == PurchaseEnum.ShipStatus.DELIVERED
                && (payStatus == null || payStatus == PurchaseEnum.PayStatus.NOT_PAYED)
                && receivedTime == null;
    }

}
