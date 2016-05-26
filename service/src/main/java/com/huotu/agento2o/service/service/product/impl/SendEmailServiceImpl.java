package com.huotu.agento2o.service.service.product.impl;

import com.huotu.agento2o.service.entity.purchase.AgentProduct;
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
import java.util.List;

/**
 * send email
 * Created by elvis on 2016/5/16.
 */
@Service
public class SendEmailServiceImpl implements SendEmailService {

    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendEmail(List<AgentProduct> agentProducts) {
        MimeMessagePreparator preparator = new MimeMessagePreparator() {
            @Override
            public void prepare(MimeMessage mimeMessage) throws Exception {



                String nick=javax.mail.internet.MimeUtility.encodeText("火图代理商系统");
                String FromEmail = "15620711024@163.com";
                String sendFrom  = nick+" <"+FromEmail+">";
                mimeMessage.setFrom(new InternetAddress(sendFrom));

//                mimeMessage.setRecipient(Message.RecipientType.TO,
//                        new InternetAddress("15620711024@163.com"));
//
//                mimeMessage.setFrom(new InternetAddress("897587615@qq.com"));

                mimeMessage.setSubject("库存预警");


                //应指定编码方式，避免中文乱码
                MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "utf-8");


                StringBuilder builder = new StringBuilder();
                builder.append("<html><body><h3><a href=\"http://localhost:8080/index\">尊敬的用户您好！你的以下商品库存已经不足，请及时采购(点击登录采购）：</a></h3><br><table>");
                for (AgentProduct agentProduct : agentProducts) {
                    if (agentProduct.getProduct() == null || agentProduct.getAuthor().getEmail() == null) {
                        continue;
                    }
                    mimeMessage.setRecipient(Message.RecipientType.TO,
                            new InternetAddress(agentProduct.getAuthor().getEmail()));

                    builder.append("<tr><td><span>" +
                            agentProduct.getProduct().getName());
                    if (agentProduct.getProduct().getBn() != null && !"".equals(agentProduct.getProduct().getBn())) {
                        builder.append("--" + agentProduct.getProduct().getBn());
                    }
                    if (agentProduct.getProduct().getStandard() != null && !"".equals(agentProduct.getProduct().getStandard())) {
                        builder.append("--" + agentProduct.getProduct().getStandard());
                    }
                    builder.append("</span></td><td style=\"padding-left: 50px\">库存数量为<span style=\"color: red;\">"+ agentProduct.getStore() +"</span>已经低于预警数量<span style=\"color: red;\">" + agentProduct.getWarning() + "</span></td></tr>");
                }
                builder.append("</table></body></html>");
                helper.setText(builder.toString(), true);
                //helper.setText("今天吃饭了么");
            }
        };
        try {
            this.mailSender.send(preparator);
        } catch (MailException ex) {
            //对于连接超时 导致的发送失败，再重试2次
            try {
                this.mailSender.send(preparator);
            }catch (MailException oneExe){
                try {
                    this.mailSender.send(preparator);
                }catch (MailException twoExe){
                    System.err.println(ex.getMessage());
                }
            }
        }
    }


    @Async
    public void sayNumber(int i) throws InterruptedException {
        Thread.sleep(1000);
        System.out.println("Execute method asynchronously. "
                + Thread.currentThread().getName() + "   Say" + i);
    }

}
