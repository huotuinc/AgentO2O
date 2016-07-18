package com.huotu.agento2o.service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * Created by helloztt on 2016/5/6.
 */
@Configuration
@EnableTransactionManagement
@ComponentScan(basePackages = "com.huotu.agento2o.service")
@EnableJpaRepositories(
        basePackages = "com.huotu.agento2o.service.repository",
        entityManagerFactoryRef = "entityManagerFactory",
        transactionManagerRef = "transactionManager"
)
public class ServiceConfig {
    @Value("${mail.apiUser}")
    private String apiUser;

    @Value("${mail.apiKey}")
    private String apiKey;

    @Value("${mail.template}")
    private String template;

    @Value("${mail.from}")
    private String from;

    @Value("${mail.fromName}")
    private String fromName;

}
