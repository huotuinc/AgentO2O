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

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by WenbinChen on 2015/9/10 10:59.
 */
@Controller
@ControllerAdvice
public class ExceptionManager {

    private static final Log log = LogFactory.getLog(ExceptionManager.class);

    /**
     * 所有异常统一处理
     */
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public ApiResult runtimeExceptionHandler(Exception ex) {
        log.error(ex);
        log.info("exception:" + ex.getMessage());
        return ApiResult.resultWith(ResultCodeEnum.SYSTEM_BAD_REQUEST, ex.getMessage(), null);
    }
}