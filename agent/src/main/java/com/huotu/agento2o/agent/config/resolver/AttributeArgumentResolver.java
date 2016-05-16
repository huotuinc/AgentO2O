/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.config.resolver;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import org.apache.commons.beanutils.ConvertUtils;
import org.springframework.core.MethodParameter;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.context.request.RequestAttributes;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by liual on 2015-10-21.
 */

public class AttributeArgumentResolver implements HandlerMethodArgumentResolver {
    @Override
    public boolean supportsParameter(MethodParameter methodParameter) {
        return methodParameter.getParameterAnnotation(RequestAttribute.class) != null;
    }

    @Override
    public Object resolveArgument(MethodParameter methodParameter, ModelAndViewContainer modelAndViewContainer, NativeWebRequest nativeWebRequest, WebDataBinderFactory webDataBinderFactory) throws Exception {
        String attributeName = methodParameter.getParameterAnnotation(RequestAttribute.class).value();
        if (StringUtils.isEmpty(attributeName)) {
            attributeName = methodParameter.getParameterName();
        }
        // TODO: 2016/5/16 类型转换
        Class methodType = methodParameter.getParameterType();
        Object result = nativeWebRequest.getAttribute(attributeName, RequestAttributes.SCOPE_REQUEST);
        return ConvertUtils.convert(result, methodType);
    }
}
