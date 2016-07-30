/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.service.service.author;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.common.AgentStatusEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.entity.author.Shop;
import com.huotu.agento2o.service.searchable.ShopSearchCondition;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
public interface ShopService {

//    Shop findByUserName(String userName);

    Shop findById(Integer id);

    Shop findByIdAndParentAuthor(Integer shopId, Agent agent);

    List<Shop> findByAgentId(Integer agentId);

    Shop findByIdAndCustomer_Id(Integer shopId, Integer customer_Id);

    ApiResult saveOrUpdateShop(Shop shop, String hotUserName, Agent agent);

    ApiResult saveShopConfig(Shop shop, String hotUserName);

    void flush();

    /**
     * 更新审核状态
     *
     * @param status
     * @param shopId
     * @param agent
     */
    ApiResult updateStatus(AgentStatusEnum status, int shopId, Agent agent);

    /**
     * 更新审核状态和备注
     *
     * @param status
     * @param id
     */
    ApiResult updateStatusAndAuditComment(AgentStatusEnum status, String auditComment, int id);

    ApiResult deleteById(int shopId, Agent agent);

    /**
     * 冻结解冻
     *
     * @param id
     */
    ApiResult updateIsDisabledById(int id);

    Page<Shop> findAll(int pageIndex, int pageSize, ShopSearchCondition searchCondition);

    List<Shop> findAll();

    List<Shop> findByCustomerId(Integer customerId);

    HSSFWorkbook createWorkBook(List<Shop> shops);

    /**
     * 根据平台id和用户名模糊查询小伙伴的用户名集合
     *
     * @param customerId
     * @param name
     * @return
     */
    List<String> getHotUserNames(Integer customerId, String name);

    ApiResult updateAccountInfo(Integer shopId, String bankName, String accountName, String accountNo);
}
