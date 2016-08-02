/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.controller.common;

import com.alibaba.fastjson.JSONObject;
import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.service.StaticResourceService;
import com.huotu.agento2o.common.SysConstant;
import com.huotu.agento2o.common.httputil.HttpClientUtil;
import com.huotu.agento2o.common.httputil.HttpResult;
import com.huotu.agento2o.service.entity.author.Author;
import org.apache.http.HttpStatus;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import sun.misc.BASE64Encoder;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.net.URI;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by helloztt on 2016/5/18.
 */
@Controller
public class UploadController {
    @Autowired
    private StaticResourceService resourceService;



    /**
     * 上传商品相关图片到 huobanmall
     * 宽度和高度一致
     *
     * @param author
     * @param files
     * @return
     */
    @RequestMapping(value = "/uploadImage", method = RequestMethod.POST)
    @ResponseBody
    public Map<Object, Object> upLoadImage(
            @AgtAuthenticationPrincipal Author author,
            @RequestParam(value = "btnFile", required = false) MultipartFile files) {
        int result = 0;
        Map<Object, Object> responseData = new HashMap<Object, Object>();
        try {
            String fileName = files.getOriginalFilename();
            String prefix = fileName.substring(fileName.lastIndexOf("."));
            BufferedImage image = ImageIO.read(files.getInputStream());
            BASE64Encoder encoder = new BASE64Encoder();
            String imgStr = encoder.encode(files.getBytes());
            boolean isSave = false;
            if (image != null) {
                int width = image.getWidth();
                int height = image.getHeight();
                Map<String, Object> map = new TreeMap<>();
                map.put("customid",author.getId());
                map.put("base64Image",imgStr);
                map.put("size",width + "x" + height);
                map.put("extenName",prefix);

                HttpResult httpResult = HttpClientUtil.getInstance().post(SysConstant.HUOBANMALL_PUSH_URL + "/gallery/uploadPhoto", map);
                if (httpResult.getHttpStatus() == HttpStatus.SC_OK) {
                    JSONObject obj = JSONObject.parseObject(httpResult.getHttpContent());
                    if(obj.getIntValue("code") == 200){
                        String fileUri = obj.getString("data");
                        URI uri = resourceService.getResource(StaticResourceService.huobanmallMode,fileUri);
                        responseData.put("fileUrl", uri);
                        responseData.put("fileUri", fileUri);
                        responseData.put("msg", "上传成功！");
                        responseData.put("code", 200);
                        result = 1;
                        isSave = true;
                    }
                } else {
                    result = 0;
                    isSave = false;
                }
            }
            if (!isSave) {
                responseData.put("code", 500);
                responseData.put("msg", "请上传正方形文件");
            }
        } catch (Exception e) {
            responseData.put("msg", e.getMessage());
        }
        responseData.put("result", result);

        return responseData;
    }
}
