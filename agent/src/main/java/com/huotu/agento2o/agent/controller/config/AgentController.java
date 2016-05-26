/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 */

package com.huotu.agento2o.agent.controller.config;

import com.huotu.agento2o.agent.config.annotataion.AgtAuthenticationPrincipal;
import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.service.author.AgentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * Created by WangJie on 2016/5/23.
 */
@Controller
@RequestMapping("/config")
@PreAuthorize("hasAnyRole('BASE_AGENT','AGENT')")
public class AgentController {

    @Autowired
    private AgentService agentService;

    /**
     * 展示代理商基本信息
     *
     * @param agent
     * @param model
     * @return
     */
    @RequestMapping(value = "/agentConfig", method = RequestMethod.GET)
    public String showAgent(@AgtAuthenticationPrincipal Agent agent, Model model) {
        model.addAttribute("agent", agentService.findById(agent.getId()));
        return "config/agentConfig";
    }

    /**
     * 保存代理商基本信息
     *
     * @param agent
     * @param requestAgent
     * @return
     */
    @RequestMapping(value = "/saveAgentConfig", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult saveAgentConfig(@AgtAuthenticationPrincipal Agent agent, Agent requestAgent) {
        return agentService.saveAgentConfig(agent.getId(), requestAgent);
    }

}
