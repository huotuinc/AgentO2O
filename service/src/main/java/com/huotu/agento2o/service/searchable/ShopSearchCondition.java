package com.huotu.agento2o.service.searchable;

import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.author.Agent;
import lombok.Data;

/**
 * Created by zhangchao on 2016-05-17.
 */
@Data
public class ShopSearchCondition {
    /**
     * 平台过滤
     */
    private MallCustomer mallCustomer;

    /**
     * 门店查询条件
     */
    private String name;
    private String province;
    private String city;
    private String district;
    private int status = -1;
    private Agent parentAuthor;

    /**
     * list 平台门店管理列表
     * audit 平台门店审核列表
     */
    private String type;

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
