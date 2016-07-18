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

import com.hot.datacenter.entity.customer.AgentShop;
import com.hot.datacenter.entity.customer.MallAgent;
import com.hot.datacenter.ienum.AgentStatusEnum;
import com.hot.datacenter.search.AgentShopSearch;
import com.hot.datacenter.service.CrudService;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.author.ShopAuthor;
import com.huotu.agento2o.service.entity.author.Agent;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
public interface AgentShopService extends CrudService<AgentShop, Integer, AgentShopSearch>, UserDetailsService {

    AgentShop findByUserName(String userName);

    List<AgentShop> findByAgentId(Integer agentId);

    ApiResult saveOrUpdateShop(AgentShop shop, String hotUserName, MallAgent agent);

    ApiResult saveShopConfig(ShopAuthor shop, String hotUserName);

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

    /**
     * 冻结解冻
     *
     * @param id
     */
    ApiResult updateIsDisabledById(int id);

    ApiResult updatePasswordById(String password, int shopId);

    Page<AgentShop> findAll(int pageIndex, int pageSize, AgentShopSearch agentShopSearch);

    List<AgentShop> findByCustomerId(Integer customerId);

    HSSFWorkbook createWorkBook(List<ShopAuthor> shops);

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
