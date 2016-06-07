package com.huotu.agento2o.service.entity.author;

import com.huotu.agento2o.service.common.AuthorityEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Column;
import javax.persistence.Entity;
import java.util.ArrayList;
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
    private Double lan;

    /**
     * 纬度
     */
    @Column(name = "Lat")
    private Double lat;

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

    /**
     * 审核备注
     */
    @Column(name = "Audit_Comment")
    private String auditComment;


    @SuppressWarnings("Duplicates")
    public Collection<? extends GrantedAuthority> getAuthorities() {
        List<GrantedAuthority> authorityList = new ArrayList<>();
        //增加角色
        authorityList.add(new SimpleGrantedAuthority(AuthorityEnum.ROLE_SHOP.getCode()));
        return authorityList;
    }
}
