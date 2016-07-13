package com.huotu.agento2o.service.entity.author;

import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.common.AuthorityEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
@Entity
@Table(name = "Agt_Agent")
@Cacheable(value = false)
@Getter
@Setter
public class Agent{

    private static final long serialVersionUID = 1298342488758535678L;

    @Id
    @Column(name = "Agent_ID")
    private Integer id;

    /**
     * 登录名(用来传递数据)
     */
    @Transient
    private String username;

    /**
     * 登录密码(用来传递数据)
     */
    @Transient
    private String password;
    /**
     * 平台方
     */
    @ManyToOne
    @JoinColumn(name = "Customer_Id")
    private MallCustomer customer;

    /**
     * 名称
     */
    @Column(name = "Name")
    private String name;

    /**
     * 联系人
     */
    @Column(name = "Contact")
    private String contact;

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
    @Column(name = "Address")
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
     *
     * @return
     */
    @JoinColumn(name = "Parent_Agent_Id")
    @ManyToOne
    private Agent parentAuthor;

    /**
     * 小伙伴
     */
    @OneToOne
    @JoinColumn(name = "UB_UserID")
    private UserBaseInfo userBaseInfo;

    /**
     * 省市区code
     *
     * @return
     */
    @Column(name = "Address_AreaCode")
    private String address_AreaCode;

    /**
     * 省市区
     *
     * @return
     */
    @Column(name = "Address_Area")
    private String address_Area;

    /**
     * 审核状态
     * 代理商添加时审核状态默认为 审核通过
     */
    @Column(name = "Status")
    private AgentStatusEnum status;

    /**
     * 备注
     *
     * @return
     */
    @Column(name = "Comment")
    private String comment;

    /**
     * 创建时间
     */
    @Column(name = "CreateTime")
    private Date createTime;


    /**
     * 开户银行名称
     */
    @Column(name = "BankName")
    private String bankName;

    /**
     * 账户名
     */
    @Column(name = "AccountName")
    private String accountName;

    /**
     * 银行账号
     */
    @Column(name = "AccountNo")
    private String accountNo;

    /**
     * 邮箱
     */
    @Column(name = "Email")
    private String email;

    /**
     * 代理商等级
     */
    @JoinColumn(name = "Level_Id")
    @ManyToOne
    private AgentLevel agentLevel;

    public boolean isAgent() {
        return true;
    }

    public boolean hasAccountInfo() {
        return !StringUtil.isEmptyStr(bankName) && !StringUtil.isEmptyStr(accountName) && !StringUtil.isEmptyStr(accountNo);
    }
}
