/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.config.impl;

import com.huotu.agento2o.service.common.InvoiceEnum;
import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.config.InvoiceConfig;
import com.huotu.agento2o.service.repository.config.InvoiceConfigRepository;
import com.huotu.agento2o.service.service.config.InvoiceService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by AiWelv on 2016/5/20.
 */
@Service
public class InvoiceServiceImpl implements InvoiceService {
    @Autowired
    private InvoiceConfigRepository invoiceConfigRepository;

    @Override
    public List<InvoiceConfig> findByAuthor(Author author) {
        return invoiceConfigRepository.findByAuthor(author);
    }

    @Override
    @Transactional(value = "transactionManager")
    public Boolean updateInvoice(Author author, InvoiceConfig invoiceConfig) {

        if (invoiceConfig == null || invoiceConfig.getType() == null) {
            return false;
        }
        InvoiceConfig oldInvoiceConfig = invoiceConfigRepository.findByAuthorAndType(author, invoiceConfig.getType());
        InvoiceConfig defaultInvoiceConfig = invoiceConfigRepository.findByAuthorAndDefaultType(author, 1);
        if (oldInvoiceConfig == null){
            oldInvoiceConfig = new InvoiceConfig();
            oldInvoiceConfig.setAuthor(author);
            oldInvoiceConfig.setType(invoiceConfig.getType());
        }
        oldInvoiceConfig.setTaxTitle(invoiceConfig.getTaxTitle());
        oldInvoiceConfig.setTaxContent(invoiceConfig.getTaxContent());
        if (invoiceConfig.getType() == InvoiceEnum.InvoiceTypeStatus.TAXINVOICE){
            oldInvoiceConfig.setTaxpayerCode(invoiceConfig.getTaxpayerCode());
            oldInvoiceConfig.setBankName(invoiceConfig.getBankName());
            oldInvoiceConfig.setAccountNo(invoiceConfig.getAccountNo());
        }

        if (defaultInvoiceConfig != null){
            defaultInvoiceConfig.setDefaultType(0);
            invoiceConfigRepository.save(defaultInvoiceConfig);
        }
        oldInvoiceConfig.setDefaultType(1);
        invoiceConfigRepository.save(oldInvoiceConfig);
        return true;
    }

    @Override
    public InvoiceConfig findDefaultByAuthor(Author author) {
        return invoiceConfigRepository.findByAuthorAndDefaultType(author, 1);
    }
}
