package com.huotu.agento2o.service.searchable;

import com.hot.datacenter.search.OrderSearch;
import com.huotu.agento2o.service.author.Author;
import lombok.Data;

/**
 * Created by liual on 2015-09-29.
 */
@Data
public class CusOrderSearch extends OrderSearch {
    /**
     * 发货形式（代发or自发）
     */
    private int shipMode = -1;

    private Author author;

}
