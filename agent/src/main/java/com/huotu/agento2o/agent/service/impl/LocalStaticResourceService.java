/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.service.impl;

import com.huotu.agento2o.common.SysConstant;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.DependsOn;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;

/**
 * 本地测试的资源处理器
 * @author CJ
 */
@Service
@Profile("!container")
@DependsOn("sysConstant")
public class LocalStaticResourceService extends AbstractStaticResourceService{

    private static final Log log = LogFactory.getLog(LocalStaticResourceService.class);

    @Autowired
    public void setWebApplicationContext(WebApplicationContext context){
        File file = new File(context.getServletContext().getRealPath("/"));
        this.fileHome = file.toURI();
        String url = System.getProperty("user.dir");
        StringBuilder stringBuilder = new StringBuilder("http://localhost:8080");
        stringBuilder.append(context.getServletContext().getContextPath());
        try {
            this.huobanmallPrefix = new URI(SysConstant.HUOBANMALL_RESOURCE_HOST);
            this.uriPrefix = new URI(stringBuilder.toString());
        } catch (URISyntaxException e) {
            log.error("解析失败",e);
            throw new InternalError("解析"+stringBuilder.toString()+"失败");
        }
    }



}
