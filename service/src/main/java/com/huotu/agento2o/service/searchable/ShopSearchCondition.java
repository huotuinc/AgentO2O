package com.huotu.agento2o.service.searchable;

import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Author;
import lombok.Data;

/**
 * Created by zhangchao on 2016-05-17.
 */
@Data
public class ShopSearchCondition {
    private String name;
    private String province;
    private String city;
    private String district;
    /**
     * 门店审核状态
     */
    private int status = -1;
    private Author parentAuthor;

    /**
     * 上级代理商查询条件
     */
    private String parent_name;
    private String parent_username;
    private int parent_agentLevel = -1;
    private String parent_province;
    private String parent_city;
    private String parent_district;
}
