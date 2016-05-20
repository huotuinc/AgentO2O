/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.service.order;

import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallAfterSalesItem;

import java.util.List;

/**
 * Created by AiWelv on 2016/5/17.
 */
public interface MallAfterSalesItemService {

    List<MallAfterSalesItem> findByAfterId(String afterId);

    void updateStatus(AfterSaleEnum.AfterItemsStatus afterItemsStatus, int itemId);

    /**
     * 得到最近一条协商表记录
     *
     * @param afterId
     * @return
     */
    MallAfterSalesItem findTopByAfterId(String afterId);

    /**
     * 保存实体
     *
     * @param afterSalesItem
     * @return
     */
    MallAfterSalesItem save(MallAfterSalesItem afterSalesItem);

    /**
     * 得到最近一条非留言记录
     */
    MallAfterSalesItem findTopByIsLogic(MallAfterSales afterSales, int isLogic);



}
