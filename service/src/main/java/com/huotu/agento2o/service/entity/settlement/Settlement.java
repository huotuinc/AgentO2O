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

import com.huotu.agento2o.service.common.SettlementEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/6/8.
 */
@Entity
@Table(name = "Agt_Settlement")
@Cacheable(value = false)
@Getter
@Setter
public class Settlement {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    /**
     * 结算单编号
     */
    @Column(name = "SettlementNo")
    private String settlementNo;

    /**
     * 门店
     */
    @OneToOne
    @JoinColumn(name = "Agent_Id" )
    private Shop shop;

    /**
     * 分销商编号
     */
    @Column(name = "Customer_Id")
    private Integer customerId;

    /**
     * 结算日期
     */
    @Column(name = "SettlementDate")
    private Date settlementDate;
    /**
     * 结算单产生时间
     */
    @Column(name = "CreateDateTime")
    private Date createDateTime;
    /**
     * 实付金额
     */
    @Column(name = "FinalAmount",precision = 2)
    private double finalAmount;

    /**
     * 邮费
     */
    @Column(name = "Freight",precision = 2)
    private double freight;

    /**
     * 备注
     */
    @Column(name = "Remark")
    private String remark;

    /**
     * 分销商审核状态 0：待审核 1：已审核通过 2:审核不通过
     */
    @Column(name = "CustomerStatus")
    private SettlementEnum.SettlementCheckStatus customerStatus;
    /**
     * 门店审核状态 0：待审核 1：已审核通过 2:审核不通过
     */
    @Column(name = "AgentStatus")
    private SettlementEnum.SettlementCheckStatus authorStatus;

    @OneToMany(mappedBy = "settlement",cascade = CascadeType.ALL)
    private List<SettlementOrder> settlementOrders;

    public boolean customerCheckable(){
        return customerStatus == SettlementEnum.SettlementCheckStatus.NOT_CHECK ? true : false;
    }

    public boolean shopCheckable(){
        return authorStatus == SettlementEnum.SettlementCheckStatus.NOT_CHECK ? true : false;
    }

    public boolean checkAble(){
        return customerStatus == SettlementEnum.SettlementCheckStatus.RETURNED || authorStatus == SettlementEnum.SettlementCheckStatus.RETURNED ? false:true;
    }
}
