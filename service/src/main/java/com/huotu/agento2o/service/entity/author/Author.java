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

    /**
     * 登录名
     */
    @Column(name = "LoginName")
    private String username;
    /**
     * 登录密码
     */
    @Column(name = "LoginPwd")
    private String password;

    /**
     * 名称
     */
    @Column(name = "Name")
    private String name;

    /**
     * 联系人
     */
    @Column(name = "Contact")
    private String Contact;

    /**
     * 手机号码
     */
    @Column(name = "Mobile")
    private String mobile;

    /**
     * 电话号码
     */
    @Column(name = "Telephone")
    private String telephone;

    /**
     * 地址
     */
    @Column(name = "address")
    private String address;

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

    /**
     * 上级代理商
     * @return
     */
    @JoinColumn(name = "Parent_Agent_Id")
    @ManyToOne
    private Agent parentAuthor;

    // TODO: 2016/5/11 小伙伴信息

    /**
     * 省
     * @return
     */
    @Column(name = "Province")
    private String province;

    /**
     * 市
     * @return
     */
    @Column(name = "City")
    private String city;

    /**
     * 门店审核状态
     * 代理商添加时审核状态默认为 审核通过
     */
    @Column(name = "Status")
    private AgentStatusEnum status;

    /**
     * 区
     * @return
     */
    @Column(name = "District")
    private String district;

    /**
     * 备注
     * @return
     */
    @Column(name = "Comment")
    private String comment;


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
