package com.huotu.agento2o.service.entity.support;

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.common.ienum.OrderEnum;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by allan on 5/4/16.
 */
@Converter(autoApply = true)
public class OrderSourceTypeConverter implements AttributeConverter<OrderEnum.OrderSourceType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(OrderEnum.OrderSourceType attribute) {
        return attribute.getCode();
    }

    @Override
    public OrderEnum.OrderSourceType convertToEntityAttribute(Integer dbData) {
        return EnumHelper.getEnumType(OrderEnum.OrderSourceType.class, dbData);
    }
}
