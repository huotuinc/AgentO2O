/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.goods.impl;

import com.huotu.agento2o.service.entity.goods.MallProduct;
import com.huotu.agento2o.service.repository.goods.MallProductRepository;
import com.huotu.agento2o.service.service.goods.MallProductService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * Created by helloztt on 2016/5/16.
 */
@Service
public class MallProductServiceImpl implements MallProductService {
    @Autowired
    private MallProductRepository productRepository;


    @Override
    public MallProduct findByProductId(Integer productId) {
        return productRepository.findOne(productId);
    }

    @Override
    public List<MallProduct> findByGoodsId(Integer goodsId) {
        return productRepository.findByGoods_GoodsId(goodsId);
    }
}
