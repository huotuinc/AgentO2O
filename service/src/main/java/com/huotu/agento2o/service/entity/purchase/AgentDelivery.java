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

import com.alibaba.fastjson.annotation.JSONField;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/19.
 */
@Entity
@Table(name = "Agt_Delivery")
@Getter
@Setter
@Cacheable(value = false)
public class AgentDelivery {
    @Id
    @Column(name = "Delivery_Id")
    private String deliveryId;
    @ManyToOne
    @JoinColumn(name = "P_Order_Id")
    @JSONField(deserialize = false)
    private AgentPurchaseOrder purchaseOrder;
//    @ManyToOne
//    @JoinColumn(name = "Agent_Id")
//    private Author author;
    @Column(name = "Agent_Id")
    private Integer agentId;
//    @ManyToOne
//    @JoinColumn(name = "Parent_Agent_Id")
//    private Agent parentAgentId;
    @Column(name = "Parent_Agent_Id")
    private Integer parentAgentId;
    @Column(name = "DType")
    private String type;
    @Column(name = "Logi_Name")
    private String logisticsName;//物流公司
    @Column(name = "Logi_No")
    private String logisticsNo;//物流单号
    @Column(name = "Money")
    private double freight;//物流费用
//    @ManyToOne
//    @JoinColumn(name = "Customer_Id")
//    private MallCustomer customer;
    @Column(name = "Customer_Id")
    private Integer customerId;
    @Column(name = "T_Begin")
    private Date createTime;//单据生成时间
    @Column(name = "Ship_Addr")
    private String shipAddr;
    @Column(name = "Ship_Name")
    private String shipName;
    @Column(name = "Ship_Mobile")
    private String shipMobile;
    @Column(name = "Ship_Zip")
    private String shipZip;
    @Column(name = "Ship_Tel")
    private String shipTel;
    @Column(name = "Memo")
    private String memo;

    @OneToMany(mappedBy = "delivery")
    private List<AgentDeliveryItem> deliveryItems;
}
