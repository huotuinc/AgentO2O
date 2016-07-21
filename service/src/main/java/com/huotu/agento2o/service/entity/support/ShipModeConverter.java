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
import com.huotu.agento2o.service.common.ActEnum;
import com.huotu.agento2o.service.common.OrderEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by helloztt on 2016/7/21.
 */
@Converter(autoApply = true)
public class ShipModeConverter  implements AttributeConverter<OrderEnum.ShipMode, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderEnum.ShipMode attribute) {
        return attribute.getCode();
    }

    @Override
    public OrderEnum.ShipMode convertToEntityAttribute(Integer dbData) {
        if (dbData == null) {
            return null;
        }
        return EnumHelper.getEnumType(OrderEnum.ShipMode.class, dbData);
    }
}
