package com.huotu.agento2o.agent.huobanmall.level;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.List;

/**
 * Created by WangJie on 2016/5/12.
 */
@Controller
@RequestMapping("/level")
public class AgentLevelController {

    @Autowired
    private AgentLevelService agentLevelService;

    @Autowired
    private AgentService agentService;

    /**
     * 增加或修改代理商等级
     * @param customer
     * @param requestAgentLevel
     * @param levelId
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addAndSaveLevel(MallCustomer customer,AgentLevel requestAgentLevel,Integer levelId) {
        customer.setCustomerId(5919);
        AgentLevel agentLevel;
        if (levelId > 0) {
            agentLevel = agentLevelService.findById(levelId);
        } else {
            Integer level = agentLevelService.findLastLevel(customer.getCustomerId());
            agentLevel = new AgentLevel();
            agentLevel.setCustomer(customer);
            agentLevel.setLevel(level == null ? 0:level+1);
        }
        agentLevel.setLevelName(requestAgentLevel.getLevelName());
        agentLevel.setComment(requestAgentLevel.getComment());
        agentLevelService.addAgentLevel(agentLevel);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 删除代理商等级
     * @param levelId
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteLevel(Integer levelId) {
        if(agentService.findByAgentLevelId(levelId).size() > 0){
            return new ApiResult("等级已被使用",800);
        }
        agentLevelService.deleteAgentLevel(levelId);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 根据id获取代理商等级
     * @param levelId
     * @return
     */
    @RequestMapping(value = "/{levelId}", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult showLevel(@PathVariable Integer levelId) {
        AgentLevel agentLevel = agentLevelService.findById(levelId);
        if (agentLevel == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, agentLevel);
    }

    /**
     * 展示代理商等级列表
     * @param customer
     * @param model
     * @return
     */
    @RequestMapping("/levelList")
    public String showLevelList( MallCustomer customer, Model model) {
        customer.setCustomerId(5919);
        List<AgentLevel> agentLevels = agentLevelService.findByCustomertId(customer.getCustomerId());
        model.addAttribute("agentLevels", agentLevels);
        return "level/agentLevelList";
    }

}
