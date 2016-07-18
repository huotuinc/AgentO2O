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

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.service.author.Author;
import com.huotu.agento2o.service.author.CustomerAuthor;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.author.Agent;
import org.springframework.core.MethodParameter;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.support.WebDataBinderFactory;
import org.springframework.web.context.request.NativeWebRequest;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.method.support.ModelAndViewContainer;

/**
 * Created by helloztt on 2016/5/10.
 */
public class AuthArgumentResolver implements HandlerMethodArgumentResolver {
    public boolean supportsParameter(MethodParameter parameter) {
        return parameter.getParameterAnnotation(AgtAuthenticationPrincipal.class) != null;
    }

    public Object resolveArgument(MethodParameter parameter, ModelAndViewContainer mavContainer, NativeWebRequest webRequest, WebDataBinderFactory binderFactory) throws Exception {
        AgtAuthenticationPrincipal authenticationPrincipal = parameter.getParameterAnnotation(AgtAuthenticationPrincipal.class);
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if(userDetails == null){
            throw new Exception("没有权限");
        }
        if (userDetails instanceof Author) {
            if (authenticationPrincipal.type() == ShopAuthor.class && userDetails instanceof ShopAuthor) {
                return userDetails;
            } else if (authenticationPrincipal.type() == CustomerAuthor.class && userDetails instanceof CustomerAuthor) {
                return userDetails;
            } else if (authenticationPrincipal.type() == Agent.class && userDetails instanceof CustomerAuthor) {
                Agent agent = ((CustomerAuthor) userDetails).getAuthorAgent();
                if(agent == null){
                    throw new Exception("没有权限");
                }
                return agent;
            } else if (authenticationPrincipal.type() == null || authenticationPrincipal.type() == Author.class) {
                return userDetails;
            }
        }
        throw new Exception("没有权限");
    }
}
