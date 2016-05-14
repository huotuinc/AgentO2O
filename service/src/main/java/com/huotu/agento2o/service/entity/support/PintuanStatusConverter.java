package com.huotu.agento2o.service.entity.support;

import com.huotu.agento2o.service.common.ActEnum;
import com.huotu.agento2o.common.ienum.EnumHelper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by allan on 5/4/16.
 */
@Converter(autoApply = true)
public class PintuanStatusConverter implements AttributeConverter<ActEnum.OrderPintuanStatusOption, Integer> {

    @Override
    public Integer convertToDatabaseColumn(ActEnum.OrderPintuanStatusOption attribute) {
        return attribute.getCode();
    }

    @Override
    public ActEnum.OrderPintuanStatusOption convertToEntityAttribute(Integer dbData) {
        return EnumHelper.getEnumType(ActEnum.OrderPintuanStatusOption.class, dbData);
    }
}
