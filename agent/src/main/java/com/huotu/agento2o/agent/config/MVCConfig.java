package com.huotu.agento2o.agent.config;

import com.huotu.agento2o.agent.config.resolver.AttributeArgumentResolver;
import com.huotu.agento2o.agent.config.resolver.AuthArgumentResolver;
import com.huotu.agento2o.agent.interceptor.CustomerInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.EnableAspectJAutoProxy;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.http.converter.HttpMessageConverter;
import org.springframework.http.converter.json.MappingJackson2HttpMessageConverter;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.transaction.annotation.EnableTransactionManagement;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.multipart.commons.CommonsMultipartResolver;
import org.springframework.web.servlet.config.annotation.*;
import org.thymeleaf.extras.springsecurity4.dialect.SpringSecurityDialect;
import org.thymeleaf.spring4.SpringTemplateEngine;
import org.thymeleaf.spring4.view.ThymeleafViewResolver;
import org.thymeleaf.templateresolver.ServletContextTemplateResolver;
import org.thymeleaf.templateresolver.TemplateResolver;

import java.util.Arrays;
import java.util.List;

/**
 * Created by helloztt on 2016/5/7.
 */
@Configuration
@EnableWebMvc
@EnableScheduling
@EnableTransactionManagement
@ComponentScan({
        "com.huotu.agento2o.agent",
        "com.huotu.agento2o.common"
})
public class MVCConfig extends WebMvcConfigurerAdapter {
    @Autowired
    private Environment env;
    @Autowired
    private CustomerInterceptor customerInterceptor;

    /**
     * for upload
     */
    @Bean
    public CommonsMultipartResolver multipartResolver() {
        CommonsMultipartResolver resolver = new CommonsMultipartResolver();
        resolver.setDefaultEncoding("UTF-8");
        return resolver;
    }

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(customerInterceptor).addPathPatterns("/huobanmall/**");
    }

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        super.addResourceHandlers(registry);
        registry.addResourceHandler("/resource/**/*", "image/**/*", "/**/*.html").addResourceLocations("/resource/", "/image/", "/");
    }

    @Override
    public void configureMessageConverters(List<HttpMessageConverter<?>> converters) {
        MappingJackson2HttpMessageConverter converter = new MappingJackson2HttpMessageConverter();
        converter.setSupportedMediaTypes(Arrays.asList(MediaType.APPLICATION_JSON));
        converters.add(converter);
    }

    @Override
    public void configureViewResolvers(ViewResolverRegistry registry) {
        super.configureViewResolvers(registry);
        registry.viewResolver(viewResolver());
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> argumentResolvers) {
        argumentResolvers.add(new AuthArgumentResolver());
        argumentResolvers.add(new AttributeArgumentResolver());
    }

    @Override
    public void addViewControllers(ViewControllerRegistry registry) {
    }

    /**
     * 国际化设置
     * @return
     */
    @Bean
    public ResourceBundleMessageSource messageSource(){
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setBasename("message/message");
        return messageSource;
    }

    public ThymeleafViewResolver viewResolver() {

        ServletContextTemplateResolver rootTemplateResolver = new ServletContextTemplateResolver();
        rootTemplateResolver.setPrefix("/");
        rootTemplateResolver.setSuffix(".html");
        rootTemplateResolver.setTemplateMode("HTML5");
        rootTemplateResolver.setCharacterEncoding("UTF-8");
        if (env.acceptsProfiles("!container")) {
            rootTemplateResolver.setCacheable(false);
        } else {
            rootTemplateResolver.setCacheable(true);
        }

        SpringTemplateEngine engine = new SpringTemplateEngine();
        engine.addDialect(new SpringSecurityDialect());
        engine.setTemplateResolver(rootTemplateResolver);

        ThymeleafViewResolver resolver = new ThymeleafViewResolver();
        resolver.setOrder(1);
        resolver.setCharacterEncoding("UTF-8");
        resolver.setTemplateEngine(engine);
        resolver.setContentType("text/html;charset=utf-8");
        return resolver;
    }

}
