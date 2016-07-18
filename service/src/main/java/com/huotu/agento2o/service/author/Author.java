package com.huotu.agento2o.service.author;

import com.hot.datacenter.entity.customer.MallAgent;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.Serializable;

/**
 * Created by helloztt on 2016/5/9.
 */

public interface Author extends Serializable, UserDetails {

    Integer getId();

    /**
     * 如果为Agent类型，获取当前agent；否则返回null
     * 不取名为getAgent因为shop里面有agent的get方法
     * @return
     */
    MallAgent getAuthorAgent();

    /**
     * 如果为Shop类型，返回当前实体；否则返回null
     * @return
     */
    ShopAuthor getAuthorShop();

    MallAgent getParentAgent();

    Integer getCustomerId();

    String getName();

    /**
     * 返回角色类型
     *
     * @return
     */
    Class getAuthType();

}
