/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.model.settlement;


import com.huotu.agento2o.service.common.WithdrawEnum;
import lombok.Getter;
import lombok.Setter;

/**
 * 申请提现信息
 * Created by Administrator on 2015/11/6.
 */
@Getter
@Setter
public class WithdrawApplyInfo {

    /**
     * 提款单号
     */
    private String withdrawNo;

    /**
     * 门店编号
     */
    private String shopId;

    /**
     * 门店名称
     */
    private String shopName;

    /**
     * 提款金额
     */
    private double balance;

    /**
     * 开户银行名称
     */
    private String bankName;

    /**
     * 账户名
     */
    private String accountName;

    /**
     * 银行账号
     */
    private String accountNo;

    /**
     * 申请时间
     */
    private String applyTime;

    /**
     * 审核状态 0：申请中 1：已打款
     */
    private WithdrawEnum.WithdrawEnumStatus status;
}
