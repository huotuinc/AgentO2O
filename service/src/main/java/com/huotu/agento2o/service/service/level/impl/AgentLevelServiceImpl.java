package com.huotu.agento2o.service.service.level.impl;

import com.huotu.agento2o.common.util.ApiResult;
import com.huotu.agento2o.common.util.ResultCodeEnum;
import com.huotu.agento2o.service.entity.MallCustomer;
import com.huotu.agento2o.service.entity.level.AgentLevel;
import com.huotu.agento2o.service.repository.MallCustomerRepository;
import com.huotu.agento2o.service.repository.level.AgentLevelRepository;
import com.huotu.agento2o.service.service.MallCustomerService;
import com.huotu.agento2o.service.service.author.AgentService;
import com.huotu.agento2o.service.service.level.AgentLevelService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * Created by WangJie on 2016/5/11.
 */
@Service("agentLevelService")
public class AgentLevelServiceImpl implements AgentLevelService {

    @Autowired
    private AgentLevelRepository agentLevelRepository;

    @Autowired
    private MallCustomerService mallCustomerService;

    @Autowired
    private AgentService agentService;

    @Override
    public List<AgentLevel> findByCustomerId(Integer customerId) {
        return agentLevelRepository.findByCustomer_customerIdOrderByLevel(customerId);
    }

    @Override
    public AgentLevel addAgentLevel(AgentLevel agentLevel) {
        return agentLevel == null ? null : agentLevelRepository.save(agentLevel);
    }

    @Override
    @Transactional
    public ApiResult deleteAgentLevel(Integer levelId,Integer customerId) {
        AgentLevel agentLevel = findById(levelId,customerId);
        if(agentLevel == null){
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (agentService.findByAgentLevelId(levelId).size() > 0) {
            return new ApiResult("等级已被绑定");
        }
        agentLevelRepository.delete(agentLevel);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }

    @Override
    public AgentLevel findById(Integer levelId, Integer customerId) {
        return levelId == null || customerId == null ? null : agentLevelRepository.findByLevelIdAndCustomer_customerId(levelId,customerId);
    }

    @Override
    public void flush() {
        agentLevelRepository.flush();
    }

    @Override
    public Integer findLastLevel(Integer customerId) {
        return customerId == null ? null : agentLevelRepository.findLastLevel(customerId);
    }

    @Override
    @Transactional
    public ApiResult addOrUpdate(Integer levelId, Integer customerId, AgentLevel requestAgentLevel) {
        if(levelId == null || customerId == null || requestAgentLevel == null){
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        MallCustomer customer = mallCustomerService.findByCustomerId(customerId);
        AgentLevel agentLevel;
        if (customer == null) {
            return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
        }
        if (levelId > 0) {
            agentLevel = findById(levelId,customerId);
            if (agentLevel == null) {
                return ApiResult.resultWith(ResultCodeEnum.DATA_NULL);
            }
        } else {
            Integer level = findLastLevel(customerId);
            agentLevel = new AgentLevel();
            agentLevel.setCustomer(customer);
            //等级依次递增，初始值为0
            agentLevel.setLevel(level == null ? 0 : level + 1);
        }
        agentLevel.setLevelName(requestAgentLevel.getLevelName());
        agentLevel.setComment(requestAgentLevel.getComment());
        addAgentLevel(agentLevel);
        return ApiResult.resultWith(ResultCodeEnum.SUCCESS);
    }
}
