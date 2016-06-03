package com.huotu.agento2o.service.config;

import com.huotu.agento2o.service.model.purchase.EmailConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.transaction.annotation.EnableTransactionManagement;

import javax.annotation.Resource;
import javax.sql.DataSource;

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
@ImportResource({"classpath:hbm_config_prod.xml", "classpath:hbm_config_test.xml"})
public class ServiceConfig {
    @Resource(name = "dataSource")
    private DataSource dataSource;

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

    @Bean
    public JdbcTemplate getTemplate() {
        return new JdbcTemplate(dataSource);
    }

    @Bean
    public EmailConfig emailConfig() {
        return new EmailConfig(apiUser, apiKey,template,from,fromName);
    }

    @Bean
    public JavaMailSenderImpl getJavaMailSender() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost("smtp.163.com");
        javaMailSender.setUsername("15620711024");
        javaMailSender.setPassword("liu8975");


        return javaMailSender;
    }
}
