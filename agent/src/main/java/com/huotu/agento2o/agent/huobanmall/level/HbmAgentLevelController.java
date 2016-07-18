package com.huotu.agento2o.agent.huobanmall.level;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
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
@RequestMapping("/huobanmall/level")
public class HbmAgentLevelController {

    @Autowired
    private AgentLevelService agentLevelService;

    @Autowired
    private AgentService agentService;

    /**
     * 增加或修改代理商等级
     *
     * @param customerId
     * @param requestAgentLevel
     * @param levelId
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addAndSaveLevel(@RequestAttribute(value = "customerId") Integer customerId, AgentLevel requestAgentLevel, Integer levelId) {
        if(StringUtil.isEmptyStr(requestAgentLevel.getLevelName())){
            return new ApiResult("请输入等级名称");
        }
        return agentLevelService.addOrUpdate(levelId, customerId, requestAgentLevel);
    }

    /**
     * 删除代理商等级
     *
     * @param customerId
     * @param levelId
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteLevel(@RequestAttribute(value = "customerId") Integer customerId,Integer levelId) {
        return agentLevelService.deleteAgentLevel(levelId,customerId);
    }

    /**
     * 根据id获取代理商等级
     *
     * @param customerId
     * @param levelId
     * @return
     */
    @RequestMapping(value = "/{levelId}", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult showLevel(@RequestAttribute(value = "customerId") Integer customerId,@PathVariable Integer levelId) {
        AgentLevel agentLevel = agentLevelService.findById(levelId,customerId);
        if (agentLevel == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, agentLevel);
    }

    /**
     * 展示代理商等级列表
     *
     * @param customerId
     * @param model
     * @return
     */
    @RequestMapping("/levelList")
    public String showLevelList(@RequestAttribute(value = "customerId") Integer customerId, Model model) {
        List<AgentLevel> agentLevels = agentLevelService.findByCustomertId(customerId);
        model.addAttribute("agentLevels", agentLevels);
        return "huobanmall/level/agentLevelList";
    }

}
