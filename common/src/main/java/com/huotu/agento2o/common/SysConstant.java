package com.huotu.agento2o.common;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

/**
 * Created by helloztt on 2016/5/9.
 */
@Component
public class SysConstant {
    public static String COOKIE_DOMAIN;

    @Autowired
    private void initConstant(Environment environment) {
        COOKIE_DOMAIN = environment.getProperty("cookie.domain", ".huobantest.com");
    }
}
