package com.huotu.agento2o.service.entity.author;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;

/**
 * Created by helloztt on 2016/5/9.
 */
@Entity
@Table(name = "Agt_Agent")
@Cacheable(value = false)
@Inheritance(strategy = InheritanceType.SINGLE_TABLE)
@Getter
@Setter
public abstract class Author implements Serializable,UserDetails{
    private static final long serialVersionUID = -1578005701688952668L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Integer id;
    /**
     * 平台方
     */
    @ManyToOne
    @JoinColumn(name = "Customer_Id")
    private MallCustomer customer;

    @Column(name = "LoginName")
    private String username;

    @Column(name = "LoginPwd")
    private String password;

    /**
     * 是否冻结
     */
    @Column(name = "Disabled")
    private boolean isDisabled;

    /**
     * 是否删除
     */
    @Column(name = "Deleted")
    private boolean isDeleted;

    @Column(name = "Status")
    private AgentStatusEnum status;

    public boolean isAccountNonLocked() {
        return this.isDisabled == false;
    }

    public boolean isEnabled() {
        return this.isDeleted == false;
    }

    public boolean isAccountNonExpired() {
        return true;
    }

    public boolean isCredentialsNonExpired() {
        return true;
    }
}
