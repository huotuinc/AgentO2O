package com.huotu.agento2o.service.entity.author;

import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.level.ShopLevel;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.Date;


/**
 * Created by helloztt on 2016/5/9.
 */
@Entity
@Table(name = "Agt_Shop")
@Cacheable(value = false)
@Getter
@Setter
public class Shop{

    private static final long serialVersionUID = -5103560839055745939L;

    @Id
    @Column(name = "Shop_ID")
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
     * 登录密码(用来传递数据)
     */
    @Transient
//    @Column(name = "LoginPwd")
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
     * 所属代理商
     *
     * @return
     */
    @JoinColumn(name = "Agent_Id")
    @ManyToOne
    private Agent agent;

    /**
     * 小伙伴
     */
    @OneToOne
    @JoinColumn(name = "UB_UserID")
    private UserBaseInfo userBaseInfo;

    /**
     * 省code
     *
     * @return
     */
    @Column(name = "ProvinceCode")
    private String provinceCode;

    /**
     * 市code
     *
     * @return
     */
    @Column(name = "CityCode")
    private String cityCode;

    /**
     * 区code
     *
     * @return
     */
    @Column(name = "DistrictCode")
    private String districtCode;

    /**
     * 省市区
     *
     * @return
     */
    @Column(name = "Address_Area")
    private String addressArea;

    /**
     * 门店审核状态
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

    /**
     * 图标
     */
    @Column(name = "Logo")
    private String logo;

    /**
     * 门店等级
     */
    @JoinColumn(name = "Level_Id")
    @ManyToOne
    private ShopLevel shopLevel;

    /**
     * 工作日
     */
    @Column(name = "Workday")
    private String workday;

    /**
     * 营业开始时间
     */
    @Temporal(TemporalType.TIME)
    @Column(name = "OpenTime")
    private Date openTime;

    /**
     * 营业结束时间
     */
    @Temporal(TemporalType.TIME)
    @Column(name = "CloseTime")
    private Date closeTime;


    public boolean hasAccountInfo() {
        return !StringUtil.isEmptyStr(bankName) && !StringUtil.isEmptyStr(accountName) && !StringUtil.isEmptyStr(accountNo);
    }
}
