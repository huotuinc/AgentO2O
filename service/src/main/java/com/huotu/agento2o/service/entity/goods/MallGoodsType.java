/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.goods;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

/**
 * Created by helloztt on 2016/5/25.
 */
@Entity
@Table(name = "Mall_Goods_Type")
@Cacheable(value = false)
@Getter
@Setter
public class MallGoodsType {
    /**
     * 类型主键
     */
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "Type_Id")
    private Integer typeId;
    /**
     * 类型名称
     */
    private String name;
    /**
     * 是否是实体商品类型
     */
    @Column(name = "Is_Physical")
    private boolean isPhysical;
    /**
     * 标准商品类目ID
     */
    @Column(name = "Standard_Type_Id")
    private String standardTypeId;
    /**
     * 是否有子类目
     */
    @Column(name = "Is_Parent")
    private boolean isParent;
    /**
     * 父类目ID
     * parentStandardTypeId="0" 为顶级标准类目
     */
    @Column(name = "Parent_Standard_Type_Id")
    private String parentStandardTypeId;
    /**
     * 标准类目路径
     */
    private String path;
    /**
     * 是否有效
     */
    private boolean disabled;
    /**
     * 分销商ID
     * customerId=-1 为标准类目；其他为自定义类目
     */
    @Column(name = "Customer_Id")
    private int customerId;

    @Column(name = "T_Order")
    private int tOrder;

    @Transient
    private List<MallGoodsType> subGoodsType;

}
