package com.huotu.agento2o.service.author;

import com.hot.datacenter.entity.customer.MallAgent;
import com.hot.datacenter.entity.customer.MallCustomer;
import com.huotu.agento2o.service.common.AuthorityEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * Created by allan on 3/22/16.
 */
public class CustomerAuthor extends MallCustomer implements Author {

    private static final long serialVersionUID = -2257929562869996527L;

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
        return getAgent() != null && !getAgent().isDisabled();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return getAgent() != null && !getAgent().isDeleted();
    }

    @Override
    public Integer getId() {
        return getCustomerId();
    }

    @Override
    public MallAgent getAuthorAgent() {
        return getAgent();
    }

    @Override
    public ShopAuthor getAuthorShop() {
        return null;
    }

    @Override
    public MallAgent getParentAgent() {
        if (getAgent() == null) {
            return null;
        }
        return getAgent().getParentAgent();
    }

    @Override
    public String getName() {
        if (getAgent() == null) {
            return null;
        }
        return getAgent().getName();
    }

    @Override
    public Class getAuthType() {
        if (getAgent() == null) {
            return null;
        }
        return MallAgent.class;
    }
}
