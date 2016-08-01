package com.huotu.agento2o.service.entity.user;

import com.huotu.agento2o.service.entity.MallCustomer;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by allan on 12/30/15.
 */
@Entity
@Table(name = "Hot_UserBaseInfo")
@Setter
@Getter
@Cacheable(value = false)
public class UserBaseInfo {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "UB_UserID")
    private Integer userId;
    @Column(name = "UB_UserLoginName")
    private String loginName;
    @Column(name = "UB_UserNickName")
    private String nickname;
    @Column(name = "UB_UserRealName")
    private String realName;
    @Column(name = "UB_UserMobile")
    private String mobile;
    @ManyToOne
    @JoinColumn(name = "UB_CustomerID")
    private MallCustomer mallCustomer;
    /**
     * 0代表普通会员，1代表小伙伴
     */
    @Column(name = "UB_UserType")
    private Integer type;
}
