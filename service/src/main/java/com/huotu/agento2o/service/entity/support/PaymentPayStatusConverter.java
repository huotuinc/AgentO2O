/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.entity.support;

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.service.common.PaymentEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by helloztt on 2016/8/5.
 */
@Converter(autoApply = true)
public class PaymentPayStatusConverter implements AttributeConverter<PaymentEnum.PayStatus, String> {
    @Override
    public String convertToDatabaseColumn(PaymentEnum.PayStatus attribute) {
        return String.valueOf(attribute.getCode());
    }

    @Override
    public PaymentEnum.PayStatus convertToEntityAttribute(String dbData) {
        return EnumHelper.getEnumType(PaymentEnum.PayStatus.class, Integer.parseInt(dbData));
    }
}
