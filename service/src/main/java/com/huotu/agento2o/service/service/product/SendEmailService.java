package com.huotu.agento2o.service.service.product;

import org.springframework.scheduling.annotation.Async;

/**
 * Created by admin on 2016/5/16.
 */
public interface SendEmailService {

    /**
     * 发送邮件
     */
    public void sendEmail();

    @Async
    public void sayNumber(int i) throws InterruptedException;

}
