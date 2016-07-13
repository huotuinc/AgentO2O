/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.common.httputil;

import com.huotu.agento2o.common.util.StringUtil;
import org.apache.http.HttpEntity;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.util.EntityUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by liual on 2015-11-11.
 */
public class HttpClientUtil {
    private CloseableHttpClient httpClient = null;

    private static HttpClientUtil httpClientUtil = new HttpClientUtil();

    private HttpClientUtil() {
    }

    public static HttpClientUtil getInstance() {
        return httpClientUtil;
    }

    private void initHttpClient() {
        httpClient = HttpClients.createDefault();
    }

    public HttpResult post(String url, Map<String, Object> requestMap) {
        initHttpClient();
        String msg = null;
        List<NameValuePair> nameValuePairs = new ArrayList<>();
        CloseableHttpResponse response = null;
        try {
            requestMap.forEach((key, value) -> {
                if (value != null) {
                    nameValuePairs.add(new BasicNameValuePair(key, String.valueOf(value)));
                }
            });

//            UrlEncodedFormEntity encodedFormEntity = new UrlEncodedFormEntity(nameValuePairs, StringUtil.UTF8);

            HttpPost httpPost = new HttpPost(url);
            HttpEntity httpEntity = new UrlEncodedFormEntity(nameValuePairs, StringUtil.UTF8);

            httpPost.setEntity(httpEntity);
            response = httpClient.execute(httpPost);
            HttpResult httpResult = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            EntityUtils.consume(response.getEntity());
            return httpResult;
        } catch (IOException e) {
            msg = e.getMessage();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
            }
        }
        return new HttpResult(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }

    public HttpResult get(String url, Map requestMap) {
        initHttpClient();
        String msg = null;
        CloseableHttpResponse response = null;
        try {
            //urlEncoding
            StringBuilder finalUrl = new StringBuilder(url);
            Iterator iterator = requestMap.entrySet().iterator();
            int index = 0;
            while (iterator.hasNext()) {
                Map.Entry entry = (Map.Entry) iterator.next();
                if (entry.getValue() != null) {
                    if (index == 0) {
                        finalUrl.append("?").append(entry.getKey()).append("=").append(entry.getValue());
                    } else {
                        finalUrl.append("&").append(entry.getKey()).append("=").append(URLEncoder.encode(String.valueOf(entry.getValue()), StringUtil.UTF8));
                    }
                    index++;
                }
            }
            HttpGet httpGet = new HttpGet(finalUrl.toString());
            response = httpClient.execute(httpGet);
            HttpResult httpResult = new HttpResult(response.getStatusLine().getStatusCode(), EntityUtils.toString(response.getEntity()));
            EntityUtils.consume(response.getEntity());

            return httpResult;
        } catch (IOException e) {
            msg = e.getMessage();
        } finally {
            try {
                if (response != null) {
                    response.close();
                }
                if (httpClient != null) {
                    httpClient.close();
                }
            } catch (IOException e) {
            }
        }

        return new HttpResult(HttpStatus.SC_INTERNAL_SERVER_ERROR, msg);
    }
}
