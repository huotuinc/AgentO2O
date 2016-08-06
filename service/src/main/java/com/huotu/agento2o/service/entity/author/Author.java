package com.huotu.agento2o.service.entity.author;

import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.user.UserBaseInfo;
import lombok.Getter;
import lombok.Setter;
import org.springframework.security.core.userdetails.UserDetails;

import javax.persistence.*;
import java.io.Serializable;
import java.util.Date;

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
    Agent getAuthorAgent();

    /**
     * 如果为Shop类型，返回当前实体；否则返回null
     * @return
     */
    Shop getAuthorShop();

    Agent getParentAgent();

    MallCustomer getCustomer();

    String getName();

    Class getType();

    /**
     * 用于前端访问
     * @return
     */
    String getTypeName();

}
