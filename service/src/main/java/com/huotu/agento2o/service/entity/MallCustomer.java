package com.huotu.agento2o.service.entity;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by allan on 3/22/16.
 */
@Entity
@Table(name = "Swt_CustomerManage")
@Setter
@Getter
@Cacheable(false)
public class MallCustomer {
    private static final long serialVersionUID = 5043033208318805044L;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "SC_UserID")
    private Integer customerId;
    @Column(name = "SC_UserLoginName")
    private String username;
    @Column(name = "SC_UserNickName")
    private String nickName;
    @Column(name = "SC_UserLoginPassword")
    private String password;
//    @OneToOne
//    @PrimaryKeyJoinColumn(referencedColumnName = "Supplier_Id")
//    private MallSupplier supplier;
}
