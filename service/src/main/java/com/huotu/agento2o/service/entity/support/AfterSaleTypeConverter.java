package com.huotu.agento2o.service.entity.support;


import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.common.ienum.EnumHelper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by allan on 12/31/15.
 */
@Converter(autoApply = true)
public class AfterSaleTypeConverter implements AttributeConverter<AfterSaleEnum.AfterSaleType, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AfterSaleEnum.AfterSaleType afterSaleType) {
        return afterSaleType.getCode();
    }

    @Override
    public AfterSaleEnum.AfterSaleType convertToEntityAttribute(Integer integer) {
        if (integer == null) {
            return null;
        }
        return EnumHelper.getEnumType(AfterSaleEnum.AfterSaleType.class, integer);
    }
}
