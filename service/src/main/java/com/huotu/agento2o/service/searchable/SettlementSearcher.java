/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.searchable;

import com.huotu.agento2o.common.util.Constant;
import lombok.Getter;
import lombok.Setter;

/**
 * 结算对账单检索条件
 * Created by WenbinChen on 2015/10/27 10:04.
 */
@Getter
@Setter
public class SettlementSearcher {

    /**
     * 结算单号
     */
    private String settlementNo;

    /**
     * 分销商审核状态
     */
    private int customerStatus = -1;

    /**
     * 门店审核状态
     */
    private int authorStatus = -1;

    /**
     * 起始日期 格式：yyyy-MM-dd
     */
    private String startDate;

    /**
     * 结束日期 格式：yyyy-MM-dd
     */
    private String endDate;
    /**
     * 结算单产生日期 起始时间 格式：yyyy-MM-dd
     */
    private String createStartDate;
    /**
     * 结算单产生日期 结束时间 格式：yyyy-MM-dd
     */
    private String createEndDate;

    /**
     * 门店Id 用于分销商结算查询
     */
    private Integer shopId;
    /**
     * 代理商Id
     */
    private Integer agentId;

    /**
     * 页码
     */
    private int pageNo = 1;

    /**
     * 分页大小
     */
    private int pageSize = Constant.PAGESIZE;
}
