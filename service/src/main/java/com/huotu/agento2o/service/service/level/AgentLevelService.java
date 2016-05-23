package com.huotu.agento2o.service.service.level;

import com.huotu.agento2o.service.entity.level.AgentLevel;

import java.util.List;

/**
 * Created by WangJie on 2016/5/11.
 */
public interface AgentLevelService {

    /**
     * 根据平台方id获取代理商等级集合
     * @param customerId
     * @return
     */
    List<AgentLevel> findByCustomertId(Integer customerId);

    /**
     * 增加代理商等级
     * @param agentLevel
     * @return
     */
    AgentLevel addAgentLevel(AgentLevel agentLevel);

    /**
     * 删除代理商等级
     * @param id
     */
    void deleteAgentLevel(Integer id);

    /**
     * 获取某个代理商等级
     * @param id
     * @return
     */
    AgentLevel findById(Integer id);

    /**
     * 刷新，缓存与数据库同步
     */
    void flush();

    /**
     * 根据平台方id获取当前最小等级
     * 数字越小代表等级越大
     * @param customerId
     * @return
     */
    Integer findLastLevel(Integer customerId);

    /**
     * 根据levelId增加或修改代理商等级
     * @param levelId 当levelId大于0时修改，否则增加
     * @param customerId
     * @param agentLevel
     * @return
     */
    boolean addOrUpdate(Integer levelId,Integer customerId,AgentLevel agentLevel);
}
