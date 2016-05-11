package com.huotu.agento2o.agent.config;

import com.huotu.agento2o.agent.config.security.AuthenticationProvider;
import com.huotu.agento2o.agent.config.security.SecurityFailureHandler;
import com.huotu.agento2o.agent.config.security.filter.AuthenticationProcessingFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.builders.WebSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.web.authentication.SimpleUrlAuthenticationSuccessHandler;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Created by helloztt on 2016/5/7.
 */
@Configuration
@EnableWebSecurity

public class SecurityConfig extends WebSecurityConfigurerAdapter {
    public static final String LOGIN_PAGE = "/login";
    public static final String LOGIN_SUCCESS_URL = "/index";
    public static final String LOGOUT_SUCCESS_URL = "/";
    public static final String LOGIN_ERROR_URL = "/loginFailed";

    private static String[] STATIC_RESOURCE_PATH = {
            "/resource/**",
            "/loginFailed",
            "/huobanmall/**",
            "/agent/index/**"
    };

    @Autowired
    private AuthenticationManager authenticationManager;
    @Autowired
    private AuthenticationProcessingFilter authenticationProcessingFilter;

    @Override
    public void configure(WebSecurity web) throws Exception {
        web.ignoring().antMatchers(STATIC_RESOURCE_PATH);
    }

    @Bean
    public AuthenticationProcessingFilter authenticationProcessingFilter() throws Exception {
        AuthenticationProcessingFilter authenticationProcessingFilter = new AuthenticationProcessingFilter();
        authenticationProcessingFilter.setAuthenticationManager(authenticationManager);
        authenticationProcessingFilter.setAuthenticationSuccessHandler(new SimpleUrlAuthenticationSuccessHandler(LOGIN_SUCCESS_URL));
        authenticationProcessingFilter.setAuthenticationFailureHandler(new SecurityFailureHandler(LOGIN_ERROR_URL));
        return authenticationProcessingFilter;
    }

    @Bean
    public AuthenticationManager authenticationManager() {
        return new AuthenticationProvider();
    }

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .headers().frameOptions().sameOrigin()
                .and()
                .addFilterBefore(authenticationProcessingFilter, UsernamePasswordAuthenticationFilter.class)
                .authorizeRequests()
                .anyRequest().authenticated()
                .and()
                .csrf().disable()
                .formLogin()
                .loginPage(LOGIN_PAGE)
                .defaultSuccessUrl(LOGIN_SUCCESS_URL)
                .permitAll()
                .and()
                .logout()
                .logoutSuccessUrl(LOGOUT_SUCCESS_URL);
    }
}
