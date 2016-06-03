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

        String info = "货品名称：2014春季新款韩版修身时尚中长款女装外套，规格：红色,S，可用库存：<span style='color:red;'>41</span>（预警数：80）<br/>货品名称：2014春季新款韩版修身时尚中长款女装外套，规格：红色,M，可用库存：<span style='color:red;'>0</span>（预警数：70）<br/>货品名称：2014春季新款韩版修身时尚中长款女装连衣裙，规格：红色,S，可用库存：<span style='color:red;'>35</span>（预警数：90）<br/>货品名称：韩风休闲情侣毛衣外套，规格：白色,S，可用库存：<span style='color:red;'>0</span>（预警数：100）<br/>";
        Email email = Email.template("agent_product_notify")
                .from("service@sendcloud.im")
                .fromName("火图科技")
                .substitutionVars(Substitution.sub()
                .set("info",info)
                .set("name","张婷婷"))
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
