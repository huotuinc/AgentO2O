package com.huotu.agento2o.service.model.purchase;

import com.huotu.agento2o.service.common.PurchaseEnum;
import lombok.Data;

/**
 * Created by wuxiongliu on 2016/5/19.
 */

@Data
public class ReturnOrderInfo {

    /**
     *  联系方式
     */
    private String mobile;

    /**
     *  配送方式
     */
    private PurchaseEnum.SendmentStatus sendmentStatus;

    /**
     *  代理商/门店备注
     */
    private String authorComment;
}
