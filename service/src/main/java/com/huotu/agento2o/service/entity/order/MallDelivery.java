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

import com.alibaba.fastjson.annotation.JSONField;
import com.huotu.agento2o.service.common.OrderEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * 发货表,退货表
 * Created by helloztt on 2016/3/25.
 */
@Entity
@Table(name = "Mall_Delivery")
@Setter
@Getter
@Cacheable(value = false)
public class MallDelivery {
    @Id
    @Column(name = "Delivery_Id")
    private String deliveryId;

    @ManyToOne
    @JoinColumn(name = "Order_Id")
    @JSONField(deserialize = false)
    private MallOrder order;
    @ManyToOne
    @JoinColumn(name = "Member_Id", referencedColumnName = "UB_UserID")
    private UserBaseInfo userBaseInfo;
    @Column(name = "Type")
    private String type;
    //    @Transient
//    private String logisticsCode;//物流公司 编码
    @Column(name = "Logi_Name")
    private String logisticsName;//物流公司
    @Column(name = "Logi_No")
    private String logisticsNo;//物流单号
    @Column(name = "Money")
    private double freight;//物流费用
    //    private String remark;//备注
    @ManyToOne
    @JoinColumn(name = "Agent_Shop_Id")
    private Shop shop;
    /**
     * 代理商发货类型，默认为0或者空，
     */
    /*@Column(name = "Agent_Shop_Type")
    private OrderEnum.ShipMode agentShopType;*/

//    @ManyToOne
//    @JoinColumn(name = "Beneficiary_Agent_id")
//    private Shop beneficiaryShop;

    //    private String dicDeliverItemsStr;//发货数量序列化字段（货品id,发货数量|货品id,发货数量）
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
    private List<MallDeliveryItem> deliveryItems;
}
