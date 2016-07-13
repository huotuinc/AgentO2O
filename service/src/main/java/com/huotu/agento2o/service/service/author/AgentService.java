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
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.searchable.AgentSearcher;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.data.domain.Page;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
public interface AgentService {

    /**
     * 根据唯一id和平台方id获取某个代理商
     *
     * @param agentId
     * @param customerId
     * @return
     */
    Agent findById(Integer agentId, Integer customerId);

    /**
     * 根据唯一id获取某个代理商
     *
     * @param agentId
     * @return
     */
    Agent findByAgentId(Integer agentId);

    /**
     * 根据登录名获取审核通过的代理商
     *
     * @param userName
     * @return
     */
    Agent findByUserName(String userName);

    /**
     * 增加代理商
     *
     * @param agent
     * @return
     */
    Agent addAgent(Agent agent);

    /**
     * 刷新，缓存与数据库同步
     */
    void flush();

    /**
     * 根据代理商等级id获取未删除的代理商集合
     *
     * @param levelId
     * @return
     */
    List<Agent> findByAgentLevelId(Integer levelId);

    /**
     * 判断用户名是否可用
     *
     * @param userName
     * @return true--可用
     */
    boolean isEnableAgent(String userName);

    /**
     * 根据平台方id和搜索条件获取代理商页面
     *
     * @param customerId
     * @param agentSearcher
     * @return
     */
    Page<Agent> getAgentList(Integer customerId, AgentSearcher agentSearcher);

    /**
     * 根据唯一id删除代理商，实际上是修改isDeleted的状态
     *
     * @param agentId
     * @return
     */
    int deleteAgent(Integer agentId);

    /**
     * 根据唯一id冻结代理商账号
     *
     * @param agentId
     * @return
     */
    int freezeAgent(Integer agentId);

    /**
     * 根据唯一id解冻代理商账号
     *
     * @param agentId
     * @return
     */
    int unfreezeAgent(Integer agentId);

    /**
     * 根据父代理商id获取代理商集合
     *
     * @param agentId
     * @return
     */
    List<Agent> findByParentAgentId(Integer agentId);

    /**
     * 增加或修改代理商
     *
     * @param customerId
     * @param agentLevelId
     * @param parentAgentId
     * @param hotUserName
     * @param requestAgent  当id>0时为修改
     * @return
     */
    ApiResult addOrUpdate(Integer customerId, Integer agentLevelId, Integer parentAgentId, String hotUserName, Agent requestAgent);

    /**
     * 将代理商信息导出到excel
     *
     * @param agents
     * @return
     */
    HSSFWorkbook createWorkBook(List<Agent> agents);

    /**
     * 根据小伙伴id查找绑定的代理商
     *
     * @param userId
     * @return
     */
    Agent findByUserBaseInfoId(Integer userId);

    /**
     * 根据代理商id重置代理商密码
     *
     * @param agentId
     * @param password
     * @return
     */
    int resetPassword(Integer agentId, String password);

    /**
     * 保存代理商基本信息
     *
     * @param agentId
     * @param requestAgent
     * @return
     */
    ApiResult saveAgentConfig(Integer agentId, Agent requestAgent,String hotUserName);

    /**
     * 根据平台id和用户名模糊查询小伙伴的用户名集合
     *
     * @param customerId
     * @param name
     * @return
     */
    List<String> getHotUserNames(Integer customerId, String name);

}
