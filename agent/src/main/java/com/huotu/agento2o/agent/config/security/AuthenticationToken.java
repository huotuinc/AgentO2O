package com.huotu.agento2o.agent.config.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

/**
 * Created by helloztt on 2016/5/9.
 */
@Getter
@Setter
public class AuthenticationToken extends UsernamePasswordAuthenticationToken {
    /**
     * 登录角色
     */
    private int roleType;

    public AuthenticationToken(Object principal, Object credentials,int roleType) {
        super(principal, credentials);
        this.roleType = roleType;
    }

    public AuthenticationToken(Object principal, Object credentials, Collection<? extends GrantedAuthority> authorities,int roleType) {
        super(principal, credentials, authorities);
        this.roleType = roleType;
    }
}
