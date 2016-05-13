package com.huotu.agento2o.service.entity.marketing;


import com.huotu.agento2o.common.ienum.ActEnum;
import com.huotu.agento2o.service.entity.order.MallOrder;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

/**
 * Created by allan on 5/4/16.
 */
@Entity
@Table(name = "Mall_Pintuan_Promoter")
@Getter
@Setter
@Cacheable(false)
public class MallPintuan {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "PromoterId")
    private Integer id;
    @Column(name = "MemberId")
    private int memberId;
    @OneToOne
    @JoinColumn(name = "OrderId")
    private MallOrder order;
    @Column(name = "Status")
    private ActEnum.OrderPintuanStatusOption orderPintuanStatusOption;
    @Column(name = "CustomerId")
    private int customerId;
}
