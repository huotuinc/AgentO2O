/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.author;

import com.huotu.agento2o.service.entity.author.Author;

/**
 * Created by helloztt on 2016/5/9.
 */
public interface AuthorService {
    /**
     * 查找代理商或门店
     * @param requestAuthor
     * @return
     */
    Author findById(Author requestAuthor);

    /**
     * 校验密码是否正确
     */
    boolean checkPwd(Author requestAuthor,String password);
}
