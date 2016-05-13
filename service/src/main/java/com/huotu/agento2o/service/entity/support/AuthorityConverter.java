package com.huotu.agento2o.service.entity.support;

import com.huotu.agento2o.common.ienum.EnumHelper;
import com.huotu.agento2o.service.common.AuthorityEnum;
import org.springframework.util.StringUtils;

import javax.persistence.AttributeConverter;
import javax.persistence.Converter;
import java.util.HashSet;
import java.util.Set;

/**
 * Created by allan on 3/24/16.
 */
@Converter(autoApply = true)
public class AuthorityConverter implements AttributeConverter<Set<AuthorityEnum>, String> {
    @Override
    public String convertToDatabaseColumn(Set<AuthorityEnum> attribute) {
        if (attribute == null) {
            return null;
        }
        String authoritiesStr = "";
        if (attribute.size() > 0) {
            for (AuthorityEnum authority : attribute) {
                authoritiesStr += authority.getCode() + ",";
            }

            return authoritiesStr.substring(0, authoritiesStr.length() - 1);
        }
        return "";
    }

    @Override
    public Set<AuthorityEnum> convertToEntityAttribute(String dbData) {
        Set<AuthorityEnum> authorities = new HashSet<>();
        if (StringUtils.isEmpty(dbData)) {
            return authorities;
        }
        String[] authorityArray = dbData.split(",");
        for (String auth : authorityArray) {
            authorities.add(EnumHelper.getEnumType(AuthorityEnum.class, auth));
        }
        return authorities;
    }
}
