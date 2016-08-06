package com.huotu.agento2o.service.entity;

import com.huotu.agento2o.service.common.AuthorityEnum;
import com.huotu.agento2o.service.common.CustomerTypeEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by allan on 3/22/16.
 */
@Entity
@Table(name = "Swt_CustomerManage")
@Setter
@Getter
@Cacheable(false)
public class MallCustomer implements Author {

    private static final long serialVersionUID = 5043033208318805044L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SC_UserID")
    private Integer customerId;

    @Column(name = "SC_UserLoginName")
    private String username;

    @Column(name = "SC_UserNickName")
    private String nickName;

    @Column(name = "SC_UserLoginPassword")
    private String password;

    @Column(name = "SC_IndustryType")
    private Integer industryType;

    @Column(name = "SC_UserActivate")
    private Integer userActivate;

    @Column(name = "SC_RoleID")
    private Integer roleID;

    @Column(name = "SC_BelongManagerID")
    private Integer belongManagerID;

    @Column(name = "SC_Email")
    private String email;

    @Column(name = "SC_IsOld")
    private Integer isOld;

    @Column(name = "SC_DeveloperUrl")
    private String developerUrl;

    @Column(name = "SC_developerToken")
    private String developerToken;

    @Column(name = "SC_TYPE")
    private Integer scType;

    @Column(name = "SC_Score")
    private Double score;

    @Column(name = "SC_CityID")
    private Integer cityID;

    @Column(name = "SC_CustomerType")
    private CustomerTypeEnum customerType;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(referencedColumnName = "Agent_ID")
    private Agent agent;

    @OneToOne(cascade = CascadeType.ALL)
    @PrimaryKeyJoinColumn(referencedColumnName = "Shop_Id")
    private Shop shop;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        //当前账号为代理商
        if (customerType == CustomerTypeEnum.AGENT) {
            authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_AGENT.getCode()));
        } else if (customerType == CustomerTypeEnum.AGENT_SHOP) {
            authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_SHOP.getCode()));
        }
        return authorityList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        if (this.getCustomerType() == CustomerTypeEnum.AGENT) {
            return agent != null && !agent.isDisabled();
        } else if (this.getCustomerType() == CustomerTypeEnum.AGENT_SHOP) {
            return shop != null && !shop.isDisabled();
        }
        return false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        if (this.getCustomerType() == CustomerTypeEnum.AGENT) {
            return agent != null && !agent.isDeleted();
        } else if (this.getCustomerType() == CustomerTypeEnum.AGENT_SHOP) {
            return shop != null && !shop.isDeleted();
        }
        return false;
    }

    @Override
    public Integer getId() {
        return this.getCustomerId();
    }

    @Override
    public Agent getAuthorAgent() {
        if (this.getCustomerType() == CustomerTypeEnum.AGENT) {
            return this.agent;
        } else {
            return null;
        }
    }

    @Override
    public Shop getAuthorShop() {
        if (this.getCustomerType() == CustomerTypeEnum.AGENT_SHOP) {
            return this.shop;
        } else {
            return null;
        }
    }

    public Agent getParentAgent() {
        if (getType() == Agent.class) {
            return this.agent.getParentAgent();
        } else if (getType() == Shop.class) {
            return this.shop.getAgent();
        }
        return null;
    }

    public MallCustomer getCustomer() {
        if (getType() == Agent.class) {
            return this.agent.getCustomer();
        } else if (getType() == Shop.class) {
            return this.shop.getCustomer();
        }
        return null;
    }

    public String getName() {
        if (getType() == Agent.class) {
            return this.agent.getName();
        } else if (getType() == Shop.class) {
            return this.shop.getName();
        }
        return null;
    }

    public Class getType() {
        if (this.getCustomerType() == CustomerTypeEnum.AGENT) {
            return Agent.class;
        } else if (this.getCustomerType() == CustomerTypeEnum.AGENT_SHOP) {
            return Shop.class;
        }
        return null;
    }

    @Override
    public String getTypeName() {
        if (this.getCustomerType() == CustomerTypeEnum.AGENT) {
            return "Agent";
        } else if (this.getCustomerType() == CustomerTypeEnum.AGENT_SHOP) {
            return "Shop";
        }
        return null;
    }
}
