/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.config;

import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.config.InvoiceConfig;

import java.util.List;

/**
 * Created by AiWelv on 2016/5/20.
 */
public interface InvoiceService {

    List<InvoiceConfig> findByAuthor(Author author);

    Boolean updateInvoice(Author author, InvoiceConfig invoiceConfig);

    InvoiceConfig findDefaultByAuthor(Author author);

}
