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

import org.springframework.security.authentication.AccountStatusException;

/**
 * Created by helloztt on 2016/8/8.
 */
public class VerifyCodeErrorException extends AccountStatusException {
    public VerifyCodeErrorException(String msg){
        super(msg);
    }

    public VerifyCodeErrorException(String msg, Throwable t){
        super(msg, t);
    }
}
