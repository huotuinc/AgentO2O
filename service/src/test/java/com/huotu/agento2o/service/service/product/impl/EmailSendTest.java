/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.product.impl;

import io.jstack.sendcloud4j.SendCloud;
import io.jstack.sendcloud4j.mail.Email;
import io.jstack.sendcloud4j.mail.Result;
import io.jstack.sendcloud4j.mail.Substitution;

/**
 * Created by helloztt on 2016/6/3.
 */
public class EmailSendTest {

    public static void main(String[] args)throws Exception {
        final String url = "http://api.sendcloud.net/apiv2/mail/send";
        final String apiUser = "helloztt_test_BFTyqh";
        final String apiKey = "PZN0aaIWL56ZYcrp";

        /*HttpClient httpclient = new DefaultHttpClient();
        HttpPost httpost = new HttpPost(url);

        List params = new ArrayList();
        // 不同于登录SendCloud站点的帐号，您需要登录后台创建发信子帐号，使用子帐号和密码才可以进行邮件的发送。
        params.add(new BasicNameValuePair("apiUser", apiUser));
        params.add(new BasicNameValuePair("apiKey", apiKey));
        params.add(new BasicNameValuePair("from", "service@sendcloud.im"));
        params.add(new BasicNameValuePair("fromName", ""));
        params.add(new BasicNameValuePair("to", "347871727@qq.com"));
        params.add(new BasicNameValuePair("subject", "来自SendCloud的第一封邮件！"));
//        params.add(new BasicNameValuePair("template_invoke_name", "test_template"));
        params.add(new BasicNameValuePair("html", "你太棒了！你已成功的从SendCloud发送了一封测试邮件，接下来快登录前台去完善账户信息吧！"));
        params.add(new BasicNameValuePair("resp_email_id", "true"));

        httpost.setEntity(new UrlEncodedFormEntity(params, "UTF-8"));
        // 请求
        HttpResponse response = httpclient.execute(httpost);
        // 处理响应
        if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) { // 正常返回
            // 读取xml文档
            String result = EntityUtils.toString(response.getEntity());
            System.out.println(result);
        } else {
            System.err.println("error");
        }
        httpost.releaseConnection();*/


        Email email = Email.template("test_template_active")
                .from("service@sendcloud.im")
                .fromName("火图科技")
                .substitutionVars(Substitution.sub()
                .set("name","helloztt"))
//                .html("hello ztt")          // or .plain()
                .subject("库存预警")
                .to("347871727@qq.com");
        SendCloud webapi = SendCloud.createWebApi(apiUser, apiKey);
        Result result = webapi.mail().send(email);
        System.out.println(result.isSuccess()); //API 请求是否成功
        System.out.println(result.getStatusCode());  //API 返回码
        System.out.println(result.getMessage());     //API 返回码的中文解释

    }

}
