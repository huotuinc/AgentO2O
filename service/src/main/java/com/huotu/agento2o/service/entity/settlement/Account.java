/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.settlement;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.huotu.agento2o.common.util.Constant;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by helloztt on 2016/6/8.
 */
@Entity
@Table(name = "Agt_Account")
@Cacheable(value = false)
@Getter
@Setter
public class Account {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "Agent_Id")
    private Agent agent;

    @JsonIgnore
    @OneToOne
    @JoinColumn(name = "Shop_Id")
    private Shop shop;

    /**
     * 账户余额（待提货款）
     */
    @Column(name = "Balance", precision = 2)
    private double balance = 0.0;

    /**
     * 已提货款
     */
    @Column(name = "Withdrew", precision = 2)
    private double withdrew = 0.0;

    /**
     * 申请中货款
     */
    @Column(name = "ApplyingMoney", precision = 2)
    private double applyingMoney = 0.0;

    /**
     * 提现次数，默认为2次，在 Constant 中设置
     */
    @Column(name = "WithdrawCount")
    private int withdrawCount = Constant.WITHDRAWCOUNT;

    // TODO: 2016/7/13 将这些方法写在一个类里？
    public void setAuthor(Author author) {
        this.setAgent(author.getAuthorAgent());
        this.setShop(author.getAuthorShop());
    }

    public Integer getAuthorId(){
        if(this.getAgent() != null){
            return this.getAgent().getId();
        }else if(this.getShop() != null){
            return this.getShop().getId();
        }
        return null;
    }

    public String getAuthorName(){
        if(this.getAgent() != null){
            return this.getAgent().getName();
        }else if(this.getShop() != null){
            return this.getShop().getName();
        }
        return null;
    }

    public String getAuthorBankName(){
        if(this.getAgent() != null){
            return this.getAgent().getBankName();
        }else if(this.getShop() != null){
            return this.getAgent().getBankName();
        }
        return null;
    }
    public String getAuthorAccountName(){
        if(this.getAgent() != null){
            return this.getAgent().getAccountName();
        }else if(this.getShop() != null){
            return this.getShop().getAccountName();
        }
        return null;
    }
    public String getAuthorAccountNo(){
        if(this.getAgent() != null){
            return this.getAgent().getAccountNo();
        }else if(this.getShop() != null){
            return this.getShop().getAccountNo();
        }
        return null;
    }

    public MallCustomer getAuthorCustomer(){
        if(this.getAgent() != null){
            return this.getAgent().getCustomer();
        }else if(this.getShop() != null){
            return this.getShop().getCustomer();
        }
        return null;
    }
}
