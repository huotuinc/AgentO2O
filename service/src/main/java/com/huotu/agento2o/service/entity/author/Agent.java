package com.huotu.agento2o.service.entity.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.AuthorityEnum;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Entity
@Getter
@Setter
public class Agent extends Author {

    /**
     * 门店等级
     */
    @JoinColumn(name = "Level_Id")
    @ManyToOne
    private AgentLevel agentLevel;

    @SuppressWarnings("Duplicates")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.AGENT_ROOT.getCode()));
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_SHOP.getCode()));
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_ORDER.getCode()));
        return authorityList;
    }
}
