package com.huotu.agento2o.service.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.ImportResource;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.jdbc.core.JdbcTemplate;
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

    @Bean
    public JdbcTemplate getTemplate() {
        return new JdbcTemplate(dataSource);
    }
}
