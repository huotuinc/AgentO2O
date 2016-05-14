package com.huotu.agento2o.service.entity.order;


import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.goods.MallProduct;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by allan on 1/8/16.
 */
@Entity
@Table(name = "Mall_Order_Items")
@Cacheable(false)
@Setter
@Getter
public class MallOrderItem {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Item_Id")
    private Long itemId;
    @ManyToOne
    @JoinColumn(name = "Order_Id")
    private MallOrder order;
    @ManyToOne
    @JoinColumn(name = "Product_Id", referencedColumnName = "Product_Id")
    private MallProduct product;
    @Column(name = "Bn")
    private String bn;
    @Column(name = "Name")
    private String name;
    @Column(name = "Cost")
    private double cost;
    @Column(name = "Price")
    private double price;
    @Column(name = "Amount")
    private double amount;
    @Column(name = "Nums")
    private int nums;
    @Column(name = "Sendnum")
    private int sendNum;
    @ManyToOne
    @JoinColumn(name = "Agent_Id")
    private Agent agent;
    @Column(name = "Customer_Id")
    private long customerId;
    @Column(name = "Goods_Id")
    private int goodId;
    @Column(name = "Ship_Status")
    private OrderEnum.ShipStatus shipStatus;
    @Lob
    @Column(name = "customFieldValues")
    private String customFieldValues;
    @OneToOne(mappedBy = "orderItem")
    private MallAfterSales afterSales;

    public boolean returnable() {
        return afterSales != null && (afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.WAITING_FOR_CONFIRM ||
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.REFUNDING ||
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.REFUND_SUCCESS) &&
                shipStatus == OrderEnum.ShipStatus.DELIVERED &&
                afterSales.getAfterSaleType() == AfterSaleEnum.AfterSaleType.RETURN_AND_REFUND;
    }

    public boolean deliverable() {
        return (afterSales == null ||
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.CANCELED ||
                afterSales.getAfterSaleStatus() == AfterSaleEnum.AfterSaleStatus.AFTER_SALE_REFUSED) &&
                shipStatus == OrderEnum.ShipStatus.NOT_DELIVER;
    }
}
