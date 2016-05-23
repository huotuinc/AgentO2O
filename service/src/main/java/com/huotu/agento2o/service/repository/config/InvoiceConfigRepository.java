/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.repository.config;

import com.huotu.agento2o.service.common.InvoiceEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.config.InvoiceConfig;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by Administrator on 2016/5/20.
 */
@Repository
public interface InvoiceConfigRepository extends JpaRepository<InvoiceConfig, Integer> {
    List<InvoiceConfig> findByAuthor(Author author);

    InvoiceConfig findByAuthorAndType(Author author, InvoiceEnum.InvoiceTypeStatus type);

    InvoiceConfig findByAuthorAndDefaultType(Author author, Integer defaultType);
}
