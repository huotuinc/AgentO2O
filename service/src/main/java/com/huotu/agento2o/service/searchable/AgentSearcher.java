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
import lombok.Data;

/**
 * Created by WangJie on 2016/5/16.
 */
@Data
public class AgentSearcher {

    /**
     * 当前页数
     */
    private int pageNo = 1;

    /**
     * 每页数量
     */
    private int pageSize = Constant.PAGESIZE;

    /**
     * 省
     */
    private String provinceCode;

    /**
     * 市
     */
    private String cityCode;

    /**
     * 区
     */
    private String districtCode;

    /**
     * 等级id
     */
    private Integer levelId = -1;

    /**
     * 代理商名称
     */
    private String agentName;

    /**
     * 代理商账号
     */
    private String agentLoginName;

    /**
     * 代理商状态 0激活 1冻结
     */
    private Integer agentStatus = -1;

    /**
     * 开始时间
     */
    private String beginTime;

    /**
     * 结束时间
     */
    private String endTime;

}
