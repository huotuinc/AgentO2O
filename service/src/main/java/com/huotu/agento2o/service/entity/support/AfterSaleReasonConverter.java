package com.huotu.agento2o.service.entity.support;

import com.huotu.agento2o.common.ienum.AfterSaleEnum;
import com.huotu.agento2o.common.ienum.EnumHelper;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;

/**
 * Created by allan on 12/31/15.
 */
@Converter(autoApply = true)
public class AfterSaleReasonConverter implements AttributeConverter<AfterSaleEnum.AfterSalesReason, Integer> {
    @Override
    public Integer convertToDatabaseColumn(AfterSaleEnum.AfterSalesReason afterSalesReason) {
        return afterSalesReason.getCode();
    }

    @Override
    public AfterSaleEnum.AfterSalesReason convertToEntityAttribute(Integer integer) {
        if (integer == null) {
            return null;
        }
        return EnumHelper.getEnumType(AfterSaleEnum.AfterSalesReason.class, integer);
    }
}
