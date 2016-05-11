/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.level;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by helloztt on 2016/5/11.
 */
@Entity
@Table(name = "Agt_Level")
@Cacheable(value = false)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public abstract class Level implements Serializable{
    private static final long serialVersionUID = -8261933601826901437L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Level_Id")
    private Integer levelId;

    /**
     * 等级名称
     */
    @Column(name = "Level_Name")
    private String levelName;

    /**
     * 等级
     */
    @Column(name = "Level")
    private Integer level;

    /**
     * 备注
     */
    @Column(name = "Comment")
    private String comment;




}
