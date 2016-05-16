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

import com.huotu.agento2o.service.entity.author.Agent;
import org.springframework.security.core.userdetails.UserDetailsService;

import java.util.List;

/**
 * Created by helloztt on 2016/5/9.
 */
public interface AgentService extends UserDetailsService {

    /**
     * 根据唯一id获取某个代理商
     * @param id
     * @return
     */
    Agent findById(Integer id);

    /**
     * 根据登录名获取审核通过的代理商
     * @param userName
     * @return
     */
    Agent findByUserName(String userName);

    /**
     * 增加代理商
     * @param agent
     * @return
     */
    Agent addAgent(Agent agent);

    /**
     * 刷新，缓存与数据库同步
     */
    void flush();

    /**
     * 根据代理商等级id获取代理商集合
     * @param id
     * @return
     */
    List<Agent> findByAgentLevelId(Integer id);

    /**
     * 判断用户名是否可用
     * @param userName
     * @return true--可用
     */
    boolean ifEnable(String userName);

}
