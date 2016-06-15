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


import com.huotu.agento2o.service.common.WithdrawEnum;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * 提现记录
 * Created by WenbinChen on 2015/10/26 16:46.
 */
@Entity
@Table(name = "Agt_WithdrawRecord")
@Cacheable(false)
@Getter
@Setter
public class WithdrawRecord {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    /**
     * 提款单号
     */
    @Column(name = "WidthdrawNo")
    private String withdrawNo;

    /**
     * 申请时间
     */
    @Column(name = "ApplyTime")
    private Date applyTime;

    /**
     * 打款时间
     */
    @Column(name = "RemitTime")
    private Date remitTime;

    /**
     * 提款金额
     */
    @Column(name = "Amount",precision = 2)
    private double amount;

    /**
     * 审核状态 0：申请中 1：已打款  2： 申请不通过
     */
    @Column(name = "Status")
    private WithdrawEnum.WithdrawEnumStatus status;

    @ManyToOne
    private AuthorAccount shopAccount;

    /**
     * 开户银行名称
     */
    @Column(name = "BankName")
    private String bankName;

    /**
     * 账户名
     */
    @Column(name = "AccountName")
    private String accountName;

    /**
     * 银行账号
     */
    @Column(name = "AccountNo")
    private String accountNo;
}
