/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.entity.config;

import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.common.InvoiceEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by AiWelv on 2016/5/20.
 */
@Entity
@Table(name = "Agt_InvoiceConfig")
@Cacheable(false)
@Getter
@Setter
public class InvoiceConfig {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @OneToOne
    @JoinColumn(name = "Agent_Id")
    private Agent agent;

    @OneToOne
    @JoinColumn(name = "Shop_Id")
    private ShopAuthor shop;

    /**
     * 发票抬头
     */
    @Column(name = "Tax_Title")
    private String taxTitle;

    /**
     * 发票内容
     */
    @Column(name = "Tax_Content")
    private String taxContent;

    /**
     * 纳税人识别码
     */
    @Column(name = "TaxPayerCode")
    private String taxpayerCode;

    /**
     * 开户银行名称
     */
    @Column(name = "Bank_Name")
    private String bankName;

    /**
     * 开户银行账号
     */
    @Column(name = "Account_No")
    private String accountNo;

    /**
     * 是否为默认发票 1
     */
    @Column(name = "DefaultType")
    private int defaultType;

    /**
     * 发票类型
     */
    @Column(name = "Tax_Type")
    private InvoiceEnum.InvoiceTypeStatus type;

    public void setAuthor(Author author){
        this.setAgent(author.getAuthorAgent());
        this.setShop(author.getAuthorShop());
    }
}