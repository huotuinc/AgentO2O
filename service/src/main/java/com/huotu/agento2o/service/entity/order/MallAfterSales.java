package com.huotu.agento2o.service.entity.order;

import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.entity.author.Shop;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 售后表
 * Created by allan on 12/30/15.
 */
@Entity
@Table(name = "Mall_AfterSales")
@Setter
@Getter
@Cacheable(value = false)
public class MallAfterSales {
    @Id
    @Column(name = "After_Id")
    private String afterId;
    @Column(name = "Member_Id")
    private int memberId;
    @Column(name = "Customer_Id")
    private int customerId;
    @Column(name = "Goods_Id")
    private int goodId;
    @Column(name = "Product_Id")
    private int productId;
    @OneToOne
    @JoinColumn(name = "Item_Id")
    private MallOrderItem orderItem;
    @Column(name = "Product_Name")
    private String productName;
    @Column(name = "Product_Num")
    private int productNum;
    @Column(name = "Integral")
    private int integral;
    @Column(name = "Bn")
    private String bn;
    @Column(name = "After_Status")
    private AfterSaleEnum.AfterSaleStatus afterSaleStatus;
    @Column(name = "Order_Id")
    private String orderId;
    @Column(name = "IntegralAmount")
    private double integralAmount;
    @Column(name = "AfterMoney")
    private double afterMoney;
    @Column(name = "AfterTime")
    private Date createTime;
    @Column(name = "Product_Img")
    private String productImg;
    @Column(name = "Pay_Status")
    private OrderEnum.PayStatus payStatus;
    @JoinColumn(name = "Agent_Id")
    @ManyToOne
    private Shop shop;
    @Column(name = "Apply_Type")
    private AfterSaleEnum.AfterSaleType afterSaleType;
    @Column(name = "Apply_Reason")
    private AfterSaleEnum.AfterSalesReason afterSalesReason;
    @OneToMany(mappedBy = "afterSales")
    @OrderBy(value = "itemId DESC ")
    private List<MallAfterSalesItem> afterSalesItems;
    /**
     * 合伙人金币
     */
    @Column(name = "CptGold")
    private double cptCold;
    @Column(name = "Apply_Mobile")
    private String applyMobile;

    public boolean refundable() {
        if (afterSaleType == AfterSaleEnum.AfterSaleType.REFUND) {
            return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.APPLYING;
        } else {
            return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.WAITING_FOR_CONFIRM;
        }
    }

    public boolean returnable() {
        return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.APPLYING &&
                afterSaleType == AfterSaleEnum.AfterSaleType.RETURN_AND_REFUND;
    }

    public boolean refusable() {
//        return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.APPLYING ||
//                afterSaleStatus == AfterSaleEnum.AfterSaleStatus.WAITING_BUYER_RETURN;
        //伙伴商城要求放开拒绝条件
        return afterSaleStatus == AfterSaleEnum.AfterSaleStatus.APPLYING ||
                afterSaleStatus == AfterSaleEnum.AfterSaleStatus.REFUNDING ||
                afterSaleStatus == AfterSaleEnum.AfterSaleStatus.WAITING_BUYER_RETURN ||
                afterSaleStatus == AfterSaleEnum.AfterSaleStatus.WAITING_FOR_CONFIRM;
    }
}
