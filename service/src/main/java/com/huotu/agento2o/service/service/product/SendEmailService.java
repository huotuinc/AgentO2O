package com.huotu.agento2o.service.service.product;

import com.huotu.agento2o.service.entity.purchase.AgentProduct;
import org.springframework.scheduling.annotation.Async;

import java.util.List;

/**
 * Created by admin on 2016/5/16.
 */
public interface SendEmailService {

    /**
     * 发送邮件
     */
//    @Async
//    public void sendEmail(List<AgentProduct> agentProducts);

//    @Async
    public void sendCloudEmail(List<AgentProduct> agentProducts,String emailStr) throws Exception;


    /**
     * 异步测试类
     * @param i
     * @throws InterruptedException
     */
//    @Async
//    public void sayNumber(int i) throws InterruptedException;

}
