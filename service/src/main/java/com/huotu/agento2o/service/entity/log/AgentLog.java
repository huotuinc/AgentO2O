/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.log;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;

/**
 * Created by helloztt on 2016/6/16.
 */
@Entity
@Table(name = "Agt_Log")
@Getter
@Setter
@Cacheable(value = false)
public class AgentLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    /**
     * 日志类型：into;error
     */
    @Column(name = "Type")
    private String type;

    /**
     * 访问方法
     */
    @Column(name = "Method")
    private String method;

    /**
     * 方法描述
     */
    @Column(name = "Description")
    private String description;

    /**
     * 异常代码(暂时不用)
     */
    @Column(name = "Exception_Code")
    private String exceptionCode;

    /**
     * 异常信息（暂时不用）
     */
    @Column(name = "Exception_Detail")
    private String exceptionDetail;

    /**
     * 请求参数
     */
    @Column(name = "Params")
    private String params;

    /**
     * 请求人
     */
    @Column(name = "CreateBy")
    private String createBy;

    /**
     * 请求IP
     */
    @Column(name = "Request_Ip")
    private String ip;

    /**
     * 请求时间
     */
    @Column(name = "CreateTime")
    private Date createTime;

    /**
     * 返回结果
     */
    @Column(name = "Result_Code")
    private Integer resultCode;

    /**
     * 返回信息
     */
    @Column(name = "Result_Msg")
    private String resultMsg;
}
