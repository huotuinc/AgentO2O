/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.repository.purchase;

import com.huotu.agento2o.service.entity.purchase.FreightTemplate;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * Created by helloztt on 2016/7/21.
 */
@Repository
public interface FreightTemplateRepository extends JpaRepository<FreightTemplate,Long> {
    /**
     * 查询供应商运费模板
     * @param customerId
     * @return
     */
    @Query("select a from FreightTemplate a where a.customerId = ?1 and a.freightTemplateType = 1")
    List<FreightTemplate> findByCustomerId(int customerId);
}
