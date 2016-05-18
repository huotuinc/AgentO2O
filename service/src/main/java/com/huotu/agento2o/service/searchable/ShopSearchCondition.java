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
    private int status = -1;
    private Author author;

}
