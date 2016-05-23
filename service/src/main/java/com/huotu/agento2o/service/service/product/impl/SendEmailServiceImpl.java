package com.huotu.agento2o.service.service.product.impl;

import com.huotu.agento2o.service.service.product.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.mail.javamail.MimeMessagePreparator;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import javax.mail.Message;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;

/**
 * send email
 * Created by elvis on 2016/5/16.
 */
@Service
public class SendEmailServiceImpl implements SendEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail() {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {


                mimeMessage.setRecipient(Message.RecipientType.TO,
                        new InternetAddress("897587615@qq.com"));

                mimeMessage.setFrom(new InternetAddress("15620711024@163.com"));

                // use the true flag to indicate you need a multipart message
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true,"utf-8");
                // use the true flag to indicate the text included is HTML
                helper.setText("<html><body><h1>我说我今天不想吃饭</h1></body></html>", true);
            }
        };
        try {
            this.mailSender.send(preparator);
        }
        catch (MailException ex) {
// simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }


    @Async
    public void sayNumber(int i) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("Execute method asynchronously. "
                + Thread.currentThread().getName()+"   Say"+i);

    }



}
