/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.common;

import org.springframework.security.authentication.AccountExpiredException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

/**
 * 异常处理
 * Created by allan on 1/28/16.
 */
@ControllerAdvice
public class ExceptionHandler {
    public static final String LOCKED_MSG = "该账户已被冻结";
    public static final String EXPIRED_MSG = "该账户已失效";
    public static final String BAD_CREDENTIALS_MSG = "用户名或者密码错误";

    /**
     * 处理spring security的异常
     */
    public static void securityException(HttpServletRequest request, AuthenticationException exception, boolean forwardToDestination) {
        String errorMsg;
        if (exception instanceof LockedException) {
            errorMsg = LOCKED_MSG;
        } else if (exception instanceof DisabledException) {
            errorMsg = EXPIRED_MSG;
        } else if(exception instanceof UserNameAndPasswordNullException){
            errorMsg = exception.getMessage();
        } else {
            errorMsg = BAD_CREDENTIALS_MSG;
        }
        if (forwardToDestination) {
            request.setAttribute("SPRING_SECURITY_LAST_EXCEPTION", errorMsg);
        } else {
            HttpSession session = request.getSession(false);
            if (session != null)
                request.getSession().setAttribute("SPRING_SECURITY_LAST_EXCEPTION", errorMsg);
        }
    }
}
