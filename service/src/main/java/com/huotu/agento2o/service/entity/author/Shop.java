package com.huotu.agento2o.service.entity.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.AuthorityEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
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
public class Shop extends Author{

    /**
     * 经度
     */
    @Column(name = "Lan")
    private double lan;

    /**
     * 纬度
     */
    @Column(name = "Lat")
    private double lat;

    /**
     * 客服电话
     */
    @Column(name = "Service_Tel")
    private String serveiceTel;

    /**
     * 售后电话
     */
    @Column(name = "AfterSal_Tel")
    private String afterSalTel;

    /**
     * 售后QQ
     */
    @Column(name = "AfterSal_QQ")
    private String afterSalQQ;



    @SuppressWarnings("Duplicates")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.AGENT_ROOT.getCode()));
//        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_SHOP.getCode()));
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_SHOPDATA.getCode()));
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_ORDER.getCode()));
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_PURCHASE.getCode()));
        return authorityList;
    }
}
