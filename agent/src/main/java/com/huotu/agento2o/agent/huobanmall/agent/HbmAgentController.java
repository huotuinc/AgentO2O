/*
 * 版权所有:杭州火图科技有限公司
 * 地址:浙江省杭州市滨江区西兴街道阡陌路智慧E谷B幢4楼在地图中查看
 *
 * (c) Copyright Hangzhou Hot Technology Co., Ltd.
 * Floor 4,Block B,Wisdom E Valley,Qianmo Road,Binjiang District
 * 2013-2016. All rights reserved.
 *
 */

package com.huotu.agento2o.agent.huobanmall.agent;

import com.huotu.agento2o.agent.config.annotataion.RequestAttribute;
import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.common.util.StringUtil;
import com.huotu.agento2o.service.common.AgentActiveEnum;
import com.huotu.agento2o.service.entity.author.Agent;
import com.huotu.agento2o.service.searchable.AgentSearcher;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.author.impl.AgentServiceImpl;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.io.OutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Date;
import java.util.List;

/**
 * Created by WangJie on 2016/5/13.
 */
@Controller
@RequestMapping("/huobanmall/agent")
public class HbmAgentController {

    @Autowired
    private AgentService agentService;

    @Autowired
    private AgentLevelService agentLevelService;

    /**
     * 代理商列表
     *
     * @param customerIdStr
     * @param model
     * @return
     */
    @RequestMapping("/agentList")
    public String showAgentList(@RequestAttribute(value = "customerId") String customerIdStr, Model model, AgentSearcher agentSearcher) {
        int customerId = Integer.parseInt(customerIdStr);
        Page<Agent> page = agentService.getAgentList(customerId, agentSearcher);
        model.addAttribute("agentLevels", agentLevelService.findByCustomertId(customerId));
        model.addAttribute("page", page);
        model.addAttribute("agentActiveEnums", AgentActiveEnum.values());
        return "huobanmall/agent/agentList";
    }

    /**
     * 删除代理商，实际上是修改代理商的isDeleted状态
     *
     * @param agentId
     * @return
     */
    @RequestMapping(value = "/delete", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult deleteAgent(Integer agentId) {
        if (agentService.findByParentAgentId(agentId).size() > 0) {
            return new ApiResult("代理商已被绑定", 801);
        }
        agentService.deleteAgent(agentId);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 冻结或解冻代理商账号
     *
     * @param status  0-激活状态  1-冻结状态
     * @param agentId
     * @return
     */
    @RequestMapping(value = "/updateStatus", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult updateDisabledStatus(Integer status, Integer agentId) {
        if (status == 0) {
            agentService.freezeAgent(agentId);
        } else {
            agentService.unfreezeAgent(agentId);
        }
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    /**
     * 显示增加或修改代理商页面
     *
     * @param customerIdStr
     * @param model
     * @param agent         当agentId>0时，是修改页面
     * @return
     */
    @RequestMapping(value = "/showAgent", method = RequestMethod.GET)
    public String showAgent(@RequestAttribute(value = "customerId") String customerIdStr, Model model, Agent agent) {
        int customerId = Integer.parseInt(customerIdStr);
        Integer agentId = agent.getId();
        Integer parentAgentLevelId = -1;
        if (agentId > 0) {
            Agent oldAgent = agentService.findById(agentId);
            //获取上级代理商的代理商等级
            if (oldAgent.getParentAuthor() != null && oldAgent.getParentAuthor().getAgentLevel() != null) {
                parentAgentLevelId = oldAgent.getParentAuthor().getAgentLevel().getLevelId();
            }
            model.addAttribute("agent", agentService.findById(agentId));
        }
        model.addAttribute("agentLevels", agentLevelService.findByCustomertId(customerId));
        model.addAttribute("parentAgentLevelId", parentAgentLevelId);
        return "huobanmall/agent/addAgent";
    }

    /**
     * 通过上级代理商等级获取上级代理商集合
     *
     * @param parentAgentLevelId
     * @return
     */
    @RequestMapping(value = "/getParentAgents", method = RequestMethod.GET)
    @ResponseBody
    public ApiResult getParentAgents(Integer parentAgentLevelId) {
        List<Agent> data = agentService.findByAgentLevelId(parentAgentLevelId);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS, data);
    }

    /**
     * 增加或修改代理商
     *
     * @param customerIdStr 平台id
     * @param agentLevelId  等级id
     * @param parentAgentId 上级代理商id
     * @param hotUserName   小伙伴用户名
     * @param requestAgent  代理商
     * @return
     */
    @RequestMapping(value = "/save", method = RequestMethod.POST)
    @ResponseBody
    public ApiResult addOrSaveAgent(@RequestAttribute(value = "customerId") String customerIdStr, Integer agentLevelId, Integer parentAgentId, String hotUserName, Agent requestAgent) {
        int customerId = Integer.parseInt(customerIdStr);
        return agentService.addOrUpdate(customerId, agentLevelId, parentAgentId, hotUserName, requestAgent);
    }

    @RequestMapping("/exportExcel")
    public void exportExcel(AgentSearcher searchCondition, String province,
                            int txtBeginPage,
                            int txtEndPage,
                            @RequestAttribute(value = "customerId") String customerIdStr,
                            HttpSession session,
                            HttpServletResponse response) throws UnsupportedEncodingException {
        int customerId = Integer.parseInt(customerIdStr);
        int pageSize = searchCondition.getPageSize() * (txtEndPage - txtBeginPage + 1);
        searchCondition.setPageSize(pageSize);
        searchCondition.setPageNo(txtBeginPage);
        Page<Agent> pageInfo = agentService.getAgentList(customerId, searchCondition);
        session.setAttribute("state", null);
        // 生成提示信息，
        response.setContentType("apsplication/vnd.ms-excel");
        String codedFileName = null;
        OutputStream fOut = null;
        try {
            // 进行转码，使其支持中文文件名
            String excelName = "agent-" + StringUtil.DateFormat(new Date(), StringUtil.DATETIME_PATTERN_WITH_NOSUP);
            excelName = java.net.URLEncoder.encode(excelName, "UTF-8");
            response.setHeader("content-disposition", "attachment;filename=" + excelName + ".xls");
            HSSFWorkbook workbook = agentService.createWorkBook(pageInfo.getContent());
            fOut = response.getOutputStream();
            workbook.write(fOut);
        } catch (Exception ignored) {
        } finally {
            try {
                assert fOut != null;
                fOut.flush();
                fOut.close();
            } catch (IOException ignored) {
            }
            session.setAttribute("state", "open");
        }
    }
}
