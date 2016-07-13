package com.huotu.agento2o.service.entity;

import com.huotu.agento2o.service.common.AuthorityEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.author.Shop;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.*;
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
public class MallCustomer implements Author{

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

    @Column(name = "SC_TYPE")
    private Integer type;

    @Column(name = "SC_Score")
    private Double score;

    @Column(name = "SC_CityID")
    private Integer cityID;

    @OneToOne
    @PrimaryKeyJoinColumn(referencedColumnName = "Agent_ID")
    private Agent agent;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_AGENT.getCode()));
        return authorityList;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return agent != null && agent.isDisabled() == false;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return agent != null && agent.isDeleted() == false;
    }

    @Override
    public Integer getId() {
        return this.customerId;
    }

    @Override
    public Agent getAuthorAgent() {
        return agent;
    }

    @Override
    public Shop getAuthorShop() {
        return null;
    }

    @Override
    public Agent getParentAgent() {
        if(agent == null){
            return null;
        }
        return agent.getParentAgent();
    }

    @Override
    public MallCustomer getCustomer() {
        if(agent == null){
            return null;
        }
        return agent.getCustomer();
    }

    @Override
    public String getName() {
        if(agent == null){
            return null;
        }
        return agent.getName();
    }

    @Override
    public Class getType() {
        if(agent == null){
            return null;
        }
        return Agent.class;
    }
}
