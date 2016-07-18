/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.goods;

import com.huotu.agento2o.service.entity.author.Author;
import com.huotu.agento2o.service.entity.goods.MallProduct;

import java.util.List;

/**
 * Created by helloztt on 2016/5/16.
 */
public interface MallProductService {

    MallProduct findByProductId(Integer productId);

    List<MallProduct> findByGoodsId(Author author,Integer goodsId);

    void setProductPrice(MallProduct product,Author author);
}
