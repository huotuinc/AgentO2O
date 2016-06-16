/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.interceptor;

import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.config.annotataion.SystemControllerLog;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.config.annotation.SystemServiceLog;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.log.AgentLog;
import com.huotu.agento2o.service.service.log.AgentLogService;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.annotation.*;
import org.aspectj.lang.reflect.MethodSignature;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.lang.reflect.Method;
import java.util.Date;

/**
 * 切点类
 * Created by helloztt on 2016/6/16.
 */
@Aspect
@Component
public class ControllerLogAspect {
    @Autowired
    private AgentLogService logService;
    //本地异常日志记录对象
    private static final Log log = LogFactory.getLog(ControllerLogAspect.class);

    //Service层切点
    @Pointcut("@annotation(com.huotu.agento2o.service.config.annotation.SystemServiceLog)")
    public void serviceAspect() {
    }

    //Controller层切点
    @Pointcut("@annotation(com.huotu.agento2o.agent.config.annotataion.SystemControllerLog)")
    public void controllerAspect() {
    }

    /**
     * 异常通知 用于拦截controller层记录异常日志
     *
     * @param joinPoint
     * @param e
     */
    @AfterThrowing(pointcut = "controllerAspect()", throwing = "e")
    public void doAfterThrowing(JoinPoint joinPoint, Throwable e) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //读取登录用户
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        //获取请求ip
        String ip = request.getRemoteAddr();
        //获取用户请求方法的参数并序列化为JSON格式字符串
        String params = "";
        if (joinPoint.getArgs() != null && joinPoint.getArgs().length > 0) {
            for (int i = 0; i < joinPoint.getArgs().length; i++) {
                params += JSONObject.toJSONString(joinPoint.getArgs()[i]) + ";";
            }
        }
        try {
            //========控制台输出=========
            System.out.println("=====异常通知开始=====");
            log.error("异常代码:" + e.getClass().getName());
            log.error("异常信息:" + e.getMessage());
            log.error("异常方法:" + (joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()"));
            log.error("方法描述:" + getControllerMethodDescription(joinPoint));
            log.error("请求人:" + userDetails.getUsername());
            log.error("请求IP:" + ip);
            log.error("请求参数:" + params);
            AgentLog log = new AgentLog();
            log.setType("error");
            log.setMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()");
            log.setDescription(getControllerMethodDescription(joinPoint));
            log.setExceptionCode(e.getClass().getName());
            log.setParams(params);
            log.setCreateBy(userDetails.getUsername());
            log.setCreateTime(new Date());
            log.setIp(ip);
            logService.save(log);
            System.out.println("=====异常通知结束=====");
        } catch (Exception ex) {
            //记录本地异常日志
            log.error("==异常通知异常==");
            log.error("异常信息:{}", ex);
        }
    }


    /**
     * 后置通知 用于拦截Controller层记录用户的操作
     *
     * @param joinPoint 切点
     * @param result 返回结果
     */
    @AfterReturning(pointcut = "controllerAspect()", returning = "result")
    public void doAfterReturning(JoinPoint joinPoint, Object result) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        //读取登录用户
        UserDetails userDetails = (UserDetails) SecurityContextHolder.getContext()
                .getAuthentication()
                .getPrincipal();
        if (userDetails instanceof Author) {
            //请求的IP
            String ip = request.getRemoteAddr();
            try {
                String description = getControllerMethodDescription(joinPoint);
                if (StringUtil.isNotEmpty(description)) {
                    AgentLog log = new AgentLog();
                    log.setType("info");
                    log.setMethod(joinPoint.getTarget().getClass().getName() + "." + joinPoint.getSignature().getName() + "()");
                    log.setDescription(description);
                    log.setParams(null);
                    log.setCreateBy(userDetails.getUsername());
                    log.setCreateTime(new Date());
                    log.setIp(ip);
                    if(result instanceof ApiResult){
                        log.setResultCode(((ApiResult) result).getCode());
                        log.setResultMsg(((ApiResult) result).getMsg());
                    }
                    logService.save(log);
                }
            } catch (Exception e) {
                //记录本地异常日志
                log.error("异常信息:{}", e);
            }
        }
    }


    /**
     * 获取注解中对方法的描述信息 用于service层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getServiceMethodDescription(JoinPoint joinPoint)
            throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String description = method.getAnnotation(SystemServiceLog.class).value();
        return description;
    }

    /**
     * 获取注解中对方法的描述信息 用于Controller层注解
     *
     * @param joinPoint 切点
     * @return 方法描述
     * @throws Exception
     */
    public static String getControllerMethodDescription(JoinPoint joinPoint) throws Exception {
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        Method method = signature.getMethod();
        String description = method.getAnnotation(SystemControllerLog.class).value();
        return description;
    }


}
