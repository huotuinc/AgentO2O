/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.common;

import com.gargoylesoftware.htmlunit.WebClient;
import org.junit.After;
import org.junit.Before;
import org.mockito.MockitoAnnotations;
import org.openqa.selenium.WebDriver;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.orm.jpa.JpaTransactionManager;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.FilterChainProxy;
import org.springframework.security.web.context.HttpSessionSecurityContextRepository;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.htmlunit.MockMvcWebClientBuilder;
import org.springframework.test.web.servlet.htmlunit.webdriver.MockMvcHtmlUnitDriverBuilder;
import org.springframework.web.context.WebApplicationContext;

import javax.annotation.Resource;
import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.PersistenceContext;
import javax.servlet.ServletContext;
import javax.servlet.http.HttpSession;

import static org.springframework.test.web.servlet.setup.MockMvcBuilders.webAppContextSetup;

/**
 * Created by liual on 2015-11-25.
 */
public class SpringWebTest {
    /**
     * 自动注入应用程序上下文
     **/
    @Autowired
    protected WebApplicationContext context;
    /**
     * 自动注入servlet上下文
     **/
    @Autowired
    protected ServletContext servletContext;
    /**
     * 选配 只有在SecurityConfig起作用的情况下
     **/
    @SuppressWarnings("SpringJavaAutowiringInspection")
    @Autowired(required = false)
    private FilterChainProxy springSecurityFilter;

    /**
     * mock请求
     **/
    @Autowired
    protected MockHttpServletRequest request;

    /**
     * mockMvc等待初始化
     **/
    protected MockMvc mockMvc;
    protected WebClient webClient;
    protected WebDriver webDriver;

    @PersistenceContext(unitName = "entity")
    protected EntityManager entityManager;

    @Resource(name = "entityManagerFactory")
    protected EntityManagerFactory entityManagerFactory;

    @Resource(name = "transactionManager")
    protected JpaTransactionManager transactionManager;

    protected void createMockMVC() {
        MockitoAnnotations.initMocks(this);
        if (springSecurityFilter != null)
            mockMvc = webAppContextSetup(context)
                    .addFilters(springSecurityFilter)
                    .build();
        else
            mockMvc = webAppContextSetup(context)
                    .build();
    }
    @Before
    public void initTest(){
        //初始化mockMvc
        this.createMockMVC();
        this.webClient = MockMvcWebClientBuilder
                .mockMvcSetup(this.mockMvc)
                .build();
        this.webDriver = MockMvcHtmlUnitDriverBuilder
                .mockMvcSetup(this.mockMvc)
                .build();
    }

    @After
    public void afterTest() {
        if (webDriver != null) {
            webDriver.close();
        }
    }

    /**
     * 保存登陆过以后的信息
     **/
    protected void saveAuthedSession(HttpSession session) {
        SecurityContext securityContext = (SecurityContext) session.getAttribute(HttpSessionSecurityContextRepository.SPRING_SECURITY_CONTEXT_KEY);

        if (securityContext == null)
            throw new IllegalStateException("尚未登录");

        request.setSession(session);

        // context 不为空 表示成功登陆
        SecurityContextHolder.setContext(securityContext);
    }

}
