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
    private PurchaseEnum.SendmentStatus sendMode;
    /**
     * 发票类型
     */
    @Column(name = "Tax_Type")
    private PurchaseEnum.TaxType taxType;

    /*以下为发票信息*/
    /**
     * 公司名称
     */
    @Column(name = "Company_Name")
    private String companyName;

    /**
     * 公司电话
     */
    @Column(name = "Company_Tel")
    private String companyTel;

    /**
     * 公司地址
     */
    @Column(name = "CompanyAddr")
    private String companyAddr;

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
     * 税务登记证
     */
    @Column(name = "TaxRegistCertificateUrl")
    private String taxRegistCertificateUrl;

    /**
     * 一般纳税人资格证
     */
    @Column(name = "GeneralCertificateUrl")
    private String generalCertificateUrl;

    /**
     * 采购单状态
     */
    @Column(name = "Status")
    private PurchaseEnum.OrderStatus status;
    /**
     * 发货状态
     */
    @Column(name = "Ship_Status")
    private PurchaseEnum.ShipStatus shipStatus;
    /**
     * 付款状态
     */
    @Column(name = "Pay_Status")
    private PurchaseEnum.PayStatus payStatus;

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
    private boolean disabled;

    @OneToMany(mappedBy = "purchaseOrder")
    private List<AgentPurchaseOrderItem> orderItemList;

}
