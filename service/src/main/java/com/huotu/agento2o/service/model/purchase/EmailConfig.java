/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.model.purchase;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * 发送邮件相关配置
 * Created by helloztt on 2016/6/3.
 */
@Getter
@Setter
public class EmailConfig {
    private String apiUser;
    private String apiKey;
    private String template;
    private String from;
    private String fromName;

    public EmailConfig(String apiUser, String apiKey, String template, String from, String fromName) {
        this.apiUser = apiUser;
        this.apiKey = apiKey;
        this.template = template;
        this.from = from;
        this.fromName = fromName;
    }
}
