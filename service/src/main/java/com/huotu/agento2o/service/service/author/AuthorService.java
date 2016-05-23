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

import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Author;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
public interface AuthorService {
    /**
     * 根据ID查找 代理商
     * @param id
     * @return
     */
    Author findById(Integer id);

    /**
     * 根据平台方ID 查找代理商列表
     */
    List<Author> findByCustomerId(Integer customerId);
    /**
     * 查找下级代理商/门店
     */
    List<Author> findByParentAgentId(Agent agent);

    /**
     * 新建代理商
     */
    Author addAuthor(Author author);

    /**
     * 修改密码
     */
    boolean updatePwd(Integer id,String password);
    /**
     * 校验密码是否正确
     */
    boolean checkPwd(Integer id,String password);
}
