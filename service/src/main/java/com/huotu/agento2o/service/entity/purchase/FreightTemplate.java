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

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * 运费模板实体
 * Created by allan on 7/7/16.
 */
@Entity
@Table(name = "Mall_Freight_Template")
@Setter
@Getter
public class FreightTemplate {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Id")
    private Long id;
    /**
     * 模板名称
     */
    @Column(name = "Name")
    private String name;
    /**
     * 商户ID
     */
    @Column(name = "Customer_Id", updatable = false)
    private int customerId;
    /**
     * 商户类型
     * 0:伙伴商城
     * 1:供应商
     * 2:代理商
     */
    @Column(name = "Template_Type", updatable = false)
    private int freightTemplateType;
}
