package com.huotu.agento2o.service.author;

import com.hot.datacenter.entity.customer.AgentShop;
import com.hot.datacenter.entity.customer.MallAgent;
import com.huotu.agento2o.service.common.AuthorityEnum;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;


/**
 * Created by helloztt on 2016/5/9.
 */
public class ShopAuthor extends AgentShop implements Author {

    private static final long serialVersionUID = 4137485693570154686L;

    public boolean isAccountNonLocked() {
        return !isDisabled();
    }

    public boolean isEnabled() {
        return !isDisabled();
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }

    @SuppressWarnings("Duplicates")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        //增加角色
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_SHOP.getCode()));
        return authorityList;
    }

    @Override
    public MallAgent getAuthorAgent() {
        return null;
    }

    @Override
    public ShopAuthor getAuthorShop() {
        return this;
    }

    @Override
    public MallAgent getParentAgent() {
        return getAgent();
    }

    @Override
    public Class getAuthType() {
        return ShopAuthor.class;
    }
}
