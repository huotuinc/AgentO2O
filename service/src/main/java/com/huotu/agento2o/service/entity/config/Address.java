/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.entity.config;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import lombok.Data;

import javax.persistence.*;

/**
 * Created by WangJie on 2016/5/24.
 */
@Entity
@Table(name = "Agt_Address")
@Cacheable(false)
@Data
public class Address {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    /**
     * 收货人
     */
    @Column(name = "Receiver")
    private String receiver;

    /**
     * 联系电话
     */
    @Column(name = "Telephone")
    private String telephone;

    /**
     * 省
     */
    @Column(name = "Province")
    private String province;

    /**
     * 市
     */
    @Column(name = "City")
    private String city;

    /**
     * 区
     */
    @Column(name = "District")
    private String district;

    /**
     * 详细地址
     */
    @Column(name = "Address")
    private String address;

    /**
     * 备注
     */
    @Column(name = "Comment")
    private String comment;

    /**
     * 是否为默认地址
     */
    @Column(name = "Is_Default")
    private boolean isDefault;

    @ManyToOne
    @JoinColumn(name = "Agent_Id")
    @JsonIgnore
    private Agent agent;

    @ManyToOne
    @JoinColumn(name = "Shop_Id")
    @JsonIgnore
    private Shop shop;

    public void setAuthor(Author author){
        this.setAgent(author.getAuthorAgent());
        this.setShop(author.getAuthorShop());
    }
}
