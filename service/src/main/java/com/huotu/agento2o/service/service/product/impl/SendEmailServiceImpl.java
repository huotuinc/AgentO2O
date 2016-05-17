package com.huotu.agento2o.service.service.product.impl;

import com.huotu.agento2o.service.service.product.SendEmailService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessagePreparator;
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
        MimeMessagePreparator preparator = mimeMessage -> {
            mimeMessage.setRecipient(Message.RecipientType.TO,
                    new InternetAddress("897587615@qq.com"));
            mimeMessage.setFrom(new InternetAddress("15620711024@163.com"));
            mimeMessage.setText("hh");
        };
        try {
            this.mailSender.send(preparator);
        }
        catch (MailException ex) {
// simply log it and go on...
            System.err.println(ex.getMessage());
        }
    }
}
