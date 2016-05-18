/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.service.repository.order;

import com.huotu.agento2o.service.common.AfterSaleEnum;
import com.huotu.agento2o.service.entity.order.MallAfterSales;
import com.huotu.agento2o.service.entity.order.MallAfterSalesItem;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by allan on 12/30/15.
 */
@Repository
public interface MallAfterSalesItemRepository extends JpaRepository<MallAfterSalesItem, Integer> {

    List<MallAfterSalesItem> findByAfterSales_AfterIdOrderByItemIdDesc(String afterId);

    MallAfterSalesItem findTopByAfterSales_AfterIdOrderByItemIdDesc(String afterId);

    @Query("update MallAfterSalesItem set afterItemsStatus=?1 where itemId=?2")
    void statusUpdate(AfterSaleEnum.AfterItemsStatus afterItemsStatus, int itemId);

    MallAfterSalesItem findTopByAfterSalesAndIsLogicNotOrderByItemIdDesc(MallAfterSales afterSales, int isLogic);
}
