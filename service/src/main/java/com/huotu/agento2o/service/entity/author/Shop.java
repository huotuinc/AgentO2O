package com.huotu.agento2o.service.entity.author;

import com.huotu.agento2o.service.common.AuthorityEnum;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

import javax.persistence.Entity;
import java.util.Arrays;
import java.util.Collection;


/**
 * Created by helloztt on 2016/5/9.
 */
@Entity
@Getter
@Setter
public class Shop extends Author{
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return Arrays.asList(new SimpleGrantedAuthority(AuthorityEnum.SHOP_ROOT.getCode()));
    }
}
