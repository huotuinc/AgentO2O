/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.config;

import com.huotu.agento2o.common.util.Constant;
import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.io.UnsupportedEncodingException;

/**
 * Created by allan on 3/22/16.
 */
@Component
public class MallPasswordEncoder implements PasswordEncoder {
    public String encode(CharSequence rawPassword) {
        if (rawPassword == null) {
            return null;
        }
        try {
            return DigestUtils.md5Hex(rawPassword.toString().getBytes(Constant.ENCODING));
        } catch (UnsupportedEncodingException e) {
            return null;
        }
    }

    public boolean matches(CharSequence rawPassword, String encodedPassword) {
        if (rawPassword == null) {
            return false;
        }
        try {
            String rawEncodedPassword = DigestUtils.md5Hex(rawPassword.toString().getBytes(Constant.ENCODING));
            return rawEncodedPassword.equals(encodedPassword);
        } catch (UnsupportedEncodingException e) {
        }
        return false;
    }
}

