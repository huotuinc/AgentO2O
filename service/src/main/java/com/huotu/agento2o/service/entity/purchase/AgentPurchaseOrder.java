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
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/12.
 */
@Entity
@Table(name = "Agt_Purchase_Order")
@Getter
@Setter
@Cacheable(value = false)
public class AgentPurchaseOrder {
    @Id
    @Column(name = "P_Order_Id")
    private String pOrderId;

    /**
     * 代理商/门店
     */
    @JoinColumn(name = "Agent_Id")
    @ManyToOne
    private Author author;

    /**
     * 总金额
     */
    @Column(name = "Final_Amount", precision = 2)
    private double finalAmount;
    /**
     * 邮费
     */
    @Column(name = "Cost_Freight", precision = 2)
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
    private PurchaseEnum.SendmentStatus sendMode;
    /**
     * 发票类型
     */
    @Column(name = "Tax_Type")
    private PurchaseEnum.TaxType taxType;

    /*以下为发票信息*/
    /**
     * 发票抬头
     */
    @Column(name = "Tax_Title")
    private String taxTitle;

    /**
     * 发票内容
     */
    @Column(name = "Tax_Content")
    private String taxContent;

    /**
     * 纳税人识别码
     */
    @Column(name = "TaxPayerCode")
    private String taxpayerCode;

    /**
     * 开户银行名称
     */
    @Column(name = "Bank_Name")
    private String bankName;

    /**
     * 开户银行账号
     */
    @Column(name = "Account_No")
    private String accountNo;

    /**
     * 采购单状态
     */
    @Column(name = "Status")
    private PurchaseEnum.OrderStatus status = PurchaseEnum.OrderStatus.CHECKING;
    /**
     * 发货状态
     */
    @Column(name = "Ship_Status")
    private PurchaseEnum.ShipStatus shipStatus = PurchaseEnum.ShipStatus.NOT_DELIVER;
    /**
     * 付款状态
     */
    @Column(name = "Pay_Status")
    private PurchaseEnum.PayStatus payStatus = PurchaseEnum.PayStatus.NOT_PAYED;

    /**
     * 生成时间
     */
    @Column(name = "Createtime")
    private Date createTime;
    /**
     * 付款时间
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
     * 确认收货时间
     */
    @Temporal(value = TemporalType.TIMESTAMP)
    @Column(name = "Received_Time")
    private Date receivedTime;

    /**
     * 审核备注
     */
    @Column(name = "Status_Comment")
    private String statusComment;
    /**
     * 发货方备注
     */
    @Column(name = "Parent_Comment")
    private String parentComment;
    /**
     * 代理商/门店备注
     */
    @Column(name = "Agent_Comment")
    private String authorComment;
    /**
     * 采购单状态
     */
    @Column(name = "Disabled")
    private boolean disabled = false;

    @OneToMany(mappedBy = "purchaseOrder", cascade = CascadeType.PERSIST)
    private List<AgentPurchaseOrderItem> orderItemList;

    //采购状态为 待审核 或 审核不通过 或 审核通过且未支付
    //可取消采购单
    public boolean deletable() {
        return disabled == false && (status == PurchaseEnum.OrderStatus.CHECKING || status == PurchaseEnum.OrderStatus.RETURNED
                || (status == PurchaseEnum.OrderStatus.CHECKED && payStatus == PurchaseEnum.PayStatus.NOT_PAYED));
    }

    //采购状态为 待审核
    //可审核
    public boolean checkable() {
        return disabled == false && status == PurchaseEnum.OrderStatus.CHECKING;
    }

    //采购状态为已审核,且 支付状态 为空或 未支付
    //可支付
    public boolean payabled() {
        return disabled == false && status == PurchaseEnum.OrderStatus.CHECKED
                && (payStatus == null || payStatus == PurchaseEnum.PayStatus.NOT_PAYED);
    }

    //采购状态为已审核 且支付状态为 已支付 且发货状态 为空 或未发货
    //可发货
    public boolean deliverable() {
        return disabled == false && status == PurchaseEnum.OrderStatus.CHECKED
                && payStatus == PurchaseEnum.PayStatus.PAYED
                && (shipStatus == null || shipStatus == PurchaseEnum.ShipStatus.NOT_DELIVER);
    }

    //采购状态为已审核 且支付状态为 已支付 且发货状态为已发货 且确认收货时间为空
    //可确认收货
    public boolean receivable() {
        return disabled == false && status == PurchaseEnum.OrderStatus.CHECKED
                && payStatus == PurchaseEnum.PayStatus.PAYED
                && shipStatus == PurchaseEnum.ShipStatus.DELIVERED
                && receivedTime == null;
    }

}
