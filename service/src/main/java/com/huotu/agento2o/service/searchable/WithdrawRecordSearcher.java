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
 * 提现记录检索条件
 * Created by Administrator on 2015/11/5.
 */
@Getter
@Setter
public class WithdrawRecordSearcher {

    private int customerNo;
    private String applyStartTime;//申请时间查询范围-起始
    private String applyEndTime;//申请时间查询范围-结束
    private String remitStartTime;//提现时间查询范围-起始
    private String remitEndTime;//提现时间查询范围-结束

    private int pageNo = 1;
    private int pageSize = Constant.PAGESIZE;
    private int status = -1;
    private int shopId;
    private String withdrawNo;


}
